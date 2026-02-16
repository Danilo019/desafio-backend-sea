package com.sea.desafio_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sea.desafio_backend.dto.request.EnderecoRequest;
import com.sea.desafio_backend.dto.response.ViaCepResponse;
import com.sea.desafio_backend.exception.CepNotFoundException;
import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.Endereco;
import com.sea.desafio_backend.service.EnderecoService;
import com.sea.desafio_backend.service.ViaCepService;
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
 * Testes de integração para EnderecoController
 */
@WebMvcTest(EnderecoController.class)
@DisplayName("EnderecoController - Testes de Integração")
class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EnderecoService enderecoService;

    @MockBean
    private ViaCepService viaCepService;

    // ==================== TESTES GET /api/cep/{cep} ====================

    @Test
    @DisplayName("GET /api/cep/{cep} - Consultar CEP válido deve retornar 200")
    void consultarCep_CepValido_DeveRetornar200() throws Exception {
        // Arrange
        ViaCepResponse response = new ViaCepResponse();
        response.setCep("01001-000");
        response.setLogradouro("Praça da Sé");
        response.setComplemento("lado ímpar");
        response.setBairro("Sé");
        response.setLocalidade("São Paulo");
        response.setUf("SP");

        when(viaCepService.buscarEnderecoPorCep("01001000")).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/cep/01001000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("01001-000"))
                .andExpect(jsonPath("$.logradouro").value("Praça da Sé"))
                .andExpect(jsonPath("$.bairro").value("Sé"))
                .andExpect(jsonPath("$.localidade").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"));

        verify(viaCepService).buscarEnderecoPorCep("01001000");
    }

    @Test
    @DisplayName("GET /api/cep/{cep} - CEP não encontrado deve retornar 404")
    void consultarCep_CepNaoEncontrado_DeveRetornar404() throws Exception {
        // Arrange
        when(viaCepService.buscarEnderecoPorCep("99999999"))
                .thenThrow(new CepNotFoundException("99999999"));

        // Act & Assert
        mockMvc.perform(get("/api/cep/99999999"))
                .andExpect(status().isNotFound());

        verify(viaCepService).buscarEnderecoPorCep("99999999");
    }

    @Test
    @DisplayName("GET /api/cep/{cep} - CEP com máscara deve funcionar")
    void consultarCep_CepComMascara_DeveRetornar200() throws Exception {
        // Arrange
        ViaCepResponse response = new ViaCepResponse();
        response.setCep("01001-000");
        response.setLogradouro("Praça da Sé");
        response.setLocalidade("São Paulo");
        response.setUf("SP");

        when(viaCepService.buscarEnderecoPorCep("01001-000")).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/cep/01001-000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("01001-000"));

        verify(viaCepService).buscarEnderecoPorCep("01001-000");
    }

    // ==================== TESTES GET /api/enderecos/{id} ====================

    @Test
    @DisplayName("GET /api/enderecos/{id} - Buscar existente deve retornar 200")
    void buscarPorId_EnderecoExistente_DeveRetornar200() throws Exception {
        // Arrange
        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCep("01001000");
        endereco.setLogradouro("Praça da Sé");
        endereco.setBairro("Sé");
        endereco.setCidade("São Paulo");
        endereco.setUf("SP");

        when(enderecoService.buscarPorId(1L)).thenReturn(endereco);

        // Act & Assert
        mockMvc.perform(get("/api/enderecos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cep").value("01001000"))
                .andExpect(jsonPath("$.logradouro").value("Praça da Sé"))
                .andExpect(jsonPath("$.cidade").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"));

        verify(enderecoService).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/enderecos/{id} - Buscar inexistente deve retornar 404")
    void buscarPorId_EnderecoInexistente_DeveRetornar404() throws Exception {
        // Arrange
        when(enderecoService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Endereco", 99L));

        // Act & Assert
        mockMvc.perform(get("/api/enderecos/99"))
                .andExpect(status().isNotFound());
    }

    // ==================== TESTES PUT /api/enderecos/{id} ====================

    @Test
    @DisplayName("PUT /api/enderecos/{id} - Atualizar com dados válidos deve retornar 200")
    void atualizarEndereco_ComDadosValidos_DeveRetornar200() throws Exception {
        // Arrange
        EnderecoRequest request = new EnderecoRequest();
        request.setCep("12345-678");
        request.setLogradouro("Rua Nova");
        request.setComplemento("Apt 101");
        request.setBairro("Centro");
        request.setCidade("Rio de Janeiro");
        request.setUf("RJ");

        Endereco enderecoAtualizado = new Endereco();
        enderecoAtualizado.setId(1L);
        enderecoAtualizado.setCep("12345678");
        enderecoAtualizado.setLogradouro("Rua Nova");
        enderecoAtualizado.setComplemento("Apt 101");
        enderecoAtualizado.setBairro("Centro");
        enderecoAtualizado.setCidade("Rio de Janeiro");
        enderecoAtualizado.setUf("RJ");

        when(enderecoService.atualizarEndereco(anyLong(), any(Endereco.class)))
                .thenReturn(enderecoAtualizado);

        // Act & Assert
        mockMvc.perform(put("/api/enderecos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Rua Nova"))
                .andExpect(jsonPath("$.cidade").value("Rio de Janeiro"))
                .andExpect(jsonPath("$.uf").value("RJ"));

        verify(enderecoService).atualizarEndereco(anyLong(), any(Endereco.class));
    }

    @Test
    @DisplayName("PUT /api/enderecos/{id} - Atualizar inexistente deve retornar 404")
    void atualizarEndereco_EnderecoInexistente_DeveRetornar404() throws Exception {
        // Arrange
        EnderecoRequest request = new EnderecoRequest();
        request.setCep("12345-678");
        request.setLogradouro("Rua Teste");
        request.setComplemento("Casa");
        request.setBairro("Centro");
        request.setCidade("São Paulo");
        request.setUf("SP");

        when(enderecoService.atualizarEndereco(anyLong(), any(Endereco.class)))
                .thenThrow(new ResourceNotFoundException("Endereco", 99L));

        // Act & Assert
        mockMvc.perform(put("/api/enderecos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/enderecos/{id} - Dados inválidos deve retornar 400")
    void atualizarEndereco_DadosInvalidos_DeveRetornar400() throws Exception {
        // Arrange - Request sem campos obrigatórios
        EnderecoRequest request = new EnderecoRequest();
        // Todos os campos são null

        // Act & Assert
        mockMvc.perform(put("/api/enderecos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(enderecoService, never()).atualizarEndereco(anyLong(), any());
    }
}
