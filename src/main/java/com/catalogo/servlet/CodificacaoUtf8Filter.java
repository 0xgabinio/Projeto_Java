package com.catalogo.servlet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Garante que toda requisição/resposta use UTF-8.
 * <p>
 * Por padrão, servidores de aplicação (incluindo o Tomcat) decodificam parâmetros de
 * formulário ({@code request.getParameter()}) usando ISO-8859-1 quando nenhuma codificação é
 * explicitada — o que corrompe caracteres acentuados (ex.: "Ficção Científica" vira
 * "FicÃ§Ã£o CientÃ­fica") antes mesmo de chegar à validação ou ao banco de dados. Este filtro
 * roda antes de qualquer Servlet e força UTF-8 tanto na leitura da requisição quanto na
 * resposta, independentemente da configuração do conector do servidor.
 */
@WebFilter("/*")
public class CodificacaoUtf8Filter implements Filter {

    private static final String UTF_8 = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) {
        // Sem configuração necessária.
    }

    /**
     * Força UTF-8 na requisição e na resposta antes de repassar para o restante da cadeia de
     * filtros/Servlets.
     *
     * @param request  requisição a ter a codificação de caracteres ajustada
     * @param response resposta a ter a codificação de caracteres ajustada
     * @param chain    cadeia de filtros a continuar após o ajuste
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // setCharacterEncoding() só tem efeito se chamado ANTES de qualquer getParameter()/
        // getParameterMap() — por isso este filtro precisa ser o primeiro na cadeia.
        request.setCharacterEncoding(UTF_8);
        response.setCharacterEncoding(UTF_8);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Sem recursos para liberar.
    }
}
