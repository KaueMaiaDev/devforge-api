package br.com.devforge.repository;

import br.com.devforge.model.Solucao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolucaoRepository  extends JpaRepository<Solucao, Long> {

    /**
     * Busca todas as solucoes vinculadas a um desafio especifico
     * @param desafioId ID do desafio pai
     * @return Lista de solucoes encontradas ou lista vazia
     */
    List<Solucao> findByDesafioId(Long desafioId);
}
