package com.catalogo.dao;

import com.catalogo.model.Filme;
import java.sql.SQLException;
import java.util.List;

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

    /**
     * Lista todos os filmes cadastrados, ordenados por título.
     *
     * @return lista de filmes (vazia, nunca {@code null}, se não houver nenhum cadastrado)
     * @throws SQLException se a consulta falhar
     */
    List<Filme> listarTodos() throws SQLException;

    /**
     * Busca um filme pelo seu {@code id}.
     *
     * @param id chave primária do filme
     * @return o filme encontrado, ou {@code null} se não existir nenhum com esse {@code id}
     * @throws SQLException se a consulta falhar
     */
    Filme buscarPorId(int id) throws SQLException;

    /**
     * Atualiza um filme já existente, identificado por {@code filme.getId()}.
     *
     * @param filme filme com os novos valores (o {@code id} deve corresponder a um registro
     *              existente; esta camada não valida a existência — isso é responsabilidade
     *              do Servlet, via {@link #buscarPorId(int)})
     * @throws SQLException se a atualização falhar
     */
    void atualizar(Filme filme) throws SQLException;

    /**
     * Exclui um filme pelo seu {@code id}. Não lança exceção se o {@code id} não existir
     * (0 linhas afetadas não é considerado erro).
     *
     * @param id chave primária do filme a excluir
     * @throws SQLException se a exclusão falhar
     */
    void excluir(int id) throws SQLException;

    /**
     * Busca filmes cujo {@code titulo} ou {@code diretor} contenham o {@code termo} (busca
     * parcial, case-insensitive). Cenário da Situação-Problema 1 do enunciado da disciplina —
     * implementações devem usar {@code LIKE} via {@link java.sql.PreparedStatement}, nunca
     * concatenar {@code termo} diretamente na string SQL.
     *
     * @param termo termo de busca (não deve incluir os caracteres curinga {@code %} — a
     *              implementação os adiciona)
     * @return lista de filmes correspondentes, ordenada por título (vazia se nenhum corresponder)
     * @throws SQLException se a consulta falhar
     */
    List<Filme> buscarPorTituloOuDiretor(String termo) throws SQLException;
}
