# 🎬 Catálogo Simples de Filmes

Aplicação web de catálogo pessoal de filmes — CRUD completo (cadastrar, listar, detalhar,
editar, excluir) e busca por título/diretor, com integração opcional à API do
[TMDB](https://www.themoviedb.org/) e interface em Bootstrap 5 (com modo escuro).

Projeto Integrador Transdisciplinar (PIT) em Ciência da Computação, disciplina *Projetos
Computacionais: da Teoria à Prática* — Cruzeiro do Sul Virtual. Desenvolvido individualmente,
seguindo a metodologia de Aprendizagem Baseada em Projetos (ABP).

## Funcionalidades

- **CRUD de filmes**: cadastro, listagem, detalhe, edição e exclusão, com validação de dados e
  checagem de duplicidade (mesmo título + ano).
- **Busca** por título ou diretor (parcial, case-insensitive).
- **Integração com o TMDB** (opcional — a aplicação funciona normalmente sem ela):
  - Carrossel de filmes populares (`/descobrirFilmes`), com importação direta ao catálogo;
  - Busca por título dentro do próprio formulário de cadastro, pré-preenchendo os campos
    (incluindo pôster e nota pública) a partir de um resultado escolhido.
- **Interface** em Bootstrap 5 (via CDN, com Subresource Integrity), modo escuro alternável e
  efeitos visuais sutis.

## Stack técnica

| Camada | Tecnologia |
|---|---|
| Linguagem | Java (25) |
| Web | Servlets 4.0 + JSP/JSTL |
| Acesso a dados | JDBC (`PreparedStatement`, try-with-resources) |
| Banco de dados | MySQL/MariaDB |
| Build | Apache Maven |
| Servidor de aplicação | Apache Tomcat 9 |
| Testes | JUnit 5 |

Arquitetura em camadas: **JSP** (view) → **Servlet** (controller) → **Service** (regra de
negócio) → **DAO** (JDBC) → banco de dados. Ver detalhes em
[`docs/relatorio-tecnico/`](docs/relatorio-tecnico/).

## Como rodar localmente

### Pré-requisitos

- JDK 17+ (desenvolvido/testado com JDK 25)
- Apache Maven 3.9+
- MySQL ou MariaDB
- Apache Tomcat 9.x (**não** o Tomcat 10 — este projeto usa a API `javax.servlet`, não
  `jakarta.servlet`)

### 1. Banco de dados

Crie um schema e rode a DDL:

```bash
mysql -u <usuario> -p<senha> -h localhost <schema> < docs/modelagem/der.sql
```

Copie `src/main/resources/db.properties.example` para `db.properties` (mesmo diretório) e
preencha com suas credenciais — esse arquivo nunca é versionado.

### 2. Integração com o TMDB (opcional)

Copie `src/main/resources/tmdb.properties.example` para `tmdb.properties` e preencha com sua
[chave de API do TMDB](https://www.themoviedb.org/settings/api). Sem esse arquivo, o catálogo
funciona normalmente — só as telas de descoberta/importação ficam indisponíveis.

### 3. Build e deploy

```bash
mvn clean package -DskipTests
cp target/catalogo-simples-filme.war "$CATALINA_HOME/webapps/"
"$CATALINA_HOME/bin/startup.sh"
```

A aplicação fica disponível em `http://localhost:8080/catalogo-simples-filme/listarFilmes`.

### 4. Testes

```bash
mvn test
```

Os testes (`FilmeDAOTest`, 20 casos) rodam contra um banco MySQL/MariaDB real — configure
`src/test/resources/db.properties` apontando para um schema de teste dedicado (nunca o de
desenvolvimento, pois os testes limpam a tabela `filme` antes de cada caso).

## Estrutura do projeto

```
com.catalogo.model     → Filme (POJO)
com.catalogo.dao       → FilmeDAO (interface + implementação JDBC), FabricaDeConexoes
com.catalogo.service   → FilmeValidator (validação compartilhada)
com.catalogo.servlet   → Servlets (controllers) e Filters (codificação UTF-8, headers de segurança)
com.catalogo.tmdb      → Cliente da integração opcional com o TMDB
src/main/webapp/WEB-INF/jsp → páginas JSP (view)
docs/modelagem         → diagrama de classes (PlantUML) e DER/script SQL
docs/relatorio-tecnico → relatório técnico do projeto (LaTeX)
docs/manual-usuario.md → manual do usuário simplificado
```

## Documentação

- **Relatório técnico completo**: [`docs/relatorio-tecnico/relatorio-tecnico.tex`](docs/relatorio-tecnico/relatorio-tecnico.tex)
  (introdução, metodologia, modelagem, arquitetura, resultados, segurança, conclusões e
  referências).
- **Manual do usuário**: [`docs/manual-usuario.md`](docs/manual-usuario.md).
- **Modelagem do sistema**: [`docs/modelagem/`](docs/modelagem/) (diagrama de classes e DER).
- **Javadoc**: presente em todas as classes/métodos públicos de `dao`, `servlet` e `service`.

## Segurança

Todo acesso a banco via `PreparedStatement`; toda saída de dado do usuário em JSP escapada via
`<c:out>`; validação de entrada centralizada antes de qualquer persistência; nenhuma credencial
hardcoded; cabeçalhos HTTP de defesa em profundidade (`X-Frame-Options`, `Content-Security-Policy`,
`X-Content-Type-Options`, cookie de sessão com `SameSite`). Detalhes completos — incluindo a
trilha de teste de penetração *blackbox* (OWASP Top 10) — na Seção "Considerações sobre
Segurança" do relatório técnico.
