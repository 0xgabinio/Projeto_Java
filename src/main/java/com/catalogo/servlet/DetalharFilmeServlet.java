package com.catalogo.servlet;

import com.catalogo.dao.FilmeDAO;
import com.catalogo.dao.FilmeDAOImpl;
import com.catalogo.model.Filme;

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
 * Controller (Servlet) para o detalhe de um {@link Filme} específico.
 * <p>
 * Implementa {@code specs/detalhar-filme.md}: valida o parâmetro {@code id} antes de
 * qualquer consulta, busca via {@link FilmeDAO} e nunca expõe detalhes técnicos ao usuário
 * (Situação-Problema 2 do PDF do enunciado).
 */
@WebServlet("/detalharFilme")
public class DetalharFilmeServlet extends HttpServlet {

    private final FilmeDAO filmeDAO = new FilmeDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lê e consome a mensagem flash de sucesso (setada por EditarFilmeServlet após uma
        // edição bem-sucedida) — mesmo padrão de ListarFilmesServlet.
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("mensagemSucesso") != null) {
            request.setAttribute("mensagemSucesso", session.getAttribute("mensagemSucesso"));
            session.removeAttribute("mensagemSucesso");
        }

        String idParam = request.getParameter("id");
        Integer id = tratarId(idParam);

        if (id == null) {
            request.setAttribute("naoEncontrado", true);
            request.getRequestDispatcher("/WEB-INF/jsp/detalheFilme.jsp").forward(request, response);
            return;
        }

        Filme filme;
        try {
            filme = filmeDAO.buscarPorId(id);
        } catch (SQLException e) {
            getServletContext().log("Falha ao buscar filme id=" + id, e);
            request.setAttribute("erros", List.of(
                    "Não foi possível carregar o filme agora. Tente novamente em instantes."));
            request.getRequestDispatcher("/WEB-INF/jsp/detalheFilme.jsp").forward(request, response);
            return;
        }

        if (filme == null) {
            request.setAttribute("naoEncontrado", true);
        } else {
            request.setAttribute("filme", filme);
        }

        request.getRequestDispatcher("/WEB-INF/jsp/detalheFilme.jsp").forward(request, response);
    }

    /** Converte o parâmetro {@code id} em inteiro, retornando {@code null} se ausente/inválido. */
    private Integer tratarId(String idParam) {
        if (idParam == null || idParam.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
