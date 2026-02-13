package com.sea.desafio_backend.repository;

import com.sea.desafio_backend.model.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para Endereco
 * Relacionamento 1:1 com Cliente
 */
@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    /**
     * Busca endereço por ID do cliente
     * @param clienteId ID do cliente
     * @return Optional com endereço se encontrado
     */
    Optional<Endereco> findByClienteId(Long clienteId);

    /**
     * Verifica se cliente já tem endereço cadastrado
     * @param clienteId ID do cliente
     * @return true se existir
     */
    boolean existsByClienteId(Long clienteId);

    /**
     * Deleta endereço por ID do cliente
     * @param clienteId ID do cliente
     */
    void deleteByClienteId(Long clienteId);
}