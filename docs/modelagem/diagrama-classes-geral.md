# Diagrama de Classes — Catálogo Simples de Filmes (Mermaid)

Versão em [Mermaid](https://mermaid.js.org/) do diagrama de classes consolidado do projeto,
equivalente a [`diagrama-classes-geral.puml`](diagrama-classes-geral.puml) (PlantUML), mas
gerada diretamente a partir do estado atual do código-fonte (`src/main/java/com/catalogo/`) —
inclui, portanto, itens adicionados em iterações posteriores à primeira versão do `.puml`:
`FilmeDAO.existeComTituloEAno`, `FabricaDeConexoes`, `CabecalhosSegurancaFilter` e
`TmdbException`.

O GitHub renderiza o bloco ```` ```mermaid ```` abaixo automaticamente ao visualizar este
arquivo no repositório.

```mermaid
classDiagram
    direction LR

    %% ---------------------------------------------------------------
    %% com.catalogo.model
    %% ---------------------------------------------------------------
    class Filme {
        -int id
        -String titulo
        -String diretor
        -int anoLancamento
        -String genero
        -String sinopse
        -String capaUrl
        -Double notaTmdb
        +getId() int
        +getTitulo() String
        +setTitulo(String)
        +getDiretor() String
        +setDiretor(String)
        +getAnoLancamento() int
        +setAnoLancamento(int)
        +getGenero() String
        +setGenero(String)
        +getSinopse() String
        +setSinopse(String)
        +getCapaUrl() String
        +setCapaUrl(String)
        +getNotaTmdb() Double
        +setNotaTmdb(Double)
    }

    %% ---------------------------------------------------------------
    %% com.catalogo.dao
    %% ---------------------------------------------------------------
    class FilmeDAO {
        <<interface>>
        +inserir(Filme) void
        +listarTodos() List~Filme~
        +buscarPorId(int) Filme
        +atualizar(Filme) void
        +excluir(int) void
        +buscarPorTituloOuDiretor(String) List~Filme~
        +existeComTituloEAno(String, int) boolean
    }

    class FilmeDAOImpl {
        +inserir(Filme) void
        +listarTodos() List~Filme~
        +buscarPorId(int) Filme
        +atualizar(Filme) void
        +excluir(int) void
        +buscarPorTituloOuDiretor(String) List~Filme~
        +existeComTituloEAno(String, int) boolean
    }

    class FabricaDeConexoes {
        <<utility>>
        +getConexao()$ Connection
    }

    %% ---------------------------------------------------------------
    %% com.catalogo.service
    %% ---------------------------------------------------------------
    class FilmeValidator {
        <<utility>>
        +validar(String, String, String, String, String)$ List~String~
        +tratarEntrada(String)$ String
    }

    %% ---------------------------------------------------------------
    %% com.catalogo.servlet — controllers
    %% ---------------------------------------------------------------
    class CadastrarFilmeServlet {
        +doGet(request, response) void
        +doPost(request, response) void
    }
    class ListarFilmesServlet {
        +doGet(request, response) void
    }
    class DetalharFilmeServlet {
        +doGet(request, response) void
    }
    class EditarFilmeServlet {
        +doGet(request, response) void
        +doPost(request, response) void
    }
    class ExcluirFilmeServlet {
        +doPost(request, response) void
    }
    class DescobrirFilmesServlet {
        +doGet(request, response) void
    }
    class ImportarFilmeTmdbServlet {
        +doPost(request, response) void
    }

    %% ---------------------------------------------------------------
    %% com.catalogo.servlet — filtros transversais
    %% ---------------------------------------------------------------
    class CodificacaoUtf8Filter {
        <<filter>>
        +doFilter(request, response, chain) void
    }
    class CabecalhosSegurancaFilter {
        <<filter>>
        +doFilter(request, response, chain) void
    }

    %% ---------------------------------------------------------------
    %% com.catalogo.tmdb — integração opcional
    %% ---------------------------------------------------------------
    class TmdbClient {
        +listarPopulares() List~TmdbFilme~
        +buscarPorTitulo(String) List~TmdbFilme~
        +buscarDetalhes(int) TmdbFilme
    }
    class TmdbFilme {
        +int tmdbId
        +String titulo
        +int anoLancamento
        +String diretor
        +String genero
        +String sinopse
        +String capaUrl
        +Double notaTmdb
    }
    class FabricaTmdb {
        <<utility>>
        +isConfigurado()$ boolean
        +getApiKey()$ String
        +getBaseUrl()$ String
        +getImageBaseUrl()$ String
    }
    class TmdbException {
        <<exception>>
    }

    %% ---------------------------------------------------------------
    %% Relações
    %% ---------------------------------------------------------------
    FilmeDAOImpl ..|> FilmeDAO
    FilmeDAOImpl --> FabricaDeConexoes : usa
    FilmeDAOImpl --> Filme : persiste / retorna

    CadastrarFilmeServlet --> FilmeDAO : usa
    CadastrarFilmeServlet --> FilmeValidator : usa
    CadastrarFilmeServlet --> TmdbClient : usa (busca opcional)
    ListarFilmesServlet --> FilmeDAO : usa
    DetalharFilmeServlet --> FilmeDAO : usa
    EditarFilmeServlet --> FilmeDAO : usa
    EditarFilmeServlet --> FilmeValidator : usa
    ExcluirFilmeServlet --> FilmeDAO : usa
    DescobrirFilmesServlet --> TmdbClient : usa
    ImportarFilmeTmdbServlet --> TmdbClient : usa
    ImportarFilmeTmdbServlet --> FilmeDAO : usa (inserir)
    ImportarFilmeTmdbServlet --> FilmeValidator : usa

    TmdbClient --> FabricaTmdb : usa
    TmdbClient --> TmdbFilme : retorna
    TmdbClient ..> TmdbException : lança
```

## Observações

- **Filtros** (`CodificacaoUtf8Filter`, `CabecalhosSegurancaFilter`) são `@WebFilter("/*")` —
  aplicam-se a toda requisição/resposta, sem relação direta de uso com as demais classes
  (por isso aparecem no diagrama sem setas de dependência).
- `FabricaDeConexoes` e `FabricaTmdb` são classes utilitárias (construtor privado, só métodos
  estáticos) — estereotipadas como `<<utility>>`.
- `FilmeDAOImpl.existeComTituloEAno` e a checagem de duplicidade em `CadastrarFilmeServlet`/
  `ImportarFilmeTmdbServlet` foram adicionadas depois da primeira versão do diagrama PlantUML
  (correção de bug de cadastro duplicado) — este arquivo Mermaid já reflete isso; o `.puml`
  ainda não foi atualizado.
