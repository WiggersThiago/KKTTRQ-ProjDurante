package br.com.patinhas.entity.enums;

/**
 * Status do ciclo de vida de uma denúncia recebida pela ONG.
 */
public enum StatusDenuncia {

    PENDENTE("Pendente"),
    EM_ANALISE("Em análise"),
    EM_ATENDIMENTO("Em atendimento"),
    RESOLVIDA("Resolvida"),
    ARQUIVADA("Arquivada");

    private final String descricao;

    StatusDenuncia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
