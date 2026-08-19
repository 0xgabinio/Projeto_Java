package com.catalogo.servlet;

import com.catalogo.tmdb.FabricaTmdb;
import com.catalogo.tmdb.TmdbClient;
import com.catalogo.tmdb.TmdbException;
import com.catalogo.tmdb.TmdbFilme;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Controller (Servlet) para a tela "Descobrir" — carrossel de filmes populares do TMDB.
 * <p>
 * Implementa {@code specs/integrar-tmdb.md} (RF-01). Se o TMDB não estiver configurado
 * ({@code tmdb.properties} ausente) ou a chamada falhar, exibe uma mensagem amigável **sem
 * afetar** o resto da aplicação (RF-07/RNF-04) — nunca expõe detalhes técnicos ao usuário
 * (Situação-Problema 2 do PDF, aplicada por analogia à integração externa).
 */
@WebServlet("/descobrirFilmes")
public class DescobrirFilmesServlet extends HttpServlet {

    private final TmdbClient tmdbClient = new TmdbClient();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("mensagemSucesso") != null) {
            request.setAttribute("mensagemSucesso", session.getAttribute("mensagemSucesso"));
            session.removeAttribute("mensagemSucesso");
        }

        if (!FabricaTmdb.isConfigurado()) {
            request.setAttribute("tmdbIndisponivel", true);
            request.getRequestDispatcher("/WEB-INF/jsp/descobrirFilmes.jsp").forward(request, response);
            return;
        }

        try {
            List<TmdbFilme> filmes = tmdbClient.listarPopulares();
            request.setAttribute("filmes", filmes);
        } catch (TmdbException e) {
            getServletContext().log("Falha ao listar filmes populares do TMDB", e);
            request.setAttribute("tmdbIndisponivel", true);
        }

        request.getRequestDispatcher("/WEB-INF/jsp/descobrirFilmes.jsp").forward(request, response);
    }
}
