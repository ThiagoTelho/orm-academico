package persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fachada da camada de persistencia.
 *
 * Reune a conexao e os repositorios em um unico objeto, cuidando da ordem de
 * criacao e remocao das tabelas (as tabelas com chave estrangeira sao criadas
 * depois das tabelas referenciadas e removidas antes delas). A camada de
 * negocio conversa somente com esta fachada e com os objetos de dominio: nenhum
 * comando SQL atravessa essa fronteira.
 */
public class Persistencia implements AutoCloseable {

    private final Database database;
    private final ProfessorRepositorio professores;
    private final CursoRepositorio cursos;
    private final EstudanteRepositorio estudantes;
    private final DisciplinaRepositorio disciplinas;
    private final InscricaoRepositorio inscricoes;

    public Persistencia(String nomeDoBanco) {
        this(new Database(nomeDoBanco));
    }

    public Persistencia(Database database) {
        this.database = database;
        this.professores = new ProfessorRepositorio(database);
        this.estudantes = new EstudanteRepositorio(database);
        this.disciplinas = new DisciplinaRepositorio(database);
        this.cursos = new CursoRepositorio(database, estudantes, disciplinas);
        this.inscricoes = new InscricaoRepositorio(database, estudantes, disciplinas);
    }

    /** Repositorios na ordem de dependencia (referenciados antes dos referentes). */
    private List<Repositorio<?, ?>> repositorios() {
        List<Repositorio<?, ?>> lista = new ArrayList<>();
        lista.add(professores);
        lista.add(cursos);
        lista.add(estudantes);
        lista.add(disciplinas);
        lista.add(inscricoes);
        return lista;
    }

    /** Gera o esquema completo a partir das anotacoes das entidades. */
    public void criarEsquema() throws SQLException {
        for (Repositorio<?, ?> repositorio : repositorios()) {
            repositorio.criarTabela();
        }
    }

    /** Remove todas as tabelas, em ordem inversa a de criacao. */
    public void removerEsquema() throws SQLException {
        List<Repositorio<?, ?>> lista = repositorios();
        for (int i = lista.size() - 1; i >= 0; i--) {
            lista.get(i).removerTabela();
        }
    }

    /** Recria o esquema vazio: util para comecar um teste em estado conhecido. */
    public void recriarEsquema() throws SQLException {
        removerEsquema();
        criarEsquema();
    }

    /** DDL completo que o ORM deriva das anotacoes (documentacao do esquema). */
    public List<String> ddl() throws SQLException {
        List<String> comandos = new ArrayList<>();
        for (Repositorio<?, ?> repositorio : repositorios()) {
            comandos.addAll(repositorio.ddl());
        }
        return comandos;
    }

    /**
     * Remocao em cascata do estudante: primeiro as linhas da tabela de
     * associacao que o referenciam, depois o proprio estudante. E a traducao,
     * em codigo, do comportamento que uma agregacao espera do SGBD quando o
     * objeto "todo" e excluido.
     */
    public int removerEstudanteEmCascata(model.Estudante estudante) throws SQLException {
        int inscricoesRemovidas = inscricoes.cancelarTodasDe(estudante);
        estudantes.delete(estudante);
        return inscricoesRemovidas;
    }

    public Database getDatabase() {
        return database;
    }

    public ProfessorRepositorio professores() {
        return professores;
    }

    public CursoRepositorio cursos() {
        return cursos;
    }

    public EstudanteRepositorio estudantes() {
        return estudantes;
    }

    public DisciplinaRepositorio disciplinas() {
        return disciplinas;
    }

    public InscricaoRepositorio inscricoes() {
        return inscricoes;
    }

    @Override
    public void close() {
        database.close();
    }
}
