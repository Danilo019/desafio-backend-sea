package com.sea.desafio_backend.dto.request;

import com.sea.desafio_backend.model.enums.TipoTelefone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de validação Bean Validation para ClienteRequest
 */
@DisplayName("ClienteRequest - Testes de Validação")
class ClienteRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private ClienteRequest criarClienteValido() {
        ClienteRequest request = new ClienteRequest();
        request.setNome("João da Silva");
        request.setCpf("123.456.789-09");  // CPF válido com dígitos verificadores corretos
        
        EnderecoRequest endereco = new EnderecoRequest();
        endereco.setCep("01001-000");
        endereco.setLogradouro("Praça da Sé");
        endereco.setBairro("Sé");
        endereco.setCidade("São Paulo");
        endereco.setUf("SP");
        request.setEndereco(endereco);
        
        TelefoneRequest telefone = new TelefoneRequest();
        telefone.setNumero("(11) 98765-4321");
        telefone.setTipo(TipoTelefone.CELULAR);
        telefone.setPrincipal(true);
        
        List<TelefoneRequest> telefones = new ArrayList<>();
        telefones.add(telefone);
        request.setTelefones(telefones);
        
        EmailRequest email = new EmailRequest();
        email.setEnderecoEmail("joao@example.com");
        email.setPrincipal(true);
        
        List<EmailRequest> emails = new ArrayList<>();
        emails.add(email);
        request.setEmails(emails);
        
        return request;
    }

    // ==================== TESTES POSITIVOS ====================

    @Test
    @DisplayName("Cliente válido completo não deve ter violações")
    void clienteValido_NaoDeveTerViolacoes() {
        // Arrange
        ClienteRequest request = criarClienteValido();

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty(), "Cliente válido não deve ter violações");
    }

    // ==================== TESTES NOME ====================

    @Test
    @DisplayName("Nome vazio deve gerar violação")
    void nomeVazio_DeveGerarViolacao() {
        // Arrange
        ClienteRequest request = criarClienteValido();
        request.setNome("");

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Nome null deve gerar violação")
    void nomeNull_DeveGerarViolacao() {
        // Arrange
        ClienteRequest request = criarClienteValido();
        request.setNome(null);

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Nome muito curto deve gerar violação")
    void nomeMuitoCurto_DeveGerarViolacao() {
        // Arrange
        ClienteRequest request = criarClienteValido();
        request.setNome("Ab"); // Menos de 3 caracteres

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nome") &&
                        v.getMessage().contains("entre 3 e 100")));
    }

    @Test
    @DisplayName("Nome muito longo deve gerar violação")
    void nomeMuitoLongo_DeveGerarViolacao() {
        // Arrange
        ClienteRequest request = criarClienteValido();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) sb.append("A");
        request.setNome(sb.toString()); // Mais de 100 caracteres

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Nome com caracteres especiais inválidos deve gerar violação")
    void nomeComCaracteresEspeciais_DeveGerarViolacao() {
        // Arrange
        ClienteRequest request = criarClienteValido();
        request.setNome("João@Silva#");

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nome") &&
                        v.getMessage().contains("apenas letras")));
    }

    // ==================== TESTES CPF ====================

    @Test
    @DisplayName("CPF vazio deve gerar violação")
    void cpfVazio_DeveGerarViolacao() {
        // Arrange
        ClienteRequest request = criarClienteValido();
        request.setCpf("");

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    @Test
    @DisplayName("CPF null deve gerar violação")
    void cpfNull_DeveGerarViolacao() {
        // Arrange
        ClienteRequest request = criarClienteValido();
        request.setCpf(null);

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    @Test
    @DisplayName("CPF sem máscara mas válido não deve gerar violação")
    void cpfSemMascara_MasValido_NaoDeveGerarViolacao() {
        // Arrange
        ClienteRequest request = criarClienteValido();
        request.setCpf("12345678909");  // CPF válido sem máscara

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("cpf")),
                "CPF válido sem máscara não deve gerar violação");
    }

    @Test
    @DisplayName("CPF com formato inválido deve gerar violação")
    void cpfFormatoInvalido_DeveGerarViolacao() {
        // Arrange
        ClienteRequest request = criarClienteValido();
        request.setCpf("123.456.789-0"); // Faltando último dígito

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    // ==================== TESTES TELEFONES ====================

    @Test
    @DisplayName("Lista de telefones vazia deve gerar violação")
    void telefonesVazio_DeveGerarViolacao() {
        // Arrange
        ClienteRequest request = criarClienteValido();
        request.setTelefones(new ArrayList<>());

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("telefones") &&
                        v.getMessage().contains("pelo menos um telefone")));
    }

    @Test
    @DisplayName("Lista de telefones null deve gerar violação")
    void telefonesNull_DeveGerarViolacao() {
        // Arrange
        ClienteRequest request = criarClienteValido();
        request.setTelefones(null);

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("telefones")));
    }

    // ==================== TESTES EMAILS ====================

    @Test
    @DisplayName("Lista de emails vazia deve gerar violação")
    void emailsVazio_DeveGerarViolacao() {
        // Arrange
        ClienteRequest request = criarClienteValido();
        request.setEmails(new ArrayList<>());

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("emails") &&
                        v.getMessage().contains("pelo menos um email")));
    }

    @Test
    @DisplayName("Lista de emails null deve gerar violação")
    void emailsNull_DeveGerarViolacao() {
        // Arrange
        ClienteRequest request = criarClienteValido();
        request.setEmails(null);

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("emails")));
    }

    @Test
    @DisplayName("Endereço null deve gerar violação")
    void enderecoNull_DeveGerarViolacao() {
        // Arrange
        ClienteRequest request = criarClienteValido();
        request.setEndereco(null);

        // Act
        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);

        // Assert  
        assertFalse(violations.isEmpty());
    }
}
