# 🚀 Guia de Deploy para Produção

## 📋 Pré-requisitos

- Docker 20.10+
- Docker Compose 2.0+
- 2GB RAM disponível
- Portas 8080 e 5432 livres

---

## 🔧 Configuração Inicial

### 1. Clonar Repositório
```bash
git clone https://github.com/Danilo019/desafio-backend-sea.git
cd desafio-backend-sea
```

### 2. Configurar Variáveis de Ambiente
```bash
# Copiar template
cp .env.example .env

# Editar .env com suas configurações
nano .env
```

**Configurações importantes no `.env`:**
```env
DATABASE_NAME=desafio_backend_prod
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=SuaSenhaSegura123!
DATABASE_PORT=5432
BACKEND_PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

⚠️ **IMPORTANTE**: Altere `DATABASE_PASSWORD` para uma senha forte em produção!

---

## 🐳 Deploy com Docker Compose

### Deploy Completo (Backend + PostgreSQL)

```bash
# Build e iniciar todos os serviços
docker-compose up -d --build

# Verificar logs
docker-compose logs -f

# Verificar status dos containers
docker-compose ps
```

### Comandos Úteis

```bash
# Parar todos os serviços
docker-compose down

# Parar e remover volumes (CUIDADO: apaga dados)
docker-compose down -v

# Reiniciar apenas o backend
docker-compose restart backend

# Ver logs do backend
docker-compose logs -f backend

# Ver logs do PostgreSQL
docker-compose logs -f postgres

# Executar comando no container
docker-compose exec backend sh

# Acessar PostgreSQL
docker-compose exec postgres psql -U postgres -d desafio_backend_prod
```

---

## 🔍 Verificação de Saúde

### Health Check Endpoints

```bash
# Health check geral
curl http://localhost:8080/actuator/health

# Resposta esperada:
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"}
  }
}

# Info da aplicação
curl http://localhost:8080/actuator/info
```

### Swagger UI
Acesse: http://localhost:8080/swagger-ui/index.html

---

## 🗄️ Migração de Dados

### Backup do Banco

```bash
# Backup completo
docker-compose exec postgres pg_dump -U postgres desafio_backend_prod > backup_$(date +%Y%m%d).sql

# Restaurar backup
docker-compose exec -T postgres psql -U postgres desafio_backend_prod < backup_20260215.sql
```

### Migrations com Flyway (Opcional)

Para gerenciar migrations de forma profissional, adicione Flyway ao `pom.xml`:

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

E crie migrations em `src/main/resources/db/migration/`:
```
V1__create_initial_schema.sql
V2__add_indexes.sql
```

---

## 🔐 Segurança em Produção

### 1. Habilitar Spring Security

Remover do `application-prod.properties`:
```properties
# Comentar ou remover esta linha:
# spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```

### 2. Configurar HTTPS (Nginx Reverse Proxy)

Criar `nginx.conf`:
```nginx
server {
    listen 80;
    server_name seu-dominio.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name seu-dominio.com;

    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;

    location / {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Adicionar ao `docker-compose.yml`:
```yaml
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/conf.d/default.conf
      - ./ssl:/etc/nginx/ssl
    depends_on:
      - backend
```

### 3. Firewall

```bash
# Permitir apenas portas necessárias
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable

# NÃO expor diretamente portas 8080 e 5432 em produção
```

---

## 📊 Monitoramento

### Logs Centralizados

```bash
# Ver logs em tempo real
docker-compose logs -f --tail=100

# Exportar logs para arquivo
docker-compose logs > logs/app_$(date +%Y%m%d_%H%M%S).log
```

### Prometheus + Grafana (Opcional)

Adicionar ao `pom.xml`:
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

Endpoint de métricas: http://localhost:8080/actuator/prometheus

---

## 🔄 CI/CD - GitHub Actions

Criar `.github/workflows/deploy.yml`:

```yaml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 8
      uses: actions/setup-java@v3
      with:
        java-version: '8'
        distribution: 'temurin'
    
    - name: Run tests
      run: cd backend && mvn clean test
    
    - name: Build and push Docker image
      run: |
        docker build -t seu-registry/desafio-backend:latest ./backend
        docker push seu-registry/desafio-backend:latest
    
    - name: Deploy to server
      uses: appleboy/ssh-action@master
      with:
        host: ${{ secrets.SERVER_HOST }}
        username: ${{ secrets.SERVER_USER }}
        key: ${{ secrets.SSH_PRIVATE_KEY }}
        script: |
          cd /app/desafio-backend-sea
          docker-compose pull
          docker-compose up -d
```

---

## 🚨 Troubleshooting

### Backend não inicia

```bash
# Verificar logs
docker-compose logs backend

# Problemas comuns:
# 1. PostgreSQL não está pronto
# Solução: Aguardar health check (40s)

# 2. Erro de conexão com banco
# Verificar: DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD no .env

# 3. Porta 8080 já em uso
# Solução: Alterar BACKEND_PORT no .env
```

### Erro de memória

```bash
# Aumentar memória no docker-compose.yml
environment:
  JAVA_OPTS: "-Xmx1024m -Xms512m"  # 1GB heap
```

### PostgreSQL não inicia

```bash
# Verificar permissões do volume
docker-compose down -v
docker volume rm desafio-backend-sea_postgres_data
docker-compose up -d
```

---

## 📈 Performance Tuning

### Otimizar PostgreSQL

Editar `docker-compose.yml`:
```yaml
  postgres:
    command:
      - "postgres"
      - "-c"
      - "max_connections=200"
      - "-c"
      - "shared_buffers=256MB"
      - "-c"
      - "effective_cache_size=1GB"
```

### Pool de Conexões (HikariCP)

Adicionar ao `application-prod.properties`:
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

---

## 🧪 Testes em Produção

```bash
# Executar testes antes do deploy
cd backend
mvn clean test

# Testes de carga com Apache Bench
ab -n 1000 -c 10 http://localhost:8080/actuator/health

# Testes de API com curl
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Teste",
    "cpf": "123.456.789-00",
    "endereco": {...},
    "telefones": [...],
    "emails": [...]
  }'
```

---

## 📞 Suporte

- **Documentação**: [README.md](../README.md)
- **Swagger**: http://localhost:8080/swagger-ui/index.html
- **GitHub Issues**: https://github.com/Danilo019/desafio-backend-sea/issues

---

## ✅ Checklist de Produção

- [ ] Variáveis de ambiente configuradas (`.env`)
- [ ] Senha forte do PostgreSQL
- [ ] Spring Security habilitado
- [ ] HTTPS configurado (Nginx)
- [ ] Firewall configurado
- [ ] Backup automatizado
- [ ] Monitoramento ativo
- [ ] Health checks funcionando
- [ ] Logs centralizados
- [ ] Testes passando (181/181)
- [ ] Documentação atualizada

---

**Última atualização:** 15 de Fevereiro de 2026  
**Versão:** 1.0

**Desenvolvido por** Danilo019
