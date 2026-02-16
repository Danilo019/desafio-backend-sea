package com.sea.desafio_backend.service;

import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.model.entity.Telefone;
import com.sea.desafio_backend.model.enums.TipoTelefone;
import com.sea.desafio_backend.repository.TelefoneRepository;
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
import static org.mockito.Mockito.*;

/**
 * Testes unitários para TelefoneService
 * 11 testes cobrindo: criação, atualização, deleção, validações, máscaras
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TelefoneService - Testes Unitários")
class TelefoneServiceTest {

    @Mock
    private TelefoneRepository telefoneRepository;

    @InjectMocks
    private TelefoneService telefoneService;

    // ==================== TESTES DE CRIAÇÃO ====================

    @Test
    @DisplayName("Criar telefone: Primeiro telefone deve ser principal automaticamente")
    void criarTelefone_PrimeiroTelefone_DeveSerPrincipalAutomaticamente() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Telefone telefone = new Telefone();
        telefone.setCliente(cliente);
        telefone.setNumero("(11) 98765-4321");
        telefone.setTipo(TipoTelefone.CELULAR);
        telefone.setPrincipal(false); // Cliente não marcou como principal

        when(telefoneRepository.countByClienteId(1L)).thenReturn(0L); // Primeiro telefone
        when(telefoneRepository.findByClienteIdAndNumero(anyLong(), anyString())).thenReturn(Optional.empty());
        when(telefoneRepository.save(any(Telefone.class))).thenAnswer(invocation -> {
            Telefone t = invocation.getArgument(0);
            t.setId(10L);
            return t;
        });

        // Act
        Telefone resultado = telefoneService.criarTelefone(telefone);

        // Assert
        assertThat(resultado.getPrincipal()).isTrue(); // ✅ Automático
        assertThat(resultado.getNumero()).isEqualTo("11987654321"); // Sem máscara
        verify(telefoneRepository).countByClienteId(1L);
        verify(telefoneRepository).save(any(Telefone.class));
    }

    @Test
    @DisplayName("Criar telefone: Com número duplicado deve lançar exception")
    void criarTelefone_ComNumeroDuplicado_DeveLancarException() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Telefone telefoneExistente = new Telefone();
        telefoneExistente.setId(10L);
        telefoneExistente.setNumero("11987654321");

        Telefone novoTelefone = new Telefone();
        novoTelefone.setCliente(cliente);
        novoTelefone.setNumero("(11) 98765-4321"); // Mesmo número com máscara
        novoTelefone.setTipo(TipoTelefone.CELULAR);

        when(telefoneRepository.findByClienteIdAndNumero(1L, "11987654321"))
                .thenReturn(Optional.of(telefoneExistente));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            telefoneService.criarTelefone(novoTelefone);
        });

        verify(telefoneRepository, never()).save(any(Telefone.class));
    }

    @Test
    @DisplayName("Criar telefone: Segundo telefone marcado como principal deve desmarcar outros")
    void criarTelefone_SegundoTelefoneMarcadoPrincipal_DeveDesmarcarOutros() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Telefone telefone = new Telefone();
        telefone.setCliente(cliente);
        telefone.setNumero("11987654321");
        telefone.setTipo(TipoTelefone.CELULAR);
        telefone.setPrincipal(true); // Quer ser o principal

        when(telefoneRepository.countByClienteId(1L)).thenReturn(1L); // Já existe 1
        when(telefoneRepository.findByClienteIdAndNumero(anyLong(), anyString())).thenReturn(Optional.empty());
        when(telefoneRepository.save(any(Telefone.class))).thenAnswer(invocation -> {
            Telefone t = invocation.getArgument(0);
            t.setId(20L);
            return t;
        });

        // Act
        telefoneService.criarTelefone(telefone);

        // Assert
        verify(telefoneRepository).desmarcarTodosPrincipaisPorCliente(1L);
        verify(telefoneRepository).save(any(Telefone.class));
    }

    // ==================== TESTES DE BUSCA ====================

    @Test
    @DisplayName("Buscar telefone por ID: Deve retornar quando existir")
    void buscarPorId_QuandoExistir_DeveRetornar() {
        // Arrange
        Telefone telefone = new Telefone();
        telefone.setId(1L);
        telefone.setNumero("11987654321");

        when(telefoneRepository.findById(1L)).thenReturn(Optional.of(telefone));

        // Act
        Telefone resultado = telefoneService.buscarPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(telefoneRepository).findById(1L);
    }

    @Test
    @DisplayName("Buscar telefone por ID: Deve lançar exception quando não existir")
    void buscarPorId_QuandoNaoExistir_DeveLancarException() {
        // Arrange
        when(telefoneRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            telefoneService.buscarPorId(99L);
        });
    }

    // ==================== TESTES DE ATUALIZAÇÃO ====================

    @Test
    @DisplayName("Atualizar telefone: Com dados válidos deve retornar atualizado")
    void atualizarTelefone_ComDadosValidos_DeveRetornarAtualizado() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Telefone telefoneExistente = new Telefone();
        telefoneExistente.setId(10L);
        telefoneExistente.setCliente(cliente);
        telefoneExistente.setNumero("11987654321");
        telefoneExistente.setTipo(TipoTelefone.CELULAR);
        telefoneExistente.setPrincipal(false);

        Telefone telefoneAtualizado = new Telefone();
        telefoneAtualizado.setNumero("(11) 91234-5678"); // Novo número
        telefoneAtualizado.setTipo(TipoTelefone.COMERCIAL);
        telefoneAtualizado.setPrincipal(false);

        when(telefoneRepository.findById(10L)).thenReturn(Optional.of(telefoneExistente));
        when(telefoneRepository.findByClienteIdAndNumero(1L, "11912345678")).thenReturn(Optional.empty());
        when(telefoneRepository.save(any(Telefone.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Telefone resultado = telefoneService.atualizarTelefone(10L, telefoneAtualizado);

        // Assert
        assertThat(resultado.getNumero()).isEqualTo("11912345678");
        assertThat(resultado.getTipo()).isEqualTo(TipoTelefone.COMERCIAL);
        verify(telefoneRepository).save(any(Telefone.class));
    }

    @Test
    @DisplayName("Atualizar telefone: Com número duplicado deve lançar exception")
    void atualizarTelefone_ComNumeroDuplicado_DeveLancarException() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Telefone telefoneExistente = new Telefone();
        telefoneExistente.setId(10L);
        telefoneExistente.setCliente(cliente);
        telefoneExistente.setNumero("11987654321");

        Telefone outroTelefone = new Telefone();
        outroTelefone.setId(20L);
        outroTelefone.setNumero("11912345678");

        Telefone telefoneAtualizado = new Telefone();
        telefoneAtualizado.setNumero("11912345678"); // Quer usar número que já existe

        when(telefoneRepository.findById(10L)).thenReturn(Optional.of(telefoneExistente));
        when(telefoneRepository.findByClienteIdAndNumero(1L, "11912345678"))
                .thenReturn(Optional.of(outroTelefone)); // Já existe

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            telefoneService.atualizarTelefone(10L, telefoneAtualizado);
        });

        verify(telefoneRepository, never()).save(any(Telefone.class));
    }

    // ==================== TESTES DE DELEÇÃO ====================

    @Test
    @DisplayName("Deletar telefone: Último telefone deve lançar exception")
    void deletarTelefone_UltimoTelefone_DeveLancarException() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Telefone telefone = new Telefone();
        telefone.setId(10L);
        telefone.setCliente(cliente);
        telefone.setNumero("11987654321");

        when(telefoneRepository.findById(10L)).thenReturn(Optional.of(telefone));
        when(telefoneRepository.countByClienteId(1L)).thenReturn(1L); // Último telefone

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            telefoneService.deletarTelefone(10L);
        });

        verify(telefoneRepository, never()).delete(any(Telefone.class));
    }

    @Test
    @DisplayName("Deletar telefone: Telefone principal deve eleger novo principal")
    void deletarTelefone_TelefonePrincipal_DeveElegerNovoPrincipal() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Telefone telefonePrincipal = new Telefone();
        telefonePrincipal.setId(10L);
        telefonePrincipal.setCliente(cliente);
        telefonePrincipal.setNumero("11987654321");
        telefonePrincipal.setPrincipal(true);

        Telefone telefoneSecundario = new Telefone();
        telefoneSecundario.setId(20L);
        telefoneSecundario.setCliente(cliente);
        telefoneSecundario.setNumero("11912345678");
        telefoneSecundario.setPrincipal(false);

        when(telefoneRepository.findById(10L)).thenReturn(Optional.of(telefonePrincipal));
        when(telefoneRepository.countByClienteId(1L)).thenReturn(2L); // Tem 2 telefones
        when(telefoneRepository.findByClienteId(1L)).thenReturn(Arrays.asList(telefoneSecundario));

        // Act
        telefoneService.deletarTelefone(10L);

        // Assert
        verify(telefoneRepository).delete(telefonePrincipal);
        verify(telefoneRepository).flush();
        verify(telefoneRepository).findByClienteId(1L);
        verify(telefoneRepository).save(telefoneSecundario); // Salvou o novo principal
        assertThat(telefoneSecundario.getPrincipal()).isTrue(); // Foi promovido
    }

    // ==================== TESTES DE DEFINIR COMO PRINCIPAL ====================

    @Test
    @DisplayName("Definir como principal: Deve desmarcar outros telefones")
    void definirComoPrincipal_DeveDesmarcarOutros() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Telefone telefone = new Telefone();
        telefone.setId(20L);
        telefone.setCliente(cliente);
        telefone.setNumero("11912345678");
        telefone.setPrincipal(false);

        when(telefoneRepository.findById(20L)).thenReturn(Optional.of(telefone));

        // Act
        telefoneService.definirComoPrincipal(20L);

        // Assert
        verify(telefoneRepository).desmarcarTodosPrincipaisPorCliente(1L);
        assertThat(telefone.getPrincipal()).isTrue();
        verify(telefoneRepository).save(telefone);
    }

    // ==================== TESTES DE MÁSCARAS ====================

    @Test
    @DisplayName("Remover máscara de telefone: Deve remover toda formatação")
    void removerMascaraTelefone_DeveRemoverFormatacao() {
        // Act
        String resultado1 = telefoneService.removerMascaraTelefone("(11) 98765-4321");
        String resultado2 = telefoneService.removerMascaraTelefone("11987654321");
        String resultado3 = telefoneService.removerMascaraTelefone(null);

        // Assert
        assertThat(resultado1).isEqualTo("11987654321");
        assertThat(resultado2).isEqualTo("11987654321");
        assertThat(resultado3).isEmpty();
    }

    @Test
    @DisplayName("Aplicar máscara telefone celular: Deve formatar com 11 dígitos")
    void aplicarMascaraTelefone_Celular_DeveFormatarCorretamente() {
        // Act
        String resultado = telefoneService.aplicarMascaraTelefone("11987654321", TipoTelefone.CELULAR);

        // Assert
        assertThat(resultado).isEqualTo("(11) 98765-4321");
    }

    @Test
    @DisplayName("Aplicar máscara telefone fixo: Deve formatar com 10 dígitos")
    void aplicarMascaraTelefone_FixoResidencial_DeveFormatarCorretamente() {
        // Act
        String resultado = telefoneService.aplicarMascaraTelefone("1134567890", TipoTelefone.RESIDENCIAL);

        // Assert
        assertThat(resultado).isEqualTo("(11) 3456-7890");
    }
}
