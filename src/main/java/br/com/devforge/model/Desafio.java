package br.com.devforge.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

import java.time.LocalDate;

/**
 * Mapea automaticamente para uma tabela no banco de dados
 */
@Data //  Lomnbok: Gera Getters, Setters, equals, hashCode, toString na compilação
@Entity // JPA: Define que esta classe é uma entidade persistente
public class Desafio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titulo do desafio
    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    // Descrição técnica detalhada do que deve ser desenvolvido
    @NotBlank
    @Size(min = 10, message = "A descrição deve ter pelo menos 10 caracteres")
    private String descricao;

    // Nível de senioridade sugerido
    private String nivel;

    // Lista de tecnologias requeridas
    private String stack;

    // Data de publicação do desafio
    // Inicializada automaticamente com a data do servidor
    private LocalDate dataCriacao = LocalDate.now();
}