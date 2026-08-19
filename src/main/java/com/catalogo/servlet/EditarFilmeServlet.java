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
 * Controller (Servlet) para a edição de um {@link Filme} existente.
 * <p>
 * Implementa {@code specs/editar-filme.md}: {@code doGet} exibe o formulário pré-preenchido;
 * {@code doPost} valida (via {@link FilmeValidator}, mesma lógica do cadastro) e atualiza via
 * {@link FilmeDAO}. Nunca expõe detalhes técnicos ao usuário (Situação-Problema 2 do PDF).
 */
@WebServlet("/editarFilme")
public class EditarFilmeServlet extends HttpServlet {

    private final FilmeDAO filmeDAO = new FilmeDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer id = tratarId(request.getParameter("id"));
        Filme filme = buscarOuNulo(request, id);

        if (filme == null) {
            request.setAttribute("naoEncontrado", true);
        } else {
            request.setAttribute("id", filme.getId());
            request.setAttribute("titulo", filme.getTitulo());
            request.setAttribute("diretor", filme.getDiretor());
            request.setAttribute("anoLancamento",
                    filme.getAnoLancamento() > 0 ? String.valueOf(filme.getAnoLancamento()) : null);
            request.setAttribute("genero", filme.getGenero());
            request.setAttribute("sinopse", filme.getSinopse());
            request.setAttribute("capaUrl", filme.getCapaUrl());
            // notaTmdb não tem campo editável no formulário — é preservada via campo oculto
            // (ver editarFilme.jsp) para não ser apagada por uma edição manual (ver
            // specs/integrar-tmdb.md).
            request.setAttribute("notaTmdb", filme.getNotaTmdb());
        }

        request.getRequestDispatcher("/WEB-INF/jsp/editarFilme.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer id = tratarId(request.getParameter("id"));
        if (id == null) {
            request.setAttribute("naoEncontrado", true);
            request.getRequestDispatcher("/WEB-INF/jsp/editarFilme.jsp").forward(request, response);
            return;
        }

        String titulo = FilmeValidator.tratarEntrada(request.getParameter("titulo"));
        String diretor = FilmeValidator.tratarEntrada(request.getParameter("diretor"));
        String anoLancamentoStr = FilmeValidator.tratarEntrada(request.getParameter("anoLancamento"));
        String genero = FilmeValidator.tratarEntrada(request.getParameter("genero"));
        String sinopse = FilmeValidator.tratarEntrada(request.getParameter("sinopse"));
        String capaUrl = FilmeValidator.tratarEntrada(request.getParameter("capaUrl"));
        // Round-trip de notaTmdb via campo oculto do formulário (ver editarFilme.jsp) — não é
        // editável pelo usuário, só precisa sobreviver à edição sem ser apagada.
        Double notaTmdb = tratarNotaTmdb(request.getParameter("notaTmdb"));

        List<String> erros = FilmeValidator.validar(titulo, diretor, anoLancamentoStr, genero, capaUrl);

        request.setAttribute("id", id);
        request.setAttribute("titulo", titulo);
        request.setAttribute("diretor", diretor);
        request.setAttribute("anoLancamento", anoLancamentoStr);
        request.setAttribute("genero", genero);
        request.setAttribute("sinopse", sinopse);
        request.setAttribute("capaUrl", capaUrl);
        request.setAttribute("notaTmdb", notaTmdb);

        if (!erros.isEmpty()) {
            request.setAttribute("erros", erros);
            request.getRequestDispatcher("/WEB-INF/jsp/editarFilme.jsp").forward(request, response);
            return;
        }

        Filme filme = new Filme();
        filme.setId(id);
        filme.setTitulo(titulo);
        filme.setDiretor(diretor);
        filme.setAnoLancamento(anoLancamentoStr == null ? 0 : Integer.parseInt(anoLancamentoStr));
        filme.setGenero(genero);
        filme.setSinopse(sinopse);
        filme.setCapaUrl(capaUrl);
        filme.setNotaTmdb(notaTmdb);

        try {
            filmeDAO.atualizar(filme);
        } catch (SQLException e) {
            getServletContext().log("Falha ao atualizar Filme id=" + id, e);
            request.setAttribute("erros", List.of(
                    "Não foi possível salvar as alterações agora. Tente novamente em instantes."));
            request.getRequestDispatcher("/WEB-INF/jsp/editarFilme.jsp").forward(request, response);
            return;
        }

        // Mensagem de sucesso via sessão (mesmo padrão flash message do cadastro), consumida
        // por DetalharFilmeServlet/detalheFilme.jsp.
        HttpSession session = request.getSession();
        session.setAttribute("mensagemSucesso", "Filme \"" + titulo + "\" atualizado com sucesso!");
        response.sendRedirect(request.getContextPath() + "/detalharFilme?id=" + id);
    }

    /** Busca o filme pelo id, tratando falha de banco sem expor detalhes técnicos. */
    private Filme buscarOuNulo(HttpServletRequest request, Integer id) {
        if (id == null) {
            return null;
        }
        try {
            return filmeDAO.buscarPorId(id);
        } catch (SQLException e) {
            getServletContext().log("Falha ao buscar filme id=" + id + " para edição", e);
            request.setAttribute("erros", List.of(
                    "Não foi possível carregar o filme agora. Tente novamente em instantes."));
            return null;
        }
    }

    /** Converte o campo oculto {@code notaTmdb}, retornando {@code null} se ausente/inválido. */
    private Double tratarNotaTmdb(String notaTmdbParam) {
        String tratado = FilmeValidator.tratarEntrada(notaTmdbParam);
        if (tratado == null) {
            return null;
        }
        try {
            return Double.parseDouble(tratado);
        } catch (NumberFormatException e) {
            return null;
        }
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
