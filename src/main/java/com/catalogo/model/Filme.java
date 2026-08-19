package com.catalogo.model;

/**
 * Representa um filme catalogado.
 * <p>
 * POJO puro: nenhuma lógica de acesso a dados ou validação de negócio vive aqui — isso é
 * responsabilidade de {@link com.catalogo.dao.FilmeDAO} e das classes de Servlet/Service.
 * <p>
 * Nota de projeto: {@code anoLancamento} é {@code int} (primitivo) por simplicidade — o valor
 * {@code 0} é tratado como "não informado" nas camadas de persistência e apresentação, já que
 * o campo é opcional na tabela {@code filme} (ver {@code docs/modelagem/der.sql}).
 * <p>
 * {@code capaUrl} e {@code notaTmdb} vêm de {@code specs/integrar-tmdb.md}: {@code capaUrl} é
 * a URL do pôster (importada do TMDB ou digitada manualmente); {@code notaTmdb} é {@code null}
 * para filmes cadastrados manualmente e só é preenchida quando o filme é importado do TMDB —
 * por isso é {@link Double} (objeto), não {@code double} primitivo, para distinguir "sem nota"
 * de "nota zero".
 */
public class Filme {

    private int id;
    private String titulo;
    private String diretor;
    private int anoLancamento;
    private String genero;
    private String sinopse;
    private String capaUrl;
    private Double notaTmdb;

    public Filme() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDiretor() {
        return diretor;
    }

    public void setDiretor(String diretor) {
        this.diretor = diretor;
    }

    public int getAnoLancamento() {
        return anoLancamento;
    }

    public void setAnoLancamento(int anoLancamento) {
        this.anoLancamento = anoLancamento;
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
