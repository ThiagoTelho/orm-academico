package persistence;

import com.j256.ormlite.jdbc.JdbcConnectionSource;

import java.sql.SQLException;

/**
 * Fonte de conexao com o SGBD (SQLite via JDBC).
 *
 * E o unico ponto do sistema que conhece a URL do banco. Trocar de SGBD
 * (SQLite -> MySQL/PostgreSQL) exige alterar esta classe e a dependencia do
 * driver; nem as entidades nem os repositorios mudam -- e esse isolamento o
 * objetivo da camada de persistencia.
 *
 * Aplica inicializacao tardia: a conexao fisica so e aberta na primeira chamada
 * de getConnection() e reaproveitada nas seguintes.
 */
public class Database implements AutoCloseable {

    private final String databaseName;
    private JdbcConnectionSource connection;

    public Database(String databaseName) {
        if (databaseName == null || databaseName.isBlank()) {
            throw new IllegalArgumentException("Nome do banco de dados nao informado");
        }
        this.databaseName = databaseName;
    }

    /** Banco em memoria: util para testes, desaparece quando a conexao e fechada. */
    public static Database emMemoria() {
        return new Database(":memory:");
    }

    /**
     * Devolve a JdbcConnectionSource do ORMLite. A URL "jdbc:sqlite:<arquivo>"
     * faz o driver criar o arquivo caso ele ainda nao exista.
     */
    public JdbcConnectionSource getConnection() throws SQLException {
        if (connection == null) {
            connection = new JdbcConnectionSource("jdbc:sqlite:" + databaseName);
        }
        return connection;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                System.err.println("Erro ao fechar a conexao: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
