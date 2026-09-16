package model;

/**
 * Tipo enumerado do atributo Professor.titulacao.
 *
 * Demonstra o mapeamento de um tipo que nao existe no modelo relacional: o
 * ORMLite converte a constante para texto na coluna (DataType.ENUM_STRING) e
 * a reconverte para a constante Java na leitura.
 */
public enum Titulacao {
    GRADUADO,
    ESPECIALISTA,
    MESTRE,
    DOUTOR
}
