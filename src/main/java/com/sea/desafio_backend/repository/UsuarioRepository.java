package com.sea.desafio_backend.repository;

import com.sea.desafio_backend.model.entity.Usuario;
import com.sea.desafio_backend.model.enums.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para Usuario
 * Métodos para operações no banco de dados
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuário por username
     * @param username Nome de usuário
     * @return Optional com usuário se encontrado
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Verifica se username já existe
     * @param username Nome de usuário
     * @return true se existir
     */
    boolean existsByUsername(String username);

    /**
     * Busca usuário por username e tipo
     * @param username Nome de usuário
     * @param tipo Tipo de usuário (ADMIN/PADRAO)
     * @return Optional com usuário se encontrado
     */
    Optional<Usuario> findByUsernameAndTipo(String username, TipoUsuario tipo);
}