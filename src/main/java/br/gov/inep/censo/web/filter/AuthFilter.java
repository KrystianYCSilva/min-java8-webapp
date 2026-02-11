package br.gov.inep.censo.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthFilter implements Filter {

    private String loginPath;

    public void init(FilterConfig filterConfig) throws ServletException {
        loginPath = filterConfig.getInitParameter("loginPath");
        if (loginPath == null || loginPath.trim().length() == 0) {
            loginPath = "/login.zul";
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        boolean autenticado = session != null && session.getAttribute("usuarioLogado") != null;
        if (!autenticado) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + loginPath);
            return;
        }

        chain.doFilter(request, response);
    }

    public void destroy() {
        // Nada a destruir
    }
}
