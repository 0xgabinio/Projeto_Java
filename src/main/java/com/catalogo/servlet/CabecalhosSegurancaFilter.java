package com.catalogo.servlet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Acrescenta, em toda resposta HTTP, cabeçalhos de segurança recomendados como defesa em
 * profundidade (OWASP A05:2021 - Security Misconfiguration).
 * <p>
 * Motivação: achado {@code headers-seguranca-ausentes} do {@code pentest-blackbox}
 * (ver {@code security/reports/2026-08-20-headers-seguranca-ausentes.md}), triado pelo
 * {@code revisor-seguranca-qa} e delegado a este agente para correção. A configuração padrão do
 * Tomcat não inclui nenhum desses cabeçalhos — nenhum deles é, isoladamente, uma vulnerabilidade
 * explorável neste projeto (o escaping via JSTL {@code <c:out>} já mitiga XSS refletido/
 * armazenado, por exemplo), mas sua ausência reduz camadas de proteção que custam pouco para
 * adicionar:
 * <ul>
 *   <li>{@code X-Frame-Options: DENY} — impede que a aplicação seja embutida em um
 *       {@code <iframe>} de terceiros (clickjacking).</li>
 *   <li>{@code X-Content-Type-Options: nosniff} — impede que navegadores mais antigos tentem
 *       adivinhar (MIME-sniff) o tipo de conteúdo de uma resposta, ignorando o
 *       {@code Content-Type} declarado.</li>
 *   <li>{@code Content-Security-Policy} — restringe a origem de scripts/estilos/imagens a
 *       {@code 'self'} mais os domínios efetivamente usados pela aplicação (CDN do Bootstrap via
 *       jsDelivr e imagens do TMDB), como camada adicional independente do escaping em JSP.</li>
 *   <li>{@code Referrer-Policy: no-referrer-when-downgrade} — evita vazar a URL completa da
 *       aplicação em requisições subsequentes para origens menos seguras (HTTPS→HTTP).</li>
 * </ul>
 * <p>
 * O atributo {@code SameSite} do cookie de sessão ({@code JSESSIONID}), também apontado no mesmo
 * achado, não é tratado aqui: a API Servlet 4.0 ({@code javax.servlet}, usada neste projeto) não
 * expõe esse atributo em {@code javax.servlet.http.Cookie}, e o cookie de sessão é gerado pelo
 * próprio container antes de qualquer {@code Filter} conseguir interceptá-lo de forma confiável
 * (o Tomcat manipula o cabeçalho {@code Set-Cookie} da sessão internamente, sem passar pelo
 * {@code HttpServletResponse} exposto à aplicação). A forma portável e recomendada pelo próprio
 * Tomcat é configurar o {@code CookieProcessor} por aplicação — ver
 * {@code src/main/webapp/META-INF/context.xml}, empacotado dentro do WAR (nenhum caminho ou
 * versão específica de instalação do Tomcat é referenciado no código-fonte).
 */
@WebFilter("/*")
public class CabecalhosSegurancaFilter implements Filter {

    private static final String CSP =
            "default-src 'self'; "
                    + "img-src 'self' https://image.tmdb.org; "
                    + "script-src 'self' https://cdn.jsdelivr.net; "
                    + "style-src 'self' https://cdn.jsdelivr.net";

    @Override
    public void init(FilterConfig filterConfig) {
        // Sem configuração necessária.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("X-Frame-Options", "DENY");
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("Content-Security-Policy", CSP);
            httpResponse.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Sem recursos para liberar.
    }
}
