package com.catalogo.dao;

import com.catalogo.model.Filme;
import java.sql.SQLException;

/**
 * Contrato de acesso a dados para {@link Filme}.
 * <p>
 * Implementações devem sempre usar {@link java.sql.PreparedStatement} — nunca concatenação de
 * SQL a partir de entrada do usuário (ver Situação-Problema 1 do enunciado da disciplina).
 */
public interface FilmeDAO {

    /**
     * Insere um novo filme no banco de dados.
     *
     * @param filme filme a ser persistido; o {@code id} é ignorado na entrada e preenchido
     *              com a chave gerada pelo banco após a inserção
     * @throws SQLException se a inserção falhar
     */
    void inserir(Filme filme) throws SQLException;
}
