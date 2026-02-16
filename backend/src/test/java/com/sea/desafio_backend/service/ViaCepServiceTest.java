package com.sea.desafio_backend.service;

import com.sea.desafio_backend.dto.response.ViaCepResponse;
import com.sea.desafio_backend.exception.CepNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ViaCepService
 * 9 testes cobrindo: busca de CEP, validações, tratamento de erros
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ViaCepService - Testes Unitários")
class ViaCepServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ViaCepService viaCepService;

    // ==================== TESTES DE BUSCA CEP ====================

    @Test
    @DisplayName("Buscar CEP válido: Deve retornar dados completos")
    void buscarEnderecoPorCep_CepValido_DeveRetornarDados() {
        // Arrange
        String cep = "01001000";
        ViaCepResponse mockResponse = new ViaCepResponse();
        mockResponse.setCep("01001-000");
        mockResponse.setLogradouro("Praça da Sé");
        mockResponse.setBairro("Sé");
        mockResponse.setLocalidade("São Paulo");
        mockResponse.setUf("SP");
        mockResponse.setErro(null); // Sem erro

        when(restTemplate.getForObject(anyString(), eq(ViaCepResponse.class), anyString()))
                .thenReturn(mockResponse);

        // Act
        ViaCepResponse resultado = viaCepService.buscarEnderecoPorCep(cep);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCep()).isEqualTo("01001-000");
        assertThat(resultado.getLogradouro()).isEqualTo("Praça da Sé");
        assertThat(resultado.getBairro()).isEqualTo("Sé");
        assertThat(resultado.getLocalidade()).isEqualTo("São Paulo");
        assertThat(resultado.getUf()).isEqualTo("SP");
        verify(restTemplate).getForObject(anyString(), eq(ViaCepResponse.class), anyString());
    }

    @Test
    @DisplayName("Buscar CEP com máscara: Deve remover máscara e funcionar")
    void buscarEnderecoPorCep_CepComMascara_DeveRemoverMascara() {
        // Arrange
        String cepComMascara = "71573-008";
        String cepSemMascara = "71573008";
        
        ViaCepResponse mockResponse = new ViaCepResponse();
        mockResponse.setCep("71573-008");
        mockResponse.setLogradouro("Paranoá");
        mockResponse.setLocalidade("Brasília");
        mockResponse.setUf("DF");

        when(restTemplate.getForObject(anyString(), eq(ViaCepResponse.class), anyString()))
                .thenReturn(mockResponse);

        // Act
        ViaCepResponse resultado = viaCepService.buscarEnderecoPorCep(cepComMascara);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getLogradouro()).isEqualTo("Paranoá");
        verify(restTemplate).getForObject(anyString(), eq(ViaCepResponse.class), anyString());
    }

    @Test
    @DisplayName("Buscar CEP inválido: API retorna erro=true, deve lançar exception")
    void buscarEnderecoPorCep_CepInvalido_DeveLancarException() {
        // Arrange
        String cep = "71573008";
        ViaCepResponse mockResponse = new ViaCepResponse();
        mockResponse.setErro(true); // API retorna erro

        when(restTemplate.getForObject(anyString(), eq(ViaCepResponse.class), anyString()))
                .thenReturn(mockResponse);

        // Act & Assert
        assertThrows(CepNotFoundException.class, () -> {
            viaCepService.buscarEnderecoPorCep(cep);
        });
    }

    @Test
    @DisplayName("Buscar CEP: API retorna null, deve lançar exception")
    void buscarEnderecoPorCep_ApiRetornaNull_DeveLancarException() {
        // Arrange
        String cep = "71573008";

        when(restTemplate.getForObject(anyString(), eq(ViaCepResponse.class), anyString()))
                .thenReturn(null);

        // Act & Assert
        assertThrows(CepNotFoundException.class, () -> {
            viaCepService.buscarEnderecoPorCep(cep);
        });
    }

    @Test
    @DisplayName("Buscar CEP: Erro de comunicação com API, deve lançar exception")
    void buscarEnderecoPorCep_ErroNaRequisicao_DeveLancarException() {
        // Arrange
        String cep = "71573008";

        when(restTemplate.getForObject(anyString(), eq(ViaCepResponse.class), anyString()))
                .thenThrow(new RestClientException("Timeout"));

        // Act & Assert
        assertThrows(CepNotFoundException.class, () -> {
            viaCepService.buscarEnderecoPorCep(cep);
        });
    }

    // ==================== TESTES DE VALIDAÇÃO ====================

    @Test
    @DisplayName("Validar formato CEP: CEP com 8 dígitos deve ser válido")
    void validarFormatoCep_CepValido_DeveRetornarTrue() {
        // Act
        boolean resultado = viaCepService.validarFormatoCep("12345678");

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("Validar formato CEP: CEP com menos de 8 dígitos deve lançar exception")
    void validarFormatoCep_CepCurto_DeveLancarException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.validarFormatoCep("7157300"); // 7 dígitos
        });
    }

    @Test
    @DisplayName("Validar formato CEP: CEP com mais de 8 dígitos deve lançar exception")
    void validarFormatoCep_CepLongo_DeveLancarException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.validarFormatoCep("123456789"); // 9 dígitos
        });
    }

    @Test
    @DisplayName("Validar formato CEP: CEP nulo deve lançar exception")
    void validarFormatoCep_CepNulo_DeveLancarException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.validarFormatoCep(null);
        });
    }

    @Test
    @DisplayName("Validar formato CEP: CEP vazio deve lançar exception")
    void validarFormatoCep_CepVazio_DeveLancarException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            viaCepService.validarFormatoCep("");
        });
    }

    @Test
    @DisplayName("Validar formato CEP: CEP com máscara válido deve passar")
    void validarFormatoCep_CepComMascara_DevePassar() {
        // Act
        boolean resultado = viaCepService.validarFormatoCep("12345-678");

        // Assert
        assertThat(resultado).isTrue();
    }
}
