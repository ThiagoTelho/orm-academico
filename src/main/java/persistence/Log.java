package persistence;

import com.j256.ormlite.logger.LogBackendType;
import com.j256.ormlite.logger.LoggerFactory;

/**
 * Controle do log do ORMLite.
 *
 * Por padrao o framework escreve em DEBUG, no console, cada comando SQL que
 * emite -- util para estudar o mapeamento, ruidoso na execucao normal. O nivel
 * e lido pela biblioteca de log na primeira vez que uma classe do ORMLite e
 * carregada; por isso estes metodos devem ser chamados ANTES de qualquer outro
 * uso da camada de persistencia.
 *
 * Para inspecionar o SQL de uma consulta especifica ha um caminho melhor, que
 * nao depende do log: queryBuilder().prepare().getStatement() devolve a string
 * SQL que o ORM montou a partir das anotacoes.
 */
public final class Log {

    /** Propriedade lida pela biblioteca de log embarcada no ORMLite. */
    private static final String PROPRIEDADE_NIVEL = "com.j256.simplelogging.level";

    private Log() {
    }

    /** Somente erros: e o modo usado pela aplicacao e pelo notebook. */
    public static void silencioso() {
        definirNivel("ERROR");
    }

    /** Exibe no console cada comando SQL emitido pelo ORM. */
    public static void mostrarSql() {
        definirNivel("DEBUG");
    }

    /**
     * Fixa o nivel e forca o uso da implementacao de log embarcada no ORMLite.
     * Sem isso o framework adota qualquer biblioteca de log que encontre no
     * classpath (SLF4J, Log4j...), cuja configuracao e externa ao projeto.
     */
    private static void definirNivel(String nivel) {
        System.setProperty(PROPRIEDADE_NIVEL, nivel);
        LoggerFactory.setLogBackendType(LogBackendType.LOCAL);
    }
}
