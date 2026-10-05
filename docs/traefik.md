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
As labels anunciam o domínio configurado em `API_HOST` no `.env` e a porta
interna `SERVER_PORT` (padrão `8082`). O Traefik configura a rota automaticamente.
A API não publica porta no host; o RabbitMQ é acessado como `rabbitmq:5672`.
Sua porta publicada no host continua disponível para o ambiente local/legado.

## Conferir o encaminhamento

```bash
curl -i -H 'Host: api.example.com' http://127.0.0.1:8090/api/actuator/health/readiness
docker compose logs --tail=100 api
```

Substitua `api.example.com` pelo valor de `API_HOST` do seu `.env`.
O endpoint deve responder HTTP 200 com `status: UP`.
Um 404 na raiz do proxy sem domínio é esperado.

Para passar a produção por essa rota, o hostname configurado em `API_HOST` no Cloudflare
Tunnel deve apontar para `http://localhost:8090`, preservando o header Host. Isso
pressupõe que o Tunnel roda no host. Teste antes a resposta local e depois a URL
pública.

## Origem do frontend (CORS)

Configure `FRONTEND_URL` no `.env` com a origem completa do frontend, sem caminho
ou barra final, por exemplo `https://web.example.com`. As labels associam o
middleware `openjobs-cors` somente à rota desta API, permitindo os métodos
`GET, POST, PUT, PATCH, DELETE, OPTIONS` e os headers `Authorization, Content-Type`.

O Spring também valida CORS; ao trocar o domínio, mantenha a origem permitida
no backend alinhada com `FRONTEND_URL`. CORS controla o acesso pelo navegador;
a autenticação e as permissões continuam sendo responsabilidade da API.

## Banco externo

A rede privada `openjobs` habilita IPv6 para permitir a conexão direta ao Supabase
quando o endereço do banco resolve apenas para IPv6. O servidor também precisa
ter conectividade e encaminhamento IPv6 funcionando. A rede do Traefik pode
continuar em IPv4.

Se a rede `openjobs_openjobs` já foi criada sem IPv6, recrie-a após atualizar
o Compose:

```bash
docker compose down
docker compose up -d --build
```

Execute `down` sem `-v` para preservar o volume do RabbitMQ. Nas próximas
atualizações, continue usando apenas `docker compose up -d --build`.

## Fluxo de deploy

Para atualizar a API, execute na raiz do projeto:

```bash
docker compose up -d --build
```

O Compose recria o container quando a imagem muda. Pode haver uma breve
indisponibilidade enquanto a API inicia. O Traefik acompanha as labels do container
e encaminha as requisições quando ele estiver saudável.
