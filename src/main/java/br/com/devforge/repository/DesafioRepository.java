package br.com.devforge.repository;

import br.com.devforge.model.Desafio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Interface interação com Banco de dados
 * Estende JpaRepository para herdar métodos prontos
 */
public interface DesafioRepository extends JpaRepository<Desafio, Long>{
    // Spring data cria uma implementaçãp automaticamente em tempo de execução

    // Busca onde a coluna 'nivel' for igual ao parametro
    List<Desafio> findByNivelIgnoreCase(String nivel);
}
