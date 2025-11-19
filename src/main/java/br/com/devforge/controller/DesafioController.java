package br.com.devforge.controller;

import br.com.devforge.model.Desafio;
import br.com.devforge.repository.DesafioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Avisa que esta classe controla endpoints REST
@RequestMapping("/desafios") // Define o endereço base: localhost:8080/desafios
public class DesafioController {

    @Autowired
    private DesafioRepository repository;

    /**
     * Endpoint para listar todos os desafios cadastrados
     */
    @GetMapping
    public List<Desafio> listar(@RequestParam(required = false) String nivel) {
        if (nivel != null) {
            return repository.findByNivelIgnoreCase(nivel);
        }
        return repository.findAll();
    }

    /**
     * Endpoint para criar um novo desafio
     * @param desafio O objeto JSON enviado no corpo da requisição
     */
    @PostMapping
    public Desafio criar(@RequestBody @Valid Desafio desafio) {
        return repository.save(desafio);
    }
}
