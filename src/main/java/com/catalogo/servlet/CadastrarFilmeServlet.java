package com.catalogo.servlet;

import com.catalogo.dao.FilmeDAO;
import com.catalogo.dao.FilmeDAOImpl;
import com.catalogo.model.Filme;
import com.catalogo.service.FilmeValidator;
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
import java.sql.SQLException;
import java.util.List;

/**
 * Controller (Servlet) para o cadastro de um novo {@link Filme}.
 * <p>
 * Implementa {@code specs/cadastrar-filme.md}: recebe os dados do formulário
 * ({@code cadastroFilme.jsp}), valida (via {@link FilmeValidator}) e checa duplicidade (mesmo
 * título + ano já cadastrado, via {@link FilmeDAO#existeComTituloEAno}) antes de persistir,
 * delega a inserção ao {@link FilmeDAO} e nunca expõe detalhes técnicos (stack trace, SQL) ao
 * usuário final — Situação-Problema 2 do PDF do enunciado.
 * <p>
 * Também implementa {@code specs/integrar-tmdb.md} (RF-03/RF-04): {@code doGet} aceita os
 * parâmetros opcionais {@code buscaTmdb} (mostra até 5 resultados de busca por título) e
 * {@code tmdbId} (pré-preenche o formulário com os detalhes do filme escolhido) — ambos
 * falham de forma graciosa se o TMDB estiver indisponível (RF-07), sem afetar o cadastro
 * manual normal.
 */
@WebServlet("/cadastrarFilme")
public class CadastrarFilmeServlet extends HttpServlet {

    private final FilmeDAO filmeDAO = new FilmeDAOImpl();
    private final TmdbClient tmdbClient = new TmdbClient();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tmdbIdParam = request.getParameter("tmdbId");
        String buscaTmdb = FilmeValidator.tratarEntrada(request.getParameter("buscaTmdb"));

        if (tmdbIdParam != null && FabricaTmdb.isConfigurado()) {
            preencherFormularioComTmdb(request, tmdbIdParam);
        } else if (buscaTmdb != null && FabricaTmdb.isConfigurado()) {
            buscarNoTmdb(request, buscaTmdb);
        }

        request.setAttribute("tmdbConfigurado", FabricaTmdb.isConfigurado());
        request.getRequestDispatcher("/WEB-INF/jsp/cadastroFilme.jsp").forward(request, response);
    }

    /** Busca até 5 filmes por título no TMDB e disponibiliza os resultados para a JSP. */
    private void buscarNoTmdb(HttpServletRequest request, String termo) {
        request.setAttribute("buscaTmdb", termo);
        try {
            List<TmdbFilme> resultados = tmdbClient.buscarPorTitulo(termo);
            request.setAttribute("resultadosTmdb", resultados);
        } catch (TmdbException e) {
            getServletContext().log("Falha ao buscar '" + termo + "' no TMDB", e);
            request.setAttribute("erros", List.of(
                    "Não foi possível buscar no TMDB agora. Você ainda pode cadastrar manualmente."));
        }
    }

    /** Pré-preenche os campos do formulário com os detalhes de um filme do TMDB. */
    private void preencherFormularioComTmdb(HttpServletRequest request, String tmdbIdParam) {
        try {
            int tmdbId = Integer.parseInt(tmdbIdParam.trim());
            TmdbFilme tmdbFilme = tmdbClient.buscarDetalhes(tmdbId);

            request.setAttribute("titulo", tmdbFilme.getTitulo());
            request.setAttribute("diretor", tmdbFilme.getDiretor());
            request.setAttribute("anoLancamento",
                    tmdbFilme.getAnoLancamento() > 0 ? String.valueOf(tmdbFilme.getAnoLancamento()) : null);
            request.setAttribute("genero", tmdbFilme.getGenero());
            request.setAttribute("sinopse", tmdbFilme.getSinopse());
            request.setAttribute("capaUrl", tmdbFilme.getCapaUrl());
        } catch (NumberFormatException | TmdbException e) {
            getServletContext().log("Falha ao pré-preencher formulário a partir do TMDB", e);
            request.setAttribute("erros", List.of(
                    "Não foi possível carregar os detalhes deste filme do TMDB. Preencha manualmente."));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String titulo = FilmeValidator.tratarEntrada(request.getParameter("titulo"));
        String diretor = FilmeValidator.tratarEntrada(request.getParameter("diretor"));
        String anoLancamentoStr = FilmeValidator.tratarEntrada(request.getParameter("anoLancamento"));
        String genero = FilmeValidator.tratarEntrada(request.getParameter("genero"));
        String sinopse = FilmeValidator.tratarEntrada(request.getParameter("sinopse"));
        String capaUrl = FilmeValidator.tratarEntrada(request.getParameter("capaUrl"));

        List<String> erros = FilmeValidator.validar(titulo, diretor, anoLancamentoStr, genero, capaUrl);

        // Reexibe os valores preenchidos em caso de erro, para o usuário não perder o que digitou.
        request.setAttribute("titulo", titulo);
        request.setAttribute("diretor", diretor);
        request.setAttribute("anoLancamento", anoLancamentoStr);
        request.setAttribute("genero", genero);
        request.setAttribute("sinopse", sinopse);
        request.setAttribute("capaUrl", capaUrl);

        if (!erros.isEmpty()) {
            request.setAttribute("erros", erros);
            request.getRequestDispatcher("/WEB-INF/jsp/cadastroFilme.jsp").forward(request, response);
            return;
        }

        int anoLancamento = anoLancamentoStr == null ? 0 : Integer.parseInt(anoLancamentoStr);

        try {
            if (filmeDAO.existeComTituloEAno(titulo, anoLancamento)) {
                request.setAttribute("erros", List.of(
                        "Já existe um filme cadastrado com esse título e ano de lançamento."));
                request.getRequestDispatcher("/WEB-INF/jsp/cadastroFilme.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            getServletContext().log("Falha ao verificar duplicidade de Filme", e);
            request.setAttribute("erros", List.of(
                    "Não foi possível salvar o filme agora. Tente novamente em instantes."));
            request.getRequestDispatcher("/WEB-INF/jsp/cadastroFilme.jsp").forward(request, response);
            return;
        }

        Filme filme = new Filme();
        filme.setTitulo(titulo);
        filme.setDiretor(diretor);
        filme.setAnoLancamento(anoLancamento);
        filme.setGenero(genero);
        filme.setSinopse(sinopse);
        filme.setCapaUrl(capaUrl);

        try {
            filmeDAO.inserir(filme);
        } catch (SQLException e) {
            // Log server-side; usuário recebe só uma mensagem amigável (nunca a exceção crua).
            getServletContext().log("Falha ao inserir Filme", e);
            request.setAttribute("erros", List.of(
                    "Não foi possível salvar o filme agora. Tente novamente em instantes."));
            request.getRequestDispatcher("/WEB-INF/jsp/cadastroFilme.jsp").forward(request, response);
            return;
        }

        // Mensagem de sucesso via sessão (padrão flash message): sobrevive ao redirect,
        // é lida e removida por ListarFilmesServlet (RF-05 de specs/listar-filmes.md).
        HttpSession session = request.getSession();
        session.setAttribute("mensagemSucesso", "Filme \"" + titulo + "\" cadastrado com sucesso!");
        response.sendRedirect(request.getContextPath() + "/listarFilmes");
    }
}
