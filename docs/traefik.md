# OpenJobs com Traefik

O Traefik central roda pelo projeto `home-lab-docker`. Suba primeiro esse proxy:

```bash
cd /caminho/home-lab-docker/traefik
docker compose up -d --wait
```

Ele cria a rede `traefik-proxy`. Na raiz do OpenJobs, com `.env` configurado:

```bash
docker compose config --quiet
docker compose up -d --build
```

A API participa da rede privada `openjobs` e da rede externa `traefik-proxy`.
As labels anunciam o domínio `API_HOST` (padrão `api.openjobs.shop`) e a porta
interna `SERVER_PORT` (padrão `8082`). O Traefik configura a rota automaticamente.
A API não publica porta no host; o RabbitMQ é acessado como `rabbitmq:5672`.
Sua porta publicada no host continua disponível para o ambiente local/legado.

## Conferir o encaminhamento

```bash
curl -i -H 'Host: api.openjobs.shop' http://127.0.0.1:8090/api/actuator/health/readiness
docker compose logs --tail=100 api
```

O endpoint deve responder HTTP 200 com `status: UP`. Se `API_HOST` foi alterado,
use esse mesmo domínio no header. Um 404 na raiz do proxy sem domínio é esperado.

Para passar a produção por essa rota, o hostname `api.openjobs.shop` no Cloudflare
Tunnel deve apontar para `http://localhost:8090`, preservando o header Host. Isso
pressupõe que o Tunnel roda no host. Teste antes a resposta local e depois a URL
pública. Não é necessário configurar CORS no proxy: a API já trata as origens.

## Banco externo

A API passa a usar a rede bridge do Docker. Verifique a conectividade do
`POSTGRES_HOST` a partir do container, especialmente se o endereço do Supabase
resolve apenas para IPv6. Caso a rede Docker não alcance esse endereço, configure
uma conexão de banco compatível com a conectividade do servidor antes de migrar.
Esta alteração não modifica o `.env`, o banco nem seus dados.

## Fluxo de deploy

Para atualizar a API, execute na raiz do projeto:

```bash
docker compose up -d --build
```

O Compose recria o container quando a imagem muda. Pode haver uma breve
indisponibilidade enquanto a API inicia. O Traefik acompanha as labels do container
e encaminha as requisições quando ele estiver saudável.
