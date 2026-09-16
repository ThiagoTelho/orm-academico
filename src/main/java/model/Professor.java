package model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Docente do sistema academico.
 *
 * Participa de duas associacoes:
 *   - 1:1 com Curso (um professor coordena no maximo um curso). A chave
 *     estrangeira fica em "curso", conforme a regra do mapeamento 1:1: o lado
 *     com participacao opcional recebe a coluna.
 *   - 1:N com Disciplina (um professor ministra varias disciplinas). A chave
 *     estrangeira fica no lado "muitos", isto e, na tabela "disciplina".
 */
@DatabaseTable(tableName = "professor")
public class Professor {

    @DatabaseField(generatedId = true)
    private int id;

    /** Matricula funcional; unique = true gera a restricao UNIQUE na coluna. */
    @DatabaseField(canBeNull = false, unique = true)
    private String matricula;

    @DatabaseField(canBeNull = false)
    private String nome;

    @DatabaseField
    private String email;

    /** Enumeracao gravada como texto ("DOUTOR", "MESTRE", ...). */
    @DatabaseField(dataType = DataType.ENUM_STRING)
    private Titulacao titulacao;

    /**
     * Lado "um" da associacao 1:N com Disciplina. Nao vira coluna: e o ORMLite
     * que monta o SELECT ... WHERE professor_id = ? quando a colecao e
     * percorrida (eager = false => carga tardia).
     */
    @ForeignCollectionField(eager = false)
    private ForeignCollection<Disciplina> disciplinas;

    /** Construtor sem argumentos exigido pelo ORMLite (instanciacao por reflexao). */
    public Professor() {
    }

    public Professor(String matricula, String nome, String email, Titulacao titulacao) {
        this.matricula = matricula;
        this.nome = nome;
        this.email = email;
        this.titulacao = titulacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Titulacao getTitulacao() {
        return titulacao;
    }

    public void setTitulacao(Titulacao titulacao) {
        this.titulacao = titulacao;
    }

    public ForeignCollection<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    @Override
    public String toString() {
        return "Professor{id=" + id
                + ", matricula='" + matricula + '\''
                + ", nome='" + nome + '\''
                + ", titulacao=" + titulacao + '}';
    }
}
