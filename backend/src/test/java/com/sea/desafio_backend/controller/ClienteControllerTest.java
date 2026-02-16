package com.sea.desafio_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sea.desafio_backend.dto.request.ClienteRequest;
import com.sea.desafio_backend.dto.request.EmailRequest;
import com.sea.desafio_backend.dto.request.EnderecoRequest;
import com.sea.desafio_backend.dto.request.TelefoneRequest;
import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.model.entity.ClienteEmail;
import com.sea.desafio_backend.model.entity.Endereco;
import com.sea.desafio_backend.model.entity.Telefone;
import com.sea.desafio_backend.model.enums.TipoTelefone;
import com.sea.desafio_backend.service.ClienteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para ClienteController
 * Usa @WebMvcTest para testar a camada REST sem subir o servidor completo
 */
@WebMvcTest(ClienteController.class)
@DisplayName("ClienteController - Testes de Integração")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService clienteService;

    // ==================== TESTES POST /api/clientes ====================

    @Test
    @DisplayName("POST /api/clientes - Criar cliente com sucesso deve retornar 201")
    void criarCliente_ComDadosValidos_DeveRetornar201() throws Exception {
        // Arrange
        ClienteRequest request = criarClienteRequestCompleto();
        Cliente cliente = criarClienteCompleto();

        when(clienteService.criarCliente(any(ClienteRequest.class))).thenReturn(cliente);

        // Act & Assert
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-09")) // CPF formatado válido
                .andExpect(jsonPath("$.endereco").exists())
                .andExpect(jsonPath("$.telefones").isArray())
                .andExpect(jsonPath("$.emails").isArray());

        verify(clienteService).criarCliente(any(ClienteRequest.class));
    }

    @Test
    @DisplayName("POST /api/clientes - Com dados inválidos deve retornar 400")
    void criarCliente_ComDadosInvalidos_DeveRetornar400() throws Exception {
        // Arrange - Request sem nome (campo obrigatório)
        ClienteRequest request = new ClienteRequest();
        request.setCpf("12345678901");
        // Nome é null

        // Act & Assert
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(clienteService, never()).criarCliente(any());
    }

    @Test
    @DisplayName("POST /api/clientes - Com CPF duplicado deve retornar 400")
    void criarCliente_CpfDuplicado_DeveRetornar400() throws Exception {
        // Arrange
        ClienteRequest request = criarClienteRequestCompleto();

        when(clienteService.criarCliente(any(ClienteRequest.class)))
                .thenThrow(new IllegalArgumentException("CPF já cadastrado"));

        // Act & Assert
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==================== TESTES GET /api/clientes ====================

    @Test
    @DisplayName("GET /api/clientes - Listar todos deve retornar 200 com lista")
    void listarTodos_DeveRetornar200ComLista() throws Exception {
        // Arrange
        Cliente cliente1 = criarClienteCompleto();
        Cliente cliente2 = criarClienteCompleto();
        cliente2.setId(2L);
        cliente2.setNome("Maria Santos");

        when(clienteService.listarTodos()).thenReturn(Arrays.asList(cliente1, cliente2));

        // Act & Assert
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome").value("João Silva"))
                .andExpect(jsonPath("$[1].nome").value("Maria Santos"));

        verify(clienteService).listarTodos();
    }

    @Test
    @DisplayName("GET /api/clientes - Lista vazia deve retornar 200 com array vazio")
    void listarTodos_ListaVazia_DeveRetornar200() throws Exception {
        // Arrange
        when(clienteService.listarTodos()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ==================== TESTES GET /api/clientes/{id} ====================

    @Test
    @DisplayName("GET /api/clientes/{id} - Buscar existente deve retornar 200")
    void buscarPorId_ClienteExistente_DeveRetornar200() throws Exception {
        // Arrange
        Cliente cliente = criarClienteCompleto();
        when(clienteService.buscarPorId(1L)).thenReturn(cliente);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-09"));

        verify(clienteService).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/clientes/{id} - Buscar inexistente deve retornar 404")
    void buscarPorId_ClienteInexistente_DeveRetornar404() throws Exception {
        // Arrange
        when(clienteService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Cliente", 99L));

        // Act & Assert
        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound());

        verify(clienteService).buscarPorId(99L);
    }

    // ==================== TESTES GET /api/clientes/cpf/{cpf} ====================

    @Test
    @DisplayName("GET /api/clientes/cpf/{cpf} - Buscar existente deve retornar 200")
    void buscarPorCpf_ClienteExistente_DeveRetornar200() throws Exception {
        // Arrange
        Cliente cliente = criarClienteCompleto();
        when(clienteService.buscarPorCpf("123.456.789-09")).thenReturn(cliente);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/cpf/123.456.789-09"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("123.456.789-09"))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(clienteService).buscarPorCpf("123.456.789-09");
    }

    @Test
    @DisplayName("GET /api/clientes/cpf/{cpf} - Buscar inexistente deve retornar 404")
    void buscarPorCpf_ClienteInexistente_DeveRetornar404() throws Exception {
        // Arrange
        when(clienteService.buscarPorCpf(anyString()))
                .thenThrow(new ResourceNotFoundException("Cliente", "cpf", "99999999999"));

        // Act & Assert
        mockMvc.perform(get("/api/clientes/cpf/99999999999"))
                .andExpect(status().isNotFound());
    }

    // ==================== TESTES DELETE /api/clientes/{id} ====================

    @Test
    @DisplayName("DELETE /api/clientes/{id} - Deletar existente deve retornar 204")
    void deletar_ClienteExistente_DeveRetornar204() throws Exception {
        // Arrange
        doNothing().when(clienteService).deletarCliente(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());

        verify(clienteService).deletarCliente(1L);
    }

    @Test
    @DisplayName("DELETE /api/clientes/{id} - Deletar inexistente deve retornar 404")
    void deletar_ClienteInexistente_DeveRetornar404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Cliente", 99L))
                .when(clienteService).deletarCliente(99L);

        // Act & Assert
        mockMvc.perform(delete("/api/clientes/99"))
                .andExpect(status().isNotFound());
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private ClienteRequest criarClienteRequestCompleto() {
        ClienteRequest request = new ClienteRequest();
        request.setNome("João Silva");
        request.setCpf("123.456.789-09");  // CPF válido com dígitos verificadores corretos

        EnderecoRequest endereco = new EnderecoRequest();
        endereco.setCep("01001000");
        endereco.setLogradouro("Praça da Sé");
        endereco.setBairro("Sé");
        endereco.setCidade("São Paulo");
        endereco.setUf("SP");
        request.setEndereco(endereco);

        TelefoneRequest telefone = new TelefoneRequest();
        telefone.setNumero("11987654321");
        telefone.setTipo(TipoTelefone.CELULAR);
        telefone.setPrincipal(true);
        request.setTelefones(Collections.singletonList(telefone));

        EmailRequest email = new EmailRequest();
        email.setEnderecoEmail("joao@example.com");
        email.setPrincipal(true);
        request.setEmails(Collections.singletonList(email));

        return request;
    }

    private Cliente criarClienteCompleto() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("123.456.789-09");  // CPF válido formatado

        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCep("01001000");
        endereco.setLogradouro("Praça da Sé");
        endereco.setBairro("Sé");
        endereco.setCidade("São Paulo");
        endereco.setUf("SP");
        endereco.setCliente(cliente);
        cliente.setEndereco(endereco);

        Telefone telefone = new Telefone();
        telefone.setId(1L);
        telefone.setNumero("11987654321");
        telefone.setTipo(TipoTelefone.CELULAR);
        telefone.setPrincipal(true);
        telefone.setCliente(cliente);
        cliente.setTelefones(Collections.singletonList(telefone));

        ClienteEmail email = new ClienteEmail();
        email.setId(1L);
        email.setEnderecoEmail("joao@example.com");
        email.setPrincipal(true);
        email.setCliente(cliente);
        cliente.setEmails(Collections.singletonList(email));

        return cliente;
    }
}
