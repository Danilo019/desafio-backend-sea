package com.sea.desafio_backend.service;

import com.sea.desafio_backend.dto.request.ClienteRequest;
import com.sea.desafio_backend.dto.request.EmailRequest;
import com.sea.desafio_backend.dto.request.EnderecoRequest;
import com.sea.desafio_backend.dto.request.TelefoneRequest;
import com.sea.desafio_backend.exception.CpfInvalidoException;
import com.sea.desafio_backend.exception.CpfJaCadastradoException;
import com.sea.desafio_backend.exception.DadosMinimosException;
import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.model.entity.ClienteEmail;
import com.sea.desafio_backend.model.entity.Endereco;
import com.sea.desafio_backend.model.entity.Telefone;
import com.sea.desafio_backend.model.enums.TipoTelefone;
import com.sea.desafio_backend.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes Unitários para ClienteService
 * 
 * Cobertura:
 * - Criação de clientes
 * - Busca por ID e CPF
 * - Atualização de dados
 * - Deleção de clientes
 * - Validações de CPF
 * - Máscaras (aplicar/remover)
 * - Regras de negócio (mínimo 1 telefone/email)
 * - Exceções customizadas (CpfInvalidoException, CpfJaCadastradoException, DadosMinimosException)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService - Testes Unitários")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EnderecoService enderecoService;

    @Mock
    private TelefoneService telefoneService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteRequest clienteRequestValido;
    private Cliente clienteMock;

    @BeforeEach
    void setUp() {
        // Preparar dados de teste reutilizáveis
        clienteRequestValido = criarClienteRequestValido();
        clienteMock = criarClienteMock();
    }

    // ==================== TESTES DE CRIAÇÃO ====================

    @Test
    @DisplayName("Deve criar cliente com dados válidos")
    void criarCliente_ComDadosValidos_DeveRetornarClienteSalvo() {
        // ARRANGE
        when(telefoneService.removerMascaraTelefone(anyString())).thenAnswer(i -> i.getArgument(0).toString().replaceAll("[^0-9]", ""));
        when(enderecoService.removerMascaraCEP(anyString())).thenAnswer(i -> i.getArgument(0).toString().replaceAll("[^0-9]", ""));
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteMock);

        // ACT
        Cliente resultado = clienteService.criarCliente(clienteRequestValido);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("João Silva", resultado.getNome());
        assertNotNull(resultado.getEndereco());
        assertFalse(resultado.getTelefones().isEmpty());
        assertFalse(resultado.getEmails().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com CPF duplicado")
    void criarCliente_ComCpfDuplicado_DeveLancarCpfJaCadastradoException() {
        // ARRANGE
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.of(clienteMock));

        // ACT & ASSERT
        assertThrows(CpfJaCadastradoException.class, 
            () -> clienteService.criarCliente(clienteRequestValido));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com CPF inválido")
    void criarCliente_ComCpfInvalido_DeveLancarCpfInvalidoException() {
        // ARRANGE
        clienteRequestValido.setCpf("123");

        // ACT & ASSERT
        assertThrows(CpfInvalidoException.class, 
            () -> clienteService.criarCliente(clienteRequestValido));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente sem telefone")
    void criarCliente_SemTelefone_DeveLancarDadosMinimosException() {
        // ARRANGE
        clienteRequestValido.setTelefones(Collections.emptyList());

        // ACT & ASSERT
        assertThrows(DadosMinimosException.class, 
            () -> clienteService.criarCliente(clienteRequestValido));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente sem email")
    void criarCliente_SemEmail_DeveLancarDadosMinimosException() {
        // ARRANGE
        clienteRequestValido.setEmails(Collections.emptyList());
        when(telefoneService.removerMascaraTelefone(anyString())).thenAnswer(i -> i.getArgument(0).toString().replaceAll("[^0-9]", ""));
        when(enderecoService.removerMascaraCEP(anyString())).thenAnswer(i -> i.getArgument(0).toString().replaceAll("[^0-9]", ""));
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(DadosMinimosException.class, 
            () -> clienteService.criarCliente(clienteRequestValido));
    }

    // ==================== TESTES DE BUSCA ====================

    @Test
    @DisplayName("Deve buscar cliente por ID existente")
    void buscarPorId_ComIdExistente_DeveRetornarCliente() {
        // ARRANGE
        Long id = 1L;
        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteMock));

        // ACT
        Cliente resultado = clienteService.buscarPorId(id);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("João Silva", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar ID inexistente")
    void buscarPorId_ComIdInexistente_DeveLancarResourceNotFoundException() {
        // ARRANGE
        Long id = 999L;
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, 
            () -> clienteService.buscarPorId(id));
    }

    @Test
    @DisplayName("Deve buscar cliente por CPF existente")
    void buscarPorCpf_ComCpfExistente_DeveRetornarCliente() {
        // ARRANGE
        String cpf = "12345678901";
        String cpfComMascara = "123.456.789-01";
        when(clienteRepository.findByCpf(cpfComMascara)).thenReturn(Optional.of(clienteMock));

        // ACT
        Cliente resultado = clienteService.buscarPorCpf(cpf);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar CPF inexistente")
    void buscarPorCpf_ComCpfInexistente_DeveLancarResourceNotFoundException() {
        // ARRANGE
        String cpf = "12345678901";
        String cpfComMascara = "123.456.789-01";
        when(clienteRepository.findByCpf(cpfComMascara)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, 
            () -> clienteService.buscarPorCpf(cpf));
    }

    @Test
    @DisplayName("Deve listar todos os clientes")
    void listarTodos_DeveRetornarListaDeClientes() {
        // ARRANGE
        List<Cliente> clientesMock = Arrays.asList(clienteMock, clienteMock);
        when(clienteRepository.findAll()).thenReturn(clientesMock);

        // ACT
        List<Cliente> resultado = clienteService.listarTodos();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    // ==================== TESTES DE ATUALIZAÇÃO ====================

    @Test
    @DisplayName("Deve atualizar cliente com dados válidos")
    void atualizarCliente_ComDadosValidos_DeveRetornarClienteAtualizado() {
        // ARRANGE
        Long id = 1L;
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setNome("João Silva Atualizado");
        clienteAtualizado.setCpf("123.456.789-09");  // CPF válido

        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteMock));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteMock);

        // ACT
        Cliente resultado = clienteService.atualizarCliente(id, clienteAtualizado);

        // ASSERT
        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve validar CPF único ao atualizar com CPF diferente")
    void atualizarCliente_ComCpfDiferente_DeveValidarUnicidade() {
        // ARRANGE
        Long id = 1L;
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setNome("João Silva");
        clienteAtualizado.setCpf("111.444.777-35");  // CPF válido diferente

        Cliente clienteExistente = criarClienteMock();
        clienteExistente.setCpf("123.456.789-09");

        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.findByCpf("111.444.777-35")).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteExistente);

        // ACT
        Cliente resultado = clienteService.atualizarCliente(id, clienteAtualizado);

        // ASSERT
        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com CPF duplicado")
    void atualizarCliente_ComCpfDuplicado_DeveLancarCpfJaCadastradoException() {
        // ARRANGE
        Long id = 1L;
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setNome("João Silva");
        clienteAtualizado.setCpf("111.444.777-35");  // CPF válido

        Cliente clienteExistente = criarClienteMock();
        clienteExistente.setCpf("123.456.789-09");

        Cliente clienteComCpfDuplicado = new Cliente();
        clienteComCpfDuplicado.setId(2L);

        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.findByCpf("111.444.777-35")).thenReturn(Optional.of(clienteComCpfDuplicado));

        // ACT & ASSERT
        assertThrows(CpfJaCadastradoException.class, 
            () -> clienteService.atualizarCliente(id, clienteAtualizado));
    }

    // ==================== TESTES DE DELEÇÃO ====================

    @Test
    @DisplayName("Deve deletar cliente existente")
    void deletarCliente_ComIdExistente_DeveRemoverCliente() {
        // ARRANGE
        Long id = 1L;
        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteMock));
        doNothing().when(clienteRepository).delete(any(Cliente.class));

        // ACT
        clienteService.deletarCliente(id);

        // ASSERT
        verify(clienteRepository).delete(clienteMock);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar ID inexistente")
    void deletarCliente_ComIdInexistente_DeveLancarResourceNotFoundException() {
        // ARRANGE
        Long id = 999L;
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, 
            () -> clienteService.deletarCliente(id));
    }

    // ==================== TESTES DE VALIDAÇÕES ====================

    @Test
    @DisplayName("Deve validar CPF válido com dígitos verificadores corretos")
    void validarCPF_ComCpfValido_DeveRetornarTrue() {
        // CPF válido: 123.456.789-09
        // ACT
        @SuppressWarnings("deprecation")
        boolean resultado = clienteService.validarCPF("12345678909");

        // ASSERT
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve invalidar CPF com menos de 11 dígitos")
    void validarCPF_ComMenosDe11Digitos_DeveRetornarFalse() {
        // ACT
        @SuppressWarnings("deprecation")
        boolean resultado = clienteService.validarCPF("123456789");

        // ASSERT
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve invalidar CPF com todos os dígitos iguais")
    void validarCPF_ComDigitosIguais_DeveRetornarFalse() {
        // ACT
        @SuppressWarnings("deprecation")
        boolean resultado = clienteService.validarCPF("11111111111");

        // ASSERT
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve invalidar CPF com dígitos verificadores incorretos")
    void validarCPF_ComDigitosVerificadoresIncorretos_DeveRetornarFalse() {
        // CPF com dígitos verificadores errados: 123.456.789-00 (correto seria 09)
        // ACT
        @SuppressWarnings("deprecation")
        boolean resultado = clienteService.validarCPF("12345678900");

        // ASSERT
        assertFalse(resultado);
    }

    // ==================== TESTES DE MÁSCARAS ====================

    @Test
    @DisplayName("Deve remover máscara do CPF corretamente")
    void removerMascaraCPF_DeveRemoverFormatacao() {
        // ACT
        @SuppressWarnings("deprecation")
        String resultado = clienteService.removerMascaraCPF("123.456.789-09");

        // ASSERT
        assertEquals("12345678909", resultado);
    }

    @Test
    @DisplayName("Deve aplicar máscara no CPF corretamente")
    void aplicarMascaraCPF_DeveAplicarFormatacao() {
        // ACT
        @SuppressWarnings("deprecation")
        String resultado = clienteService.aplicarMascaraCPF("12345678909");

        // ASSERT
        assertEquals("123.456.789-09", resultado);
    }

    @Test
    @DisplayName("Deve retornar string vazia ao remover máscara de CPF nulo")
    void removerMascaraCPF_ComCpfNulo_DeveRetornarStringVazia() {
        // ACT
        @SuppressWarnings("deprecation")
        String resultado = clienteService.removerMascaraCPF(null);

        // ASSERT
        assertEquals("", resultado);
    }

    @Test
    @DisplayName("Deve retornar CPF original se formato inválido ao aplicar máscara")
    void aplicarMascaraCPF_ComFormatoInvalido_DeveRetornarOriginal() {
        // ACT
        @SuppressWarnings("deprecation")
        String resultado = clienteService.aplicarMascaraCPF("123");

        // ASSERT
        assertEquals("123", resultado);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private ClienteRequest criarClienteRequestValido() {
        ClienteRequest request = new ClienteRequest();
        request.setNome("João Silva");
        request.setCpf("123.456.789-09");  // CPF válido com dígitos verificadores corretos

        EnderecoRequest endereco = new EnderecoRequest();
        endereco.setCep("01310-100");
        endereco.setLogradouro("Avenida Paulista");
        endereco.setComplemento("Apto 123");
        endereco.setBairro("Bela Vista");
        endereco.setCidade("São Paulo");
        endereco.setUf("SP");
        request.setEndereco(endereco);

        TelefoneRequest telefone = new TelefoneRequest();
        telefone.setNumero("11987654321");
        telefone.setTipo(TipoTelefone.CELULAR);
        telefone.setPrincipal(true);
        request.setTelefones(Collections.singletonList(telefone));

        EmailRequest email = new EmailRequest();
        email.setEnderecoEmail("joao.silva@example.com");
        email.setPrincipal(true);
        request.setEmails(Collections.singletonList(email));

        return request;
    }

    private Cliente criarClienteMock() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("123.456.789-09");  // CPF válido com dígitos verificadores corretos

        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCep("01310100");
        endereco.setLogradouro("Avenida Paulista");
        endereco.setBairro("Bela Vista");
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
        email.setEnderecoEmail("joao.silva@example.com");
        email.setPrincipal(true);
        email.setCliente(cliente);
        cliente.setEmails(Collections.singletonList(email));

        return cliente;
    }
}
