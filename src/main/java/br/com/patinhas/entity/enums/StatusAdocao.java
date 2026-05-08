package br.com.patinhas.entity.enums;

/**
 * Status do animal no processo de adoção.
 */
public enum StatusAdocao {

    DISPONIVEL("Disponível para adoção"),
    EM_PROCESSO("Em processo de adoção"),
    ADOTADO("Adotado"),
    INDISPONIVEL("Indisponível");

    private final String descricao;

    StatusAdocao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
