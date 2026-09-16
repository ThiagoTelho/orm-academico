package persistence;

import model.Curso;
import model.Professor;

import java.sql.SQLException;
import java.util.List;

/** Repositorio da entidade Curso; concentra a navegacao 1:1 da coordenacao. */
public class CursoRepositorio extends Repositorio<Curso, Integer> {

    private final EstudanteRepositorio estudantes;
    private final DisciplinaRepositorio disciplinas;

    public CursoRepositorio(Database database,
                            EstudanteRepositorio estudantes,
                            DisciplinaRepositorio disciplinas) {
        super(database, Curso.class);
        this.estudantes = estudantes;
        this.disciplinas = disciplinas;
    }

    public Curso buscarPorSigla(String sigla) throws SQLException {
        return dao.queryBuilder().where().eq("sigla", sigla).queryForFirst();
    }

    /**
     * Navegacao inversa do 1:1: dado o professor, encontra o curso que ele
     * coordena. Como a coluna coordenador_id e UNIQUE, o resultado e no maximo
     * uma linha.
     */
    public Curso coordenadoPor(Professor professor) throws SQLException {
        return dao.queryBuilder().where().eq("coordenador_id", professor.getId()).queryForFirst();
    }

    /** Define o coordenador do curso e grava a alteracao. */
    public Curso definirCoordenador(Curso curso, Professor professor) throws SQLException {
        curso.setCoordenador(professor);
        return update(curso);
    }

    public List<Curso> semCoordenador() throws SQLException {
        return dao.queryBuilder().where().isNull("coordenador_id").query();
    }

    /** Quantas linhas de outras tabelas apontam para este curso. */
    public int dependentesDe(Curso curso) throws SQLException {
        return estudantes.doCurso(curso).size() + disciplinas.doCurso(curso).size();
    }

    /**
     * Impede a remocao de um curso ainda referenciado.
     *
     * O DDL gerado pelo ORMLite para o SQLite cria as colunas de chave
     * estrangeira, mas nao declara a restricao FOREIGN KEY: o banco aceitaria o
     * DELETE e deixaria linhas orfas em estudante e disciplina. Manter a
     * integridade referencial passa a ser responsabilidade desta camada.
     */
    @Override
    public void delete(Curso curso) throws SQLException {
        int dependentes = dependentesDe(curso);
        if (dependentes > 0) {
            throw new SQLException("O curso " + curso.getSigla() + " nao pode ser removido: "
                    + dependentes + " registro(s) ainda o referenciam");
        }
        super.delete(curso);
    }
}
