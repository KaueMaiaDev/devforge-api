package br.com.devforge.service;

import br.com.devforge.model.Desafio;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Serviço responsável pela moderação automática de conteúdo gerado por usuários.
 * <p>
 * Atua como uma primeira barreira de defesa contra SPAM e conteúdo ofensivo.
 * Carrega uma lista de termos proibidos (blocklist) de um arquivo externo e verifica
 * se os desafios submetidos contêm algum desses termos.
 * </p>
 */
@Service
public class ModeracaoService {

    private static final Logger LOGGER = Logger.getLogger(ModeracaoService.class.getName());
    private static final String BLOCKLIST_FILENAME = "blocklist.txt";

    // Armazena os termos em memória para acesso rápido (O(1) ou O(n)) sem ler disco toda vez
    private final List<String> termosProibidos = new ArrayList<>();

    /**
     * Inicializa o serviço carregando a blocklist do disco para a memória.
     * Executado automaticamente pelo Spring logo após a injeção de dependências (@PostConstruct).
     */
    @PostConstruct
    public void carregarTermos() {
        LOGGER.info("Iniciando carregamento da lista de moderação...");

        try {
            ClassPathResource resource = new ClassPathResource(BLOCKLIST_FILENAME);

            // Verifica se o arquivo existe antes de tentar ler
            if (!resource.exists()) {
                LOGGER.warning("AVISO: Arquivo 'blocklist.txt' não encontrado em resources. Moderação automática estará inativa.");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String linha;
                while ((linha = reader.readLine()) != null) {
                    // Ignora linhas vazias ou comentários (se houver)
                    if (!linha.trim().isEmpty()) {
                        termosProibidos.add(linha.trim().toLowerCase());
                    }
                }
            }

            LOGGER.info("Moderação carregada com sucesso. Total de termos monitorados: " + termosProibidos.size());

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro crítico ao ler arquivo de moderação: " + e.getMessage(), e);
        }
    }

    /**
     * Analisa o conteúdo textual de um Desafio em busca de termos proibidos.
     * Verifica: Título, Contexto, Requisitos Funcionais e Técnicos.
     *
     * @param desafio O objeto Desafio a ser analisado.
     * @return {@code true} se o conteúdo for considerado seguro (livre de termos proibidos),
     * {@code false} caso contrário.
     */
    public boolean isConteudoSeguro(Desafio desafio) {
        if (termosProibidos.isEmpty()) {
            return true; // Se não há lista, aprova tudo (Fail-open) ou poderia bloquear (Fail-closed)
        }

        // Concatena todos os campos de texto para uma única varredura
        // Normaliza para lowercase para garantir que o match funcione independente da caixa
        String conteudoCompleto = (
                desafio.getTitulo() + " " +
                        desafio.getContexto() + " " +
                        desafio.getRequisitosFuncionais() + " " +
                        desafio.getRequisitosTecnicos()
        ).toLowerCase();

        for (String termo : termosProibidos) {
            // Verifica a presença do termo proibido
            // TODO: Futuramente implementar Regex para evitar falsos positivos em palavras compostas
            if (conteudoCompleto.contains(termo)) {
                LOGGER.info("Conteúdo retido pela moderação automática. Termo detectado: " + termo);
                return false; // Conteúdo impróprio detectado
            }
        }

        return true; // Conteúdo limpo
    }
}