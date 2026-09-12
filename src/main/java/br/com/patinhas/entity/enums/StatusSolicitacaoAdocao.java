package br.com.patinhas.entity.enums;

/**
 * Status do ciclo de vida de uma solicitação de adoção.
 */
public enum StatusSolicitacaoAdocao {

    NOVA("Nova"),
    EM_ANALISE("Em análise"),
    CONTATADO("Contatado"),
    APROVADA("Aprovada"),
    CONCLUIDA("Concluída"),
    RECUSADA("Recusada");

    private final String descricao;

    StatusSolicitacaoAdocao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
