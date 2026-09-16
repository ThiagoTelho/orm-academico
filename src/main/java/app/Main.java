package app;

import model.Curso;
import model.Disciplina;
import model.Estudante;
import model.Inscricao;
import model.Professor;
import model.Titulacao;
import persistence.Log;
import persistence.Persistencia;

import java.time.LocalDate;
import java.util.List;

/**
 * Roteiro de demonstracao da camada de persistencia, executavel fora do
 * notebook: cria o esquema, popula as tres conectividades (1:1, 1:N e N:M) e
 * percorre as associacoes nos dois sentidos.
 *
 * Execucao: mvn -q exec:java   (ou  -Dapp.database=outro.sqlite)
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Log.silencioso();
        String banco = System.getProperty("app.database", "academico.sqlite");

        try (Persistencia persistencia = new Persistencia(banco)) {
            persistencia.recriarEsquema();

            // ---- Professores -------------------------------------------------
            Professor ana = persistencia.professores()
                    .create(new Professor("P001", "Ana Ribeiro", "ana@ufg.br", Titulacao.DOUTOR));
            Professor bruno = persistencia.professores()
                    .create(new Professor("P002", "Bruno Carvalho", "bruno@ufg.br", Titulacao.MESTRE));

            // ---- Cursos (1:1 com Professor, via coordenador_id UNIQUE) -------
            Curso si = persistencia.cursos()
                    .create(new Curso("Sistemas de Informacao", "SI", 3200));
            persistencia.cursos().definirCoordenador(si, ana);

            // ---- Estudantes (1:N com Curso) ----------------------------------
            Estudante carla = persistencia.estudantes()
                    .create(new Estudante("2026001", "Carla Nunes", LocalDate.of(2004, 3, 12), si));
            Estudante diego = persistencia.estudantes()
                    .create(new Estudante("2026002", "Diego Alves", LocalDate.of(2003, 9, 30), si));

            // ---- Disciplinas (1:N com Curso e com Professor) -----------------
            Disciplina poo = persistencia.disciplinas()
                    .create(new Disciplina("INF0285", "Programacao Orientada a Objetos", 64, si, ana));
            Disciplina bd = persistencia.disciplinas()
                    .create(new Disciplina("INF0287", "Banco de Dados", 64, si, bruno));

            // ---- Inscricoes (N:M entre Estudante e Disciplina) ---------------
            String semestre = "2026/1";
            Inscricao i1 = persistencia.inscricoes().matricular(carla, poo, semestre);
            persistencia.inscricoes().matricular(carla, bd, semestre);
            persistencia.inscricoes().matricular(diego, poo, semestre);
            persistencia.inscricoes().lancarResultado(i1, 8.5, 92);

            // ---- Navegacao ---------------------------------------------------
            System.out.println("Banco: " + banco);
            System.out.println();

            System.out.println("[1:1] Coordenacao");
            Curso lido = persistencia.cursos().buscarPorSigla("SI");
            System.out.println("  " + lido.getNome() + " e coordenado por " + lido.getCoordenador().getNome());
            System.out.println("  " + ana.getNome() + " coordena " + persistencia.cursos().coordenadoPor(ana).getSigla());
            System.out.println();

            System.out.println("[1:N] Estudantes do curso " + lido.getSigla());
            for (Estudante estudante : persistencia.estudantes().doCurso(lido)) {
                System.out.println("  " + estudante);
            }
            System.out.println();

            System.out.println("[N:M] Disciplinas de " + carla.getNomeCompleto());
            for (Disciplina disciplina : persistencia.inscricoes().disciplinasDe(carla)) {
                System.out.println("  " + disciplina);
            }
            System.out.println();

            System.out.println("[N:M] Estudantes de " + poo.getCodigo());
            for (Estudante estudante : persistencia.inscricoes().estudantesDe(poo)) {
                System.out.println("  " + estudante.getNomeCompleto());
            }
            System.out.println();

            System.out.println("[Classe associativa] Historico de " + carla.getMatricula());
            List<Inscricao> historico = persistencia.inscricoes().historicoDe(carla);
            for (Inscricao inscricao : historico) {
                System.out.println("  " + inscricao + " aprovado=" + inscricao.isAprovado());
            }
            System.out.println();

            System.out.println("Linhas por tabela: professor=" + persistencia.professores().count()
                    + ", curso=" + persistencia.cursos().count()
                    + ", estudante=" + persistencia.estudantes().count()
                    + ", disciplina=" + persistencia.disciplinas().count()
                    + ", inscricao=" + persistencia.inscricoes().count());
        }
    }
}
