package com.sea.desafio_backend.repository;

import com.sea.desafio_backend.model.entity.ClienteEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Email
 * Relacionamento N:1 com Cliente (múltiplos emails por cliente)
 */
@Repository
public interface EmailRepository extends JpaRepository<ClienteEmail, Long> {

    /**
     * Busca todos os emails de um cliente
     * @param clienteId ID do cliente
     * @return Lista de emails
     */
    List<ClienteEmail> findByClienteId(Long clienteId);

    /**
     * Busca email principal do cliente
     * @param clienteId ID do cliente
     * @param principal true para buscar o principal
     * @return Optional com email principal
     */
    Optional<ClienteEmail> findByClienteIdAndPrincipal(Long clienteId, Boolean principal);

    /**
     * Conta quantos emails o cliente possui
     * @param clienteId ID do cliente
     * @return Número de emails
     */
    long countByClienteId(Long clienteId);

    /**
     * Verifica se email já existe para o cliente
     * @param enderecoEmail Endereço de email
     * @param clienteId ID do cliente
     * @return true se existir
     */
    boolean existsByEnderecoEmailAndClienteId(String enderecoEmail, Long clienteId);

    /**
     * Deleta todos os emails de um cliente
     * @param clienteId ID do cliente
     */
    void deleteByClienteId(Long clienteId);
}