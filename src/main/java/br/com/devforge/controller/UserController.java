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
     * Finaliza o cadastro do usuário (Onboarding) via POST.
     * Chamado quando o usuário clica em "Confirmar" na tela de boas-vindas.
     *
     * @param payload JSON contendo os dados confirmados (ex: nome)
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
     * Atualiza os dados do usuário (PUT /users/{id}).
     * Usado na tela de Onboarding para salvar bio, localização e ativar a conta.
     * ESTE É O MÉTODO QUE O SEU FRONTEND ESTÁ CHAMANDO AGORA.
     */
    @PutMapping("/{id}")
    public Usuario updateUser(@PathVariable Long id, @RequestBody Usuario dadosAtualizados, @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            throw new RuntimeException("Não autenticado");
        }

        // Segurança: Garante que o usuário só pode editar a si mesmo
        String emailLogado = getEmailFromPrincipal(principal);
        Usuario usuarioBanco = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        // Verifica se o usuário logado é dono da conta que está tentando alterar
        if (!usuarioBanco.getEmail().equals(emailLogado)) {
            throw new RuntimeException("Acesso negado: você só pode editar seu próprio perfil.");
        }

        // Atualiza os campos permitidos se não forem nulos
        if (dadosAtualizados.getBio() != null) {
            usuarioBanco.setBio(dadosAtualizados.getBio());
        }
        if (dadosAtualizados.getLocalizacao() != null) {
            usuarioBanco.setLocalizacao(dadosAtualizados.getLocalizacao());
        }
        if (dadosAtualizados.getGithubUsername() != null) {
            usuarioBanco.setGithubUsername(dadosAtualizados.getGithubUsername());
        }

        // Se o payload mandou true para cadastroCompleto, ativamos a conta
        if (dadosAtualizados.isCadastroCompleto()) {
            usuarioBanco.setCadastroCompleto(true);
        }

        return usuarioRepository.save(usuarioBanco);
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