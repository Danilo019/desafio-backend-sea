package com.sea.desafio_backend.repository;

import com.sea.desafio_backend.model.entity.ClienteEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    /**
     * ✅ PERFORMANCE: Busca email específico de forma otimizada (1 query)
     * Usado para validação de duplicidade sem carregar todos os emails
     * @param clienteId ID do cliente
     * @param enderecoEmail Endereço de email
     * @return Optional com email encontrado
     */
    Optional<ClienteEmail> findByClienteIdAndEnderecoEmail(Long clienteId, String enderecoEmail);

    /**
     * ✅ PERFORMANCE: Desmarca todos os emails principais em 1 única query UPDATE
     * Evita N+1 Problem (buscar todos + loop de saves)
     * @param clienteId ID do cliente
     */
    @Modifying
    @Query("UPDATE ClienteEmail e SET e.principal = false WHERE e.cliente.id = :clienteId")
    void desmarcarTodosPrincipaisPorCliente(@Param("clienteId") Long clienteId);
}