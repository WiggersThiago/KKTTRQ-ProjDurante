package br.com.patinhas.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Configuração central do Spring Security para o sistema da ONG Patinhas.
 *
 * - Autenticação por sessão (form login).
 * - Senhas armazenadas com BCrypt.
 * - Rotas públicas configuradas explicitamente.
 * - Tudo dentro de /admin/** exige role ADMIN.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos
                .requestMatchers("/css/**", "/js/**", "/img/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                // Páginas públicas
                .requestMatchers(
                        "/", "/home",
                        "/animais", "/animais/**",
                        "/denuncia", "/denuncia/**",
                        "/sobre",
                        "/login", "/login/**",
                        "/erro", "/error"
                ).permitAll()
                // API pública (denúncia anônima e listagem pública de animais/eventos/info)
                .requestMatchers("/api/v1/public/**").permitAll()
                // Toda a área administrativa exige role ADMIN
                .requestMatchers("/admin/**", "/api/v1/admin/**").hasRole("ADMIN")
                // Demais rotas exigem autenticação
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("senha")
                .successHandler(adminSuccessHandler())
                .failureHandler(loginFailureHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/erro?codigo=403")
            )
            // --- Estratégia CSRF (habilitado por padrão; desabilitamos apenas onde indicado) ---
            //
            // 1) /api/v1/public/** — CSRF DESABILITADO
            //    Endpoints anônimos para integrações externas (ex.: POST /denuncias sem sessão).
            //    Não há cookie de sessão autenticada a proteger; exigir token bloquearia clientes
            //    não-browser.
            //
            // 2) /api/v1/admin/** — CSRF ATIVO (não entra em ignoringRequestMatchers)
            //    Mutações (POST/PUT/DELETE) são chamadas pelo painel admin via sessão do browser.
            //    Sem CSRF, páginas maliciosas poderiam forçar ações em nome de um admin logado.
            //    O JS do painel envia o token no header X-CSRF-TOKEN (ver static/js/admin-api.js).
            //
            // 3) /admin/** (páginas MVC) — CSRF ATIVO
            //    Formulários server-side recebem o parâmetro _csrf via Thymeleaf/Spring Security.
            //
            // 4) Demais rotas (login, logout, forms públicos) — CSRF ATIVO (padrão do Spring Security).
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/v1/public/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            );

        return http.build();
    }

    /**
     * Após o login, redireciona o admin para o dashboard.
     */
    @Bean
    public SimpleUrlAuthenticationSuccessHandler adminSuccessHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/admin/dashboard");
        handler.setAlwaysUseDefaultTargetUrl(false);
        return handler;
    }

    @Bean
    public SimpleUrlAuthenticationFailureHandler loginFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler("/login?error");
    }
}
