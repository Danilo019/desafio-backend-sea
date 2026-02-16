package com.sea.desafio_backend.util;

import com.sea.desafio_backend.exception.CpfInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para CpfUtil
 * Valida algoritmo de dígitos verificadores do CPF
 */
@DisplayName("CpfUtil - Testes de Validação Robusta")
class CpfUtilTest {

    // ==================== TESTES DE VALIDAÇÃO ====================

    @Test
    @DisplayName("Deve validar CPF correto com máscara")
    void validar_ComCpfValidoComMascara_DeveRetornarTrue() {
        // CPFs válidos conhecidos
        assertTrue(CpfUtil.validar("123.456.789-09"));
        assertTrue(CpfUtil.validar("111.444.777-35"));
        assertTrue(CpfUtil.validar("012.345.678-90"));
    }

    @Test
    @DisplayName("Deve validar CPF correto sem máscara")
    void validar_ComCpfValidoSemMascara_DeveRetornarTrue() {
        assertTrue(CpfUtil.validar("12345678909"));
        assertTrue(CpfUtil.validar("11144477735"));
        assertTrue(CpfUtil.validar("01234567890"));
    }

    @Test
    @DisplayName("Deve invalidar CPF com dígitos verificadores incorretos")
    void validar_ComDigitosVerificadoresIncorretos_DeveRetornarFalse() {
        // CPF com dígitos errados (correto seria 09, não 00)
        assertFalse(CpfUtil.validar("123.456.789-00"));
        assertFalse(CpfUtil.validar("12345678900"));
        
        // Outro CPF inválido
        assertFalse(CpfUtil.validar("111.444.777-00"));
    }

    @Test
    @DisplayName("Deve invalidar CPF com todos dígitos iguais")
    void validar_ComDigitosIguais_DeveRetornarFalse() {
        assertFalse(CpfUtil.validar("111.111.111-11"));
        assertFalse(CpfUtil.validar("00000000000"));
        assertFalse(CpfUtil.validar("999.999.999-99"));
    }

    @Test
    @DisplayName("Deve invalidar CPF com tamanho incorreto")
    void validar_ComTamanhoIncorreto_DeveRetornarFalse() {
        assertFalse(CpfUtil.validar("123"));
        assertFalse(CpfUtil.validar("123.456.789"));
        assertFalse(CpfUtil.validar(""));
    }

    @Test
    @DisplayName("Deve invalidar CPF nulo")
    void validar_ComCpfNulo_DeveRetornarFalse() {
        assertFalse(CpfUtil.validar(null));
    }

    // ==================== TESTES DE MÁSCARA ====================

    @Test
    @DisplayName("Deve aplicar máscara corretamente")
    void aplicarMascara_ComCpfValido_DeveFormatarCorretamente() {
        assertEquals("123.456.789-09", CpfUtil.aplicarMascara("12345678909"));
        assertEquals("111.444.777-35", CpfUtil.aplicarMascara("11144477735"));
    }

    @Test
    @DisplayName("Deve remover máscara corretamente")
    void removerMascara_ComCpfFormatado_DeveRemoverFormatacao() {
        assertEquals("12345678909", CpfUtil.removerMascara("123.456.789-09"));
        assertEquals("11144477735", CpfUtil.removerMascara("111.444.777-35"));
    }

    @Test
    @DisplayName("Deve aplicar máscara idempotente")
    void aplicarMascara_ComCpfJaFormatado_DeveManter() {
        String cpfFormatado = "123.456.789-09";
        assertEquals(cpfFormatado, CpfUtil.aplicarMascara(cpfFormatado));
    }

    @Test
    @DisplayName("Deve retornar vazio ao remover máscara de CPF nulo")
    void removerMascara_ComCpfNulo_DeveRetornarVazio() {
        assertEquals("", CpfUtil.removerMascara(null));
    }

    // ==================== TESTES DE VALIDAÇÃO COM EXCEÇÃO ====================

    @Test
    @DisplayName("Deve lançar exceção para CPF com tamanho inválido")
    void validarOuLancarExcecao_ComTamanhoInvalido_DeveLancarExcecao() {
        CpfInvalidoException exception = assertThrows(
            CpfInvalidoException.class,
            () -> CpfUtil.validarOuLancarExcecao("123")
        );
        assertTrue(exception.getMessage().contains("11 dígitos"));
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF com dígitos iguais")
    void validarOuLancarExcecao_ComDigitosIguais_DeveLancarExcecao() {
        CpfInvalidoException exception = assertThrows(
            CpfInvalidoException.class,
            () -> CpfUtil.validarOuLancarExcecao("111.111.111-11")
        );
        assertTrue(exception.getMessage().contains("todos iguais"));
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF com dígitos verificadores incorretos")
    void validarOuLancarExcecao_ComDigitosVerificadoresIncorretos_DeveLancarExcecao() {
        CpfInvalidoException exception = assertThrows(
            CpfInvalidoException.class,
            () -> CpfUtil.validarOuLancarExcecao("123.456.789-00")
        );
        assertTrue(exception.getMessage().contains("verificadores inválidos"));
    }

    @Test
    @DisplayName("Não deve lançar exceção para CPF válido")
    void validarOuLancarExcecao_ComCpfValido_NaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> CpfUtil.validarOuLancarExcecao("123.456.789-09"));
        assertDoesNotThrow(() -> CpfUtil.validarOuLancarExcecao("11144477735"));
    }

    // ==================== TESTES DE CASOS EXTREMOS ====================

    @Test
    @DisplayName("Deve validar CPF iniciando com zeros")
    void validar_ComCpfIniciandoComZeros_DeveValidarCorretamente() {
        // CPF válido: 012.345.678-90
        assertTrue(CpfUtil.validar("01234567890"));
        assertTrue(CpfUtil.validar("012.345.678-90"));
    }

    @Test
    @DisplayName("Deve invalidar CPF com caracteres não numéricos além dos separadores")
    void validar_ComCaracteresInvalidos_DeveInvalidar() {
        assertFalse(CpfUtil.validar("123.456.789-0a"));
        assertFalse(CpfUtil.validar("abc.def.ghi-jk"));
    }
}
