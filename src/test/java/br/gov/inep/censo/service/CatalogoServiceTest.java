package br.gov.inep.censo.service;

import br.gov.inep.censo.config.TestDatabaseConfig;
import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.model.OpcaoDominio;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Testes de integracao para leitura de catalogos de opcoes e campos de leiaute.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestDatabaseConfig.class)
public class CatalogoServiceTest {

    @Autowired
    private CatalogoService catalogoService;

    @Test
    public void deveCarregarCatalogoDeOpcoesELayout() throws Exception {
        List<OpcaoDominio> deficiencias = catalogoService.listarOpcoesPorCategoria(CategoriasOpcao.ALUNO_TIPO_DEFICIENCIA);
        Assert.assertTrue(deficiencias.size() >= 10);

        List<LayoutCampo> camposAluno = catalogoService.listarCamposModulo(ModulosLayout.ALUNO_41);
        List<LayoutCampo> camposCurso = catalogoService.listarCamposModulo(ModulosLayout.CURSO_21);
        List<LayoutCampo> camposCursoAluno = catalogoService.listarCamposModulo(ModulosLayout.ALUNO_42);

        Assert.assertTrue(camposAluno.size() >= 23);
        Assert.assertTrue(camposCurso.size() >= 67);
        Assert.assertTrue(camposCursoAluno.size() >= 72);
        Assert.assertEquals(Integer.valueOf(1), camposAluno.get(0).getNumeroCampo());
    }
}
