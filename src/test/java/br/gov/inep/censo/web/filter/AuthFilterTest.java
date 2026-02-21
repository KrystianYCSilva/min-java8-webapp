package br.gov.inep.censo.web.filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

/**
 * Testes unitarios de AuthFilter.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @Mock
    private FilterConfig filterConfig;
    @Mock
    private HttpSession session;

    private AuthFilter filter;

    @Before
    public void setUp() throws Exception {
        filter = new AuthFilter();
        when(filterConfig.getInitParameter("loginPath")).thenReturn("/login.zul");
        filter.init(filterConfig);
    }

    @Test
    public void doFilter_semSessao_redirecionaParaLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login.zul");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void doFilter_sessaoSemUsuario_redirecionaParaLogin() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuarioLogado")).thenReturn(null);
        when(request.getContextPath()).thenReturn("");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login.zul");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void doFilter_sessaoComUsuario_continuaCadeia() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuarioLogado")).thenReturn("admin");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void doFilter_semLoginPathConfigurado_usaLoginZulPadrao() throws Exception {
        when(filterConfig.getInitParameter("loginPath")).thenReturn(null);
        filter.init(filterConfig);

        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/app");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/app/login.zul");
    }
}
