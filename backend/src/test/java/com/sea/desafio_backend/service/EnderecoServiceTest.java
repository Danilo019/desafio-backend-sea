package com.sea.desafio_backend.service;

import com.sea.desafio_backend.dto.response.ViaCepResponse;
import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.model.entity.Endereco;
import com.sea.desafio_backend.repository.EnderecoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para EnderecoService
 * 12 testes cobrindo: criação, atualização, deleção, integração ViaCEP, máscaras
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EnderecoService - Testes Unitários")
class EnderecoServiceTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private ViaCepService viaCepService;

    @InjectMocks
    private EnderecoService enderecoService;

    // ==================== TESTES DE CRIAÇÃO ====================

    @Test
    @DisplayName("Criar endereço: Deve remover máscara do CEP e salvar")
    void criarEndereco_DeveSalvarComCepSemMascara() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Endereco endereco = new Endereco();
        endereco.setCliente(cliente);
        endereco.setCep("71573-008"); // Com máscara
        endereco.setLogradouro("Rua Teste");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setUf("SP");

        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(invocation -> {
            Endereco e = invocation.getArgument(0);
            e.setId(10L);
            return e;
        });

        // Act
        Endereco resultado = enderecoService.criarEndereco(endereco);

        // Assert
        assertThat(resultado.getCep()).isEqualTo("71573008"); // Sem máscara
        assertThat(resultado.getId()).isEqualTo(10L);
        verify(enderecoRepository).save(any(Endereco.class));
    }

    @Test
    @DisplayName("Criar endereço com ViaCEP: Deve buscar dados da API e salvar")
    void criarEnderecoComViaCep_DeveBuscarDadosEPreencher() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        ViaCepResponse viaCepResponse = new ViaCepResponse();
        viaCepResponse.setCep("01001-000");
        viaCepResponse.setLogradouro("Praça da Sé");
        viaCepResponse.setBairro("Sé");
        viaCepResponse.setLocalidade("São Paulo");
        viaCepResponse.setUf("SP");

        Endereco endereco = new Endereco();
        endereco.setCliente(cliente);
        endereco.setComplemento("lado ímpar");

        when(viaCepService.buscarEnderecoPorCep("01001000")).thenReturn(viaCepResponse);
        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(invocation -> {
            Endereco e = invocation.getArgument(0);
            e.setId(20L);
            return e;
        });

        // Act
        Endereco resultado = enderecoService.criarEnderecoComViaCep("01001000", endereco);

        // Assert
        assertThat(resultado.getCep()).isEqualTo("01001000");
        assertThat(resultado.getLogradouro()).isEqualTo("Praça da Sé");
        assertThat(resultado.getBairro()).isEqualTo("Sé");
        assertThat(resultado.getCidade()).isEqualTo("São Paulo");
        assertThat(resultado.getUf()).isEqualTo("SP");
        assertThat(resultado.getComplemento()).isEqualTo("lado ímpar");
        verify(viaCepService).buscarEnderecoPorCep("01001000");
        verify(enderecoRepository).save(any(Endereco.class));
    }

    // ==================== TESTES DE BUSCA ====================

    @Test
    @DisplayName("Buscar endereço por ID: Deve retornar quando existir")
    void buscarPorId_QuandoExistir_DeveRetornar() {
        // Arrange
        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCep("71573008");
        endereco.setLogradouro("Rua Teste");

        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));

        // Act
        Endereco resultado = enderecoService.buscarPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(enderecoRepository).findById(1L);
    }

    @Test
    @DisplayName("Buscar endereço por ID: Deve lançar exception quando não existir")
    void buscarPorId_QuandoNaoExistir_DeveLancarException() {
        // Arrange
        when(enderecoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            enderecoService.buscarPorId(99L);
        });
    }

    @Test
    @DisplayName("Buscar endereço por cliente: Deve retornar quando existir")
    void buscarPorCliente_QuandoExistir_DeveRetornar() {
        // Arrange
        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCep("71573008");

        when(enderecoRepository.findByClienteId(1L)).thenReturn(Optional.of(endereco));

        // Act
        Endereco resultado = enderecoService.buscarPorCliente(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(enderecoRepository).findByClienteId(1L);
    }

    @Test
    @DisplayName("Buscar endereço por cliente: Deve lançar exception quando não existir")
    void buscarPorCliente_QuandoNaoExistir_DeveLancarException() {
        // Arrange
        when(enderecoRepository.findByClienteId(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            enderecoService.buscarPorCliente(99L);
        });
    }

    @Test
    @DisplayName("Listar todos: Deve retornar lista de endereços")
    void listarTodos_DeveRetornarLista() {
        // Arrange
        Endereco endereco1 = new Endereco();
        endereco1.setId(1L);
        endereco1.setCep("71573008");
        Endereco endereco2 = new Endereco();
        endereco2.setId(2L);

        when(enderecoRepository.findAll()).thenReturn(Arrays.asList(endereco1, endereco2));

        // Act
        List<Endereco> resultado = enderecoService.listarTodos();

        // Assert
        assertThat(resultado).hasSize(2);
        verify(enderecoRepository).findAll();
    }

    // ==================== TESTES DE ATUALIZAÇÃO ====================

    @Test
    @DisplayName("Atualizar endereço: Deve atualizar todos os campos")
    void atualizarEndereco_ComDadosValidos_DeveAtualizar() {
        // Arrange
        Endereco enderecoExistente = new Endereco();
        enderecoExistente.setId(10L);
        enderecoExistente.setCep("12345678");
        enderecoExistente.setLogradouro("Rua Antiga");

        Endereco enderecoAtualizado = new Endereco();
        enderecoAtualizado.setCep("98765-432"); // Com máscara
        enderecoAtualizado.setLogradouro("Rua Nova");
        enderecoAtualizado.setComplemento("Apto 101");
        enderecoAtualizado.setBairro("Novo Bairro");
        enderecoAtualizado.setCidade("Rio de Janeiro");
        enderecoAtualizado.setUf("RJ");

        when(enderecoRepository.findById(10L)).thenReturn(Optional.of(enderecoExistente));
        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Endereco resultado = enderecoService.atualizarEndereco(10L, enderecoAtualizado);

        // Assert
        assertThat(resultado.getCep()).isEqualTo("98765432"); // Sem máscara
        assertThat(resultado.getLogradouro()).isEqualTo("Rua Nova");
        assertThat(resultado.getBairro()).isEqualTo("Novo Bairro");
        assertThat(resultado.getCidade()).isEqualTo("Rio de Janeiro");
        assertThat(resultado.getUf()).isEqualTo("RJ");
        verify(enderecoRepository).save(any(Endereco.class));
    }

    @Test
    @DisplayName("Atualizar com ViaCEP: Deve buscar novos dados e atualizar")
    void atualizarComViaCep_DeveBuscarNovoCepEAtualizar() {
        // Arrange
        Endereco enderecoExistente = new Endereco();
        enderecoExistente.setId(10L);
        enderecoExistente.setCep("12345678");
        enderecoExistente.setComplemento("Complemento Antigo");

        ViaCepResponse viaCepResponse = new ViaCepResponse();
        viaCepResponse.setCep("01001-000");
        viaCepResponse.setLogradouro("Praça da Sé");
        viaCepResponse.setBairro("Sé");
        viaCepResponse.setLocalidade("São Paulo");
        viaCepResponse.setUf("SP");

        when(enderecoRepository.findById(10L)).thenReturn(Optional.of(enderecoExistente));
        when(viaCepService.buscarEnderecoPorCep("01001000")).thenReturn(viaCepResponse);
        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Endereco resultado = enderecoService.atualizarComViaCep(10L, "01001000");

        // Assert
        assertThat(resultado.getCep()).isEqualTo("01001000");
        assertThat(resultado.getLogradouro()).isEqualTo("Praça da Sé");
        assertThat(resultado.getBairro()).isEqualTo("Sé");
        assertThat(resultado.getCidade()).isEqualTo("São Paulo");
        assertThat(resultado.getUf()).isEqualTo("SP");
        assertThat(resultado.getComplemento()).isEqualTo("Complemento Antigo"); // Mantém complemento
        verify(viaCepService).buscarEnderecoPorCep("01001000");
        verify(enderecoRepository).save(any(Endereco.class));
    }

    // ==================== TESTES DE DELEÇÃO ====================

    @Test
    @DisplayName("Deletar endereço: Deve validar existência e deletar")
    void deletarEndereco_DeveValidarEDeletar() {
        // Arrange
        Endereco endereco = new Endereco();
        endereco.setId(10L);

        when(enderecoRepository.findById(10L)).thenReturn(Optional.of(endereco));

        // Act
        enderecoService.deletarEndereco(10L);

        // Assert
        verify(enderecoRepository).findById(10L);
        verify(enderecoRepository).deleteById(10L);
    }

    // ==================== TESTES DE MÉTODOS AUXILIARES ====================

    @Test
    @DisplayName("Remover máscara CEP: Deve remover toda formatação")
    void removerMascaraCEP_DeveRemoverFormatacao() {
        // Act
        String resultado1 = enderecoService.removerMascaraCEP("12345-678");
        String resultado2 = enderecoService.removerMascaraCEP("12345678");
        String resultado3 = enderecoService.removerMascaraCEP(null);

        // Assert
        assertThat(resultado1).isEqualTo("12345678");
        assertThat(resultado2).isEqualTo("12345678");
        assertThat(resultado3).isEmpty();
    }

    @Test
    @DisplayName("Aplicar máscara CEP: Deve formatar corretamente")
    void aplicarMascaraCEP_DeveFormatarCorretamente() {
        // Act
        String resultado1 = enderecoService.aplicarMascaraCEP("12345678");
        String resultado2 = enderecoService.aplicarMascaraCEP("12345-678");
        String resultado3 = enderecoService.aplicarMascaraCEP("123"); // CEP inválido

        // Assert
        assertThat(resultado1).isEqualTo("12345-678");
        assertThat(resultado2).isEqualTo("12345-678");
        assertThat(resultado3).isEqualTo("123"); // Retorna original
    }

    @Test
    @DisplayName("Consultar CEP: Deve delegar para ViaCepService")
    void consultarCep_DeveDelegarParaViaCepService() {
        // Arrange
        ViaCepResponse viaCepResponse = new ViaCepResponse();
        viaCepResponse.setCep("01001-000");
        viaCepResponse.setLogradouro("Praça da Sé");

        when(viaCepService.buscarEnderecoPorCep("01001000")).thenReturn(viaCepResponse);

        // Act
        ViaCepResponse resultado = enderecoService.consultarCep("01001000");

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCep()).isEqualTo("01001-000");
        assertThat(resultado.getLogradouro()).isEqualTo("Praça da Sé");
        verify(viaCepService).buscarEnderecoPorCep("01001000");
    }
}
