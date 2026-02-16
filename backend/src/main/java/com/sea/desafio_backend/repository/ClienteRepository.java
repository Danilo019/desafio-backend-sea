package com.sea.desafio_backend.repository;

import com.sea.desafio_backend.model.entity.Cliente;
import org.springframework.data.jpa.repository.EntityGraph;
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
     * Busca todos os clientes com endereço
     * NOTA: Telefones e Emails devem ser carregados separadamente para evitar MultipleBagFetchException
     * Hibernate não permite JOIN FETCH de múltiplas coleções List simultaneamente
     */
    @Query("SELECT DISTINCT c FROM Cliente c LEFT JOIN FETCH c.endereco")
    List<Cliente> findAllWithEndereco();

    /**
     * Busca cliente por ID com endereço
     * @param id ID do cliente
     * @return Optional com cliente e endereço
     */
    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.endereco WHERE c.id = :id")
    Optional<Cliente> findByIdWithEndereco(@Param("id") Long id);

    /**
     * Busca cliente por ID com telefones
     * @param id ID do cliente
     * @return Optional com cliente e telefones
     */
    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.telefones WHERE c.id = :id")
    Optional<Cliente> findByIdWithTelefones(@Param("id") Long id);

    /**
     * Busca cliente por ID com emails
     * @param id ID do cliente
     * @return Optional com cliente e emails
     */
    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.emails WHERE c.id = :id")
    Optional<Cliente> findByIdWithEmails(@Param("id") Long id);

    /**
     * Busca cliente por ID com todas as dependências usando EntityGraph
     * Mais eficiente que múltiplas queries separadas
     * @param id ID do cliente
     * @return Optional com cliente completo
     */
    @EntityGraph(attributePaths = {"endereco", "telefones", "emails"})
    @Query("SELECT c FROM Cliente c WHERE c.id = :id")
    Optional<Cliente> findByIdWithAllDetails(@Param("id") Long id);

    /**
     * Lista todos os clientes com todas as dependências usando EntityGraph
     * Evita problema N+1 carregando tudo em uma query
     * @return Lista de clientes completos
     */
    @EntityGraph(attributePaths = {"endereco", "telefones", "emails"})
    @Query("SELECT DISTINCT c FROM Cliente c")
    List<Cliente> findAllWithDetails();
}