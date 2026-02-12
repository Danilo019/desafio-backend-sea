package com.sea.desafio_backend.service;

import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.Usuario;
import com.sea.desafio_backend.model.enums.TipoUsuario;
import com.sea.desafio_backend.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

/**
 * Service para gerenciamento de Usuários
 * Responsável por autenticação, CRUD e criptografia de senhas
 */
@Service
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor Injection (melhor prática)
     * @param usuarioRepository Repository de usuários
     */
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Inicializa usuários padrão do sistema ao startar a aplicação
     * Cria automaticamente:
     * - Usuario ADMIN (username: admin, senha: 123qwe!@#)
     * - Usuario PADRAO (username: padrao, senha: 123qwe123)
     */
    @PostConstruct
    public void inicializarUsuariosPadrao() {
        log.info("Verificando usuários padrão do sistema...");
        
        // Criar usuário ADMIN
        if (!existeUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword("123qwe!@#");
            admin.setTipo(TipoUsuario.ADMIN);
            criarUsuario(admin);
            log.info("✅ Usuário ADMIN criado - Username: admin | Senha: 123qwe!@#");
        } else {
            log.info("Usuário ADMIN já existe");
        }
        
        // Criar usuário PADRAO
        if (!existeUsername("padrao")) {
            Usuario padrao = new Usuario();
            padrao.setUsername("padrao");
            padrao.setPassword("123qwe123");
            padrao.setTipo(TipoUsuario.PADRAO);
            criarUsuario(padrao);
            log.info("✅ Usuário PADRAO criado - Username: padrao | Senha: 123qwe123");
        } else {
            log.info("Usuário PADRAO já existe");
        }
        
        log.info("Usuários padrão inicializados com sucesso!");
    }

    // ==================== CRIAR USUÁRIO ====================
    

    /**
     * Cria um novo usuário com senha criptografada
     * 
     * @param usuario Usuário a ser criado
     * @return Usuário salvo no banco
     * @throws IllegalArgumentException se username já existe
     */
    @Transactional
    public Usuario criarUsuario(Usuario usuario) {
        log.info("Criando novo usuário: {}", usuario.getUsername());
        
        // Valida se username já existe
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            log.warn("Username já existe: {}", usuario.getUsername());
            throw new IllegalArgumentException("Username já existe: " + usuario.getUsername());
        }
        
        // Criptografa a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(senhaCriptografada);
        
        // Define como ativo por padrão
        if (usuario.getAtivo() == null) {
            usuario.setAtivo(true);
        }
        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        log.info("Usuário criado com sucesso. ID: {}", usuarioSalvo.getId());
        
        return usuarioSalvo;
    }

    // ==================== BUSCAR USUÁRIOS ====================

    /**
     * Busca usuário por ID
     * 
     * @param id ID do usuário
     * @return Usuário encontrado
     * @throws ResourceNotFoundException se não encontrado
     */
    public Usuario buscarPorId(Long id) {
        log.info("Buscando usuário por ID: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    /**
     * Busca usuário por username
     * 
     * @param username Username do usuário
     * @return Usuário encontrado
     * @throws ResourceNotFoundException se não encontrado
     */
    public Usuario buscarPorUsername(String username) {
        log.info("Buscando usuário por username: {}", username);
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));
    }

    /**
     * Lista todos os usuários
     * 
     * @return Lista de todos os usuários
     */
    public List<Usuario> listarTodos() {
        log.info("Listando todos os usuários");
        return usuarioRepository.findAll();
    }

    /**
     * Busca usuários por tipo (ADMIN ou PADRAO)
     * 
     * @param tipo Tipo do usuário
     * @return Lista de usuários do tipo especificado
     */
    public List<Usuario> buscarPorTipo(TipoUsuario tipo) {
        log.info("Buscando usuários do tipo: {}", tipo);
        return usuarioRepository.findByTipo(tipo);
    }

    // ==================== VALIDAÇÃO E AUTENTICAÇÃO ====================

    /**
     * Valida credenciais de login
     * Compara senha fornecida com hash armazenado no banco
     * 
     * @param username Username do usuário
     * @param senhaPlainText Senha em texto plano (não criptografada)
     * @return true se credenciais válidas, false caso contrário
     */
    public boolean validarCredenciais(String username, String senhaPlainText) {
        log.info("Validando credenciais para username: {}", username);
        
        try {
            Usuario usuario = buscarPorUsername(username);
            
            // Verifica se usuário está ativo
            if (!usuario.getAtivo()) {
                log.warn("Tentativa de login com usuário inativo: {}", username);
                return false;
            }
            
            // Compara senha fornecida com hash do banco
            boolean senhaValida = passwordEncoder.matches(senhaPlainText, usuario.getPassword());
            
            if (senhaValida) {
                log.info("Credenciais válidas para: {}", username);
            } else {
                log.warn("Senha inválida para: {}", username);
            }
            
            return senhaValida;
            
        } catch (ResourceNotFoundException e) {
            log.warn("Usuário não encontrado: {}", username);
            return false;
        }
    }

    /**
     * Verifica se username já existe no banco
     * 
     * @param username Username a verificar
     * @return true se existe, false caso contrário
     */
    public boolean existeUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    // ==================== ATUALIZAR USUÁRIO ====================

    /**
     * Atualiza dados do usuário
     * Não permite alterar a senha por este método (use alterarSenha)
     * 
     * @param id ID do usuário a atualizar
     * @param usuarioAtualizado Dados atualizados
     * @return Usuário atualizado
     * @throws ResourceNotFoundException se usuário não existe
     * @throws IllegalArgumentException se tentar alterar username para um já existente
     */
    @Transactional
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        log.info("Atualizando usuário ID: {}", id);
        
        Usuario usuarioExistente = buscarPorId(id);
        
        // Verifica se está tentando mudar o username
        if (!usuarioExistente.getUsername().equals(usuarioAtualizado.getUsername())) {
            // Valida se novo username já existe
            if (usuarioRepository.existsByUsername(usuarioAtualizado.getUsername())) {
                throw new IllegalArgumentException("Username já existe: " + usuarioAtualizado.getUsername());
            }
            usuarioExistente.setUsername(usuarioAtualizado.getUsername());
        }
        
        // Atualiza apenas campos permitidos (não altera senha aqui)
        usuarioExistente.setTipo(usuarioAtualizado.getTipo());
        usuarioExistente.setAtivo(usuarioAtualizado.getAtivo());
        
        Usuario usuarioSalvo = usuarioRepository.save(usuarioExistente);
        log.info("Usuário atualizado com sucesso. ID: {}", id);
        
        return usuarioSalvo;
    }

    /**
     * Altera senha do usuário
     * 
     * @param id ID do usuário
     * @param senhaAtual Senha atual (para validação)
     * @param novaSenha Nova senha em texto plano
     * @throws ResourceNotFoundException se usuário não existe
     * @throws IllegalArgumentException se senha atual está incorreta
     */
    @Transactional
    public void alterarSenha(Long id, String senhaAtual, String novaSenha) {
        log.info("Alterando senha do usuário ID: {}", id);
        
        Usuario usuario = buscarPorId(id);
        
        // Valida senha atual
        if (!passwordEncoder.matches(senhaAtual, usuario.getPassword())) {
            log.warn("Senha atual incorreta para usuário ID: {}", id);
            throw new IllegalArgumentException("Senha atual incorreta");
        }
        
        // Criptografa e salva nova senha
        String novaSenhaCriptografada = passwordEncoder.encode(novaSenha);
        usuario.setPassword(novaSenhaCriptografada);
        
        usuarioRepository.save(usuario);
        log.info("Senha alterada com sucesso para usuário ID: {}", id);
    }

    // ==================== DELETAR USUÁRIO ====================

    /**
     * Deleta usuário (exclusão física)
     * 
     * @param id ID do usuário a deletar
     * @throws ResourceNotFoundException se usuário não existe
     */
    @Transactional
    public void deletarUsuario(Long id) {
        log.info("Deletando usuário ID: {}", id);
        
        // Verifica se usuário existe
        buscarPorId(id);
        
        usuarioRepository.deleteById(id);
        log.info("Usuário deletado com sucesso. ID: {}", id);
    }

    /**
     * Desativa usuário (exclusão lógica - mantém no banco)
     * Melhor prática do que deletar fisicamente
     * 
     * @param id ID do usuário a desativar
     * @throws ResourceNotFoundException se usuário não existe
     */
    @Transactional
    public void desativarUsuario(Long id) {
        log.info("Desativando usuário ID: {}", id);
        
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        
        usuarioRepository.save(usuario);
        log.info("Usuário desativado com sucesso. ID: {}", id);
    }

    /**
     * Reativa usuário previamente desativado
     * 
     * @param id ID do usuário a reativar
     * @throws ResourceNotFoundException se usuário não existe
     */
    @Transactional
    public void reativarUsuario(Long id) {
        log.info("Reativando usuário ID: {}", id);
        
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(true);
        
        usuarioRepository.save(usuario);
        log.info("Usuário reativado com sucesso. ID: {}", id);
    }
}
