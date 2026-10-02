# Deploy com Caddy e troca de instâncias

O `deploy.sh` usa o **Caddy já instalado como serviço systemd**. Requisitos:
servidor Linux, Docker Compose v2, Bash, curl, flock (util-linux), Caddy e
permissão de sudo para atualizar a configuração e recarregar o serviço.

## Fluxo

1. Faz o build de uma imagem com tag exclusiva enquanto a versão antiga atende.
2. Sobe a instância inativa: `blue` na porta 8083 ou `green` na 8084.
3. Espera a readiness ficar saudável por 30 segundos contínuos, verificando
   também banco e RabbitMQ.
4. Atualiza apenas `/etc/caddy/openjobs-upstream.caddy`, valida o Caddyfile e
   executa `systemctl reload caddy`.
5. Confirma o destino local e pelo domínio público pelo header `X-OpenJobs-Slot`
   e pelo status JSON `UP`, e observa por mais 30 segundos.
6. Em caso de falha até essa confirmação, restaura o upstream anterior.
7. Após confirmar, espera 65 segundos para drenar a versão antiga e a para
   com SIGTERM, permitindo até 50 segundos para o encerramento.

As APIs escutam somente em localhost. O Caddy mantém o bloco HTTPS existente
de `api.openjobs.shop`, incluindo headers, compressão e outros sites. Não reinicia o serviço Caddy,
não recria RabbitMQ e não remove volumes ou imagens. O reload gracioso é
documentado pelo [Caddy](https://caddyserver.com/docs/running).

## Primeira instalação

Na raiz do repositório, com o `.env` de produção configurado e a API antiga
`openjobs-api` rodando:

```bash
git pull
bash deploy.sh --bootstrap
```

O script usa `/etc/caddy/Caddyfile` e o serviço `caddy` por padrão. **Confira se
esse é o arquivo usado pelo seu serviço** com `systemctl cat caddy`. Se for
outro, informe `CADDYFILE` na execução. O `ExecReload` do serviço precisa
recarregar esse mesmo arquivo.

O bootstrap altera somente a linha `reverse_proxy 127.0.0.1:8082` dentro
do bloco existente do domínio, substituindo-a pelo import gerenciado:

```caddyfile
api.openjobs.shop {
    header {
        Access-Control-Allow-Origin "https://openjobs.shop"
        Access-Control-Allow-Methods "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        Access-Control-Allow-Headers "Content-Type, Authorization"
    }
    encode zstd gzip
    import /etc/caddy/openjobs-upstream.caddy
}
```

Os headers acima são apenas o exemplo fornecido para o servidor: o script
preserva os valores existentes, não os substitui pelo exemplo. Formatos
diferentes de bloco/upstream são recusados antes de alterar configuração.

Depois de estabilizar a candidata, recarrega o Caddy e confirma o domínio
HTTPS localmente, com SNI e certificado válidos. A antiga continua atendendo
até confirmar a troca e terminar a drenagem, inclusive no bootstrap.

O tráfego externo deve passar pelo bloco do domínio no Caddy. Se um túnel
Cloudflare estiver apontando diretamente para a API na porta 8082, ele precisa
ser configurado para usar o Caddy; caso contrário, vai contornar a troca.
Se o domínio público não confirmar a candidata, o script faz rollback e
preserva a versão anterior, sem parar a instância que estava atendendo.
As portas 8083 e 8084 precisam estar livres.

Versões anteriores do script criavam um bloco com o comentário
`# OpenJobs API: managed listener...` na porta 8082. Essa versão remove
somente esse bloco gerado e migra o import para o domínio existente.

## Próximas atualizações

```bash
git pull
bash deploy.sh
```

O script não faz `git pull` automaticamente. Não execute `docker compose down`
nem recrie o serviço antigo `api` neste fluxo: ele não é mais o destino ativo.
Os consumidores de e-mail das duas instâncias competem pela mesma fila
durante a sobreposição.

## Parâmetros opcionais

```bash
START_TIMEOUT=420 STABLE_SECONDS=45 OBSERVE_SECONDS=45 bash deploy.sh
```

| Variável | Padrão | Uso |
| --- | --- | --- |
| `CADDYFILE` | `/etc/caddy/Caddyfile` | Mesmo arquivo usado pelo serviço |
| `CADDY_UPSTREAM_FILE` | `/etc/caddy/openjobs-upstream.caddy` | Fragmento gerenciado; mantenha após instalar |
| `CADDY_SERVICE` | `caddy` | Nome do serviço systemd |
| `API_HOST` | `api.openjobs.shop` | Bloco do domínio preservado e validado |
| `CADDY_PORT` | 443 | Porta HTTPS já usada pelo Caddy |
| `PROXY_PORT` | 8082 | Porta da API antiga; usada apenas na migração inicial |
| `API_BLUE_PORT` | 8083 | Porta da instância blue |
| `API_GREEN_PORT` | 8084 | Porta da instância green |
| `START_TIMEOUT` | 300 | Prazo em segundos para iniciar e estabilizar |
| `STABLE_SECONDS` | 30 | Saúde contínua antes da troca |
| `OBSERVE_SECONDS` | 30 | Observação após a troca |
| `DRAIN_SECONDS` | 65 | Drenagem da antiga; mínimo 65 segundos |

O servidor precisa suportar temporariamente duas APIs (até 768 MB cada),
RabbitMQ e o build. O banco também precisa comportar dois pools de conexões.
Readiness comprova inicialização e dependências; não substitui testes dos
fluxos de negócio. Requisições excepcionalmente longas podem precisar de um
`DRAIN_SECONDS` maior.

## Banco e recuperação

Flyway executa as migrations ao iniciar a candidata, com a API antiga ativa.
Use migrations compatíveis com ambas as versões: adicione campos/tabelas
primeiro; remova ou renomeie somente em uma entrega posterior. Alterações
incompatíveis ou locks longos podem afetar produção antes de trocar tráfego.
Rollback de tráfego não desfaz migrations ou dados.

Após concluir a observação, o deploy é considerado confirmado. Falhas
posteriores exigem uma nova execução com o código corrigido ou recuperação
manual da versão anterior.

```bash
docker ps --filter name=openjobs-api
docker logs --tail=100 openjobs-api-blue
docker logs --tail=100 openjobs-api-green
curl --resolve api.openjobs.shop:443:127.0.0.1 https://api.openjobs.shop/api/actuator/health/readiness
sudo journalctl -u caddy -n 50 --no-pager
```

Preserve `.deploy/`: contém a trava de deploy e backups para recuperação;
está no `.gitignore`, com acesso restrito. Os containers ativos usam
`restart: unless-stopped`; a instância parada permanece parada após reboot.

Se o script indicar falha no rollback, ele preserva as duas instâncias.
Confira o fragmento de upstream e os backups. Valide a configuração antes
de recarregar:

```bash
sudo caddy validate --config /etc/caddy/Caddyfile --adapter caddyfile
sudo systemctl reload caddy
```

## Verificar o script localmente

```bash
python scripts/test_deploy.py
```

Os testes usam comandos simulados e não iniciam APIs, não acessam produção
e não alteram o Caddy do computador. No Windows, defina `BASH_EXE` com o
caminho do Git Bash.
