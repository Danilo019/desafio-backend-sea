package com.sea.desafio_backend.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation customizada para validação de CPF
 * Valida formato e dígitos verificadores usando algoritmo oficial
 * 
 * Uso:
 * <pre>
 * public class ClienteRequest {
 *     &#64;CPF
 *     private String cpf;
 * }
 * </pre>
 */
@Documented
@Constraint(validatedBy = CpfValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CPF {
    
    String message() default "CPF inválido";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
