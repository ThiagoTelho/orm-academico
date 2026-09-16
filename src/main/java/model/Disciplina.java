package model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Disciplina da grade de um curso, ministrada por um professor.
 *
 * Lado "muitos" de duas associacoes 1:N (com Curso e com Professor): por isso a
 * tabela possui duas chaves estrangeiras. Participa do N:M com Estudante
 * atraves da classe associativa Inscricao.
 */
@DatabaseTable(tableName = "disciplina")
public class Disciplina {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, unique = true, width = 12)
    private String codigo;

    @DatabaseField(canBeNull = false)
    private String nome;

    @DatabaseField(columnName = "carga_horaria")
    private int cargaHoraria;

    @DatabaseField(columnName = "curso_id", foreign = true, foreignAutoRefresh = true,
                   canBeNull = false)
    private Curso curso;

    @DatabaseField(columnName = "professor_id", foreign = true, foreignAutoRefresh = true)
    private Professor professor;

    @ForeignCollectionField(eager = false)
    private ForeignCollection<Inscricao> inscricoes;

    public Disciplina() {
    }

    public Disciplina(String codigo, String nome, int cargaHoraria, Curso curso, Professor professor) {
        this.codigo = codigo;
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
        this.curso = curso;
        this.professor = professor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(int cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public ForeignCollection<Inscricao> getInscricoes() {
        return inscricoes;
    }

    @Override
    public String toString() {
        return "Disciplina{id=" + id
                + ", codigo='" + codigo + '\''
                + ", nome='" + nome + '\''
                + ", cargaHoraria=" + cargaHoraria
                + ", professor=" + (professor == null ? "a definir" : professor.getNome())
                + '}';
    }
}
