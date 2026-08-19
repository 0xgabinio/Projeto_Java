package com.catalogo.tmdb;

/**
 * Falha ao consumir a API do TMDB (rede, timeout, HTTP de erro, JSON malformado). Exceção
 * verificada única para esta camada — Servlets capturam só este tipo, análogo a como
 * {@link com.catalogo.dao.FilmeDAO} usa {@link java.sql.SQLException} como exceção uniforme,
 * e nunca expõem a causa técnica ao usuário final (Situação-Problema 2 do PDF).
 */
public class TmdbException extends Exception {

    public TmdbException(String message, Throwable cause) {
        super(message, cause);
    }

    public TmdbException(String message) {
        super(message);
    }
}
