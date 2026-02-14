# STAGE 1: Build
FROM maven:3.8-eclipse-temurin-8 AS builder

WORKDIR /app

# Copiar apenas pom.xml primeiro (cache de dependências)
COPY pom.xml .

# Baixar dependências
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Build da aplicação (skip tests para build mais rápido)
RUN mvn clean package -DskipTests

# STAGE 2: Runtime
FROM eclipse-temurin:8-jre

WORKDIR /app

# Instalar curl para health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copiar JAR da stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Criar usuário não-root por segurança
RUN useradd -r -u 1001 -g root appuser && \
    chown -R appuser:root /app

USER appuser

# Expor porta da aplicação
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Variáveis de ambiente com valores padrão
ENV SPRING_PROFILES_ACTIVE=docker \
    JAVA_OPTS="-Xmx512m -Xms256m"

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
