package br.com.devforge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

/**
 * Representa a entodade de um Desafio
 * Esta classe encapsula todo o "briefing" técnico (contexto, requisitos) que o
 * desenvolvedor utilizará para construir a solução, além de gerenciar o ciclo de
 * vida da publicação (Status) e autoria
 */
@Data
@Entity
public class Desafio {

    // --- GETTERS ---
    public String getTitulo() {
        return titulo;
    }

    public String getContexto() {
        return contexto;
    }

    public String getRequisitosFuncionais() {
        return requisitosFuncionais;
    }

    public String getRequisitosTecnicos() {
        return requisitosTecnicos;
    }

    public String getStatus() {
        return status;
    }

    public String getNivel() {
        return nivel;
    }

    // --- Setters ---
    public void setStatus(String status) {
        this.status = status;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título curto e descritivo do desafio
     */
    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    // --- Núcleo da Definição Técnica (Briefing) ---

    /**
     * Descrição detalhada do cenário de negócio e problema a ser resolvido
     * Armazenado como TEXT no banco para suportar descrições longas e ricas
     */
    @NotBlank(message = "O contexto é obrigatório")
    @Size(min = 20, message = "O contexto deve ser detalhado")
    @Column(columnDefinition = "TEXT") // Texto longo para a história
    private String contexto;

    /**
     * Lista de funcionalidades e comportamentos esperados do software
     * Define O QUE o sistema deve fazer
     */
    @NotBlank(message = "Os requisitos funcionais são obrigatórios")
    @Column(columnDefinition = "TEXT") // Texto longo para a lista de funcionalidades
    private String requisitosFuncionais;

    /**
     * Restrições técnicas, padrões de arquitetura e entregáveis obrigatórios
     * Define Como o sistema deve ser construído (ex: "Usar Clean Arch", "Testes Unitários")
     */
    @NotBlank(message = "Os requisitos técnicos são obrigatórios")
    @Column(columnDefinition = "TEXT") // Texto longo para backend, banco, etc.
    private String requisitosTecnicos;

    // Metadados de Classificação

    /**
     * Nível de senioridade sugerido para o desafip
     * Valores aceitos: INICIANTE, JUNIOR, PLENO, SENIOR
     */
    @NotBlank(message = "O nível é obrigatório")
    private String nivel;

    /**
     * Tecnologias principais envolvidas, separadas por vírgula
     * Utilizado para filtros de busca e ícones no frontend
     * Ex: "Java, Spring Boot, Docker"
     */
    @NotBlank(message = "A stack é obrigatória")
    private String stack;

    private LocalDate dataCriacao = LocalDate.now();

    // --- Controle de Moderação e Ciclo de Vida ---

    /**
     * Define o estado atual do desafio no fluxo de aprovação
     * <ul>
     * <li><b>PENDENTE:</b> Criado pelo usuário, aguardando revisão da moderação. (Padrão)</li>
     * <li><b>APROVADO:</b> Validado e visível na listagem pública.</li>
     * <li><b>REJEITADO:</b> Devolvido ao autor com feedback para correções.</li>
     * </ul>
     */
    private String status = "PENDENTE";

    // --- Relacionamentos (integridade Referencial) ---

    /**
     * Vínculo com o Usuário autor que propôs este desafio.
     * <p>
     * Configurado com <code>FetchType.LAZY</code> para otimização de performance (evita carregar
     * todos os dados do usuário ao listar desafios simples).
     * </p>
     * A anotação <code>@JsonIgnoreProperties</code> é necessária para evitar erros de serialização
     * do Jackson ao lidar com os proxies do Hibernate (Lazy Loading).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criador_api")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "email"})
    private Usuario criador;
}