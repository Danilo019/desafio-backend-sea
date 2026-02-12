package com.sea.desafio_backend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "emails")
@Data
@NoArgsConstructor
@AllArgsConstructor
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