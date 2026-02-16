package com.sea.desafio_backend.exception;

import com.sea.desafio_backend.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * Handler global de exceções para a API REST
 * 
 * Intercepta e trata todas as exceções lançadas pelos Controllers,
 * retornando respostas HTTP padronizadas no formato ErrorResponse.
 * 
 * Erros tratados:
 * - ResourceNotFoundException (404 Not Found)
 * - CepNotFoundException (404 Not Found)
 * - CpfJaCadastradoException (409 Conflict)
 * - EmailJaCadastradoException (409 Conflict)
 * - CpfInvalidoException (400 Bad Request)
 * - DadosMinimosException (400 Bad Request)
 * - IllegalArgumentException (400 Bad Request) - Regras de negócio
 * - MethodArgumentNotValidException (400 Bad Request) - Validações @Valid
 * - Exception genérica (500 Internal Server Error)
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Trata exceções de recurso não encontrado (404)
     * 
     * Exemplo: Cliente com ID 999 não existe
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            WebRequest request) {
        
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Trata exceções de CEP não encontrado (404)
     * 
     * Exemplo: CEP inválido ou não existe na base ViaCEP
     */
    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCepNotFound(
            CepNotFoundException ex,
            WebRequest request) {
        
        log.warn("CEP não encontrado: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Trata exceções de CPF já cadastrado (409 Conflict)
     * 
     * Exemplo: Tentativa de cadastrar CPF duplicado
     */
    @ExceptionHandler(CpfJaCadastradoException.class)
    public ResponseEntity<ErrorResponse> handleCpfJaCadastrado(
            CpfJaCadastradoException ex,
            WebRequest request) {
        
        log.warn("CPF já cadastrado: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Trata exceções de Email já cadastrado (409 Conflict)
     * 
     * Exemplo: Tentativa de cadastrar email duplicado
     */
    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErrorResponse> handleEmailJaCadastrado(
            EmailJaCadastradoException ex,
            WebRequest request) {
        
        log.warn("Email já cadastrado: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Trata exceções de CPF inválido (400 Bad Request)
     * 
     * Exemplo: CPF com dígitos verificadores incorretos
     */
    @ExceptionHandler(CpfInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleCpfInvalido(
            CpfInvalidoException ex,
            WebRequest request) {
        
        log.warn("CPF inválido: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções de dados mínimos não fornecidos (400 Bad Request)
     * 
     * Exemplo: Cliente sem telefone ou sem email
     */
    @ExceptionHandler(DadosMinimosException.class)
    public ResponseEntity<ErrorResponse> handleDadosMinimos(
            DadosMinimosException ex,
            WebRequest request) {
        
        log.warn("Dados mínimos não fornecidos: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções de regras de negócio genéricas (400)
     * 
     * Exemplo: Validações de negócio não cobertas pelas exceções específicas
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {
        
        log.warn("Erro de validação de negócio: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções de validação de Bean Validation (400)
     * 
     * Exemplo: @NotBlank, @Email, @Size falharam
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        log.warn("Erro de validação de campos: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Erro de validação nos campos enviados",
            request.getDescription(false).replace("uri=", "")
        );
        
        // Adiciona todos os erros de validação
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.addValidationError(
                fieldError.getField(),
                fieldError.getDefaultMessage()
            );
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções genéricas não mapeadas (500)
     * 
     * Fallback para erros inesperados
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request) {
        
        log.error("Erro interno não tratado: ", ex);
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "Erro interno do servidor. Por favor, contate o administrador.",
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
