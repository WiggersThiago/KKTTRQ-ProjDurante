package br.com.patinhas.dto.response;

import br.com.patinhas.entity.InformacaoONG;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformacaoONGResponseDTO {

    private Long id;
    private String nomeONG;
    private String quemSomos;
    private String proposito;
    private String pixDoacao;
    private String enderecoDoacao;
    private String telefoneContato;
    private String emailContato;
    private String instagram;
    private String facebook;

    public static InformacaoONGResponseDTO fromEntity(InformacaoONG info) {
        if (info == null) {
            return null;
        }
        return InformacaoONGResponseDTO.builder()
                .id(info.getId())
                .nomeONG(info.getNomeONG())
                .quemSomos(info.getQuemSomos())
                .proposito(info.getProposito())
                .pixDoacao(info.getPixDoacao())
                .enderecoDoacao(info.getEnderecoDoacao())
                .telefoneContato(info.getTelefoneContato())
                .emailContato(info.getEmailContato())
                .instagram(info.getInstagram())
                .facebook(info.getFacebook())
                .build();
    }
}
