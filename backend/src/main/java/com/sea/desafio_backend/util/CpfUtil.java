package com.sea.desafio_backend.util;

/**
 * Utilitário para validação e manipulação de CPF
 * Implementa o algoritmo oficial de validação com dígitos verificadores
 */
public class CpfUtil {

    /**
     * Remove máscara do CPF (mantém apenas números)
     * @param cpf CPF com ou sem máscara
     * @return CPF apenas com números
     */
    public static String removerMascara(String cpf) {
        if (cpf == null) {
            return "";
        }
        return cpf.replaceAll("[^0-9]", "");
    }

    /**
     * Aplica máscara no CPF (formato: 123.456.789-00)
     * @param cpf CPF sem máscara
     * @return CPF formatado
     */
    public static String aplicarMascara(String cpf) {
        String cpfLimpo = removerMascara(cpf);
        if (cpfLimpo.length() == 11) {
            return cpfLimpo.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        return cpf;
    }

    /**
     * Valida CPF usando algoritmo oficial com dígitos verificadores
     * 
     * Algoritmo:
     * 1. Remove máscara
     * 2. Verifica se tem 11 dígitos
     * 3. Verifica se não são todos iguais (111.111.111-11)
     * 4. Calcula primeiro dígito verificador
     * 5. Calcula segundo dígito verificador
     * 
     * @param cpf CPF com ou sem máscara
     * @return true se CPF é válido
     */
    public static boolean validar(String cpf) {
        String cpfLimpo = removerMascara(cpf);

        // Verifica tamanho
        if (cpfLimpo.length() != 11) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (ex: 111.111.111-11)
        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Calcula primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpfLimpo.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) {
            primeiroDigito = 0;
        }

        // Verifica primeiro dígito
        if (Character.getNumericValue(cpfLimpo.charAt(9)) != primeiroDigito) {
            return false;
        }

        // Calcula segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpfLimpo.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) {
            segundoDigito = 0;
        }

        // Verifica segundo dígito
        return Character.getNumericValue(cpfLimpo.charAt(10)) == segundoDigito;
    }

    /**
     * Valida e lança exceção se CPF inválido
     * @param cpf CPF a validar
     * @throws com.sea.desafio_backend.exception.CpfInvalidoException se inválido
     */
    public static void validarOuLancarExcecao(String cpf) {
        String cpfLimpo = removerMascara(cpf);
        
        if (cpfLimpo.length() != 11) {
            throw new com.sea.desafio_backend.exception.CpfInvalidoException(
                cpf, "deve conter 11 dígitos");
        }
        
        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            throw new com.sea.desafio_backend.exception.CpfInvalidoException(
                cpf, "dígitos não podem ser todos iguais");
        }
        
        if (!validar(cpf)) {
            throw new com.sea.desafio_backend.exception.CpfInvalidoException(
                cpf, "dígitos verificadores inválidos");
        }
    }
}
