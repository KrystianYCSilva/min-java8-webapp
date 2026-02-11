package br.gov.inep.censo.web.filter;

import br.gov.inep.censo.util.CsrfTokenUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro CSRF para operacoes mutaveis em /app/*.
 */
public class CsrfFilter implements Filter {

    public void init(FilterConfig filterConfig) {
        // Sem configuracao customizada.
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String method = httpRequest.getMethod();
        if (!"POST".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        if (!CsrfTokenUtil.isValid(httpRequest)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Token CSRF invalido.");
            return;
        }

        chain.doFilter(request, response);
    }

    public void destroy() {
        // Sem recursos para liberar.
    }
}

