package model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Curso de graduacao.
 *
 * Concentra os tres tipos de conectividade estudados:
 *   - 1:1  Curso 0..1 -- 1 Professor (coordenacao): a coluna coordenador_id
 *          recebe a chave estrangeira e a restricao UNIQUE, que e justamente o
 *          que transforma um 1:N em 1:1 no modelo relacional.
 *   - 1:N  Curso 1 -- * Estudante e Curso 1 -- * Disciplina: a chave
 *          estrangeira fica nas tabelas do lado "muitos".
 */
@DatabaseTable(tableName = "curso")
public class Curso {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, unique = true)
    private String nome;

    @DatabaseField(canBeNull = false, unique = true, width = 10)
    private String sigla;

    @DatabaseField(columnName = "carga_horaria")
    private int cargaHoraria;

    /**
     * Lado 1:1 da coordenacao. unique = true impede que o mesmo professor
     * apareca como coordenador de dois cursos; a coluna aceita NULL porque a
     * participacao do curso na associacao e opcional (0..1).
     * foreignAutoRefresh = true faz o ORMLite ja trazer o objeto Professor
     * preenchido ao ler o curso, em vez de devolver apenas o id.
     */
    @DatabaseField(columnName = "coordenador_id", foreign = true,
                   foreignAutoRefresh = true, unique = true)
    private Professor coordenador;

    /** Lado "um" do 1:N com Estudante (nao gera coluna na tabela curso). */
    @ForeignCollectionField(eager = false)
    private ForeignCollection<Estudante> estudantes;

    /** Lado "um" do 1:N com Disciplina. */
    @ForeignCollectionField(eager = false)
    private ForeignCollection<Disciplina> disciplinas;

    public Curso() {
    }

    public Curso(String nome, String sigla, int cargaHoraria) {
        this.nome = nome;
        this.sigla = sigla;
        this.cargaHoraria = cargaHoraria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(int cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public Professor getCoordenador() {
        return coordenador;
    }

    public void setCoordenador(Professor coordenador) {
        this.coordenador = coordenador;
    }

    public ForeignCollection<Estudante> getEstudantes() {
        return estudantes;
    }

    public ForeignCollection<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    @Override
    public String toString() {
        return "Curso{id=" + id
                + ", sigla='" + sigla + '\''
                + ", nome='" + nome + '\''
                + ", coordenador=" + (coordenador == null ? "nenhum" : coordenador.getNome())
                + '}';
    }
}
