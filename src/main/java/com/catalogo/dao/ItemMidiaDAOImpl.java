package com.catalogo.dao;

import com.catalogo.model.ItemMidia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * Implementação JDBC de {@link ItemMidiaDAO}.
 * <p>
 * Todo acesso a dados usa {@link PreparedStatement} com parâmetros (nunca concatenação de
 * SQL — Situação-Problema 1 do PDF) e try-with-resources para garantir a liberação de
 * conexões, statements e result sets mesmo em caso de exceção (Situação-Problema 2 do PDF).
 */
public class ItemMidiaDAOImpl implements ItemMidiaDAO {

    private static final String SQL_INSERIR =
            "INSERT INTO item_midia (titulo, autor_diretor, ano_lancamento, genero, sinopse, tipo_midia) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

    @Override
    public void inserir(ItemMidia item) throws SQLException {
        try (Connection conexao = FabricaDeConexoes.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, item.getTitulo());
            stmt.setString(2, item.getAutorDiretor());

            if (item.getAnoLancamento() > 0) {
                stmt.setInt(3, item.getAnoLancamento());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, item.getGenero());
            stmt.setString(5, item.getSinopse());
            stmt.setString(6, item.getTipoMidia());

            stmt.executeUpdate();

            try (ResultSet chavesGeradas = stmt.getGeneratedKeys()) {
                if (chavesGeradas.next()) {
                    item.setId(chavesGeradas.getInt(1));
                }
            }
        }
    }
}
