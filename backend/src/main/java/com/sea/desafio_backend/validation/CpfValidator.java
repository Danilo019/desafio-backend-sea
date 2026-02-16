package com.sea.desafio_backend.validation;

import com.sea.desafio_backend.util.CpfUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validador para a annotation @CPF
 * Utiliza CpfUtil para validação com dígitos verificadores
 */
public class CpfValidator implements ConstraintValidator<CPF, String> {

    @Override
    public void initialize(CPF constraintAnnotation) {
        // Nenhuma inicialização necessária
    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        // Null/empty é tratado por @NotNull/@NotBlank
        if (cpf == null || cpf.trim().isEmpty()) {
            return true;
        }

        // Valida usando utilitário
        return CpfUtil.validar(cpf);
    }
}
