package br.com.devforge.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Representa a submissão de código de um usuário para um desafio
 * Armazena o link do repositóeio e o status da avaliação
 */
@Data
@Entity
public class Solucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeAutor;
    private String linkRepositorio;

    /**
     * Status atual da submissao
     * Valores possiveis: pendente, em_review, aprovado, rejeitado
     * Default: PENDENTE
     */
    private String status = "PENDENTE";

    private LocalDateTime dataEnvio = LocalDateTime.now();

    /**
     * Vinculo com o Desafio (Muitas solucoes -> Um Desafio)
     * Mapeia a coluna 'desafio_id' no banco de dados
     * @implNote A existencia deste objeto é obrigatória para persistir a solucao
     */
    @ManyToOne
    @JoinColumn(name = "desafio_id")
    private Desafio desafio;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setDesafio(Desafio desafio) {
        this.desafio = desafio;
    }

    public Desafio getDesafio() {
        return this.desafio;
    }
}