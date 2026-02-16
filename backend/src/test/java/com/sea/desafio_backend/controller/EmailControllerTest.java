package com.sea.desafio_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sea.desafio_backend.dto.request.EmailRequest;
import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.model.entity.ClienteEmail;
import com.sea.desafio_backend.service.ClienteService;
import com.sea.desafio_backend.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para EmailController
 */
@WebMvcTest(EmailController.class)
@DisplayName("EmailController - Testes de Integração")
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @MockBean
    private ClienteService clienteService;

    // ==================== TESTES POST /api/clientes/{clienteId}/emails ====================

    @Test
    @DisplayName("POST /api/clientes/{clienteId}/emails - Adicionar email com sucesso deve retornar 201")
    void adicionarEmail_ComDadosValidos_DeveRetornar201() throws Exception {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");

        EmailRequest request = new EmailRequest();
        request.setEnderecoEmail("joao.silva@example.com");
        request.setPrincipal(true);

        ClienteEmail email = new ClienteEmail();
        email.setId(10L);
        email.setEnderecoEmail("joao.silva@example.com");
        email.setPrincipal(true);
        email.setCliente(cliente);

        when(clienteService.buscarPorId(1L)).thenReturn(cliente);
        when(emailService.criarEmail(any(ClienteEmail.class))).thenReturn(email);

        // Act & Assert
        mockMvc.perform(post("/api/clientes/1/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.enderecoEmail").value("joao.silva@example.com"))
                .andExpect(jsonPath("$.principal").value(true));

        verify(clienteService).buscarPorId(1L);
        verify(emailService).criarEmail(any(ClienteEmail.class));
    }

    @Test
    @DisplayName("POST /api/clientes/{clienteId}/emails - Cliente inexistente deve retornar 404")
    void adicionarEmail_ClienteInexistente_DeveRetornar404() throws Exception {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setEnderecoEmail("teste@example.com");

        when(clienteService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Cliente", 99L));

        // Act & Assert
        mockMvc.perform(post("/api/clientes/99/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(emailService, never()).criarEmail(any());
    }

    @Test
    @DisplayName("POST /api/clientes/{clienteId}/emails - Dados inválidos deve retornar 400")
    void adicionarEmail_DadosInvalidos_DeveRetornar400() throws Exception {
        // Arrange - Request sem enderecoEmail (campo obrigatório)
        EmailRequest request = new EmailRequest();
        request.setPrincipal(true);
        // enderecoEmail é null

        // Act & Assert
        mockMvc.perform(post("/api/clientes/1/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(clienteService, never()).buscarPorId(anyLong());
        verify(emailService, never()).criarEmail(any());
    }

    // ==================== TESTES GET /api/emails/{id} ====================

    @Test
    @DisplayName("GET /api/emails/{id} - Buscar existente deve retornar 200")
    void buscarPorId_EmailExistente_DeveRetornar200() throws Exception {
        // Arrange
        ClienteEmail email = new ClienteEmail();
        email.setId(1L);
        email.setEnderecoEmail("teste@example.com");
        email.setPrincipal(true);

        when(emailService.buscarPorId(1L)).thenReturn(email);

        // Act & Assert
        mockMvc.perform(get("/api/emails/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.enderecoEmail").value("teste@example.com"))
                .andExpect(jsonPath("$.principal").value(true));

        verify(emailService).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/emails/{id} - Buscar inexistente deve retornar 404")
    void buscarPorId_EmailInexistente_DeveRetornar404() throws Exception {
        // Arrange
        when(emailService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("ClienteEmail", 99L));

        // Act & Assert
        mockMvc.perform(get("/api/emails/99"))
                .andExpect(status().isNotFound());
    }

    // ==================== TESTES PUT /api/emails/{id} ====================

    @Test
    @DisplayName("PUT /api/emails/{id} - Atualizar com dados válidos deve retornar 200")
    void atualizarEmail_ComDadosValidos_DeveRetornar200() throws Exception {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setEnderecoEmail("novo.email@example.com");
        request.setPrincipal(false);

        ClienteEmail emailAtualizado = new ClienteEmail();
        emailAtualizado.setId(1L);
        emailAtualizado.setEnderecoEmail("novo.email@example.com");
        emailAtualizado.setPrincipal(false);

        when(emailService.atualizarEmail(anyLong(), any(ClienteEmail.class)))
                .thenReturn(emailAtualizado);

        // Act & Assert
        mockMvc.perform(put("/api/emails/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enderecoEmail").value("novo.email@example.com"))
                .andExpect(jsonPath("$.principal").value(false));

        verify(emailService).atualizarEmail(anyLong(), any(ClienteEmail.class));
    }

    @Test
    @DisplayName("PUT /api/emails/{id} - Atualizar inexistente deve retornar 404")
    void atualizarEmail_EmailInexistente_DeveRetornar404() throws Exception {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setEnderecoEmail("teste@example.com");

        when(emailService.atualizarEmail(anyLong(), any(ClienteEmail.class)))
                .thenThrow(new ResourceNotFoundException("ClienteEmail", 99L));

        // Act & Assert
        mockMvc.perform(put("/api/emails/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ==================== TESTES DELETE /api/emails/{id} ====================

    @Test
    @DisplayName("DELETE /api/emails/{id} - Remover existente deve retornar 204")
    void removerEmail_EmailExistente_DeveRetornar204() throws Exception {
        // Arrange
        doNothing().when(emailService).deletarEmail(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/emails/1"))
                .andExpect(status().isNoContent());

        verify(emailService).deletarEmail(1L);
    }

    @Test
    @DisplayName("DELETE /api/emails/{id} - Remover último email deve retornar 400")
    void removerEmail_UltimoEmail_DeveRetornar400() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Cliente deve ter pelo menos um email"))
                .when(emailService).deletarEmail(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/emails/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/emails/{id} - Remover inexistente deve retornar 404")
    void removerEmail_EmailInexistente_DeveRetornar404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("ClienteEmail", 99L))
                .when(emailService).deletarEmail(99L);

        // Act & Assert
        mockMvc.perform(delete("/api/emails/99"))
                .andExpect(status().isNotFound());
    }

    // ==================== TESTES PUT /api/emails/{id}/principal ====================

    @Test
    @DisplayName("PUT /api/emails/{id}/principal - Marcar como principal deve retornar 200")
    void marcarComoPrincipal_DeveRetornar200() throws Exception {
        // Arrange
        doNothing().when(emailService).definirComoPrincipal(1L);

        // Act & Assert
        mockMvc.perform(put("/api/emails/1/principal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email marcado como principal com sucesso"));

        verify(emailService).definirComoPrincipal(1L);
    }

    @Test
    @DisplayName("PUT /api/emails/{id}/principal - Email inexistente deve retornar 404")
    void marcarComoPrincipal_EmailInexistente_DeveRetornar404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("ClienteEmail", 99L))
                .when(emailService).definirComoPrincipal(99L);

        // Act & Assert
        mockMvc.perform(put("/api/emails/99/principal"))
                .andExpect(status().isNotFound());
    }
}
