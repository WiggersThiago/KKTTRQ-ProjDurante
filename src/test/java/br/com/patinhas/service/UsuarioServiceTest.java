package br.com.patinhas.service;

import br.com.patinhas.dto.request.UsuarioRequestDTO;
import br.com.patinhas.exception.BusinessException;
import br.com.patinhas.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void cadastrar_comEmailDuplicado_deveLancarBusinessException() {
        UsuarioRequestDTO dto = UsuarioRequestDTO.builder()
                .nome("Administrador")
                .email("admin@patinhas.org")
                .senha("123456")
                .build();

        when(usuarioRepository.existsByEmail("admin@patinhas.org")).thenReturn(true);

        assertThrows(BusinessException.class, () -> usuarioService.cadastrar(dto));

        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void atualizarSenha_comSenhaCurta_deveLancarBusinessException() {
        assertThrows(BusinessException.class, () -> usuarioService.atualizarSenha(1L, "12345"));

        verify(usuarioRepository, never()).findById(any());
        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }
}
