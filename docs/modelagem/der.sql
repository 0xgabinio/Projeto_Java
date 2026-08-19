-- DDL canônica do Catálogo Simples de Filmes.
-- Fonte de verdade única — toda spec em specs/*.md referencia esta tabela, nenhuma redefine
-- tipos de coluna. Gerado pela skill modelar-uml a partir da spec cadastrar-filme.md (primeira
-- funcionalidade a exigir o modelo de dados completo). SGBD padrão: MySQL/MariaDB (ver
-- CLAUDE.md para notas de portabilidade).

CREATE TABLE filme (
  id INT PRIMARY KEY AUTO_INCREMENT,
  titulo VARCHAR(255) NOT NULL,
  diretor VARCHAR(255),
  ano_lancamento INT,
  genero VARCHAR(100),
  sinopse TEXT,
  capa_url VARCHAR(500),
  nota_tmdb DECIMAL(3,1)
);

-- Migração para bancos já existentes (schemas criados antes de specs/integrar-tmdb.md):
-- ALTER TABLE filme ADD COLUMN capa_url VARCHAR(500);
-- ALTER TABLE filme ADD COLUMN nota_tmdb DECIMAL(3,1);
