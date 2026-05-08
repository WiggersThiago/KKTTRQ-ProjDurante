package br.com.patinhas.config;

import br.com.patinhas.entity.InformacaoONG;
import br.com.patinhas.entity.Usuario;
import br.com.patinhas.entity.enums.RoleUsuario;
import br.com.patinhas.repository.InformacaoONGRepository;
import br.com.patinhas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Inicializa os dados mínimos do sistema:
 *
 * - Cria o administrador padrão (caso não exista).
 * - Cria o registro de informações institucionais (caso não exista).
 *
 * As credenciais do admin podem ser sobrescritas via {@code application.properties}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final InformacaoONGRepository informacaoONGRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${patinhas.admin.email}")
    private String adminEmail;

    @Value("${patinhas.admin.senha}")
    private String adminSenha;

    @Value("${patinhas.admin.nome}")
    private String adminNome;

    @Override
    public void run(String... args) {
        criarAdminPadrao();
        criarInformacoesPadrao();
    }

    private void criarAdminPadrao() {
        if (usuarioRepository.existsByEmail(adminEmail)) {
            return;
        }
        Usuario admin = Usuario.builder()
                .nome(adminNome)
                .email(adminEmail)
                .senha(passwordEncoder.encode(adminSenha))
                .role(RoleUsuario.ADMIN)
                .ativo(true)
                .build();
        usuarioRepository.save(admin);
        log.info("Admin padrão criado: {}", adminEmail);
    }

    private void criarInformacoesPadrao() {
        if (informacaoONGRepository.count() > 0) {
            return;
        }
        InformacaoONG info = InformacaoONG.builder()
                .nomeONG("ONG Patinhas")
                .quemSomos("Somos uma ONG dedicada ao resgate, cuidado e adoção responsável de animais.")
                .proposito("Oferecer um lar seguro e amoroso para cada animal abandonado, promovendo a adoção responsável.")
                .pixDoacao("contato@patinhas.org")
                .enderecoDoacao("Rua Exemplo, 123 - Centro")
                .telefoneContato("(00) 0000-0000")
                .emailContato("contato@patinhas.org")
                .instagram("https://instagram.com/")
                .facebook("https://facebook.com/")
                .build();
        informacaoONGRepository.save(info);
        log.info("Informações institucionais padrão criadas.");
    }
}
