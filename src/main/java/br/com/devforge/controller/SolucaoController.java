package br.com.devforge.controller;

import br.com.devforge.model.Desafio;
import br.com.devforge.model.Solucao;
import br.com.devforge.repository.DesafioRepository;
import br.com.devforge.repository.SolucaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solucoes")
public class SolucaoController {

    @Autowired
    private SolucaoRepository solucaoRepository;

    @Autowired
    private DesafioRepository desafioRepository;

    /**
     * Registra uma nova solucao para um desafio existente
     * Valida se o desafio existe antes de salvar para manter a integridade referencial
     *
     * @param solucao Payload com dados do autor e link
     * @param desafioId ID do desafio ao qual a solucao percente (Query Param)
     * @throws RuntimeException se o desafioId nao for encontrado no banco
     */
    @PostMapping
    public Solucao enviarSolucao(@RequestBody Solucao solucao, @RequestParam Long desafioId) {
        Desafio desafio = desafioRepository.findById(desafioId)
                .orElseThrow(() -> new RuntimeException("Desafio não encontrado! ID inválido: " + desafioId));

        solucao.setDesafio(desafio);
        return solucaoRepository.save(solucao);
    }

    // 2. Listar Soluções
    @GetMapping
    public List<Solucao> listarPorDesafio(@RequestParam Long desafioId) {
        return solucaoRepository.findByDesafioId(desafioId);
    }
}
