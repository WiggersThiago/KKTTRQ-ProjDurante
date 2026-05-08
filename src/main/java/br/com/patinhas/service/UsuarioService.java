package br.com.patinhas.service;

import br.com.patinhas.dto.request.UsuarioRequestDTO;
import br.com.patinhas.dto.response.UsuarioResponseDTO;
import br.com.patinhas.entity.Usuario;
import br.com.patinhas.entity.enums.RoleUsuario;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.exception.ResourceNotFoundException;
import br.com.patinhas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return UsuarioResponseDTO.fromEntity(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Já existe um usuário cadastrado com este e-mail.");
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .role(dto.getRole() == null ? RoleUsuario.ADMIN : dto.getRole())
                .ativo(true)
                .build();

        log.info("Criando usuário: {}", dto.getEmail());
        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public void desativar(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void atualizarSenha(Long id, String novaSenha) {
        if (novaSenha == null || novaSenha.length() < 6) {
            throw new BusinessException("A senha deve ter ao menos 6 caracteres.");
        }
        Usuario usuario = buscarEntidade(id);
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }
}
