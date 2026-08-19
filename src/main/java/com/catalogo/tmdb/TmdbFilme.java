package com.catalogo.tmdb;

/**
 * Representação de um filme retornado pela API do TMDB — DTO transitório, nunca persistido
 * diretamente (ver {@code specs/integrar-tmdb.md}). Ao importar, os campos relevantes são
 * copiados para um {@link com.catalogo.model.Filme} de verdade antes de
 * {@code FilmeDAO.inserir()}.
 */
public class TmdbFilme {

    private int tmdbId;
    private String titulo;
    private int anoLancamento;
    private String diretor;
    private String genero;
    private String sinopse;
    private String capaUrl;
    private Double notaTmdb;

    public int getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getAnoLancamento() {
        return anoLancamento;
    }

    public void setAnoLancamento(int anoLancamento) {
        this.anoLancamento = anoLancamento;
    }

    public String getDiretor() {
        return diretor;
    }

    public void setDiretor(String diretor) {
        this.diretor = diretor;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getCapaUrl() {
        return capaUrl;
    }

    public void setCapaUrl(String capaUrl) {
        this.capaUrl = capaUrl;
    }

    public Double getNotaTmdb() {
        return notaTmdb;
    }

    public void setNotaTmdb(Double notaTmdb) {
        this.notaTmdb = notaTmdb;
    }
}
