package model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Estudante matriculado em um curso.
 *
 * Lado "muitos" do 1:N com Curso: e esta tabela que recebe a chave estrangeira
 * curso_id, exatamente como prescreve o mapeamento de associacoes um-para-muitos.
 * Participa tambem do N:M com Disciplina, intermediado pela classe associativa
 * Inscricao.
 */
@DatabaseTable(tableName = "estudante")
public class Estudante {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, unique = true)
    private String matricula;

    @DatabaseField(columnName = "nome_completo", canBeNull = false)
    private String nomeCompleto;

    /**
     * java.util.Date gravado como texto ISO (yyyy-MM-dd). O SQLite nao possui
     * tipo de data nativo: e o ORM que converte objeto <-> texto nos dois sentidos.
     */
    @DatabaseField(columnName = "data_nascimento", dataType = DataType.DATE_STRING,
                   format = "yyyy-MM-dd")
    private Date dataNascimento;

    @DatabaseField(columnName = "curso_id", foreign = true, foreignAutoRefresh = true,
                   canBeNull = false)
    private Curso curso;

    /** Inscricoes deste estudante: lado "um" do 1:N com a classe associativa. */
    @ForeignCollectionField(eager = false)
    private ForeignCollection<Inscricao> inscricoes;

    public Estudante() {
    }

    public Estudante(String matricula, String nomeCompleto, LocalDate dataNascimento, Curso curso) {
        this.matricula = matricula;
        this.nomeCompleto = nomeCompleto;
        setDataNascimento(dataNascimento);
        this.curso = curso;
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

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    /** Conveniencia: aceita java.time.LocalDate na camada de negocio. */
    public void setDataNascimento(LocalDate data) {
        this.dataNascimento = (data == null)
                ? null
                : Date.from(data.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public LocalDate getDataNascimentoComoLocalDate() {
        return (dataNascimento == null)
                ? null
                : dataNascimento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public ForeignCollection<Inscricao> getInscricoes() {
        return inscricoes;
    }

    @Override
    public String toString() {
        return "Estudante{id=" + id
                + ", matricula='" + matricula + '\''
                + ", nome='" + nomeCompleto + '\''
                + ", nascimento=" + getDataNascimentoComoLocalDate()
                + ", curso=" + (curso == null ? "nenhum" : curso.getSigla())
                + '}';
    }
}
