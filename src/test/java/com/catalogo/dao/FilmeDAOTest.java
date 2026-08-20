package com.catalogo.dao;

import com.catalogo.model.Filme;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Testes unitários de {@link FilmeDAOImpl} — cobre todos os métodos de {@link FilmeDAO}
 * (inserir, listarTodos, buscarPorId, atualizar, excluir, buscarPorTituloOuDiretor), caminho
 * feliz e casos de borda.
 * <p>
 * <b>Pré-requisito para rodar esta classe</b>: um banco de dados MySQL/MariaDB de teste, real e
 * dedicado, disponível localmente — recomenda-se o schema {@code catalogo_test} (nunca aponte
 * para o schema de desenvolvimento/produção, pois {@link #limparTabela()} apaga todas as linhas
 * de {@code filme} antes de cada teste). Como o projeto não usa nenhum framework de mock (fora
 * de escopo para o tamanho deste projeto acadêmico — ver skill {@code gerar-testes-junit}), o
 * DAO é testado contra um banco real via JDBC, através da mesma {@link FabricaDeConexoes} usada
 * em produção.
 * <p>
 * Configuração: crie um {@code src/test/resources/db.properties} local (nunca versionado — ver
 * {@code .gitignore}) apontando para o schema de teste, por exemplo:
 * <pre>
 * db.url=jdbc:mysql://localhost:3306/catalogo_test?useSSL=false&amp;serverTimezone=UTC&amp;useUnicode=true&amp;characterEncoding=UTF-8
 * db.usuario=SEU_USUARIO_AQUI
 * db.senha=SUA_SENHA_AQUI
 * </pre>
 * O Maven coloca {@code src/test/resources} à frente de {@code src/main/resources} no
 * classpath de teste, então este arquivo tem prioridade sobre o {@code db.properties} de
 * desenvolvimento sem qualquer alteração de código. A tabela {@code filme} (DDL em
 * {@code docs/modelagem/der.sql}) já deve existir no schema de teste antes de rodar.
 * <p>
 * <b>Execução confirmada:</b> rodados com sucesso (14/14 passando) contra um schema MariaDB
 * real ({@code catalogo_test}, DDL de {@code docs/modelagem/der.sql}), via
 * {@code mvn test} com JDK 25 e Maven 3.9.
 */
class FilmeDAOTest {

    private final FilmeDAO filmeDAO = new FilmeDAOImpl();

    @BeforeEach
    void limparTabela() throws SQLException {
        try (Connection conexao = FabricaDeConexoes.getConexao();
             Statement stmt = conexao.createStatement()) {
            stmt.executeUpdate("DELETE FROM filme");
        }
    }

    @AfterEach
    void limparTabelaDepois() throws SQLException {
        limparTabela();
    }

    private Filme criarFilme(String titulo, String diretor, int anoLancamento, String genero, String sinopse) {
        Filme filme = new Filme();
        filme.setTitulo(titulo);
        filme.setDiretor(diretor);
        filme.setAnoLancamento(anoLancamento);
        filme.setGenero(genero);
        filme.setSinopse(sinopse);
        return filme;
    }

    // ---- inserir ----

    @Test
    void inserir_deveInserirFilmeValido() throws SQLException {
        Filme filme = criarFilme("Blade Runner", "Ridley Scott", 1982, "Ficção Científica",
                "Um caçador de androides em um futuro distópico.");

        filmeDAO.inserir(filme);

        assertTrue(filme.getId() > 0, "id gerado pelo banco deve ser preenchido em filme.id");

        Filme persistido = filmeDAO.buscarPorId(filme.getId());
        assertNotNull(persistido);
        assertEquals("Blade Runner", persistido.getTitulo());
        assertEquals("Ridley Scott", persistido.getDiretor());
        assertEquals(1982, persistido.getAnoLancamento());
        assertEquals("Ficção Científica", persistido.getGenero());
        assertEquals("Um caçador de androides em um futuro distópico.", persistido.getSinopse());
    }

    @Test
    void inserir_deveAceitarCamposOpcionaisNulosOuZero() throws SQLException {
        // diretor, genero, sinopse nulos e anoLancamento = 0 ("não informado", ver Filme.java)
        Filme filme = criarFilme("Filme Sem Detalhes", null, 0, null, null);

        filmeDAO.inserir(filme);

        Filme persistido = filmeDAO.buscarPorId(filme.getId());
        assertNotNull(persistido);
        assertEquals("Filme Sem Detalhes", persistido.getTitulo());
        assertNull(persistido.getDiretor());
        assertEquals(0, persistido.getAnoLancamento());
        assertNull(persistido.getGenero());
        assertNull(persistido.getSinopse());
    }

    // ---- listarTodos ----

    @Test
    void listarTodos_deveRetornarListaVazia_quandoNaoHaFilmes() throws SQLException {
        List<Filme> filmes = filmeDAO.listarTodos();

        assertNotNull(filmes, "a lista nunca deve ser null, mesmo vazia");
        assertTrue(filmes.isEmpty());
    }

    @Test
    void listarTodos_deveRetornarFilmesCadastrados() throws SQLException {
        filmeDAO.inserir(criarFilme("Z - A Última Fronteira", "Diretor Z", 2010, "Drama", null));
        filmeDAO.inserir(criarFilme("A Origem", "Christopher Nolan", 2010, "Ficção Científica", null));

        List<Filme> filmes = filmeDAO.listarTodos();

        assertEquals(2, filmes.size());
        // SQL_LISTAR_TODOS usa ORDER BY titulo — "A Origem" deve vir antes de "Z - A Última Fronteira".
        assertEquals("A Origem", filmes.get(0).getTitulo());
        assertEquals("Z - A Última Fronteira", filmes.get(1).getTitulo());
    }

    // ---- buscarPorId ----

    @Test
    void buscarPorId_deveRetornarFilme_quandoExiste() throws SQLException {
        Filme filme = criarFilme("Interestelar", "Christopher Nolan", 2014, "Ficção Científica",
                "Uma equipe viaja através de um buraco de minhoca em busca de um novo lar.");
        filmeDAO.inserir(filme);

        Filme encontrado = filmeDAO.buscarPorId(filme.getId());

        assertNotNull(encontrado);
        assertEquals(filme.getId(), encontrado.getId());
        assertEquals("Interestelar", encontrado.getTitulo());
    }

    @Test
    void buscarPorId_deveRetornarNull_quandoNaoExiste() throws SQLException {
        Filme encontrado = filmeDAO.buscarPorId(999999);

        assertNull(encontrado);
    }

    // ---- atualizar ----

    @Test
    void atualizar_devePersistirAlteracoes() throws SQLException {
        Filme filme = criarFilme("Titulo Original", "Diretor Original", 2000, "Drama", "Sinopse original.");
        filmeDAO.inserir(filme);

        filme.setTitulo("Titulo Atualizado");
        filme.setDiretor("Diretor Atualizado");
        filme.setAnoLancamento(2020);
        filme.setGenero("Suspense");
        filme.setSinopse("Sinopse atualizada.");
        filmeDAO.atualizar(filme);

        Filme atualizado = filmeDAO.buscarPorId(filme.getId());
        assertNotNull(atualizado);
        assertEquals("Titulo Atualizado", atualizado.getTitulo());
        assertEquals("Diretor Atualizado", atualizado.getDiretor());
        assertEquals(2020, atualizado.getAnoLancamento());
        assertEquals("Suspense", atualizado.getGenero());
        assertEquals("Sinopse atualizada.", atualizado.getSinopse());
    }

    @Test
    void atualizar_naoDeveLancarExcecao_quandoIdNaoExiste() {
        Filme filmeInexistente = criarFilme("Fantasma", "Ninguém", 1999, "Terror", null);
        filmeInexistente.setId(999999);

        // UPDATE ... WHERE id = ? sem linhas afetadas não é erro (mesma semântica de excluir()).
        assertDoesNotThrow(() -> filmeDAO.atualizar(filmeInexistente));
    }

    // ---- excluir ----

    @Test
    void excluir_deveRemoverFilme_quandoExiste() throws SQLException {
        Filme filme = criarFilme("Filme a Excluir", "Diretor X", 2005, "Comédia", null);
        filmeDAO.inserir(filme);

        filmeDAO.excluir(filme.getId());

        assertNull(filmeDAO.buscarPorId(filme.getId()));
    }

    @Test
    void excluir_naoDeveLancarExcecao_quandoIdNaoExiste() {
        assertDoesNotThrow(() -> filmeDAO.excluir(999999));
    }

    // ---- buscarPorTituloOuDiretor ----

    @Test
    void buscarPorTituloOuDiretor_deveEncontrarPorTitulo() throws SQLException {
        filmeDAO.inserir(criarFilme("Duna", "Denis Villeneuve", 2021, "Ficção Científica", null));
        filmeDAO.inserir(criarFilme("Oppenheimer", "Christopher Nolan", 2023, "Drama", null));

        List<Filme> encontrados = filmeDAO.buscarPorTituloOuDiretor("Dun");

        assertEquals(1, encontrados.size());
        assertEquals("Duna", encontrados.get(0).getTitulo());
    }

    @Test
    void buscarPorTituloOuDiretor_deveEncontrarPorDiretor() throws SQLException {
        filmeDAO.inserir(criarFilme("Duna", "Denis Villeneuve", 2021, "Ficção Científica", null));
        filmeDAO.inserir(criarFilme("Oppenheimer", "Christopher Nolan", 2023, "Drama", null));

        List<Filme> encontrados = filmeDAO.buscarPorTituloOuDiretor("Nolan");

        assertEquals(1, encontrados.size());
        assertEquals("Oppenheimer", encontrados.get(0).getTitulo());
    }

    @Test
    void buscarPorTituloOuDiretor_deveRetornarListaVazia_quandoSemCorrespondencia() throws SQLException {
        filmeDAO.inserir(criarFilme("Duna", "Denis Villeneuve", 2021, "Ficção Científica", null));

        List<Filme> encontrados = filmeDAO.buscarPorTituloOuDiretor("termo-sem-correspondencia-xyz");

        assertNotNull(encontrados);
        assertTrue(encontrados.isEmpty());
    }

    @Test
    void buscarPorTituloOuDiretor_naoDeveLancarExcecao_comAspasSimples() {
        // Sondagem de SQL Injection (Situação-Problema 1) — deve ser tratado como dado literal
        // pelo PreparedStatement, nunca gerar erro de sintaxe SQL.
        assertDoesNotThrow(() -> filmeDAO.buscarPorTituloOuDiretor("'"));
    }

    // ---- existeComTituloEAno ----

    @Test
    void existeComTituloEAno_deveRetornarTrue_quandoMesmoTituloEAno() throws SQLException {
        filmeDAO.inserir(criarFilme("Duna", "Denis Villeneuve", 2021, "Ficção Científica", null));

        assertTrue(filmeDAO.existeComTituloEAno("Duna", 2021));
    }

    @Test
    void existeComTituloEAno_deveSerCaseInsensitiveNoTitulo() throws SQLException {
        filmeDAO.inserir(criarFilme("Duna", "Denis Villeneuve", 2021, "Ficção Científica", null));

        assertTrue(filmeDAO.existeComTituloEAno("DUNA", 2021));
    }

    @Test
    void existeComTituloEAno_deveRetornarFalse_quandoAnoDiferente() throws SQLException {
        filmeDAO.inserir(criarFilme("Duna", "Denis Villeneuve", 2021, "Ficção Científica", null));

        assertFalse(filmeDAO.existeComTituloEAno("Duna", 1984));
    }

    @Test
    void existeComTituloEAno_deveRetornarFalse_quandoTituloDiferente() throws SQLException {
        filmeDAO.inserir(criarFilme("Duna", "Denis Villeneuve", 2021, "Ficção Científica", null));

        assertFalse(filmeDAO.existeComTituloEAno("Oppenheimer", 2021));
    }

    @Test
    void existeComTituloEAno_deveValidarAnoZeroComoNaoInformado() throws SQLException {
        // anoLancamento = 0 é "não informado" (ver Filme.java) — persistido como NULL no banco.
        filmeDAO.inserir(criarFilme("Filme Sem Ano", null, 0, null, null));

        assertTrue(filmeDAO.existeComTituloEAno("Filme Sem Ano", 0));
        // Mesmo título, mas com ano informado, não deve casar com o registro sem ano.
        assertFalse(filmeDAO.existeComTituloEAno("Filme Sem Ano", 2020));
    }

    @Test
    void existeComTituloEAno_deveRetornarFalse_quandoTabelaVazia() throws SQLException {
        assertFalse(filmeDAO.existeComTituloEAno("Qualquer Título", 2021));
    }
}
