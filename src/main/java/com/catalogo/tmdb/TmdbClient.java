package com.catalogo.tmdb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente para a API pública do <a href="https://www.themoviedb.org/documentation/api">TMDB
 * (The Movie Database)</a> — implementa {@code specs/integrar-tmdb.md}.
 * <p>
 * Usa {@link HttpClient} (JDK 11+, sem dependência extra) e {@code org.json} para o parsing da
 * resposta — sem framework HTTP/JSON pesado (RNF-03). Timeout curto em toda chamada (RNF-02) e
 * nenhuma exceção interna (rede, HTTP, JSON) vaza para além de {@link TmdbException} — quem
 * chama nunca precisa lidar com {@code IOException} bruta ou detalhes da API (Situação-Problema
 * 2 do PDF, aplicada aqui por analogia ao tratamento de {@code SQLException}).
 */
public class TmdbClient {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final int MAX_RESULTADOS_BUSCA = 5;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();

    /**
     * Lista os filmes populares do TMDB (primeira página), para o carrossel de descoberta.
     *
     * @return até 20 filmes (tamanho de página padrão da API), com título, ano, sinopse,
     * pôster e nota — {@code diretor}/{@code genero} não vêm preenchidos aqui (só na busca
     * detalhada, ver {@link #buscarDetalhes(int)})
     * @throws TmdbException se a chamada falhar (rede, HTTP de erro, JSON malformado)
     */
    public List<TmdbFilme> listarPopulares() throws TmdbException {
        String url = FabricaTmdb.getBaseUrl() + "/movie/popular?api_key=" + FabricaTmdb.getApiKey()
                + "&language=pt-BR&page=1";
        JSONObject resposta = requisitarJson(url);
        return mapearLista(resposta.optJSONArray("results"));
    }

    /**
     * Busca filmes por título (busca parcial do TMDB), para o campo de busca no cadastro.
     *
     * @param termo termo de busca (será URL-encodado)
     * @return até {@value #MAX_RESULTADOS_BUSCA} resultados
     * @throws TmdbException se a chamada falhar
     */
    public List<TmdbFilme> buscarPorTitulo(String termo) throws TmdbException {
        // Termo do usuário é URL-encodado antes de compor a query string — nunca concatenado
        // cru (ver Considerações de Segurança de specs/integrar-tmdb.md).
        String termoCodificado = URLEncoder.encode(termo, StandardCharsets.UTF_8);
        String url = FabricaTmdb.getBaseUrl() + "/search/movie?api_key=" + FabricaTmdb.getApiKey()
                + "&language=pt-BR&query=" + termoCodificado;
        JSONObject resposta = requisitarJson(url);
        List<TmdbFilme> filmes = mapearLista(resposta.optJSONArray("results"));
        return filmes.size() > MAX_RESULTADOS_BUSCA ? filmes.subList(0, MAX_RESULTADOS_BUSCA) : filmes;
    }

    /**
     * Busca os detalhes completos de um filme (para importar ao catálogo local), incluindo
     * gênero (resolvido por nome, diferente das listas acima) e diretor (via uma segunda
     * chamada aos créditos).
     *
     * @param tmdbId id do filme no TMDB
     * @return o filme com todos os campos preenchidos quando disponíveis
     * @throws TmdbException se alguma das chamadas falhar
     */
    public TmdbFilme buscarDetalhes(int tmdbId) throws TmdbException {
        String urlDetalhes = FabricaTmdb.getBaseUrl() + "/movie/" + tmdbId + "?api_key="
                + FabricaTmdb.getApiKey() + "&language=pt-BR";
        JSONObject detalhes = requisitarJson(urlDetalhes);

        TmdbFilme filme = mapearFilme(detalhes);

        JSONArray generos = detalhes.optJSONArray("genres");
        if (generos != null && !generos.isEmpty()) {
            filme.setGenero(generos.getJSONObject(0).optString("name", null));
        }

        filme.setDiretor(buscarDiretor(tmdbId));

        return filme;
    }

    /** Busca o diretor via créditos; retorna {@code null} se não encontrar (nunca lança). */
    private String buscarDiretor(int tmdbId) throws TmdbException {
        String urlCreditos = FabricaTmdb.getBaseUrl() + "/movie/" + tmdbId + "/credits?api_key="
                + FabricaTmdb.getApiKey();
        JSONObject creditos = requisitarJson(urlCreditos);
        JSONArray equipe = creditos.optJSONArray("crew");

        if (equipe == null) {
            return null;
        }

        for (int i = 0; i < equipe.length(); i++) {
            JSONObject membro = equipe.getJSONObject(i);
            if ("Director".equals(membro.optString("job"))) {
                return membro.optString("name", null);
            }
        }

        return null;
    }

    private List<TmdbFilme> mapearLista(JSONArray resultados) {
        List<TmdbFilme> filmes = new ArrayList<>();
        if (resultados == null) {
            return filmes;
        }
        for (int i = 0; i < resultados.length(); i++) {
            filmes.add(mapearFilme(resultados.getJSONObject(i)));
        }
        return filmes;
    }

    private TmdbFilme mapearFilme(JSONObject json) {
        TmdbFilme filme = new TmdbFilme();
        filme.setTmdbId(json.optInt("id"));
        filme.setTitulo(json.optString("title", null));
        filme.setSinopse(json.optString("overview", null));

        String dataLancamento = json.optString("release_date", null);
        if (dataLancamento != null && dataLancamento.length() >= 4) {
            try {
                filme.setAnoLancamento(Integer.parseInt(dataLancamento.substring(0, 4)));
            } catch (NumberFormatException ignorado) {
                // Data em formato inesperado — deixa anoLancamento em 0 ("não informado").
            }
        }

        String posterPath = json.optString("poster_path", null);
        if (posterPath != null && !posterPath.isBlank()) {
            filme.setCapaUrl(FabricaTmdb.getImageBaseUrl() + posterPath);
        }

        if (json.has("vote_average") && !json.isNull("vote_average")) {
            filme.setNotaTmdb(json.optDouble("vote_average"));
        }

        return filme;
    }

    /** Executa um GET e devolve o corpo já parseado como {@link JSONObject}. */
    private JSONObject requisitarJson(String url) throws TmdbException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(TIMEOUT)
                    .GET()
                    .build();

            HttpResponse<String> resposta = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (resposta.statusCode() != 200) {
                throw new TmdbException("TMDB respondeu HTTP " + resposta.statusCode());
            }

            return new JSONObject(resposta.body());
        } catch (IOException e) {
            throw new TmdbException("Falha de rede ao consultar o TMDB", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TmdbException("Requisição ao TMDB interrompida", e);
        } catch (JSONException e) {
            throw new TmdbException("Resposta do TMDB em formato inesperado", e);
        }
    }
}
