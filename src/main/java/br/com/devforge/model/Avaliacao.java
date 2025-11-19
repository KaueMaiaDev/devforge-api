package br.com.devforge.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nota atribuída à solução (1 a 5).
     * Regra de Negócio: Nota 5 aprova a solução automaticamente.
     */
    private Integer nota;

    /**
     * Feedback descritivo com sugestões de melhoria.
     */
    private String comentario;

    /**
     * Vínculo com a Solução avaliada (Muitas avaliações -> Uma Solução).
     */
    @ManyToOne
    @JoinColumn(name = "solucao_id")
    private Solucao solucao;

    public void setSolucao(Solucao solucao) {
        this.solucao = solucao;
    }

    public Solucao getSolucao() {
        return this.solucao;
    }

    public Integer getNota() {
        return this.nota;
    }
}
