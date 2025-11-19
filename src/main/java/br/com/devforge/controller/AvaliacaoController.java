package br.com.devforge.controller;

import br.com.devforge.model.Avaliacao;
import br.com.devforge.model.Solucao;
import br.com.devforge.repository.AvaliacaoRepository;
import br.com.devforge.repository.SolucaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private SolucaoRepository solucaoRepository;

    @PostMapping
    public Avaliacao avaliar(@RequestBody Avaliacao avaliacao, @RequestParam Long solucaoId) {
        // 1. Busca a solucao que esta sendo avaliada
        Solucao solucao = solucaoRepository.findById(solucaoId)
                .orElseThrow(() -> new RuntimeException("Solução não encontrada!"));

        // 2. Amarra a avaliacao na solucao
        avaliacao.setSolucao(solucao);

        /**
         * REGRA DE NEGOCIO
         * Se a nota for 5, aprova a solucao automaticamente
         */
        if (avaliacao.getNota() == 5) {
            solucao.setStatus("APROVADO");
            solucaoRepository.save(solucao);
        }

        return avaliacaoRepository.save(avaliacao);
    }
}
