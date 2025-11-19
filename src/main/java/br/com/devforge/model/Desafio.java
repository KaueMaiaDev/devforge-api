package br.com.devforge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
public class Desafio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    // --- O CORAÇÃO DO BRIEFING ---

    @NotBlank(message = "O contexto é obrigatório")
    @Size(min = 20, message = "O contexto deve ser detalhado")
    @Column(columnDefinition = "TEXT") // Texto longo para a história
    private String contexto;

    @NotBlank(message = "Os requisitos funcionais são obrigatórios")
    @Column(columnDefinition = "TEXT") // Texto longo para a lista de funcionalidades
    private String requisitosFuncionais;

    @NotBlank(message = "Os requisitos técnicos são obrigatórios")
    @Column(columnDefinition = "TEXT") // Texto longo para backend, banco, etc.
    private String requisitosTecnicos;

    // ------------------------------

    @NotBlank(message = "O nível é obrigatório")
    private String nivel;

    @NotBlank(message = "A stack é obrigatória")
    private String stack;

    private LocalDate dataCriacao = LocalDate.now();
}