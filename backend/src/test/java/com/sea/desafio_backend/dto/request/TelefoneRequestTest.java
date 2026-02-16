package com.sea.desafio_backend.dto.request;

import com.sea.desafio_backend.model.enums.TipoTelefone;
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
 * Testes de validação Bean Validation para TelefoneRequest
 */
@DisplayName("TelefoneRequest - Testes de Validação")
class TelefoneRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== TESTES POSITIVOS ====================

    @Test
    @DisplayName("Telefone válido com máscara não deve ter violações")
    void telefoneValidoComMascara_NaoDeveTerViolacoes() {
        // Arrange
        TelefoneRequest request = new TelefoneRequest();
        request.setNumero("(11) 98765-4321");
        request.setTipo(TipoTelefone.CELULAR);
        request.setPrincipal(true);

        // Act
        Set<ConstraintViolation<TelefoneRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Telefone válido sem máscara não deve ter violações")
    void telefoneValidoSemMascara_NaoDeveTerViolacoes() {
        // Arrange
        TelefoneRequest request = new TelefoneRequest();
        request.setNumero("11987654321");
        request.setTipo(TipoTelefone.CELULAR);

        // Act
        Set<ConstraintViolation<TelefoneRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Telefone fixo válido não deve ter violações")
    void telefoneFixoValido_NaoDeveTerViolacoes() {
        // Arrange
        TelefoneRequest request = new TelefoneRequest();
        request.setNumero("(11) 3333-4444");
        request.setTipo(TipoTelefone.RESIDENCIAL);

        // Act
        Set<ConstraintViolation<TelefoneRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    // ==================== TESTES NÚMERO ====================

    @Test
    @DisplayName("Número vazio deve gerar violação")
    void numeroVazio_DeveGerarViolacao() {
        // Arrange
        TelefoneRequest request = new TelefoneRequest();
        request.setNumero("");
        request.setTipo(TipoTelefone.CELULAR);

        // Act
        Set<ConstraintViolation<TelefoneRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("numero")));
    }

    @Test
    @DisplayName("Número null deve gerar violação")
    void numeroNull_DeveGerarViolacao() {
        // Arrange
        TelefoneRequest request = new TelefoneRequest();
        request.setNumero(null);
        request.setTipo(TipoTelefone.CELULAR);

        // Act
        Set<ConstraintViolation<TelefoneRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("numero")));
    }

    @Test
    @DisplayName("Número com formato inválido deve gerar violação")
    void numeroFormatoInvalido_DeveGerarViolacao() {
        // Arrange
        TelefoneRequest request = new TelefoneRequest();
        request.setNumero("123456"); // Muito curto
        request.setTipo(TipoTelefone.CELULAR);

        // Act
        Set<ConstraintViolation<TelefoneRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("numero") &&
                        v.getMessage().contains("telefone inválido")));
    }

    @Test
    @DisplayName("Número sem DDD deve gerar violação")
    void numeroSemDDD_DeveGerarViolacao() {
        // Arrange
        TelefoneRequest request = new TelefoneRequest();
        request.setNumero("987654321"); // Sem DDD
        request.setTipo(TipoTelefone.CELULAR);

        // Act
        Set<ConstraintViolation<TelefoneRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("numero")));
    }

    // ==================== TESTES TIPO ====================

    @Test
    @DisplayName("Tipo null deve gerar violação")
    void tipoNull_DeveGerarViolacao() {
        // Arrange
        TelefoneRequest request = new TelefoneRequest();
        request.setNumero("(11) 98765-4321");
        request.setTipo(null);

        // Act
        Set<ConstraintViolation<TelefoneRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("tipo") &&
                        v.getMessage().contains("obrigatório")));
    }

    @Test
    @DisplayName("Principal null deve usar valor padrão false")
    void principalNull_DeveUsarValorPadrao() {
        // Arrange
        TelefoneRequest request = new TelefoneRequest();
        request.setNumero("(11) 98765-4321");
        request.setTipo(TipoTelefone.CELULAR);
        request.setPrincipal(null);

        // Act
        Set<ConstraintViolation<TelefoneRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }
}
