package com.catalogo.model;

/**
 * Representa um item de mídia (livro, filme ou série) catalogado.
 * <p>
 * POJO puro: nenhuma lógica de acesso a dados ou validação de negócio vive aqui — isso é
 * responsabilidade de {@link com.catalogo.dao.ItemMidiaDAO} e das classes de Servlet/Service.
 * <p>
 * Nota de projeto: {@code anoLancamento} é {@code int} (primitivo) por simplicidade — o valor
 * {@code 0} é tratado como "não informado" nas camadas de persistência e apresentação, já que
 * o campo é opcional na tabela {@code item_midia} (ver {@code docs/modelagem/der.sql}).
 */
public class ItemMidia {

    private int id;
    private String titulo;
    private String autorDiretor;
    private int anoLancamento;
    private String genero;
    private String sinopse;
    private String tipoMidia;

    public ItemMidia() {
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

    public String getAutorDiretor() {
        return autorDiretor;
    }

    public void setAutorDiretor(String autorDiretor) {
        this.autorDiretor = autorDiretor;
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

    public String getTipoMidia() {
        return tipoMidia;
    }

    public void setTipoMidia(String tipoMidia) {
        this.tipoMidia = tipoMidia;
    }
}
