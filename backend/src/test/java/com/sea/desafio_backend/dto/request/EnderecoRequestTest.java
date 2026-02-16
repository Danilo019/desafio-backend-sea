package com.sea.desafio_backend.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de validação Bean Validation para EnderecoRequest
 */
@DisplayName("EnderecoRequest - Testes de Validação")
class EnderecoRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private EnderecoRequest criarEnderecoValido() {
        EnderecoRequest request = new EnderecoRequest();
        request.setCep("01001-000");
        request.setLogradouro("Praça da Sé");
        request.setComplemento("lado ímpar");
        request.setBairro("Sé");
        request.setCidade("São Paulo");
        request.setUf("SP");
        return request;
    }

    // ==================== TESTES POSITIVOS ====================

    @Test
    @DisplayName("Endereço válido completo não deve ter violações")
    void enderecoValidoCompleto_NaoDeveTerViolacoes() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Endereço válido sem complemento não deve ter violações")
    void enderecoSemComplemento_NaoDeveTerViolacoes() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setComplemento(null);

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("CEP sem hífen deve ser válido")
    void cepSemHifen_DeveSerValido() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setCep("01001000");

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    // ==================== TESTES CEP ====================

    @Test
    @DisplayName("CEP vazio deve gerar violação")
    void cepVazio_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setCep("");

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cep")));
    }

    @Test
    @DisplayName("CEP null deve gerar violação")
    void cepNull_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setCep(null);

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cep")));
    }

    @Test
    @DisplayName("CEP com formato inválido deve gerar violação")
    void cepFormatoInvalido_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setCep("0100100"); // Apenas 7 dígitos

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cep") &&
                        v.getMessage().contains("CEP inválido")));
    }

    // ==================== TESTES LOGRADOURO ====================

    @Test
    @DisplayName("Logradouro vazio deve gerar violação")
    void logradouroVazio_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setLogradouro("");

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("logradouro")));
    }

    @Test
    @DisplayName("Logradouro null deve gerar violação")
    void logradouroNull_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setLogradouro(null);

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("logradouro")));
    }

    @Test
    @DisplayName("Logradouro muito longo deve gerar violação")
    void logradouroMuitoLongo_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 201; i++) sb.append("A");
        request.setLogradouro(sb.toString()); // Mais de 200

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("logradouro")));
    }

    // ==================== TESTES BAIRRO ====================

    @Test
    @DisplayName("Bairro vazio deve gerar violação")
    void bairroVazio_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setBairro("");

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("bairro")));
    }

    @Test
    @DisplayName("Bairro null deve gerar violação")
    void bairroNull_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setBairro(null);

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("bairro")));
    }

    // ==================== TESTES CIDADE ====================

    @Test
    @DisplayName("Cidade vazia deve gerar violação")
    void cidadeVazia_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setCidade("");

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cidade")));
    }

    @Test
    @DisplayName("Cidade null deve gerar violação")
    void cidadeNull_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setCidade(null);

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cidade")));
    }

    // ==================== TESTES UF ====================

    @Test
    @DisplayName("UF vazia deve gerar violação")
    void ufVazia_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setUf("");

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("uf")));
    }

    @Test
    @DisplayName("UF null deve gerar violação")
    void ufNull_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setUf(null);

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("uf")));
    }

    @Test
    @DisplayName("UF com minúsculas deve gerar violação")
    void ufMinusculas_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setUf("sp");

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("uf") &&
                        v.getMessage().contains("maiúsculas")));
    }

    @Test
    @DisplayName("UF com 1 caractere deve gerar violação")
    void ufUmCaractere_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setUf("S");

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("uf")));
    }

    @Test
    @DisplayName("UF com 3 caracteres deve gerar violação")
    void ufTresCaracteres_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        request.setUf("SPP");

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("uf")));
    }

    // ==================== TESTES COMPLEMENTO ====================

    @Test
    @DisplayName("Complemento muito longo deve gerar violação")
    void complementoMuitoLongo_DeveGerarViolacao() {
        // Arrange
        EnderecoRequest request = criarEnderecoValido();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) sb.append("A");
        request.setComplemento(sb.toString()); // Mais de 100

        // Act
        Set<ConstraintViolation<EnderecoRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("complemento")));
    }
}
