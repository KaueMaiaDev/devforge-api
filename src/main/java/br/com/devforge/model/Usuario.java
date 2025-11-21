package br.com.devforge.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representa um desenvolvedor registrado na plataforma DevForge.
 * Gerencia dados pessoais, integração com GitHub e o progresso de gamificação (XP e Nível).
 */
@Data
@Entity
@Table(name = "usuarios") // Plural para seguir convenções de SQL e evitar palavras reservadas
public class Usuario {

    // --- Getters ---
    public String getNome() {
        return nome;
    }


    // --- Setters ---
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // --- Dados Pessoais e Identificação ---
    private String nome;

    @Column(unique = true) // Email não pode repetir
    private String email;

    /**
     * URL da imagem de perfil (geralmente sincronizada do GitHub ou Google).
     */
    private String avatarUrl;

    @Column(length = 500)
    private String bio;

    private String localizacao;

    // --- Integração com GitHub ---

    /**
     * Username público do GitHub
     * Utilizado para gerar links para o perfil e repositórios
     */
    @Column(unique = true)
    private String githubUsername;

    /**
     * Data de registro na plataforma
     */
    private LocalDateTime dataCadastro = LocalDateTime.now();

    // --- Gamificação (Core do Negócio) ---

    /**
     * Pontuação total de experiência (XP) acumulada.
     * Ganho ao completar desafios, fazer code reviews ou ter desafios aprovados
     */
    private Integer xpTotal = 0;

    /**
     * Nível de senioridade na plataforma, calculado com base no XP total
     * Valores possíveis: "INICIANTE I", "JUNIOR III", "PLENO III", "SENIOR III"
     */
    private String nivel = "INICIANTE I";

    // --- Regras de Negócio (Domain Logic) ---

    /**
     * Adiciona pontos de experiência ao usuário e veridica se houve mudança de nível.
     * @param xpGanho Quantidade de XP a ser adicionada
     */
    public void adicionarXp(Integer xpGanho) {
        this.xpTotal += xpGanho;
        atualizarNivel();
    }

    /**
     * Recalcula o nível do usuário com base nas faixas de XP total
     * Sistema de progressão com 12 níveis.
     */
    public void atualizarNivel() {
        // SENIOR (5000+)
        if (this.xpTotal >= 10000) {
            this.nivel = "SENIOR III";
        } else if (this.xpTotal >= 7500) {
            this.nivel = "SENIOR III";
        } else if (this.xpTotal >= 5000) {
            this.nivel = "SENIOR I";

        // PLENO (1000 - 4999)
        } else if (xpTotal >= 3500) {
            this.nivel = "PLENO III";
        } else if (xpTotal >= 2000) {
            this.nivel = "PLENO II";
        } else if (xpTotal >= 1000) {
            this.nivel = "PLENO I";

        // JUNIOR (300 - 999)
        } else if (this.xpTotal >= 750) {
            this.nivel = "JUNIOR III";
        } else if (this.xpTotal >= 500) {
            this.nivel = "JUNIOR II";
        } else if (this.xpTotal >= 300) {
            this.nivel = "JUNIOR I";

        // INICIANTE (0 -299)
        } else if (this.xpTotal >= 200) {
            this.nivel = "INICIANTE III";
        } else if (this.xpTotal >= 100) {
            this.nivel = "INICIANTE II";
        } else {
            this.nivel = "INICIANTE I";
        }
    }
}
