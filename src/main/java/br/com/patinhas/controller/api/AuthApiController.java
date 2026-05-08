package br.com.patinhas.controller.api;

import br.com.patinhas.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoints de apoio para autenticação.
 *
 * O fluxo principal de login/logout é tratado por form login do Spring Security
 * (POST /login e POST /logout). Esta API expõe apenas a verificação do usuário
 * autenticado para o frontend.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthApiController {

    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.ok("Não autenticado.", Map.of("autenticado", false));
        }
        return ApiResponse.ok(Map.of(
                "autenticado", true,
                "email", authentication.getName(),
                "authorities", authentication.getAuthorities()
        ));
    }
}
