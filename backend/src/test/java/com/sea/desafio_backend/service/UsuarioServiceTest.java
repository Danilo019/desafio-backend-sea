package com.sea.desafio_backend.service;

import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.Usuario;
import com.sea.desafio_backend.model.enums.TipoUsuario;
import com.sea.desafio_backend.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para UsuarioService
 * 15 testes cobrindo: criação, autenticação, atualização, deleção, criptografia
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - Testes Unitários")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ==================== TESTES DE CRIAÇÃO ====================

    @Test
    @DisplayName("Criar usuário: Deve criptografar senha e salvar")
    void criarUsuario_DeveCriptografarSenhaESalvar() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setUsername("teste");
        usuario.setPassword("senha123");
        usuario.setTipo(TipoUsuario.PADRAO);

        when(usuarioRepository.existsByUsername("teste")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        // Act
        Usuario resultado = usuarioService.criarUsuario(usuario);

        // Assert
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getPassword()).isNotEqualTo("senha123"); // Senha foi criptografada
        assertThat(resultado.getPassword()).startsWith("$2a$"); // BCrypt hash prefix
        assertThat(resultado.getAtivo()).isTrue(); // Ativo por padrão
        verify(usuarioRepository).existsByUsername("teste");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Criar usuário: Com username duplicado deve lançar exception")
    void criarUsuario_UsernameExistente_DeveLancarException() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setPassword("senha123");

        when(usuarioRepository.existsByUsername("admin")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.criarUsuario(usuario);
        });

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // ==================== TESTES DE BUSCA ====================

    @Test
    @DisplayName("Buscar por ID: Deve retornar quando existir")
    void buscarPorId_QuandoExistir_DeveRetornar() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("teste");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        Usuario resultado = usuarioService.buscarPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(usuarioRepository).findById(1L);
    }

    @Test
    @DisplayName("Buscar por ID: Deve lançar exception quando não existir")
    void buscarPorId_QuandoNaoExistir_DeveLancarException() {
        // Arrange
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.buscarPorId(99L);
        });
    }

    @Test
    @DisplayName("Buscar por username: Deve retornar quando existir")
    void buscarPorUsername_QuandoExistir_DeveRetornar() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        // Act
        Usuario resultado = usuarioService.buscarPorUsername("admin");

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo("admin");
        verify(usuarioRepository).findByUsername("admin");
    }

    @Test
    @DisplayName("Buscar por tipo: Deve retornar lista de usuários do tipo")
    void buscarPorTipo_DeveRetornarLista() {
        // Arrange
        Usuario admin1 = new Usuario();
        admin1.setId(1L);
        admin1.setTipo(TipoUsuario.ADMIN);

        Usuario admin2 = new Usuario();
        admin2.setId(2L);
        admin2.setTipo(TipoUsuario.ADMIN);

        when(usuarioRepository.findByTipo(TipoUsuario.ADMIN)).thenReturn(Arrays.asList(admin1, admin2));

        // Act
        List<Usuario> resultado = usuarioService.buscarPorTipo(TipoUsuario.ADMIN);

        // Assert
        assertThat(resultado).hasSize(2);
        verify(usuarioRepository).findByTipo(TipoUsuario.ADMIN);
    }

    // ==================== TESTES DE VALIDAÇÃO ====================

    @Test
    @DisplayName("Validar credenciais: Com senha correta deve retornar true")
    void validarCredenciais_SenhaCorreta_DeveRetornarTrue() {
        // Arrange
        String senhaPlain = "senha123";
        String senhaHash = encoder.encode(senhaPlain);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("teste");
        usuario.setPassword(senhaHash);
        usuario.setAtivo(true);

        when(usuarioRepository.findByUsername("teste")).thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = usuarioService.validarCredenciais("teste", senhaPlain);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("Validar credenciais: Com senha incorreta deve retornar false")
    void validarCredenciais_SenhaIncorreta_DeveRetornarFalse() {
        // Arrange
        String senhaHash = encoder.encode("senha123");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("teste");
        usuario.setPassword(senhaHash);
        usuario.setAtivo(true);

        when(usuarioRepository.findByUsername("teste")).thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = usuarioService.validarCredenciais("teste", "senhaErrada");

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("Validar credenciais: Usuário inativo deve retornar false")
    void validarCredenciais_UsuarioInativo_DeveRetornarFalse() {
        // Arrange
        String senhaHash = encoder.encode("senha123");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("teste");
        usuario.setPassword(senhaHash);
        usuario.setAtivo(false); // Inativo

        when(usuarioRepository.findByUsername("teste")).thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = usuarioService.validarCredenciais("teste", "senha123");

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("Validar credenciais: Usuário inexistente deve retornar false")
    void validarCredenciais_UsuarioInexistente_DeveRetornarFalse() {
        // Arrange
        when(usuarioRepository.findByUsername("inexistente")).thenReturn(Optional.empty());

        // Act
        boolean resultado = usuarioService.validarCredenciais("inexistente", "senha");

        // Assert
        assertThat(resultado).isFalse();
    }

    // ==================== TESTES DE ATUALIZAÇÃO ====================

    @Test
    @DisplayName("Atualizar usuário: Deve atualizar tipo e ativo")
    void atualizarUsuario_DeveAtualizarCampos() {
        // Arrange
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setUsername("teste");
        usuarioExistente.setTipo(TipoUsuario.PADRAO);
        usuarioExistente.setAtivo(true);

        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setUsername("teste"); // Mesmo username
        usuarioAtualizado.setTipo(TipoUsuario.ADMIN); // Mudou tipo
        usuarioAtualizado.setAtivo(false); // Mudou ativo

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Usuario resultado = usuarioService.atualizarUsuario(1L, usuarioAtualizado);

        // Assert
        assertThat(resultado.getTipo()).isEqualTo(TipoUsuario.ADMIN);
        assertThat(resultado.getAtivo()).isFalse();
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Atualizar usuário: Mudar para username existente deve lançar exception")
    void atualizarUsuario_NovoUsernameJaExiste_DeveLancarException() {
        // Arrange
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setUsername("user1");

        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setUsername("admin"); // Username já existe

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.existsByUsername("admin")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.atualizarUsuario(1L, usuarioAtualizado);
        });

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Alterar senha: Com senha atual correta deve alterar")
    void alterarSenha_SenhaAtualCorreta_DeveAlterar() {
        // Arrange
        String senhaAtual = "senha123";
        String novaSenha = "novaSenha456";
        String senhaAtualHash = encoder.encode(senhaAtual);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("teste");
        usuario.setPassword(senhaAtualHash);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        usuarioService.alterarSenha(1L, senhaAtual, novaSenha);

        // Assert
        verify(usuarioRepository).save(any(Usuario.class));
        assertThat(usuario.getPassword()).isNotEqualTo(senhaAtualHash); // Senha mudou
    }

    @Test
    @DisplayName("Alterar senha: Com senha atual incorreta deve lançar exception")
    void alterarSenha_SenhaAtualIncorreta_DeveLancarException() {
        // Arrange
        String senhaAtualHash = encoder.encode("senha123");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setPassword(senhaAtualHash);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.alterarSenha(1L, "senhaErrada", "novaSenha");
        });

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // ==================== TESTES DE DELEÇÃO ====================

    @Test
    @DisplayName("Desativar usuário: Deve marcar como inativo")
    void desativarUsuario_DeveMarcaComoInativo() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setAtivo(true);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        usuarioService.desativarUsuario(1L);

        // Assert
        assertThat(usuario.getAtivo()).isFalse();
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Reativar usuário: Deve marcar como ativo")
    void reativarUsuario_DeveMarcaComoAtivo() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setAtivo(false);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        usuarioService.reativarUsuario(1L);

        // Assert
        assertThat(usuario.getAtivo()).isTrue();
        verify(usuarioRepository).save(usuario);
    }
}
