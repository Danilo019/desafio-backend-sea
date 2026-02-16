package com.sea.desafio_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sea.desafio_backend.dto.request.TelefoneRequest;
import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.model.entity.Telefone;
import com.sea.desafio_backend.model.enums.TipoTelefone;
import com.sea.desafio_backend.service.ClienteService;
import com.sea.desafio_backend.service.TelefoneService;
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
 * Testes de integração para TelefoneController
 */
@WebMvcTest(TelefoneController.class)
@DisplayName("TelefoneController - Testes de Integração")
class TelefoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TelefoneService telefoneService;

    @MockBean
    private ClienteService clienteService;

    // ==================== TESTES POST /api/clientes/{clienteId}/telefones ====================

    @Test
    @DisplayName("POST /api/clientes/{clienteId}/telefones - Adicionar telefone com sucesso deve retornar 201")
    void adicionarTelefone_ComDadosValidos_DeveRetornar201() throws Exception {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");

        TelefoneRequest request = new TelefoneRequest();
        request.setNumero("(11) 98765-4321");
        request.setTipo(TipoTelefone.CELULAR);
        request.setPrincipal(true);

        Telefone telefone = new Telefone();
        telefone.setId(10L);
        telefone.setNumero("11987654321");
        telefone.setTipo(TipoTelefone.CELULAR);
        telefone.setPrincipal(true);
        telefone.setCliente(cliente);

        when(clienteService.buscarPorId(1L)).thenReturn(cliente);
        when(telefoneService.criarTelefone(any(Telefone.class))).thenReturn(telefone);

        // Act & Assert
        mockMvc.perform(post("/api/clientes/1/telefones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.numero").value("11987654321"))
                .andExpect(jsonPath("$.tipo").value("CELULAR"))
                .andExpect(jsonPath("$.principal").value(true));

        verify(clienteService).buscarPorId(1L);
        verify(telefoneService).criarTelefone(any(Telefone.class));
    }

    @Test
    @DisplayName("POST /api/clientes/{clienteId}/telefones - Cliente inexistente deve retornar 404")
    void adicionarTelefone_ClienteInexistente_DeveRetornar404() throws Exception {
        // Arrange
        TelefoneRequest request = new TelefoneRequest();
        request.setNumero("(11) 98765-4321");
        request.setTipo(TipoTelefone.CELULAR);

        when(clienteService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Cliente", 99L));

        // Act & Assert
        mockMvc.perform(post("/api/clientes/99/telefones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(telefoneService, never()).criarTelefone(any());
    }

    @Test
    @DisplayName("POST /api/clientes/{clienteId}/telefones - Dados inválidos deve retornar 400")
    void adicionarTelefone_DadosInvalidos_DeveRetornar400() throws Exception {
        // Arrange - Request sem número (campo obrigatório)
        TelefoneRequest request = new TelefoneRequest();
        request.setTipo(TipoTelefone.CELULAR);
        // Número é null

        // Act & Assert
        mockMvc.perform(post("/api/clientes/1/telefones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(clienteService, never()).buscarPorId(anyLong());
        verify(telefoneService, never()).criarTelefone(any());
    }

    // ==================== TESTES GET /api/telefones/{id} ====================

    @Test
    @DisplayName("GET /api/telefones/{id} - Buscar existente deve retornar 200")
    void buscarPorId_TelefoneExistente_DeveRetornar200() throws Exception {
        // Arrange
        Telefone telefone = new Telefone();
        telefone.setId(1L);
        telefone.setNumero("11987654321");
        telefone.setTipo(TipoTelefone.CELULAR);

        when(telefoneService.buscarPorId(1L)).thenReturn(telefone);

        // Act & Assert
        mockMvc.perform(get("/api/telefones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numero").value("11987654321"))
                .andExpect(jsonPath("$.tipo").value("CELULAR"));

        verify(telefoneService).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/telefones/{id} - Buscar inexistente deve retornar 404")
    void buscarPorId_TelefoneInexistente_DeveRetornar404() throws Exception {
        // Arrange
        when(telefoneService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Telefone", 99L));

        // Act & Assert
        mockMvc.perform(get("/api/telefones/99"))
                .andExpect(status().isNotFound());
    }

    // ==================== TESTES PUT /api/telefones/{id} ====================

    @Test
    @DisplayName("PUT /api/telefones/{id} - Atualizar com dados válidos deve retornar 200")
    void atualizarTelefone_ComDadosValidos_DeveRetornar200() throws Exception {
        // Arrange
        TelefoneRequest request = new TelefoneRequest();
        request.setNumero("(11) 91234-5678");
        request.setTipo(TipoTelefone.COMERCIAL);
        request.setPrincipal(false);

        Telefone telefoneAtualizado = new Telefone();
        telefoneAtualizado.setId(1L);
        telefoneAtualizado.setNumero("11912345678");
        telefoneAtualizado.setTipo(TipoTelefone.COMERCIAL);
        telefoneAtualizado.setPrincipal(false);

        when(telefoneService.atualizarTelefone(anyLong(), any(Telefone.class)))
                .thenReturn(telefoneAtualizado);

        // Act & Assert
        mockMvc.perform(put("/api/telefones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("11912345678"))
                .andExpect(jsonPath("$.tipo").value("COMERCIAL"));

        verify(telefoneService).atualizarTelefone(anyLong(), any(Telefone.class));
    }

    @Test
    @DisplayName("PUT /api/telefones/{id} - Atualizar inexistente deve retornar 404")
    void atualizarTelefone_TelefoneInexistente_DeveRetornar404() throws Exception {
        // Arrange
        TelefoneRequest request = new TelefoneRequest();
        request.setNumero("(11) 91234-5678");
        request.setTipo(TipoTelefone.CELULAR);

        when(telefoneService.atualizarTelefone(anyLong(), any(Telefone.class)))
                .thenThrow(new ResourceNotFoundException("Telefone", 99L));

        // Act & Assert
        mockMvc.perform(put("/api/telefones/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ==================== TESTES DELETE /api/telefones/{id} ====================

    @Test
    @DisplayName("DELETE /api/telefones/{id} - Remover existente deve retornar 204")
    void removerTelefone_TelefoneExistente_DeveRetornar204() throws Exception {
        // Arrange
        doNothing().when(telefoneService).deletarTelefone(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/telefones/1"))
                .andExpect(status().isNoContent());

        verify(telefoneService).deletarTelefone(1L);
    }

    @Test
    @DisplayName("DELETE /api/telefones/{id} - Remover último telefone deve retornar 400")
    void removerTelefone_UltimoTelefone_DeveRetornar400() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Cliente deve ter pelo menos um telefone"))
                .when(telefoneService).deletarTelefone(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/telefones/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/telefones/{id} - Remover inexistente deve retornar 404")
    void removerTelefone_TelefoneInexistente_DeveRetornar404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Telefone", 99L))
                .when(telefoneService).deletarTelefone(99L);

        // Act & Assert
        mockMvc.perform(delete("/api/telefones/99"))
                .andExpect(status().isNotFound());
    }

    // ==================== TESTES PUT /api/telefones/{id}/principal ====================

    @Test
    @DisplayName("PUT /api/telefones/{id}/principal - Marcar como principal deve retornar 200")
    void marcarComoPrincipal_DeveRetornar200() throws Exception {
        // Arrange
        doNothing().when(telefoneService).definirComoPrincipal(1L);

        // Act & Assert
        mockMvc.perform(put("/api/telefones/1/principal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Telefone marcado como principal com sucesso"));

        verify(telefoneService).definirComoPrincipal(1L);
    }

    @Test
    @DisplayName("PUT /api/telefones/{id}/principal - Telefone inexistente deve retornar 404")
    void marcarComoPrincipal_TelefoneInexistente_DeveRetornar404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Telefone", 99L))
                .when(telefoneService).definirComoPrincipal(99L);

        // Act & Assert
        mockMvc.perform(put("/api/telefones/99/principal"))
                .andExpect(status().isNotFound());
    }
}
