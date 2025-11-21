package br.com.devforge.config.security;

import br.com.devforge.model.Usuario;
import br.com.devforge.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger LOGGER = Logger.getLogger(CustomOAuth2UserService.class.getName());

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provedor = userRequest.getClientRegistration().getRegistrationId();
        return processarUsuario(provedor, oAuth2User);
    }

    private OAuth2User processarUsuario(String provedor, OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Logs para debug no Render (ajuda a ver o que está chegando)
        LOGGER.info("Login recebido via: " + provedor);

        String email = null;
        String nome = null;
        String avatarUrl = null;
        String githubUsername = null;

        if ("github".equals(provedor)) {
            // Tenta pegar o email direto
            email = (String) attributes.get("email");
            nome = (String) attributes.get("name");
            avatarUrl = (String) attributes.get("avatar_url");
            githubUsername = (String) attributes.get("login");

            // Se o email for nulo (privado), usamos o login (username) que é único e público
            if (email == null) {
                if (githubUsername != null) {
                    email = githubUsername + "@no-email.github.com";
                    LOGGER.warning("Email privado. Usando identificador baseado no login: " + email);
                } else {
                    // Caso extremo: sem email e sem login (muito raro no GitHub)
                    throw new OAuth2AuthenticationException("Erro: GitHub não retornou email nem login.");
                }
            }
        } else {
            // Google
            email = (String) attributes.get("email");
            nome = (String) attributes.get("name");
            avatarUrl = (String) attributes.get("picture");
        }

        // Validação final
        if (email == null) {
            throw new OAuth2AuthenticationException("Falha crítica: Email não identificado.");
        }

        // Ajuste de nome
        if (nome == null || nome.isEmpty()) {
            nome = githubUsername != null ? githubUsername : "Dev Sem Nome";
        }

        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);
        Usuario usuario;

        if (usuarioExistente.isPresent()) {
            LOGGER.info("Login de veterano: " + email);
            usuario = usuarioExistente.get();
            if (usuario.getAvatarUrl() == null || usuario.getAvatarUrl().isEmpty()) {
                usuario.setAvatarUrl(avatarUrl);
            }
            // Mantém status atual
        } else {
            LOGGER.info("Novo cadastro iniciado: " + email);
            usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setNome(nome);
            usuario.setAvatarUrl(avatarUrl);
            usuario.setGithubUsername(githubUsername);
            usuario.setBio("Novo no DevForge.");
            usuario.setCadastroCompleto(false); // Trava de Onboarding
        }

        usuarioRepository.save(usuario);
        return oAuth2User;
    }
}