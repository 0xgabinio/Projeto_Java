package com.catalogo.dao;

import com.catalogo.model.Filme;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação JDBC de {@link FilmeDAO}.
 * <p>
 * Todo acesso a dados usa {@link PreparedStatement} com parâmetros (nunca concatenação de
 * SQL — Situação-Problema 1 do PDF) e try-with-resources para garantir a liberação de
 * conexões, statements e result sets mesmo em caso de exceção (Situação-Problema 2 do PDF).
 */
public class FilmeDAOImpl implements FilmeDAO {

    private static final String SQL_INSERIR =
            "INSERT INTO filme (titulo, diretor, ano_lancamento, genero, sinopse) "
                    + "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_LISTAR_TODOS =
            "SELECT id, titulo, diretor, ano_lancamento, genero, sinopse FROM filme ORDER BY titulo";

    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id, titulo, diretor, ano_lancamento, genero, sinopse FROM filme WHERE id = ?";

    private static final String SQL_ATUALIZAR =
            "UPDATE filme SET titulo = ?, diretor = ?, ano_lancamento = ?, genero = ?, sinopse = ? "
                    + "WHERE id = ?";

    private static final String SQL_EXCLUIR = "DELETE FROM filme WHERE id = ?";

    // Situação-Problema 1 do PDF: LIKE via PreparedStatement, "%termo%" passado como
    // parâmetro — nunca concatenar o termo de busca diretamente na string SQL.
    private static final String SQL_BUSCAR_POR_TITULO_OU_DIRETOR =
            "SELECT id, titulo, diretor, ano_lancamento, genero, sinopse FROM filme "
                    + "WHERE titulo LIKE ? OR diretor LIKE ? ORDER BY titulo";

    @Override
    public void inserir(Filme filme) throws SQLException {
        try (Connection conexao = FabricaDeConexoes.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, filme.getTitulo());
            stmt.setString(2, filme.getDiretor());

            if (filme.getAnoLancamento() > 0) {
                stmt.setInt(3, filme.getAnoLancamento());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, filme.getGenero());
            stmt.setString(5, filme.getSinopse());

            stmt.executeUpdate();

            try (ResultSet chavesGeradas = stmt.getGeneratedKeys()) {
                if (chavesGeradas.next()) {
                    filme.setId(chavesGeradas.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Filme> listarTodos() throws SQLException {
        List<Filme> filmes = new ArrayList<>();

        try (Connection conexao = FabricaDeConexoes.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                filmes.add(mapearFilme(rs));
            }
        }

        return filmes;
    }

    @Override
    public Filme buscarPorId(int id) throws SQLException {
        try (Connection conexao = FabricaDeConexoes.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(SQL_BUSCAR_POR_ID)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapearFilme(rs) : null;
            }
        }
    }

    @Override
    public void atualizar(Filme filme) throws SQLException {
        try (Connection conexao = FabricaDeConexoes.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(SQL_ATUALIZAR)) {

            stmt.setString(1, filme.getTitulo());
            stmt.setString(2, filme.getDiretor());

            if (filme.getAnoLancamento() > 0) {
                stmt.setInt(3, filme.getAnoLancamento());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, filme.getGenero());
            stmt.setString(5, filme.getSinopse());
            stmt.setInt(6, filme.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void excluir(int id) throws SQLException {
        try (Connection conexao = FabricaDeConexoes.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(SQL_EXCLUIR)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Filme> buscarPorTituloOuDiretor(String termo) throws SQLException {
        List<Filme> filmes = new ArrayList<>();
        String termoComCuringas = "%" + termo + "%";

        try (Connection conexao = FabricaDeConexoes.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(SQL_BUSCAR_POR_TITULO_OU_DIRETOR)) {

            stmt.setString(1, termoComCuringas);
            stmt.setString(2, termoComCuringas);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    filmes.add(mapearFilme(rs));
                }
            }
        }

        return filmes;
    }

    /** Mapeia a linha atual do {@link ResultSet} para um {@link Filme}. */
    private Filme mapearFilme(ResultSet rs) throws SQLException {
        Filme filme = new Filme();
        filme.setId(rs.getInt("id"));
        filme.setTitulo(rs.getString("titulo"));
        filme.setDiretor(rs.getString("diretor"));
        filme.setAnoLancamento(rs.getInt("ano_lancamento"));
        filme.setGenero(rs.getString("genero"));
        filme.setSinopse(rs.getString("sinopse"));
        return filme;
    }
}
