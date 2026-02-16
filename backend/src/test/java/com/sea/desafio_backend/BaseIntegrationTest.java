package com.sea.desafio_backend;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Classe base para testes de integração
 * Carrega contexto Spring completo
 * 
 * Usar para testes que precisam de:
 * - Repositórios reais
 * - Banco H2 em memória
 * - Contexto Spring completo
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    // Configurações comuns aqui (se necessário)
}
