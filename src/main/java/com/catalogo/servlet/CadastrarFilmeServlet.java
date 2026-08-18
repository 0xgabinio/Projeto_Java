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
 * Controller (Servlet) para o cadastro de um novo {@link Filme}.
 * <p>
 * Implementa {@code specs/cadastrar-filme.md}: recebe os dados do formulário
 * ({@code cadastroFilme.jsp}), valida (via {@link FilmeValidator}) antes de persistir, delega
 * a inserção ao {@link FilmeDAO} e nunca expõe detalhes técnicos (stack trace, SQL) ao
 * usuário final — Situação-Problema 2 do PDF do enunciado.
 */
@WebServlet("/cadastrarFilme")
public class CadastrarFilmeServlet extends HttpServlet {

    private final FilmeDAO filmeDAO = new FilmeDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/cadastroFilme.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String titulo = FilmeValidator.tratarEntrada(request.getParameter("titulo"));
        String diretor = FilmeValidator.tratarEntrada(request.getParameter("diretor"));
        String anoLancamentoStr = FilmeValidator.tratarEntrada(request.getParameter("anoLancamento"));
        String genero = FilmeValidator.tratarEntrada(request.getParameter("genero"));
        String sinopse = FilmeValidator.tratarEntrada(request.getParameter("sinopse"));

        List<String> erros = FilmeValidator.validar(titulo, diretor, anoLancamentoStr, genero);

        // Reexibe os valores preenchidos em caso de erro, para o usuário não perder o que digitou.
        request.setAttribute("titulo", titulo);
        request.setAttribute("diretor", diretor);
        request.setAttribute("anoLancamento", anoLancamentoStr);
        request.setAttribute("genero", genero);
        request.setAttribute("sinopse", sinopse);

        if (!erros.isEmpty()) {
            request.setAttribute("erros", erros);
            request.getRequestDispatcher("/WEB-INF/jsp/cadastroFilme.jsp").forward(request, response);
            return;
        }

        Filme filme = new Filme();
        filme.setTitulo(titulo);
        filme.setDiretor(diretor);
        filme.setAnoLancamento(anoLancamentoStr == null ? 0 : Integer.parseInt(anoLancamentoStr));
        filme.setGenero(genero);
        filme.setSinopse(sinopse);

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
