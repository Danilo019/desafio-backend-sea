package com.sea.desafio_backend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
// Importante: javax.validation é usado no Spring Boot 2.x (Java 8).
// Se fosse Spring Boot 3, seria jakarta.validation.
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    // REGRA DO DESAFIO: Apenas letras, números e espaços
    @Pattern(regexp = "^[a-zA-Z0-9 áàâãéèêíïóôõöúçñÁÀÂÃÉÈÊÍÏÓÔÕÖÚÇÑ]+$", message = "Nome deve conter apenas letras e números")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    // REGRA: Persistido COM máscara (14 caracteres: 111.222.333-44)
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    // CascadeType.ALL garante que ao salvar Cliente, salva o Endereço junto
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Endereco endereco;

    // REGRA: Pelo menos um telefone
    @NotEmpty(message = "Pelo menos um telefone deve ser cadastrado")
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Telefone> telefones = new ArrayList<>();

    // REGRA: Pelo menos um e-mail
    @NotEmpty(message = "Pelo menos um e-mail deve ser cadastrado")
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Email> emails = new ArrayList<>();

    // Auditoria (Excelente adição sua!)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /* --- Métodos Helper (Essenciais para @OneToMany bidirecional) --- */

    public void setEndereco(Endereco endereco) {
        if (endereco != null) {
            endereco.setCliente(this); // Vincula o cliente ao endereço
        }
        this.endereco = endereco;
    }

    public void addTelefone(Telefone telefone) {
        telefones.add(telefone);
        telefone.setCliente(this); // Vincula o cliente ao telefone
    }

    public void removeTelefone(Telefone telefone) {
        telefones.remove(telefone);
        telefone.setCliente(null);
    }

    public void addEmail(Email email) {
        emails.add(email);
        email.setCliente(this); // Vincula o cliente ao email
    }

    public void removeEmail(Email email) {
        emails.remove(email);
        email.setCliente(null);
    }
}