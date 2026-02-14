package com.sea.desafio_backend.model.entity;

import com.sea.desafio_backend.model.entity.BaseEntity;
import com.sea.desafio_backend.model.entity.ClienteEmail;
import com.sea.desafio_backend.model.entity.Endereco;
import com.sea.desafio_backend.model.entity.Telefone;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes", indexes = {
    @Index(name = "idx_cliente_cpf", columnList = "cpf", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"endereco", "telefones", "emails"})
public class Cliente extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include private Long id;

    @NotBlank(message = "Nome é obrigatorio")
    @Size(min =3, max =100, message = "Nome deve ter entre3 e100 caracteres")
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
    private List<ClienteEmail> emails;

    {
        emails = new ArrayList<>();
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

    public void addEmail(ClienteEmail email) {
        emails.add(email);
        email.setCliente(this); // Vincula o cliente ao email
    }

    public void removeEmail(ClienteEmail email) {
        emails.remove(email);
        email.setCliente(null);
    }
}