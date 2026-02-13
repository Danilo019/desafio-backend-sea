package com.sea.desafio_backend.model.entity;

import com.sea.desafio_backend.model.enums.TipoTelefone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * Entidade Telefone
 * Relacionamento N:1 com Cliente
 * Mínimo 1 telefone por cliente
 */
@Entity
@Table(name = "telefones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Telefone extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoTelefone tipo;

    @NotBlank(message = "Número do telefone é obrigatório")
    @Column(nullable = false, length = 11) // Persistido SEM máscara (máximo 11 dígitos: DDD + 9 dígitos)
    private String numero;

    @Column(nullable = false)
    private Boolean principal = false;
}
