package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Classe associativa da relacao N:M entre Estudante e Disciplina.
 *
 * O modelo relacional nao representa diretamente uma associacao muitos-para-
 * muitos: cria-se uma relacao de associacao com duas chaves estrangeiras. Como
 * a associacao possui atributos proprios (semestre, nota, frequencia), ela e
 * modelada como classe associativa, e esses atributos viram colunas da mesma
 * tabela.
 *
 * A chave primaria adotada e uma coluna de implementacao (id). A unicidade
 * logica da associacao e garantida pela restricao UNIQUE composta sobre
 * (estudante_id, disciplina_id, semestre), declarada com uniqueCombo = true.
 */
@DatabaseTable(tableName = "inscricao")
public class Inscricao {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "estudante_id", foreign = true, foreignAutoRefresh = true,
                   canBeNull = false, uniqueCombo = true)
    private Estudante estudante;

    @DatabaseField(columnName = "disciplina_id", foreign = true, foreignAutoRefresh = true,
                   canBeNull = false, uniqueCombo = true)
    private Disciplina disciplina;

    /** Periodo letivo no formato "2026/1". Compoe a chave logica da associacao. */
    @DatabaseField(canBeNull = false, width = 6, uniqueCombo = true)
    private String semestre;

    /** Atributo da classe associativa; Double (e nao double) para aceitar NULL. */
    @DatabaseField
    private Double nota;

    /** Frequencia em porcentagem. */
    @DatabaseField
    private int frequencia;

    public Inscricao() {
    }

    public Inscricao(Estudante estudante, Disciplina disciplina, String semestre) {
        this.estudante = estudante;
        this.disciplina = disciplina;
        this.semestre = semestre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Estudante getEstudante() {
        return estudante;
    }

    public void setEstudante(Estudante estudante) {
        this.estudante = estudante;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public int getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(int frequencia) {
        this.frequencia = frequencia;
    }

    /** Regra de negocio simples, usada nos testes do notebook. */
    public boolean isAprovado() {
        return nota != null && nota >= 6.0 && frequencia >= 75;
    }

    @Override
    public String toString() {
        return "Inscricao{id=" + id
                + ", estudante=" + (estudante == null ? "?" : estudante.getMatricula())
                + ", disciplina=" + (disciplina == null ? "?" : disciplina.getCodigo())
                + ", semestre='" + semestre + '\''
                + ", nota=" + nota
                + ", frequencia=" + frequencia
                + '}';
    }
}
