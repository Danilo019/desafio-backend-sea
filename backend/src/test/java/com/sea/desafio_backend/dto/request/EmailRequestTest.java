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
 * Testes de validação Bean Validation para EmailRequest
 */
@DisplayName("EmailRequest - Testes de Validação")
class EmailRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== TESTES POSITIVOS ====================

    @Test
    @DisplayName("Email válido não deve ter violações")
    void emailValido_NaoDeveTerViolacoes() {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setEnderecoEmail("joao.silva@example.com");
        request.setPrincipal(true);

        // Act
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Email válido com subdomínio não deve ter violações")
    void emailComSubdominio_NaoDeveTerViolacoes() {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setEnderecoEmail("usuario@mail.empresa.com.br");

        // Act
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    // ==================== TESTES ENDEREÇO EMAIL ====================

    @Test
    @DisplayName("Email vazio deve gerar violação")
    void emailVazio_DeveGerarViolacao() {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setEnderecoEmail("");

        // Act
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("enderecoEmail")));
    }

    @Test
    @DisplayName("Email null deve gerar violação")
    void emailNull_DeveGerarViolacao() {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setEnderecoEmail(null);

        // Act
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("enderecoEmail")));
    }

    @Test
    @DisplayName("Email sem @ deve gerar violação")
    void emailSemArroba_DeveGerarViolacao() {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setEnderecoEmail("joaosilva.example.com");

        // Act
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("enderecoEmail") &&
                        v.getMessage().contains("inválido")));
    }

    @Test
    @DisplayName("Email sem domínio deve gerar violação")
    void emailSemDominio_DeveGerarViolacao() {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setEnderecoEmail("joao@");

        // Act
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("enderecoEmail")));
    }

    @Test
    @DisplayName("Email sem usuário deve gerar violação")
    void emailSemUsuario_DeveGerarViolacao() {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setEnderecoEmail("@example.com");

        // Act
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("enderecoEmail")));
    }

    @Test
    @DisplayName("Email com espaços deve gerar violação")
    void emailComEspacos_DeveGerarViolacao() {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setEnderecoEmail("joao silva@example.com");

        // Act
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("enderecoEmail")));
    }

    @Test
    @DisplayName("Principal null não deve gerar violação")
    void principalNull_NaoDeveGerarViolacao() {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setEnderecoEmail("teste@example.com");
        request.setPrincipal(null);

        // Act
        Set<ConstraintViolation<EmailRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }
}
