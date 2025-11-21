package br.com.devforge.controller;

import br.com.devforge.model.Usuario;
import br.com.devforge.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Endpoint "Quem sou eu?"
     * Chamado pelo Frontend para pegar os dados do usuário logado (XP, Nível, Avatar).
     */
    @GetMapping("/me")
    public Usuario getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            // Se não tiver ninguém logado, retorna null (Frontend trata isso)
            return null;
        }

        // Lógica para extrair o email (igual fizemos no Service)
        Map<String, Object> attributes = principal.getAttributes();
        String email = (String) attributes.get("email");

        // Fallback para GitHub que não retorna email público
        if (email == null) {
            String login = (String) attributes.get("login");
            if (login != null) {
                email = login + "@no-email.github.com";
            }
        }

        // Busca os dados ATUALIZADOS do banco (com XP e Nível)
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado no banco local"));
    }
}