package br.com.devforge.repository;

import br.com.devforge.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Interface de acesso a dados para a entidade Usuario
 * Estende JpaRepository para herdar operações CRUD padrão (save, findById, delete, etc)
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

    /**
     * Busca um usuário pelo endereço de email exato
     * Fundamental para o processo de Autenticação/Login
     *
     * @param email O email a ser buscado
     * @return Um optional contendo o usuário, caso existe
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca um usuário pelo seu username do GitHub
     * Utilizado para exibir perfis públicos e na integração OAuth
     *
     * @param githubUsername O usarname do GitHub
     * @return Um Optional contendo o usuário, caso exista
     */
    Optional<Usuario> findByGithubUsername(String githubUsername);
}
