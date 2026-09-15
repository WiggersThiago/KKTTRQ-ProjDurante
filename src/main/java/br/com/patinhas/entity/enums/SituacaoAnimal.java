package br.com.patinhas.entity.enums;


public enum SituacaoAnimal {

    NORMAL("Normal"),
    EM_TRATAMENTO("Em tratamento"),
    STAND_BY("Stand by");

    private final String descricao;

    SituacaoAnimal(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}