-- DDL canônica do Catálogo Simples de Livros/Filmes.
-- Fonte de verdade única — toda spec em specs/*.md referencia esta tabela, nenhuma redefine
-- tipos de coluna. Gerado pela skill modelar-uml a partir da spec cadastrar-item.md (primeira
-- funcionalidade a exigir o modelo de dados completo). SGBD padrão: MySQL (ver CLAUDE.md para
-- notas de portabilidade).

CREATE TABLE item_midia (
  id INT PRIMARY KEY AUTO_INCREMENT,
  titulo VARCHAR(255) NOT NULL,
  autor_diretor VARCHAR(255),
  ano_lancamento INT,
  genero VARCHAR(100),
  sinopse TEXT,
  tipo_midia VARCHAR(50) NOT NULL
);
