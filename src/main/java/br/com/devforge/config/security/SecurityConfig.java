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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Ativa o CORS usando o bean corsConfigurationSource definido abaixo
                .cors(Customizer.withDefaults())

                // Desabilita CSRF (padrão para APIs REST Stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Regras de acesso
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/error", "/login/**", "/oauth2/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/desafios/**", "/solucoes/**").permitAll()
                        .anyRequest().authenticated()
                )

                // Configuração do Login Social (OAuth2)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // IMPORTANTE: Redireciona para o Front-end (Vite) após o login com sucesso
                        // Se estiver em produção, troque localhost:5173 pela URL do seu site no Vercel/Netlify
                        .defaultSuccessUrl("http://localhost:5173", true)
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Lista EXPLICITAMENTE quem pode acessar sua API (CORS)
        // Adicione a URL de produção do front-end aqui quando tiver (ex: https://meu-app.vercel.app)
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        // CRUCIAL: Permite enviar Cookies/Sessão entre domínios diferentes
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}