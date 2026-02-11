package com.sea.desafio_backend.repository;

import com.sea.desafio_backend.model.entity.Telefone;
import com.sea.desafio_backend.model.enums.TipoTelefone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Telefone
 * Relacionamento N:1 com Cliente (múltiplos telefones por cliente)
 */
@Repository
public interface TelefoneRepository extends JpaRepository<Telefone, Long> {

    /**
     * Busca todos os telefones de um cliente
     * @param clienteId ID do cliente
     * @return Lista de telefones
     */
    List<Telefone> findByClienteId(Long clienteId);

    /**
     * Busca telefones por cliente e tipo
     * @param clienteId ID do cliente
     * @param tipo Tipo do telefone
     * @return Lista de telefones do tipo especificado
     */
    List<Telefone> findByClienteIdAndTipo(Long clienteId, TipoTelefone tipo);

    /**
     * Busca telefone principal do cliente
     * @param clienteId ID do cliente
     * @param principal true para buscar o principal
     * @return Optional com telefone principal
     */
    Optional<Telefone> findByClienteIdAndPrincipal(Long clienteId, Boolean principal);

    /**
     * Conta quantos telefones o cliente possui
     * @param clienteId ID do cliente
     * @return Número de telefones
     */
    long countByClienteId(Long clienteId);

    /**
     * Deleta todos os telefones de um cliente
     * @param clienteId ID do cliente
     */
    void deleteByClienteId(Long clienteId);

    /**
     * Verifica se existe telefone com o número
     * @param numero Número do telefone
     * @param clienteId ID do cliente
     * @return true se existir
     */
    boolean existsByNumeroAndClienteId(String numero, Long clienteId);
}