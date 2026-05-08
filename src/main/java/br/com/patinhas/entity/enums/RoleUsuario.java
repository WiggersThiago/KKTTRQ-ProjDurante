package br.com.patinhas.entity.enums;

/**
 * Papéis/perfis dos usuários do sistema.
 *
 * Apenas administradores da ONG têm acesso administrativo.
 */
public enum RoleUsuario {

    ADMIN("ROLE_ADMIN", "Administrador"),
    USUARIO("ROLE_USER", "Usuário");

    private final String authority;
    private final String descricao;

    RoleUsuario(String authority, String descricao) {
        this.authority = authority;
        this.descricao = descricao;
    }

    /**
     * Retorna o nome de authority esperado pelo Spring Security
     * (sempre prefixado por ROLE_).
     */
    public String getAuthority() {
        return authority;
    }

    public String getDescricao() {
        return descricao;
    }
}
