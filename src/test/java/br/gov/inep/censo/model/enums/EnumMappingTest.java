package br.gov.inep.censo.model.enums;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testes de mapeamento dos enums de dominio.
 * Verifica que codigos e descricoes conhecidos existem e que
 * fromCodigo resolve corretamente (onde disponivel).
 */
public class EnumMappingTest {

    // ---- CorRacaEnum ----

    @Test
    public void corRacaEnum_temTodosOsValoresEsperados() {
        CorRacaEnum[] values = CorRacaEnum.values();
        assertEquals(7, values.length);
    }

    @Test
    public void corRacaEnum_brancaTemCodigo1() {
        assertEquals(1, CorRacaEnum.BRANCA.getCodigo());
    }

    @Test
    public void corRacaEnum_fromCodigo_encontraPreto() {
        CorRacaEnum resultado = CorRacaEnum.fromCodigo(2);
        assertNotNull(resultado);
        assertEquals(CorRacaEnum.PRETA, resultado);
    }

    @Test
    public void corRacaEnum_fromCodigo_codigoInexistenteRetornaNull() {
        assertNull(CorRacaEnum.fromCodigo(99));
    }

    // ---- EstadoEnum ----

    @Test
    public void estadoEnum_spTemCodigo35() {
        assertEquals(35, EstadoEnum.SP.getCodigo());
    }

    @Test
    public void estadoEnum_fromCodigo_encontraRj() {
        EstadoEnum resultado = EstadoEnum.fromCodigo(33);
        assertNotNull(resultado);
        assertEquals(EstadoEnum.RJ, resultado);
    }

    @Test
    public void estadoEnum_fromCodigo_codigoInexistenteRetornaNull() {
        assertNull(EstadoEnum.fromCodigo(0));
    }

    // ---- NacionalidadeEnum ----

    @Test
    public void nacionalidadeEnum_brasileiraNataTemCodigo1() {
        assertEquals(1, NacionalidadeEnum.BRASILEIRA_NATA.getCodigo());
    }

    // ---- NivelAcademicoEnum ----

    @Test
    public void nivelAcademicoEnum_graduacaoTemCodigoGRADUACAO() {
        assertEquals("GRADUACAO", NivelAcademicoEnum.GRADUACAO.getCodigo());
    }

    // ---- FormatoOfertaEnum ----

    @Test
    public void formatoOfertaEnum_presencialTemCodigoPRESENCIAL() {
        assertEquals("PRESENCIAL", FormatoOfertaEnum.PRESENCIAL.getCodigo());
    }
}
