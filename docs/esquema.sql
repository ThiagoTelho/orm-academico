-- Esquema gerado pelo ORMLite a partir das anotacoes das entidades
-- (TableUtils.getCreateTableStatements). SGBD: SQLite.

CREATE TABLE `professor` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `matricula` VARCHAR NOT NULL , `nome` VARCHAR NOT NULL , `email` VARCHAR , `titulacao` VARCHAR ,  UNIQUE (`matricula`)) ;
CREATE TABLE `curso` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `nome` VARCHAR NOT NULL , `sigla` VARCHAR NOT NULL , `carga_horaria` INTEGER , `coordenador_id` INTEGER ,  UNIQUE (`nome`),  UNIQUE (`sigla`),  UNIQUE (`coordenador_id`)) ;
CREATE TABLE `estudante` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `matricula` VARCHAR NOT NULL , `nome_completo` VARCHAR NOT NULL , `data_nascimento` VARCHAR , `curso_id` INTEGER NOT NULL ,  UNIQUE (`matricula`)) ;
CREATE TABLE `disciplina` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `codigo` VARCHAR NOT NULL , `nome` VARCHAR NOT NULL , `carga_horaria` INTEGER , `curso_id` INTEGER NOT NULL , `professor_id` INTEGER ,  UNIQUE (`codigo`)) ;
CREATE TABLE `inscricao` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `estudante_id` INTEGER NOT NULL , `disciplina_id` INTEGER NOT NULL , `semestre` VARCHAR NOT NULL , `nota` DOUBLE PRECISION , `frequencia` INTEGER , UNIQUE (`estudante_id`,`disciplina_id`,`semestre`) ) ;
