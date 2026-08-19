package com.catalogo.servlet;

import com.catalogo.dao.FilmeDAO;
import com.catalogo.dao.FilmeDAOImpl;
import com.catalogo.model.Filme;
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
import java.sql.SQLException;
import java.util.List;

/**
 * Controller (Servlet) para importar um filme do TMDB direto para o catálogo local.
 * <p>
 * Implementa {@code specs/integrar-tmdb.md} (RF-02) — usado pelo botão "Adicionar ao
 * catálogo" em {@code descobrirFilmes.jsp}. Aceita apenas {@code POST} (mesmo padrão de
 * {@code ExcluirFilmeServlet}: ação que altera dados não deve ser acionável via {@code GET}).
 * Busca os detalhes completos no TMDB (inclui gênero e diretor, diferente da lista de
 * populares) e insere via {@link FilmeDAO}, sem expor detalhes técnicos ao usuário em caso de
 * falha (Situação-Problema 2 do PDF).
 */
@WebServlet("/importarFilmeTmdb")
public class ImportarFilmeTmdbServlet extends HttpServlet {

    private final TmdbClient tmdbClient = new TmdbClient();
    private final FilmeDAO filmeDAO = new FilmeDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer tmdbId = tratarTmdbId(request.getParameter("tmdbId"));

        if (tmdbId == null) {
            session.setAttribute("erros", List.of("Filme do TMDB inválido — não foi possível importar."));
            response.sendRedirect(request.getContextPath() + "/descobrirFilmes");
            return;
        }

        TmdbFilme tmdbFilme;
        try {
            tmdbFilme = tmdbClient.buscarDetalhes(tmdbId);
        } catch (TmdbException e) {
            getServletContext().log("Falha ao buscar detalhes do filme TMDB id=" + tmdbId, e);
            session.setAttribute("erros", List.of(
                    "Não foi possível importar este filme agora. Tente novamente em instantes."));
            response.sendRedirect(request.getContextPath() + "/descobrirFilmes");
            return;
        }

        Filme filme = new Filme();
        filme.setTitulo(tmdbFilme.getTitulo());
        filme.setDiretor(tmdbFilme.getDiretor());
        filme.setAnoLancamento(tmdbFilme.getAnoLancamento());
        filme.setGenero(tmdbFilme.getGenero());
        filme.setSinopse(tmdbFilme.getSinopse());
        filme.setCapaUrl(tmdbFilme.getCapaUrl());
        filme.setNotaTmdb(tmdbFilme.getNotaTmdb());

        try {
            filmeDAO.inserir(filme);
        } catch (SQLException e) {
            getServletContext().log("Falha ao inserir filme importado do TMDB id=" + tmdbId, e);
            session.setAttribute("erros", List.of(
                    "Não foi possível salvar o filme importado agora. Tente novamente em instantes."));
            response.sendRedirect(request.getContextPath() + "/descobrirFilmes");
            return;
        }

        session.setAttribute("mensagemSucesso",
                "Filme \"" + filme.getTitulo() + "\" importado do TMDB com sucesso!");
        response.sendRedirect(request.getContextPath() + "/listarFilmes");
    }

    /** Converte o parâmetro {@code tmdbId} em inteiro, retornando {@code null} se inválido. */
    private Integer tratarTmdbId(String tmdbIdParam) {
        if (tmdbIdParam == null || tmdbIdParam.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(tmdbIdParam.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
