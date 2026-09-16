package persistence;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Repositorio generico (padrao DAO / Repository).
 *
 * Reune as operacoes comuns a todas as entidades -- materializacao, atualizacao
 * e remocao, os tres aspectos enumerados na aula -- para que as subclasses
 * tratem apenas das consultas especificas de cada entidade.
 *
 * Correspondencia com o modelo relacional:
 *   create      -> INSERT
 *   loadFromId  -> SELECT ... WHERE id = ?
 *   loadAll     -> SELECT * FROM tabela
 *   update      -> UPDATE ... WHERE id = ?
 *   delete      -> DELETE FROM ... WHERE id = ?
 *
 * @param <T>  tipo da entidade mapeada
 * @param <ID> tipo da chave primaria
 */
public abstract class Repositorio<T, ID> {

    protected final Database database;
    protected final Dao<T, ID> dao;
    private final Class<T> entidade;

    protected Repositorio(Database database, Class<T> entidade) {
        this.database = database;
        this.entidade = entidade;
        try {
            this.dao = DaoManager.createDao(database.getConnection(), entidade);
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao criar o DAO de " + entidade.getSimpleName(), e);
        }
    }

    /**
     * Cria a tabela a partir das anotacoes da entidade (geracao de esquema).
     * E o ORM que deriva o DDL: generatedId -> INTEGER PRIMARY KEY AUTOINCREMENT,
     * canBeNull = false -> NOT NULL, unique = true -> UNIQUE.
     */
    public void criarTabela() throws SQLException {
        TableUtils.createTableIfNotExists(database.getConnection(), entidade);
    }

    public void removerTabela() throws SQLException {
        TableUtils.dropTable(database.getConnection(), entidade, true);
    }

    /** DDL que o ORMLite emitiria para esta entidade (usado na documentacao). */
    public List<String> ddl() throws SQLException {
        return TableUtils.getCreateTableStatements(database.getConnection(), entidade);
    }

    /** CREATE: persiste o objeto e devolve-o ja com a chave primaria gerada. */
    public T create(T objeto) throws SQLException {
        int linhas = dao.create(objeto);
        if (linhas == 0) {
            throw new SQLException("Nenhuma linha inserida: o objeto nao foi salvo");
        }
        return objeto;
    }

    /** RETRIEVE por chave primaria; devolve null quando o id nao existe. */
    public T loadFromId(ID id) throws SQLException {
        return dao.queryForId(id);
    }

    /** RETRIEVE de toda a tabela. */
    public List<T> loadAll() throws SQLException {
        return dao.queryForAll();
    }

    /** UPDATE: grava no banco o estado corrente do objeto em memoria. */
    public T update(T objeto) throws SQLException {
        int linhas = dao.update(objeto);
        if (linhas == 0) {
            throw new SQLException("Nenhuma linha atualizada: o objeto nao existe no banco");
        }
        return objeto;
    }

    /** DELETE: remove a linha correspondente ao objeto. */
    public void delete(T objeto) throws SQLException {
        int linhas = dao.delete(objeto);
        if (linhas == 0) {
            throw new SQLException("Nenhuma linha removida: o objeto nao existe no banco");
        }
    }

    /** Recarrega do banco o estado do objeto (descarta alteracoes em memoria). */
    public void refresh(T objeto) throws SQLException {
        dao.refresh(objeto);
    }

    public long count() throws SQLException {
        return dao.countOf();
    }

    public QueryBuilder<T, ID> queryBuilder() {
        return dao.queryBuilder();
    }

    public Dao<T, ID> getDao() {
        return dao;
    }
}
