package com.sea.desafio_backend.repository;

import com.sea.desafio_backend.model.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Cliente
 * Operações de acesso ao banco de dados de clientes
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Busca cliente por CPF
     * @param cpf CPF do cliente (sem máscara)
     * @return Optional com cliente se encontrado
     */
    Optional<Cliente> findByCpf(String cpf);

    /**
     * Verifica se CPF já existe no banco
     * @param cpf CPF a verificar
     * @return true se existir
     */
    boolean existsByCpf(String cpf);

    /**
     * Busca clientes por nome (ignora maiúsculas/minúsculas)
     * @param nome Nome ou parte do nome
     * @return Lista de clientes encontrados
     */
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca todos os clientes com seus relacionamentos
     * Evita N+1 queries carregando tudo de uma vez
     */
    @Query("SELECT DISTINCT c FROM Cliente c " +
            "LEFT JOIN FETCH c.endereco " +
            "LEFT JOIN FETCH c.telefones " +
            "LEFT JOIN FETCH c.emails")
    List<Cliente> findAllWithRelationships();

    /**
     * Busca cliente por ID com todos os relacionamentos
     * @param id ID do cliente
     * @return Optional com cliente e relacionamentos
     */
    @Query("SELECT c FROM Cliente c " +
            "LEFT JOIN FETCH c.endereco " +
            "LEFT JOIN FETCH c.telefones " +
            "LEFT JOIN FETCH c.emails " +
            "WHERE c.id = :id")
    Optional<Cliente> findByIdWithRelationships(@Param("id") Long id);
}