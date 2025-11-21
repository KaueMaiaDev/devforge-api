package br.com.devforge.controller;

import br.com.devforge.model.Usuario;
import br.com.devforge.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

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
            return null; // Frontend trata isso como não logado
        }

        String email = getEmailFromPrincipal(principal);

        // Busca os dados ATUALIZADOS do banco (com XP e Nível)
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado no banco local"));
    }

    /**
     * Finaliza o cadastro do usuário (Onboarding).
     * Chamado quando o usuário clica em "Confirmar" na tela de boas-vindas.
     * * @param payload JSON contendo os dados confirmados (ex: nome)
     * @param principal Usuário logado via OAuth2
     */
    @PostMapping("/complete-onboarding")
    public Usuario completeOnboarding(@RequestBody Map<String, String> payload, @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            throw new RuntimeException("Não autenticado");
        }

        String email = getEmailFromPrincipal(principal);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Atualiza o nome se o usuário editou na tela de onboarding
        if (payload.containsKey("nome") && !payload.get("nome").trim().isEmpty()) {
            usuario.setNome(payload.get("nome"));
        }

        // O PULO DO GATO: Ativa a conta definitivamente
        usuario.setCadastroCompleto(true);

        return usuarioRepository.save(usuario);
    }

    /**
     * Método auxiliar para extrair o email do principal (Google ou GitHub).
     * Centraliza a lógica de fallback para evitar duplicação.
     */
    private String getEmailFromPrincipal(OAuth2User principal) {
        Map<String, Object> attributes = principal.getAttributes();
        String email = (String) attributes.get("email");

        // Fallback para GitHub que não retorna email público
        if (email == null) {
            String login = (String) attributes.get("login");
            if (login != null) {
                email = login + "@no-email.github.com";
            }
        }
        return email;
    }
}