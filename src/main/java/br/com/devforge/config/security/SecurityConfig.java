package br.com.devforge.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuração central de Segurança da API DevForge.
 * <p>
 * Esta classe define as regras de acesso (quem pode ver o quê), configura o suporte a CORS
 * para o Frontend (React) e integra o fluxo de login OAuth2 (Google/GitHub).
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    /**
     * Define a cadeia de filtros de segurança (Security Filter Chain).
     * Configura as regras HTTP, proteção CSRF e autenticação.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORREÇÃO 1: Habilita CORS usando a configuração definida no método abaixo
                .cors(Customizer.withDefaults())

                // Desabilita CSRF pois nossa API é Stateless/Rest
                .csrf(AbstractHttpConfigurer::disable)

                // Regras de Autorização (Quem pode acessar o quê)
                .authorizeHttpRequests(auth -> auth
                        // Rotas Públicas (Acesso livre sem login)
                        .requestMatchers("/", "/error", "/login/**", "/oauth2/**").permitAll()

                        // Permitir listar desafios (GET) os desafios, mas apenas logados criem (POST)
                        .requestMatchers(HttpMethod.GET, "/desafios/**", "/solucoes/**").permitAll()

                        // Todas as outras rotas exigem autenticação
                        .anyRequest().authenticated()
                )

                // Configuração do Login OAuth2 (Social Login)
                .oauth2Login(oauth2 -> oauth2
                        // Injeta nosso serviço customizado para salvar o usuário no banco após login
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // Redirecionamento após login com sucesso (Manda de volta para o React na porta 3000)
                        .defaultSuccessUrl("http://localhost:3000", true)
                );

        return http.build();
    }

    /**
     * Configuração Global de CORS.
     * Permite que o Frontend (http://localhost:3000) converse com este Backend (http://localhost:8080).
     * Sem isso, o navegador bloqueia as requisições por segurança.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Libera Frontend
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos (JSON, Auth, etc)
        configuration.setAllowedHeaders(List.of("*"));

        // Permite envio de credenciais (cookies/tokens)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}