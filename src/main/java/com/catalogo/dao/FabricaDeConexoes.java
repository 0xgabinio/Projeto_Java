package com.catalogo.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fábrica única de conexões JDBC do projeto.
 * <p>
 * Lê host/usuário/senha de {@code db.properties} (raiz do classpath) — nunca credenciais
 * hardcoded no código-fonte (ver {@code docs/sdd/seguranca/checklist-owasp-top10.md},
 * categoria A02/CWE-798). {@code db.properties} nunca é versionado (ver {@code .gitignore});
 * copie {@code db.properties.example} e preencha suas credenciais locais.
 */
public final class FabricaDeConexoes {

    private static final Properties PROPRIEDADES = new Properties();

    static {
        try (InputStream input = FabricaDeConexoes.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IllegalStateException(
                        "db.properties não encontrado no classpath. Copie "
                                + "db.properties.example para db.properties e preencha suas "
                                + "credenciais locais de banco de dados.");
            }
            PROPRIEDADES.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao carregar db.properties", e);
        }

        // Força o registro do driver JDBC explicitamente. Necessário em alguns servidores de
        // aplicação (ex.: Tomcat): quando o driver vem empacotado em WEB-INF/lib, o
        // DriverManager pode não descobri-lo automaticamente via ServiceLoader, porque sua
        // inicialização estática ocorre com o classloader comum do servidor, não com o
        // classloader da webapp — resultando em "No suitable driver found" mesmo com o JAR
        // presente. Chamar Class.forName aqui, com o classloader desta classe, garante que o
        // driver se registre (seu construtor estático chama DriverManager.registerDriver()).
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Driver JDBC do MySQL (com.mysql.cj.jdbc.Driver) não encontrado no "
                            + "classpath. Confirme a dependência mysql-connector-j no pom.xml.", e);
        }
    }

    private FabricaDeConexoes() {
    }

    /**
     * Obtém uma nova conexão JDBC configurada via {@code db.properties}.
     *
     * @return conexão aberta com o banco de dados configurado
     * @throws SQLException se a conexão não puder ser estabelecida
     */
    public static Connection getConexao() throws SQLException {
        String url = PROPRIEDADES.getProperty("db.url");
        String usuario = PROPRIEDADES.getProperty("db.usuario");
        String senha = PROPRIEDADES.getProperty("db.senha");
        return DriverManager.getConnection(url, usuario, senha);
    }
}
