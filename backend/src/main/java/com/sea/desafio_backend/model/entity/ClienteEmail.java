package com.sea.desafio_backend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "emails", indexes = {
    @Index(name = "idx_email_cliente_id", columnList = "cliente_id"),
    @Index(name = "idx_email_endereco", columnList = "enderecoEmail"),
    @Index(name = "idx_email_principal", columnList = "cliente_id, principal")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode(callSuper = false)
public class ClienteEmail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotBlank(message = "Email é obrigatório")
    @javax.validation.constraints.Email(message = "Email inválido")
    @Column(nullable = false, length = 100)
    private String enderecoEmail;
    @Column(nullable = false)
    private Boolean principal = false;
}