package com.catalogo.servlet;

import com.catalogo.dao.FilmeDAO;
import com.catalogo.dao.FilmeDAOImpl;
import com.catalogo.model.Filme;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller (Servlet) para o cadastro de um novo {@link Filme}.
 * <p>
 * Implementa {@code specs/cadastrar-filme.md}: recebe os dados do formulário
 * ({@code cadastroFilme.jsp}), valida antes de persistir, delega a inserção ao
 * {@link FilmeDAO} e nunca expõe detalhes técnicos (stack trace, SQL) ao usuário final —
 * Situação-Problema 2 do PDF do enunciado.
 */
@WebServlet("/cadastrarFilme")
public class CadastrarFilmeServlet extends HttpServlet {

    private static final int TITULO_MAX = 255;
    private static final int DIRETOR_MAX = 255;
    private static final int GENERO_MAX = 100;

    private final FilmeDAO filmeDAO = new FilmeDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/cadastroFilme.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String titulo = tratarEntrada(request.getParameter("titulo"));
        String diretor = tratarEntrada(request.getParameter("diretor"));
        String anoLancamentoStr = tratarEntrada(request.getParameter("anoLancamento"));
        String genero = tratarEntrada(request.getParameter("genero"));
        String sinopse = tratarEntrada(request.getParameter("sinopse"));

        List<String> erros = validar(titulo, diretor, anoLancamentoStr, genero);

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

        request.setAttribute("mensagemSucesso", "Filme \"" + titulo + "\" cadastrado com sucesso!");
        request.removeAttribute("titulo");
        request.removeAttribute("diretor");
        request.removeAttribute("anoLancamento");
        request.removeAttribute("genero");
        request.removeAttribute("sinopse");
        request.getRequestDispatcher("/WEB-INF/jsp/cadastroFilme.jsp").forward(request, response);
    }

    /**
     * Valida os dados recebidos do formulário antes de qualquer tentativa de persistência
     * (ver Considerações de Segurança da spec: obrigatoriedade, tipo e tamanho máximo).
     */
    private List<String> validar(String titulo, String diretor, String anoLancamentoStr, String genero) {
        List<String> erros = new ArrayList<>();

        if (titulo == null) {
            erros.add("O campo Título é obrigatório.");
        } else if (titulo.length() > TITULO_MAX) {
            erros.add("O Título deve ter no máximo " + TITULO_MAX + " caracteres.");
        }

        if (diretor != null && diretor.length() > DIRETOR_MAX) {
            erros.add("Diretor deve ter no máximo " + DIRETOR_MAX + " caracteres.");
        }

        if (genero != null && genero.length() > GENERO_MAX) {
            erros.add("Gênero deve ter no máximo " + GENERO_MAX + " caracteres.");
        }

        if (anoLancamentoStr != null) {
            try {
                Integer.parseInt(anoLancamentoStr);
            } catch (NumberFormatException e) {
                erros.add("Ano de Lançamento deve ser um número inteiro.");
            }
        }

        return erros;
    }

    /** Remove espaços nas pontas e converte string vazia em {@code null}. */
    private String tratarEntrada(String valor) {
        if (valor == null) {
            return null;
        }
        String tratado = valor.trim();
        return tratado.isEmpty() ? null : tratado;
    }
}
