package br.com.patinhas.dto.response;

import br.com.patinhas.entity.Usuario;
import br.com.patinhas.entity.enums.RoleUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private RoleUsuario role;
    private Boolean ativo;
    private LocalDateTime dataCriacao;

    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .role(usuario.getRole())
                .ativo(usuario.getAtivo())
                .dataCriacao(usuario.getDataCriacao())
                .build();
    }
}
