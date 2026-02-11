package br.gov.inep.censo.service;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.model.OpcaoDominio;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

/**
 * Testes de integracao para leitura de catalogos de opcoes e campos de leiaute.
 */
public class CatalogoServiceTest {

    @Test
    @Ignore("Requer Spring Context configurado (OpcaoDominioRepository, LayoutCampoRepository)")
    public void deveCarregarCatalogoDeOpcoesELayout() throws Exception {
        CatalogoService service = new CatalogoService();

        List<OpcaoDominio> deficiencias = service.listarOpcoesPorCategoria(CategoriasOpcao.ALUNO_TIPO_DEFICIENCIA);
        Assert.assertTrue(deficiencias.size() >= 10);

        List<LayoutCampo> camposAluno = service.listarCamposModulo(ModulosLayout.ALUNO_41);
        List<LayoutCampo> camposCurso = service.listarCamposModulo(ModulosLayout.CURSO_21);
        List<LayoutCampo> camposCursoAluno = service.listarCamposModulo(ModulosLayout.ALUNO_42);

        Assert.assertTrue(camposAluno.size() >= 23);
        Assert.assertTrue(camposCurso.size() >= 67);
        Assert.assertTrue(camposCursoAluno.size() >= 72);
        Assert.assertEquals(Integer.valueOf(1), camposAluno.get(0).getNumeroCampo());
    }
}
