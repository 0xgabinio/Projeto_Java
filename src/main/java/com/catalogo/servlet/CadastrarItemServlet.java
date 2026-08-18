package com.catalogo.servlet;

import com.catalogo.dao.ItemMidiaDAO;
import com.catalogo.dao.ItemMidiaDAOImpl;
import com.catalogo.model.ItemMidia;

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
 * Controller (Servlet) para o cadastro de um novo {@link ItemMidia}.
 * <p>
 * Implementa {@code specs/cadastrar-item.md}: recebe os dados do formulário
 * ({@code cadastroItem.jsp}), valida antes de persistir, delega a inserção ao
 * {@link ItemMidiaDAO} e nunca expõe detalhes técnicos (stack trace, SQL) ao usuário final —
 * Situação-Problema 2 do PDF do enunciado.
 */
@WebServlet("/cadastrarItem")
public class CadastrarItemServlet extends HttpServlet {

    private static final int TITULO_MAX = 255;
    private static final int AUTOR_DIRETOR_MAX = 255;
    private static final int GENERO_MAX = 100;
    private static final List<String> TIPOS_VALIDOS = List.of("Livro", "Filme", "Série");

    private final ItemMidiaDAO itemMidiaDAO = new ItemMidiaDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/cadastroItem.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String titulo = tratarEntrada(request.getParameter("titulo"));
        String autorDiretor = tratarEntrada(request.getParameter("autorDiretor"));
        String anoLancamentoStr = tratarEntrada(request.getParameter("anoLancamento"));
        String genero = tratarEntrada(request.getParameter("genero"));
        String sinopse = tratarEntrada(request.getParameter("sinopse"));
        String tipoMidia = tratarEntrada(request.getParameter("tipoMidia"));

        List<String> erros = validar(titulo, autorDiretor, anoLancamentoStr, genero, tipoMidia);

        // Reexibe os valores preenchidos em caso de erro, para o usuário não perder o que digitou.
        request.setAttribute("titulo", titulo);
        request.setAttribute("autorDiretor", autorDiretor);
        request.setAttribute("anoLancamento", anoLancamentoStr);
        request.setAttribute("genero", genero);
        request.setAttribute("sinopse", sinopse);
        request.setAttribute("tipoMidia", tipoMidia);

        if (!erros.isEmpty()) {
            request.setAttribute("erros", erros);
            request.getRequestDispatcher("/WEB-INF/jsp/cadastroItem.jsp").forward(request, response);
            return;
        }

        ItemMidia item = new ItemMidia();
        item.setTitulo(titulo);
        item.setAutorDiretor(autorDiretor);
        item.setAnoLancamento(anoLancamentoStr == null ? 0 : Integer.parseInt(anoLancamentoStr));
        item.setGenero(genero);
        item.setSinopse(sinopse);
        item.setTipoMidia(tipoMidia);

        try {
            itemMidiaDAO.inserir(item);
        } catch (SQLException e) {
            // Log server-side; usuário recebe só uma mensagem amigável (nunca a exceção crua).
            getServletContext().log("Falha ao inserir ItemMidia", e);
            request.setAttribute("erros", List.of(
                    "Não foi possível salvar o item agora. Tente novamente em instantes."));
            request.getRequestDispatcher("/WEB-INF/jsp/cadastroItem.jsp").forward(request, response);
            return;
        }

        request.setAttribute("mensagemSucesso", "Item \"" + titulo + "\" cadastrado com sucesso!");
        request.removeAttribute("titulo");
        request.removeAttribute("autorDiretor");
        request.removeAttribute("anoLancamento");
        request.removeAttribute("genero");
        request.removeAttribute("sinopse");
        request.removeAttribute("tipoMidia");
        request.getRequestDispatcher("/WEB-INF/jsp/cadastroItem.jsp").forward(request, response);
    }

    /**
     * Valida os dados recebidos do formulário antes de qualquer tentativa de persistência
     * (ver Considerações de Segurança da spec: obrigatoriedade, tipo e tamanho máximo).
     */
    private List<String> validar(String titulo, String autorDiretor, String anoLancamentoStr,
                                  String genero, String tipoMidia) {
        List<String> erros = new ArrayList<>();

        if (titulo == null) {
            erros.add("O campo Título é obrigatório.");
        } else if (titulo.length() > TITULO_MAX) {
            erros.add("O Título deve ter no máximo " + TITULO_MAX + " caracteres.");
        }

        if (tipoMidia == null) {
            erros.add("O campo Tipo de Mídia é obrigatório.");
        } else if (!TIPOS_VALIDOS.contains(tipoMidia)) {
            erros.add("Tipo de Mídia inválido. Selecione Livro, Filme ou Série.");
        }

        if (autorDiretor != null && autorDiretor.length() > AUTOR_DIRETOR_MAX) {
            erros.add("Autor/Diretor deve ter no máximo " + AUTOR_DIRETOR_MAX + " caracteres.");
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
