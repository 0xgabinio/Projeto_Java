package com.catalogo.servlet;

import com.catalogo.dao.FilmeDAO;
import com.catalogo.dao.FilmeDAOImpl;
import com.catalogo.model.Filme;
import com.catalogo.service.FilmeValidator;

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
 * Controller (Servlet) para a listagem de {@link Filme}s cadastrados.
 * <p>
 * Implementa {@code specs/listar-filmes.md} e {@code specs/buscar-filme.md}: consulta todos os
 * filmes (ou, se um parâmetro {@code termo} for informado, apenas os que correspondem por
 * título/diretor) via {@link FilmeDAO} e encaminha para {@code listarFilmes.jsp}, sem expor
 * detalhes técnicos ao usuário em caso de falha (Situação-Problema 2 do PDF do enunciado).
 */
@WebServlet("/listarFilmes")
public class ListarFilmesServlet extends HttpServlet {

    private final FilmeDAO filmeDAO = new FilmeDAOImpl();

    /**
     * Exibe a listagem completa de filmes, ou apenas os filmes cujo título/diretor correspondem
     * ao parâmetro opcional {@code termo} (RF-02 de {@code specs/buscar-filme.md}). Também
     * consome as mensagens flash de sucesso/erro gravadas na sessão por outros Servlets (ex.:
     * {@code CadastrarFilmeServlet}, {@code ExcluirFilmeServlet}).
     *
     * @param request  requisição HTTP; parâmetro opcional {@code termo} (busca por
     *                 título/diretor, case-insensitive)
     * @param response resposta HTTP, usada para encaminhar (forward) a {@code listarFilmes.jsp}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lê e consome mensagens flash (setadas por CadastrarFilmeServlet/ExcluirFilmeServlet)
        // — só aparecem uma vez, mesmo se a página for recarregada.
        HttpSession session = request.getSession(false);
        if (session != null) {
            if (session.getAttribute("mensagemSucesso") != null) {
                request.setAttribute("mensagemSucesso", session.getAttribute("mensagemSucesso"));
                session.removeAttribute("mensagemSucesso");
            }
            if (session.getAttribute("erros") != null) {
                request.setAttribute("erros", session.getAttribute("erros"));
                session.removeAttribute("erros");
            }
        }

        String termo = FilmeValidator.tratarEntrada(request.getParameter("termo"));
        request.setAttribute("termo", termo);

        try {
            List<Filme> filmes = termo == null
                    ? filmeDAO.listarTodos()
                    : filmeDAO.buscarPorTituloOuDiretor(termo);
            request.setAttribute("filmes", filmes);
        } catch (SQLException e) {
            getServletContext().log("Falha ao listar/buscar filmes (termo=" + termo + ")", e);
            request.setAttribute("erros", List.of(
                    "Não foi possível carregar a lista de filmes agora. Tente novamente em instantes."));
        }

        request.getRequestDispatcher("/WEB-INF/jsp/listarFilmes.jsp").forward(request, response);
    }
}
