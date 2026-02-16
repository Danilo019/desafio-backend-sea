package com.sea.desafio_backend.service;

import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.model.entity.ClienteEmail;
import com.sea.desafio_backend.repository.EmailRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para EmailService
 * 10 testes cobrindo: criação, atualização, deleção, validações
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService - Testes Unitários")
class EmailServiceTest {

    @Mock
    private EmailRepository emailRepository;

    @InjectMocks
    private EmailService emailService;

    // ==================== TESTES DE CRIAÇÃO ====================

    @Test
    @DisplayName("Criar email: Primeiro email deve ser principal automaticamente")
    void criarEmail_PrimeiroEmail_DeveSerPrincipalAutomaticamente() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        ClienteEmail email = new ClienteEmail();
        email.setCliente(cliente);
        email.setEnderecoEmail("usuario@example.com");
        email.setPrincipal(false); // Cliente não marcou como principal

        when(emailRepository.countByClienteId(1L)).thenReturn(0L); // Primeiro email
        when(emailRepository.findByClienteIdAndEnderecoEmail(anyLong(), anyString())).thenReturn(Optional.empty());
        when(emailRepository.save(any(ClienteEmail.class))).thenAnswer(invocation -> {
            ClienteEmail e = invocation.getArgument(0);
            e.setId(10L);
            return e;
        });

        // Act
        ClienteEmail resultado = emailService.criarEmail(email);

        // Assert
        assertThat(resultado.getPrincipal()).isTrue(); // ✅ Automático
        assertThat(resultado.getEnderecoEmail()).isEqualTo("usuario@example.com");
        verify(emailRepository).countByClienteId(1L);
        verify(emailRepository).save(any(ClienteEmail.class));
    }

    @Test
    @DisplayName("Criar email: Com email duplicado deve lançar exception")
    void criarEmail_ComEmailDuplicado_DeveLancarException() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        ClienteEmail emailExistente = new ClienteEmail();
        emailExistente.setId(10L);
        emailExistente.setEnderecoEmail("usuario@example.com");

        ClienteEmail novoEmail = new ClienteEmail();
        novoEmail.setCliente(cliente);
        novoEmail.setEnderecoEmail("usuario@example.com"); // Mesmo email

        when(emailRepository.findByClienteIdAndEnderecoEmail(1L, "usuario@example.com"))
                .thenReturn(Optional.of(emailExistente));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.criarEmail(novoEmail);
        });

        verify(emailRepository, never()).save(any(ClienteEmail.class));
    }

    @Test
    @DisplayName("Criar email: Segundo email marcado como principal deve desmarcar outros")
    void criarEmail_SegundoEmailMarcadoPrincipal_DeveDesmarcarOutros() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        ClienteEmail email = new ClienteEmail();
        email.setCliente(cliente);
        email.setEnderecoEmail("novo@example.com");
        email.setPrincipal(true); // Quer ser o principal

        when(emailRepository.countByClienteId(1L)).thenReturn(1L); // Já existe 1
        when(emailRepository.findByClienteIdAndEnderecoEmail(anyLong(), anyString())).thenReturn(Optional.empty());
        when(emailRepository.save(any(ClienteEmail.class))).thenAnswer(invocation -> {
            ClienteEmail e = invocation.getArgument(0);
            e.setId(20L);
            return e;
        });

        // Act
        emailService.criarEmail(email);

        // Assert
        verify(emailRepository).desmarcarTodosPrincipaisPorCliente(1L);
        verify(emailRepository).save(any(ClienteEmail.class));
    }

    // ==================== TESTES DE BUSCA ====================

    @Test
    @DisplayName("Buscar email por ID: Deve retornar quando existir")
    void buscarPorId_QuandoExistir_DeveRetornar() {
        // Arrange
        ClienteEmail email = new ClienteEmail();
        email.setId(1L);
        email.setEnderecoEmail("usuario@example.com");

        when(emailRepository.findById(1L)).thenReturn(Optional.of(email));

        // Act
        ClienteEmail resultado = emailService.buscarPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(emailRepository).findById(1L);
    }

    @Test
    @DisplayName("Buscar email por ID: Deve lançar exception quando não existir")
    void buscarPorId_QuandoNaoExistir_DeveLancarException() {
        // Arrange
        when(emailRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            emailService.buscarPorId(99L);
        });
    }

    // ==================== TESTES DE ATUALIZAÇÃO ====================

    @Test
    @DisplayName("Atualizar email: Com dados válidos deve retornar atualizado")
    void atualizarEmail_ComDadosValidos_DeveRetornarAtualizado() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        ClienteEmail emailExistente = new ClienteEmail();
        emailExistente.setId(10L);
        emailExistente.setCliente(cliente);
        emailExistente.setEnderecoEmail("antigo@example.com");
        emailExistente.setPrincipal(false);

        ClienteEmail emailAtualizado = new ClienteEmail();
        emailAtualizado.setEnderecoEmail("novo@example.com"); // Novo email
        emailAtualizado.setPrincipal(false);

        when(emailRepository.findById(10L)).thenReturn(Optional.of(emailExistente));
        when(emailRepository.findByClienteIdAndEnderecoEmail(1L, "novo@example.com")).thenReturn(Optional.empty());
        when(emailRepository.save(any(ClienteEmail.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ClienteEmail resultado = emailService.atualizarEmail(10L, emailAtualizado);

        // Assert
        assertThat(resultado.getEnderecoEmail()).isEqualTo("novo@example.com");
        verify(emailRepository).save(any(ClienteEmail.class));
    }

    @Test
    @DisplayName("Atualizar email: Com email duplicado deve lançar exception")
    void atualizarEmail_ComEmailDuplicado_DeveLancarException() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        ClienteEmail emailExistente = new ClienteEmail();
        emailExistente.setId(10L);
        emailExistente.setCliente(cliente);
        emailExistente.setEnderecoEmail("antigo@example.com");

        ClienteEmail outroEmail = new ClienteEmail();
        outroEmail.setId(20L);
        outroEmail.setEnderecoEmail("outro@example.com");

        ClienteEmail emailAtualizado = new ClienteEmail();
        emailAtualizado.setEnderecoEmail("outro@example.com"); // Quer usar email que já existe

        when(emailRepository.findById(10L)).thenReturn(Optional.of(emailExistente));
        when(emailRepository.findByClienteIdAndEnderecoEmail(1L, "outro@example.com"))
                .thenReturn(Optional.of(outroEmail)); // Já existe

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.atualizarEmail(10L, emailAtualizado);
        });

        verify(emailRepository, never()).save(any(ClienteEmail.class));
    }

    // ==================== TESTES DE DELEÇÃO ====================

    @Test
    @DisplayName("Deletar email: Último email deve lançar exception")
    void deletarEmail_UltimoEmail_DeveLancarException() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        ClienteEmail email = new ClienteEmail();
        email.setId(10L);
        email.setCliente(cliente);
        email.setEnderecoEmail("usuario@example.com");

        when(emailRepository.findById(10L)).thenReturn(Optional.of(email));
        when(emailRepository.countByClienteId(1L)).thenReturn(1L); // Último email

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.deletarEmail(10L);
        });

        verify(emailRepository, never()).delete(any(ClienteEmail.class));
    }

    @Test
    @DisplayName("Deletar email: Email principal deve eleger novo principal")
    void deletarEmail_EmailPrincipal_DeveElegerNovoPrincipal() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        ClienteEmail emailPrincipal = new ClienteEmail();
        emailPrincipal.setId(10L);
        emailPrincipal.setCliente(cliente);
        emailPrincipal.setEnderecoEmail("principal@example.com");
        emailPrincipal.setPrincipal(true);

        ClienteEmail emailSecundario = new ClienteEmail();
        emailSecundario.setId(20L);
        emailSecundario.setCliente(cliente);
        emailSecundario.setEnderecoEmail("secundario@example.com");
        emailSecundario.setPrincipal(false);

        when(emailRepository.findById(10L)).thenReturn(Optional.of(emailPrincipal));
        when(emailRepository.countByClienteId(1L)).thenReturn(2L); // Tem 2 emails
        when(emailRepository.findByClienteId(1L)).thenReturn(Arrays.asList(emailSecundario));

        // Act
        emailService.deletarEmail(10L);

        // Assert
        verify(emailRepository).delete(emailPrincipal);
        verify(emailRepository).flush();
        verify(emailRepository).findByClienteId(1L);
        verify(emailRepository).save(emailSecundario); // Salvou o novo principal
        assertThat(emailSecundario.getPrincipal()).isTrue(); // Foi promovido
    }

    // ==================== TESTES DE DEFINIR COMO PRINCIPAL ====================

    @Test
    @DisplayName("Definir como principal: Deve desmarcar outros emails")
    void definirComoPrincipal_DeveDesmarcarOutros() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        ClienteEmail email = new ClienteEmail();
        email.setId(20L);
        email.setCliente(cliente);
        email.setEnderecoEmail("secundario@example.com");
        email.setPrincipal(false);

        when(emailRepository.findById(20L)).thenReturn(Optional.of(email));

        // Act
        emailService.definirComoPrincipal(20L);

        // Assert
        verify(emailRepository).desmarcarTodosPrincipaisPorCliente(1L);
        assertThat(email.getPrincipal()).isTrue();
        verify(emailRepository).save(email);
    }
}
