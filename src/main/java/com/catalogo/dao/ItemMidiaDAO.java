package com.catalogo.dao;

import com.catalogo.model.ItemMidia;
import java.sql.SQLException;

/**
 * Contrato de acesso a dados para {@link ItemMidia}.
 * <p>
 * Implementações devem sempre usar {@link java.sql.PreparedStatement} — nunca concatenação de
 * SQL a partir de entrada do usuário (ver Situação-Problema 1 do enunciado da disciplina).
 */
public interface ItemMidiaDAO {

    /**
     * Insere um novo item de mídia no banco de dados.
     *
     * @param item item a ser persistido; o {@code id} é ignorado na entrada e preenchido
     *             com a chave gerada pelo banco após a inserção
     * @throws SQLException se a inserção falhar
     */
    void inserir(ItemMidia item) throws SQLException;
}
