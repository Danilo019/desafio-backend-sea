package com.sea.desafio_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Configuração do Swagger/OpenAPI
 * 
 * Gera documentação interativa da API REST em:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 * 
 * @author Danilo
 * @version 1.0.0
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configuração customizada do OpenAPI
     * Define informações gerais da API, contato e servidores disponíveis
     * 
     * @return OpenAPI configurado
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Desafio Backend SEA - API REST")
                        .version("1.0.0")
                        .description(
                            "API RESTful para gestão completa de clientes, incluindo:\n\n" +
                            "- **Cadastro de clientes** com validação de CPF\n" +
                            "- **Gerenciamento de endereços** com integração ViaCEP\n" +
                            "- **Múltiplos telefones** por cliente (celular, fixo, comercial)\n" +
                            "- **Múltiplos emails** por cliente\n" +
                            "- **Persistência em PostgreSQL** via Docker\n" +
                            "- **Validações robustas** com Bean Validation\n" +
                            "- **Tratamento global de exceções**\n\n" +
                            "**Tecnologias:** Spring Boot 2.7.18, PostgreSQL 14, Docker, Maven"
                        )
                        .contact(new Contact()
                                .name("Danilo - Desenvolvedor Backend")
                                .email("danilo.silva018@gmail.com")
                                .url("https://github.com/Danilo019/desafio-backend-sea")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                )
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor Local - Docker (PostgreSQL)"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Ambiente de Desenvolvimento")
                ));
    }
}
