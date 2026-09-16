package app;

import persistence.Database;
import persistence.Log;
import persistence.Persistencia;

/**
 * Imprime o DDL que o ORMLite deriva das anotacoes das entidades.
 *
 * E a prova concreta do mapeamento objeto-relacional: o esquema do banco nao
 * foi escrito a mao, foi gerado a partir do modelo de classes.
 *
 * Execucao: java -cp "target/orm-academico-1.0.0.jar:target/dependency/*" app.GerarEsquema > docs/esquema.sql
 */
public class GerarEsquema {

    public static void main(String[] args) throws Exception {
        Log.silencioso();
        try (Persistencia persistencia = new Persistencia(Database.emMemoria())) {
            System.out.println("-- Esquema gerado pelo ORMLite a partir das anotacoes das entidades");
            System.out.println("-- (TableUtils.getCreateTableStatements). SGBD: SQLite.");
            System.out.println();
            for (String comando : persistencia.ddl()) {
                System.out.println(comando + ";");
            }
        }
    }
}
