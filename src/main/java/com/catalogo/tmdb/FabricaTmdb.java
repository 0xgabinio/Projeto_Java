package com.catalogo.tmdb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Fábrica de configuração do TMDB (The Movie Database), análoga a
 * {@link com.catalogo.dao.FabricaDeConexoes} — lê a chave de API e URLs base de
 * {@code tmdb.properties} (raiz do classpath, gitignored), nunca hardcoded no código-fonte
 * (ver {@code docs/sdd/seguranca/checklist-owasp-top10.md}, categoria A02/CWE-798).
 * <p>
 * <b>Diferença importante em relação a {@code FabricaDeConexoes}</b>: a ausência de
 * {@code tmdb.properties} **não** impede o resto da aplicação de funcionar (RNF-04 de
 * {@code specs/integrar-tmdb.md}) — o banco de dados é obrigatório para o catálogo, mas o
 * TMDB é uma integração opcional. Por isso {@link #isConfigurado()} deve ser checado antes de
 * usar {@link TmdbClient}, em vez de deixar uma exceção estourar na inicialização da classe.
 */
public final class FabricaTmdb {

    private static final Properties PROPRIEDADES = new Properties();
    private static final boolean CONFIGURADO;

    static {
        boolean configurado = false;
        try (InputStream input = FabricaTmdb.class.getClassLoader()
                .getResourceAsStream("tmdb.properties")) {
            if (input != null) {
                PROPRIEDADES.load(input);
                configurado = PROPRIEDADES.getProperty("tmdb.apiKey") != null
                        && !PROPRIEDADES.getProperty("tmdb.apiKey").isBlank();
            }
        } catch (IOException e) {
            configurado = false;
        }
        CONFIGURADO = configurado;
    }

    private FabricaTmdb() {
    }

    /**
     * @return {@code true} se {@code tmdb.properties} existe no classpath e tem uma
     * {@code tmdb.apiKey} preenchida — deve ser checado antes de usar {@link TmdbClient}.
     */
    public static boolean isConfigurado() {
        return CONFIGURADO;
    }

    public static String getApiKey() {
        return PROPRIEDADES.getProperty("tmdb.apiKey");
    }

    public static String getBaseUrl() {
        return PROPRIEDADES.getProperty("tmdb.baseUrl", "https://api.themoviedb.org/3");
    }

    public static String getImageBaseUrl() {
        return PROPRIEDADES.getProperty("tmdb.imageBaseUrl", "https://image.tmdb.org/t/p/w342");
    }
}
