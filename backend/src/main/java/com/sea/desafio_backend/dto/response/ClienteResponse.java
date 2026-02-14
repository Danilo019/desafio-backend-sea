package com.sea.desafio_backend.dto.response;

import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.model.entity.ClienteEmail;
import com.sea.desafio_backend.model.entity.Endereco;
import com.sea.desafio_backend.model.entity.Telefone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO de resposta para Cliente
 * 
 * Retorna dados completos do cliente incluindo:
 * - Dados básicos (id, nome, cpf)
 * - Endereço completo
 * - Lista de telefones
 * - Lista de emails
 * - Datas de criação/atualização
 * 
 * Observações:
 * - CPF retorna COM máscara: 123.456.789-00
 * - CEP retorna COM máscara: 01001-000
 * - Telefone retorna COM máscara: (11) 98765-4321
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {

    private Long id;
    private String nome;
    private String cpf;
    private EnderecoResponse endereco;
    private List<TelefoneResponse> telefones;
    private List<EmailResponse> emails;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Converte Entity → DTO Response
     */
    public static ClienteResponse fromEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        return ClienteResponse.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .cpf(cliente.getCpf()) // CPF já vem com máscara da Entity
                .endereco(EnderecoResponse.fromEntity(cliente.getEndereco()))
                .telefones(cliente.getTelefones() != null ? 
                        cliente.getTelefones().stream()
                                .map(TelefoneResponse::fromEntity)
                                .collect(Collectors.toList()) : null)
                .emails(cliente.getEmails() != null ? 
                        cliente.getEmails().stream()
                                .map(EmailResponse::fromEntity)
                                .collect(Collectors.toList()) : null)
                .createdAt(cliente.getCreatedAt())
                .updatedAt(cliente.getUpdatedAt())
                .build();
    }

    /**
     * DTO interno para Endereço
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnderecoResponse {
        private Long id;
        private String cep;
        private String logradouro;
        private String complemento;
        private String bairro;
        private String cidade;
        private String uf;

        public static EnderecoResponse fromEntity(Endereco endereco) {
            if (endereco == null) {
                return null;
            }

            return EnderecoResponse.builder()
                    .id(endereco.getId())
                    .cep(aplicarMascaraCEP(endereco.getCep()))
                    .logradouro(endereco.getLogradouro())
                    .complemento(endereco.getComplemento())
                    .bairro(endereco.getBairro())
                    .cidade(endereco.getCidade())
                    .uf(endereco.getUf())
                    .build();
        }

        private static String aplicarMascaraCEP(String cep) {
            if (cep != null && cep.length() == 8) {
                return cep.replaceAll("(\\d{5})(\\d{3})", "$1-$2");
            }
            return cep;
        }
    }

    /**
     * DTO interno para Telefone
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TelefoneResponse {
        private Long id;
        private String numero;
        private String tipo;
        private Boolean principal;

        public static TelefoneResponse fromEntity(Telefone telefone) {
            if (telefone == null) {
                return null;
            }

            return TelefoneResponse.builder()
                    .id(telefone.getId())
                    .numero(aplicarMascaraTelefone(telefone.getNumero(), telefone.getTipo().name()))
                    .tipo(telefone.getTipo().name())
                    .principal(telefone.getPrincipal())
                    .build();
        }

        private static String aplicarMascaraTelefone(String numero, String tipo) {
            if (numero != null) {
                String numeroLimpo = numero.replaceAll("[^0-9]", "");
                if ("CELULAR".equals(tipo) && numeroLimpo.length() == 11) {
                    return numeroLimpo.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
                } else if (numeroLimpo.length() == 10) {
                    return numeroLimpo.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
                }
            }
            return numero;
        }
    }

    /**
     * DTO interno para Email
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailResponse {
        private Long id;
        private String enderecoEmail;
        private Boolean principal;

        public static EmailResponse fromEntity(ClienteEmail email) {
            if (email == null) {
                return null;
            }

            return EmailResponse.builder()
                    .id(email.getId())
                    .enderecoEmail(email.getEnderecoEmail())
                    .principal(email.getPrincipal())
                    .build();
        }
    }
}
