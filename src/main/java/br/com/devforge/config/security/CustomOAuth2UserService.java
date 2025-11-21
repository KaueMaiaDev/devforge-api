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

/**
 * Serviço personalizado para processar o login via OAuth2 (Google/GitHub)
 * <p>
 * Esta classe intercepta a autenticação bem-sucedida no provedor externo e
 * sincroniza os dados do usuário com o banco de dados local
 * Garante que todo usuário logado tenha um registro na tabela 'usuarios' com XP e Nível
 * </p>
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger LOGGER = Logger.getLogger(CustomOAuth2UserService.class.getName());

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Método principal chamado automaticamente pelo Spring Security após o login no provedor.
     *
     * @param userRequest Dados da requisição de autenticação contendo tokens e infos do cliente.
     * @return O usuário autenticado (OAuth2User) enriquecido com dados da sessão.
     * @throws OAuth2AuthenticationException Em caso de falha na comunicação com o provedor.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Carrega os dados brutos do usuário (JSON vindo do Google/GitHub)
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Identifica qual provedor foi usado (ex: "google", "github")
        String provador = userRequest.getClientRegistration().getRegistrationId();

        // Sincroniza com o nosso banco de dados
        return processarUsuario(provador, oAuth2User);
    }

    private OAuth2User processarUsuario(String provedor, OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email;
        String nome;
        String avatarUrl;
        String githubUsername = null;

        // Padrão Google (OpenID Connect)
        if ("github".equals(provedor)) {
            email = (String) attributes.get("email");
            nome = (String) attributes.get("name");
            avatarUrl = (String) attributes.get("avatar_url");
            githubUsername = (String) attributes.get("login");

            // Fallback: GitHub nem sempre retorna o email público.
            // Se vier nulo, criamos um identificador único baseado no login.
            if (email == null && githubUsername != null) {
                email = githubUsername + "@no-email.github.com";
                LOGGER.warning("Email não retornado pelo GitHub. Usando fallback: " + email);
            }
        } else {
            // Padrão Google (OpenID Connect)
            email = (String) attributes.get("email");
            nome = (String) attributes.get("name");
            avatarUrl = (String) attributes.get("picture");
        }

        // Verificação de Integridade
        if (email == null) {
            LOGGER.severe("Erro: Provedor " + provedor + " não retornou um email identificável.");
            throw new OAuth2AuthenticationException("Email não encontrado no provedor OAuth2");
        }

        // Persistência no Banco
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);
        Usuario usuario;

        if (usuarioExistente.isPresent()) {
            // Usuário JÁ EXISTE (Atualiza)
            LOGGER.info("Usuário existente logando: " + email);
            usuario = usuarioExistente.get();
            usuario.setNome(nome != null ? nome : usuario.getNome());
            usuario.setAvatarUrl(avatarUrl);

            // Se logou com GitHub agora, vincula o username
            if (githubUsername != null) {
                usuario.setGithubUsername(githubUsername);
            }
        } else {
            // Usuário NOVO (Cria)
            LOGGER.info("Novo usuário detectado (Onboarding): " + email);
            usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setNome(nome != null ? nome : "Dev Sem Nome");
            usuario.setAvatarUrl(avatarUrl);
            usuario.setGithubUsername(githubUsername);
            usuario.setBio("Entusiasta de tecnologia pronto para desafios.");
            // O nível "INICIANTE I" já é padrão na classe Usuario
        }

        usuarioRepository.save(usuario);

        // Agora o return está fora dos ifs, garantindo que sempre retorne algo
        return oAuth2User;
    }
}