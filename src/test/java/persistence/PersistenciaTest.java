package persistence;

import model.Curso;
import model.Disciplina;
import model.Estudante;
import model.Inscricao;
import model.Professor;
import model.Titulacao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Versao automatizada dos testes que o notebook executa de forma interativa.
 * Roda sobre um banco em memoria, recriado a cada teste.
 */
class PersistenciaTest {

    private Persistencia persistencia;
    private Professor ana;
    private Curso si;
    private Estudante carla;
    private Disciplina poo;

    @BeforeEach
    void prepararBanco() throws SQLException {
        Log.silencioso();
        persistencia = new Persistencia(Database.emMemoria());
        persistencia.criarEsquema();

        ana = persistencia.professores()
                .create(new Professor("P001", "Ana Ribeiro", "ana@ufg.br", Titulacao.DOUTOR));
        si = persistencia.cursos().create(new Curso("Sistemas de Informacao", "SI", 3200));
        carla = persistencia.estudantes()
                .create(new Estudante("2026001", "Carla Nunes", LocalDate.of(2004, 3, 12), si));
        poo = persistencia.disciplinas()
                .create(new Disciplina("INF0285", "Programacao OO", 64, si, ana));
    }

    @AfterEach
    void encerrar() {
        persistencia.close();
    }

    @Test
    @DisplayName("o esquema gerado reflete o mapeamento das tres conectividades")
    void esquemaGerado() throws SQLException {
        String ddl = String.join("\n", persistencia.ddl());
        assertTrue(ddl.contains("UNIQUE (`coordenador_id`)"), "1:1 exige UNIQUE na chave estrangeira");
        assertTrue(ddl.contains("`curso_id` INTEGER NOT NULL"), "1:N exige chave estrangeira no lado muitos");
        assertTrue(ddl.contains("UNIQUE (`estudante_id`,`disciplina_id`,`semestre`)"),
                "N:M exige a tabela de associacao com chave logica");
    }

    @Test
    @DisplayName("INSERT devolve a chave primaria gerada pelo banco")
    void chavePrimariaGerada() {
        assertTrue(carla.getId() > 0);
    }

    @Test
    @DisplayName("1:1 - a restricao UNIQUE impede dois cursos com o mesmo coordenador")
    void umParaUm() throws SQLException {
        persistencia.cursos().definirCoordenador(si, ana);
        assertEquals(si.getId(), persistencia.cursos().coordenadoPor(ana).getId());

        Curso cc = persistencia.cursos().create(new Curso("Ciencia da Computacao", "CC", 3400));
        assertThrows(SQLException.class, () -> persistencia.cursos().definirCoordenador(cc, ana));
    }

    @Test
    @DisplayName("1:N - a chave estrangeira fica no lado muitos")
    void umParaMuitos() throws SQLException {
        persistencia.estudantes()
                .create(new Estudante("2026002", "Diego Alves", LocalDate.of(2003, 9, 30), si));

        assertEquals(2, persistencia.estudantes().doCurso(si).size());
        assertEquals(1, persistencia.disciplinas().doCurso(si).size());
        assertEquals(1, persistencia.disciplinas().ministradasPor(ana).size());
    }

    @Test
    @DisplayName("N:M - a tabela de associacao liga os dois lados, sem repeticao na navegacao")
    void muitosParaMuitos() throws SQLException {
        persistencia.inscricoes().matricular(carla, poo, "2026/1");
        persistencia.inscricoes().matricular(carla, poo, "2026/2");

        assertEquals(2, persistencia.inscricoes().historicoDe(carla).size());
        assertEquals(1, persistencia.inscricoes().disciplinasDe(carla).size());
        assertEquals(1, persistencia.inscricoes().estudantesDe(poo).size());
    }

    @Test
    @DisplayName("N:M - inscricao duplicada no mesmo semestre e recusada")
    void inscricaoDuplicada() throws SQLException {
        persistencia.inscricoes().matricular(carla, poo, "2026/1");
        assertThrows(SQLException.class,
                () -> persistencia.inscricoes().matricular(carla, poo, "2026/1"));
    }

    @Test
    @DisplayName("classe associativa - nota e frequencia sao atributos da associacao")
    void classeAssociativa() throws SQLException {
        Inscricao inscricao = persistencia.inscricoes().matricular(carla, poo, "2026/1");
        persistencia.inscricoes().lancarResultado(inscricao, 8.5, 92);

        Inscricao relida = persistencia.inscricoes().loadFromId(inscricao.getId());
        assertEquals(8.5, relida.getNota());
        assertTrue(relida.isAprovado());
        assertEquals(1, persistencia.inscricoes().aprovadosEm(poo, "2026/1").size());
    }

    @Test
    @DisplayName("integridade referencial - curso referenciado nao pode ser removido")
    void remocaoBloqueada() {
        assertTrue(assertThrows(SQLException.class, () -> persistencia.cursos().delete(si))
                .getMessage().contains("nao pode ser removido"));
    }

    @Test
    @DisplayName("remocao em cascata - as inscricoes saem antes do estudante")
    void remocaoEmCascata() throws SQLException {
        persistencia.inscricoes().matricular(carla, poo, "2026/1");

        assertEquals(1, persistencia.removerEstudanteEmCascata(carla));
        assertEquals(0, persistencia.inscricoes().count());
        assertNull(persistencia.estudantes().buscarPorMatricula("2026001"));
        assertNotNull(persistencia.disciplinas().buscarPorCodigo("INF0285"));
    }

    @Test
    @DisplayName("materializacao - o objeto volta do banco com o mesmo estado")
    void materializacao() throws SQLException {
        Estudante relida = persistencia.estudantes().buscarPorMatricula("2026001");
        assertEquals("Carla Nunes", relida.getNomeCompleto());
        assertEquals(LocalDate.of(2004, 3, 12), relida.getDataNascimentoComoLocalDate());
        assertEquals("SI", relida.getCurso().getSigla());
    }
}
