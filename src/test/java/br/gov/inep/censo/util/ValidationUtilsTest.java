package br.gov.inep.censo.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testes unitarios de ValidationUtils.
 */
public class ValidationUtilsTest {

    // ---- isNumeric ----

    @Test
    public void isNumeric_soDigitos_retornaTrue() {
        assertTrue(ValidationUtils.isNumeric("12345"));
    }

    @Test
    public void isNumeric_comLetra_retornaFalse() {
        assertFalse(ValidationUtils.isNumeric("123a5"));
    }

    @Test
    public void isNumeric_nulo_retornaFalse() {
        assertFalse(ValidationUtils.isNumeric(null));
    }

    @Test
    public void isNumeric_vazio_retornaFalse() {
        assertFalse(ValidationUtils.isNumeric(""));
    }

    @Test
    public void isNumeric_espacos_retornaFalse() {
        assertFalse(ValidationUtils.isNumeric("  "));
    }

    // ---- hasExactLength ----

    @Test
    public void hasExactLength_tamanhoCorreto_retornaTrue() {
        assertTrue(ValidationUtils.hasExactLength("abc", 3));
    }

    @Test
    public void hasExactLength_tamanhoErrado_retornaFalse() {
        assertFalse(ValidationUtils.hasExactLength("abc", 4));
    }

    @Test
    public void hasExactLength_nulo_retornaFalse() {
        assertFalse(ValidationUtils.hasExactLength(null, 3));
    }

    // ---- isCpfFormatoValido ----

    @Test
    public void isCpfFormatoValido_onzeDigitos_retornaTrue() {
        assertTrue(ValidationUtils.isCpfFormatoValido("12345678901"));
    }

    @Test
    public void isCpfFormatoValido_menosDigitos_retornaFalse() {
        assertFalse(ValidationUtils.isCpfFormatoValido("1234567890"));
    }

    @Test
    public void isCpfFormatoValido_comPontuacao_retornaFalse() {
        assertFalse(ValidationUtils.isCpfFormatoValido("123.456.789-01"));
    }

    @Test
    public void isCpfFormatoValido_nulo_retornaFalse() {
        assertFalse(ValidationUtils.isCpfFormatoValido(null));
    }

    // ---- isPeriodoReferenciaValido ----

    @Test
    public void isPeriodoReferenciaValido_anoValido_retornaTrue() {
        assertTrue(ValidationUtils.isPeriodoReferenciaValido("2025"));
    }

    @Test
    public void isPeriodoReferenciaValido_anoForaFaixa_retornaFalse() {
        assertFalse(ValidationUtils.isPeriodoReferenciaValido("1800"));
        assertFalse(ValidationUtils.isPeriodoReferenciaValido("2100"));
    }

    @Test
    public void isPeriodoReferenciaValido_naoNumerico_retornaFalse() {
        assertFalse(ValidationUtils.isPeriodoReferenciaValido("abcd"));
    }

    @Test
    public void isPeriodoReferenciaValido_nulo_retornaFalse() {
        assertFalse(ValidationUtils.isPeriodoReferenciaValido(null));
    }

    // ---- isSemestreValido ----

    @Test
    public void isSemestreValido_primeiroSemestre_retornaTrue() {
        assertTrue(ValidationUtils.isSemestreValido("012025"));
    }

    @Test
    public void isSemestreValido_segundoSemestre_retornaTrue() {
        assertTrue(ValidationUtils.isSemestreValido("022025"));
    }

    @Test
    public void isSemestreValido_terceiroSemestre_retornaFalse() {
        assertFalse(ValidationUtils.isSemestreValido("032025"));
    }

    @Test
    public void isSemestreValido_tamanhoErrado_retornaFalse() {
        assertFalse(ValidationUtils.isSemestreValido("012025X"));
    }

    @Test
    public void isSemestreValido_nulo_retornaFalse() {
        assertFalse(ValidationUtils.isSemestreValido(null));
    }
}
