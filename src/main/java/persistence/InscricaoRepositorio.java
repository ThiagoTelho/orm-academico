package persistence;

import com.j256.ormlite.stmt.QueryBuilder;
import model.Disciplina;
import model.Estudante;
import model.Inscricao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio da classe associativa Inscricao.
 *
 * E aqui que a associacao N:M entre Estudante e Disciplina e operada: no modelo
 * relacional ela existe apenas como a tabela de associacao "inscricao", com as
 * chaves estrangeiras estudante_id e disciplina_id. Os metodos abaixo escondem
 * esse detalhe e devolvem objetos de dominio.
 */
public class InscricaoRepositorio extends Repositorio<Inscricao, Integer> {

    private final EstudanteRepositorio estudantes;
    private final DisciplinaRepositorio disciplinas;

    public InscricaoRepositorio(Database database,
                                EstudanteRepositorio estudantes,
                                DisciplinaRepositorio disciplinas) {
        super(database, Inscricao.class);
        this.estudantes = estudantes;
        this.disciplinas = disciplinas;
    }

    /** Inscreve um estudante em uma disciplina no semestre informado. */
    public Inscricao matricular(Estudante estudante, Disciplina disciplina, String semestre)
            throws SQLException {
        return create(new Inscricao(estudante, disciplina, semestre));
    }

    /** Lanca nota e frequencia de uma inscricao ja existente (UPDATE). */
    public Inscricao lancarResultado(Inscricao inscricao, double nota, int frequencia)
            throws SQLException {
        inscricao.setNota(nota);
        inscricao.setFrequencia(frequencia);
        return update(inscricao);
    }

    /** Todas as inscricoes de um estudante (historico escolar). */
    public List<Inscricao> historicoDe(Estudante estudante) throws SQLException {
        return dao.queryBuilder()
                .orderBy("semestre", true)
                .where().eq("estudante_id", estudante.getId())
                .query();
    }

    /** Todas as inscricoes de uma disciplina em um semestre (diario de classe). */
    public List<Inscricao> turmaDe(Disciplina disciplina, String semestre) throws SQLException {
        return dao.queryBuilder().where()
                .eq("disciplina_id", disciplina.getId())
                .and().eq("semestre", semestre)
                .query();
    }

    /**
     * Navegacao N:M no sentido estudante -> disciplinas, resolvida por uma
     * juncao entre "disciplina" e "inscricao" (o ORMLite monta o INNER JOIN a
     * partir da chave estrangeira disciplina_id).
     *
     * O DISTINCT e necessario: a juncao produz uma linha por inscricao, e o
     * mesmo par (estudante, disciplina) pode aparecer em mais de um semestre.
     * Sem ele, a disciplina repetida sairia duas vezes na lista.
     */
    public List<Disciplina> disciplinasDe(Estudante estudante) throws SQLException {
        QueryBuilder<Inscricao, Integer> qbInscricao = dao.queryBuilder();
        qbInscricao.where().eq("estudante_id", estudante.getId());

        QueryBuilder<Disciplina, Integer> qbDisciplina = disciplinas.queryBuilder();
        return qbDisciplina.distinct().join(qbInscricao).query();
    }

    /** Navegacao N:M no sentido inverso: disciplina -> estudantes. */
    public List<Estudante> estudantesDe(Disciplina disciplina) throws SQLException {
        QueryBuilder<Inscricao, Integer> qbInscricao = dao.queryBuilder();
        qbInscricao.where().eq("disciplina_id", disciplina.getId());

        QueryBuilder<Estudante, Integer> qbEstudante = estudantes.queryBuilder();
        return qbEstudante.distinct().join(qbInscricao).query();
    }

    /** Aprovados em uma disciplina, aplicando a regra de negocio da entidade. */
    public List<Inscricao> aprovadosEm(Disciplina disciplina, String semestre) throws SQLException {
        List<Inscricao> aprovados = new ArrayList<>();
        for (Inscricao inscricao : turmaDe(disciplina, semestre)) {
            if (inscricao.isAprovado()) {
                aprovados.add(inscricao);
            }
        }
        return aprovados;
    }

    /** Cancela a inscricao (DELETE na tabela de associacao). */
    public void cancelar(Inscricao inscricao) throws SQLException {
        delete(inscricao);
    }

    /**
     * Cancela todas as inscricoes de um estudante e devolve quantas foram
     * removidas. Usado na remocao em cascata: antes de apagar o objeto "todo",
     * apagam-se as linhas da associacao que o referenciam.
     */
    public int cancelarTodasDe(Estudante estudante) throws SQLException {
        int removidas = 0;
        for (Inscricao inscricao : historicoDe(estudante)) {
            delete(inscricao);
            removidas++;
        }
        return removidas;
    }
}
