package persistence;

import model.Curso;
import model.Disciplina;
import model.Professor;

import java.sql.SQLException;
import java.util.List;

/** Repositorio da entidade Disciplina. */
public class DisciplinaRepositorio extends Repositorio<Disciplina, Integer> {

    public DisciplinaRepositorio(Database database) {
        super(database, Disciplina.class);
    }

    public Disciplina buscarPorCodigo(String codigo) throws SQLException {
        return dao.queryBuilder().where().eq("codigo", codigo).queryForFirst();
    }

    /** 1:N: disciplinas da grade de um curso. */
    public List<Disciplina> doCurso(Curso curso) throws SQLException {
        return dao.queryBuilder().where().eq("curso_id", curso.getId()).query();
    }

    /** 1:N: disciplinas ministradas por um professor. */
    public List<Disciplina> ministradasPor(Professor professor) throws SQLException {
        return dao.queryBuilder().where().eq("professor_id", professor.getId()).query();
    }
}
