#!/usr/bin/env bash
set -Eeuo pipefail
cd -- "$(dirname -- "${BASH_SOURCE[0]}")"

log() { printf '[deploy] %s\n' "$*"; }
die() { log "$*" >&2; exit 1; }
if [[ ${1:-} == --help ]]; then
  printf 'Usage: bash deploy.sh [--bootstrap]\nUses the existing API domain block in the system Caddyfile.\n'
  exit 0
fi
[[ $# -eq 0 || ( $# -eq 1 && $1 == --bootstrap ) ]] || die 'Invalid argument; use --help.'
bootstrap=${1:-}
for command in docker curl flock caddy systemctl; do command -v "$command" >/dev/null || die "Missing command: $command"; done
docker compose version >/dev/null
docker info >/dev/null 2>&1 || die 'Docker unavailable.'
[[ -f .env ]] || die 'Configure .env before deploying.'
mkdir -p .deploy
chmod 700 .deploy
exec 9>.deploy/deploy.lock
flock -n 9 || die 'Another deploy is already running.'

privileged() {
  if (( EUID == 0 )); then "$@"; else sudo "$@"; fi
}
if (( EUID != 0 )); then command -v sudo >/dev/null || die 'sudo required to reload system Caddy.'; sudo -v; fi
export PROXY_PORT=${PROXY_PORT:-8082}
export API_BLUE_PORT=${API_BLUE_PORT:-8083}
export API_GREEN_PORT=${API_GREEN_PORT:-8084}
CADDYFILE=${CADDYFILE:-/etc/caddy/Caddyfile}
CADDY_UPSTREAM_FILE=${CADDY_UPSTREAM_FILE:-/etc/caddy/openjobs-upstream.caddy}
CADDY_SERVICE=${CADDY_SERVICE:-caddy}
API_HOST=${API_HOST:-api.openjobs.shop}
CADDY_PORT=${CADDY_PORT:-443}
START_TIMEOUT=${START_TIMEOUT:-300}
STABLE_SECONDS=${STABLE_SECONDS:-30}
OBSERVE_SECONDS=${OBSERVE_SECONDS:-30}
DRAIN_SECONDS=${DRAIN_SECONDS:-65}
POLL_SECONDS=5
for value in "$PROXY_PORT" "$API_BLUE_PORT" "$API_GREEN_PORT"; do
  [[ $value =~ ^[0-9]+$ && $value -ge 1024 && $value -le 65535 ]] || die 'Ports must be between 1024 and 65535.'
done
[[ $CADDY_PORT =~ ^[0-9]+$ && $CADDY_PORT -ge 1 && $CADDY_PORT -le 65535 ]] || die 'Invalid Caddy HTTPS port.'
[[ $PROXY_PORT != "$API_BLUE_PORT" && $PROXY_PORT != "$API_GREEN_PORT" && $API_BLUE_PORT != "$API_GREEN_PORT" ]] || die 'All three ports must be different.'
for value in "$START_TIMEOUT" "$STABLE_SECONDS" "$OBSERVE_SECONDS" "$DRAIN_SECONDS"; do
  [[ $value =~ ^[0-9]+$ && $value -ge 5 ]] || die 'Durations must be integers of at least 5 seconds.'
done
(( DRAIN_SECONDS >= 65 )) || die 'DRAIN_SECONDS must be at least 65.'
(( START_TIMEOUT >= STABLE_SECONDS )) || die 'START_TIMEOUT must be at least STABLE_SECONDS.'
[[ $CADDYFILE == /* && $CADDY_UPSTREAM_FILE == /* && $CADDYFILE != "$CADDY_UPSTREAM_FILE" ]] || die 'Use distinct absolute paths for Caddy files.'
[[ $CADDY_UPSTREAM_FILE != *[[:space:]]* ]] || die 'Upstream file path must not contain whitespace.'
privileged test -f "$CADDYFILE" || die 'Caddyfile not found; set CADDYFILE to the service config path.'
systemctl is-active --quiet "$CADDY_SERVICE" || die 'Start the existing Caddy service before deploying.'

compose() { docker compose -f docker-compose.yml -f docker-compose.deploy.yml --profile deploy "$@"; }
running() { [[ $(docker inspect -f '{{.State.Running}}' "$1" 2>/dev/null || true) == true ]]; }
healthy() {
  curl --fail --silent --connect-timeout 2 --max-time 5 "http://127.0.0.1:$1/api/actuator/health/readiness" |
    grep -Eq '"status"[[:space:]]*:[[:space:]]*"UP"'
}
validate_caddy() { privileged caddy validate --config "$CADDYFILE" --adapter caddyfile; }
reload_caddy() { privileged systemctl reload "$CADDY_SERVICE"; }
install_atomic() {
  privileged install -m 644 "$1" "$2.openjobs.tmp"
  if privileged test -f "$2"; then
    privileged chown --reference="$2" "$2.openjobs.tmp"
    privileged chmod --reference="$2" "$2.openjobs.tmp"
  fi
  privileged mv -f "$2.openjobs.tmp" "$2"
}
routed_to() {
  local headers
  if [[ ${2:-local} == public ]]; then
    headers=$(curl --fail --silent --noproxy '*' --connect-timeout 2 --max-time 5 \
      -D - "https://$API_HOST/api/actuator/health/readiness") || return 1
  else
    headers=$(curl --fail --silent --noproxy '*' --connect-timeout 2 --max-time 5 \
      --resolve "$API_HOST:$CADDY_PORT:127.0.0.1" -D - \
      "https://$API_HOST:$CADDY_PORT/api/actuator/health/readiness") || return 1
  fi
  printf '%s\n' "$headers" | tr -d '\r' | grep -Eiq "^x-openjobs-slot:[[:space:]]*$1[[:space:]]*$" &&
    printf '%s\n' "$headers" | grep -Eq '"status"[[:space:]]*:[[:space:]]*"UP"'
}

old_slot=''
if privileged test -f "$CADDY_UPSTREAM_FILE"; then
  privileged cat "$CADDY_UPSTREAM_FILE" > .deploy/upstream.previous
  old_slot=$(sed -n 's/^# openjobs-active: //p' .deploy/upstream.previous)
  [[ $old_slot == blue || $old_slot == green ]] || die 'Upstream config is not managed by this deploy script.'
  running "openjobs-api-$old_slot" || die 'Active API is stopped; recover it before deploying.'
else
  [[ $bootstrap == --bootstrap ]] || die 'First installation requires: bash deploy.sh --bootstrap'
  running openjobs-api || die 'Bootstrap requires the legacy openjobs-api container running.'
  [[ $(docker inspect -f '{{index .Config.Labels "com.docker.compose.service"}}' openjobs-api) == api ]] || die 'Legacy container is not the expected Compose api service.'
fi
validate_caddy
# Prepare and check the narrowly scoped edit before building any container.
privileged cat "$CADDYFILE" > .deploy/Caddyfile.previous
prepare_caddyfile() {
  awk -v host="$API_HOST" -v upstream="$CADDY_UPSTREAM_FILE" \
    -v legacy_port="$PROXY_PORT" -v blue_port="$API_BLUE_PORT" -v green_port="$API_GREEN_PORT" \
    -f docker/update-caddy-upstream.awk .deploy/Caddyfile.previous > .deploy/Caddyfile.next
}
prepare_caddyfile || die 'Expected one API domain block with a simple reverse_proxy or managed import. Caddyfile preserved.'
new_slot=blue
[[ $old_slot == blue ]] && new_slot=green
new_port=$API_BLUE_PORT
[[ $new_slot == green ]] && new_port=$API_GREEN_PORT
new_container="openjobs-api-$new_slot"
export OPENJOBS_DEPLOY_IMAGE="openjobs-api:release-$(date -u +%Y%m%dT%H%M%SZ)-$$"
candidate_started=false
config_changed=false
caddyfile_changed=false
completed=false

cleanup() {
  local result=$?
  trap - EXIT INT TERM
  if [[ $completed == false ]]; then
    log 'Deploy interrupted. Restoring the previous version.'
    local restored=true
    if [[ $config_changed == true ]]; then
      if [[ -n $old_slot ]]; then
        install_atomic .deploy/upstream.previous "$CADDY_UPSTREAM_FILE" || restored=false
      fi
      if [[ $caddyfile_changed == true ]]; then
        install_atomic .deploy/Caddyfile.previous "$CADDYFILE" || restored=false
      fi
      validate_caddy && reload_caddy || restored=false
      if [[ -n $old_slot && $restored == true ]]; then
        restored=false
        for ((i=0; i<10; i++)); do
          if routed_to "$old_slot"; then restored=true; break; fi
          sleep 1
        done
      elif [[ -z $old_slot && $restored == true ]]; then
        privileged rm -f "$CADDY_UPSTREAM_FILE" || restored=false
      fi
    fi
    if [[ $restored == true && $candidate_started == true ]]; then
      if [[ $config_changed == true && -n $old_slot ]]; then sleep "$DRAIN_SECONDS"; fi
      compose stop "api-$new_slot" || true
    elif [[ $restored == false ]]; then
      log 'Rollback failed. Both API instances were preserved; inspect Caddy manually.' >&2
    fi
  fi
  # Preserve backups for recovery, especially if Caddy reload fails.
  rm -f .deploy/upstream.next .deploy/Caddyfile.next
  exit "$result"
}
trap cleanup EXIT
trap 'exit 130' INT
trap 'exit 143' TERM

log "Building $OPENJOBS_DEPLOY_IMAGE while production continues serving."
compose build "api-$new_slot"
docker compose -f docker-compose.yml up -d --no-recreate rabbitmq
log "Starting $new_slot on port $new_port."
candidate_started=true
compose up -d --no-deps --force-recreate "api-$new_slot"
deadline=$((SECONDS + START_TIMEOUT))
stable_since=-1
restart_count=$(docker inspect -f '{{.RestartCount}}' "$new_container")
while (( SECONDS < deadline )); do
  running "$new_container" || die 'Candidate stopped during startup; inspect its logs.'
  current_restarts=$(docker inspect -f '{{.RestartCount}}' "$new_container")
  if [[ $restart_count != "$current_restarts" ]]; then stable_since=-1; restart_count=$current_restarts; fi
  if healthy "$new_port"; then
    (( stable_since >= 0 )) || stable_since=$SECONDS
    (( SECONDS - stable_since >= STABLE_SECONDS )) && break
  else
    stable_since=-1
  fi
  sleep "$POLL_SECONDS"
done
(( stable_since >= 0 && SECONDS - stable_since >= STABLE_SECONDS )) || die 'Candidate did not stabilize before the deadline; previous production preserved.'

cat > .deploy/upstream.next <<EOF
# openjobs-active: $new_slot
reverse_proxy 127.0.0.1:$new_port {
    header_down X-OpenJobs-Slot "$new_slot"
    transport http {
        dial_timeout 5s
        response_header_timeout 60s
    }
}
EOF
privileged cat "$CADDYFILE" > .deploy/Caddyfile.previous
prepare_caddyfile || die 'API Caddy block changed during build; current configuration preserved.'
config_changed=true
install_atomic .deploy/upstream.next "$CADDY_UPSTREAM_FILE"
if ! cmp -s .deploy/Caddyfile.previous .deploy/Caddyfile.next; then
  caddyfile_changed=true
  install_atomic .deploy/Caddyfile.next "$CADDYFILE"
fi
validate_caddy
log "Candidate stable. Switching Caddy to $new_slot."
reload_caddy
confirmed=false
for ((i=0; i<15; i++)); do
  if routed_to "$new_slot" && routed_to "$new_slot" public; then confirmed=true; break; fi
  sleep 1
done
[[ $confirmed == true ]] || die 'Local/public domain did not confirm the new version. Check the Cloudflare Tunnel destination; previous API preserved.'
observe_until=$((SECONDS + OBSERVE_SECONDS))
while (( SECONDS < observe_until )); do
  running "$new_container" && healthy "$new_port" && routed_to "$new_slot" && routed_to "$new_slot" public || die 'New version failed after traffic switch.'
  [[ $(docker inspect -f '{{.RestartCount}}' "$new_container") == "$restart_count" ]] || die 'New version restarted after traffic switch.'
  sleep "$POLL_SECONDS"
done
completed=true
log "Draining previous instance for $DRAIN_SECONDS seconds."
sleep "$DRAIN_SECONDS"
if [[ -n $old_slot ]]; then
  compose stop "api-$old_slot"
else
  docker stop --time 50 openjobs-api >/dev/null
fi
log "Deploy complete: $new_slot at https://$API_HOST/api."
