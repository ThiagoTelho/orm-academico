package persistence;

import model.Professor;

import java.sql.SQLException;

/** Repositorio da entidade Professor. */
public class ProfessorRepositorio extends Repositorio<Professor, Integer> {

    public ProfessorRepositorio(Database database) {
        super(database, Professor.class);
    }

    /** Consulta pela chave candidata (matricula funcional). */
    public Professor buscarPorMatricula(String matricula) throws SQLException {
        return dao.queryBuilder().where().eq("matricula", matricula).queryForFirst();
    }
}
