package persistence;

import model.Curso;
import model.Estudante;

import java.sql.SQLException;
import java.util.List;

/** Repositorio da entidade Estudante; inclui a navegacao do 1:N com Curso. */
public class EstudanteRepositorio extends Repositorio<Estudante, Integer> {

    public EstudanteRepositorio(Database database) {
        super(database, Estudante.class);
    }

    public Estudante buscarPorMatricula(String matricula) throws SQLException {
        return dao.queryBuilder().where().eq("matricula", matricula).queryForFirst();
    }

    /**
     * Lado "muitos" do 1:N: SELECT * FROM estudante WHERE curso_id = ?.
     * A consulta usa a chave estrangeira criada pelo mapeamento da associacao.
     */
    public List<Estudante> doCurso(Curso curso) throws SQLException {
        return dao.queryBuilder().where().eq("curso_id", curso.getId()).query();
    }

    /** Consulta por parte do nome (LIKE), com ordenacao. */
    public List<Estudante> buscarPorNome(String trecho) throws SQLException {
        return dao.queryBuilder()
                .orderBy("nome_completo", true)
                .where().like("nome_completo", "%" + trecho + "%")
                .query();
    }
}
