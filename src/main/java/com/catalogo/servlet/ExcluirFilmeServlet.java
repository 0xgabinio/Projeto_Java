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
 * Controller (Servlet) para a exclusão de um {@link Filme}.
 * <p>
 * Implementa {@code specs/excluir-filme.md}: aceita **apenas** {@code POST} (nunca
 * {@code GET}, para evitar exclusão acidental via link/crawler/prefetch), valida o {@code id}
 * antes de excluir e nunca expõe detalhes técnicos ao usuário (Situação-Problema 2 do PDF).
 */
@WebServlet("/excluirFilme")
public class ExcluirFilmeServlet extends HttpServlet {

    private final FilmeDAO filmeDAO = new FilmeDAOImpl();

    /**
     * Exclui o filme identificado por {@code id} e redireciona para {@code /listarFilmes} com
     * mensagem de sucesso (RF-01/RF-04 de {@code specs/excluir-filme.md}). {@code id}
     * ausente/inválido ou falha de banco resultam em redirecionamento com mensagem de erro
     * amigável (via flash message na sessão), nunca um erro técnico. Só aceita {@code POST} —
     * não há {@code doGet} nesta classe, então uma requisição {@code GET} é rejeitada pelo
     * próprio container com 405 (RF-02).
     *
     * @param request  requisição HTTP; parâmetro obrigatório {@code id} (chave primária do
     *                 filme a excluir)
     * @param response resposta HTTP, usada para redirecionar para {@code /listarFilmes}
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer id = tratarId(request.getParameter("id"));
        HttpSession session = request.getSession();

        if (id == null) {
            session.setAttribute("erros",
                    List.of("Filme não encontrado — não foi possível excluir."));
            response.sendRedirect(request.getContextPath() + "/listarFilmes");
            return;
        }

        try {
            filmeDAO.excluir(id);
        } catch (SQLException e) {
            getServletContext().log("Falha ao excluir filme id=" + id, e);
            session.setAttribute("erros", List.of(
                    "Não foi possível excluir o filme agora. Tente novamente em instantes."));
            response.sendRedirect(request.getContextPath() + "/listarFilmes");
            return;
        }

        session.setAttribute("mensagemSucesso", "Filme excluído com sucesso!");
        response.sendRedirect(request.getContextPath() + "/listarFilmes");
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
