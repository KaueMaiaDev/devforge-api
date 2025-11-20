package br.com.devforge.controller;

import br.com.devforge.model.Desafio;
import br.com.devforge.repository.DesafioRepository;
import br.com.devforge.service.ModeracaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST responsável pelos endpoints de Desafios.
 * Gerencia a listagem pública e a criação de novos desafios, integrando com o serviço de moderação.
 */
@RestController
@RequestMapping("/desafios")
public class DesafioController {

    @Autowired
    private DesafioRepository repository;

    @Autowired
    private ModeracaoService moderacaoService;

    /**
     * Lista os desafios disponíveis na plataforma.
     * <p>
     * Regra de Negócio: Retorna apenas desafios com status 'APROVADO'.
     * Conteúdo pendente ou rejeitado é invisível para a listagem pública.
     * </p>
     *
     * @param nivel (Opcional) Filtra por nível de senioridade (JUNIOR, PLENO, SENIOR).
     * @return Lista de desafios aprovados.
     */
    @GetMapping
    public List<Desafio> listar(@RequestParam(required = false) String nivel) {
        List<Desafio> todosDesafios = repository.findAll();

        // Filtra na memória (Stream) para garantir que apenas APROVADOS sejam exibidos
        // Nota: Em produção com muitos dados, idealmente faríamos isso com uma Query no Repository (findByStatusAndNivel)
        return todosDesafios.stream()
                .filter(d -> "APROVADO".equals(d.getStatus())) // Regra de segurança: Só mostra aprovados
                .filter(d -> nivel == null || d.getNivel().equalsIgnoreCase(nivel)) // Filtro opcional de nível
                .collect(Collectors.toList());
    }

    /**
     * Cria um novo desafio proposto por um usuário.
     * <p>
     * O desafio passa pelo {@link ModeracaoService} para análise automática de conteúdo.
     * Se aprovado, o status é definido como APROVADO imediatamente.
     * Caso contrário, permanece como PENDENTE para revisão humana.
     * </p>
     *
     * @param desafio Payload contendo os dados do desafio.
     * @return O desafio salvo com o status atualizado.
     */
    @PostMapping
    public Desafio criar(@RequestBody @Valid Desafio desafio) {
        // 1. Executa a moderação automática (Detector de SPAM/Ofensas)
        boolean isConteudoSeguro = moderacaoService.isConteudoSeguro(desafio);

        if (isConteudoSeguro) {
            // Caminho Feliz: Conteúdo limpo, aprovação instantânea 🚀
            desafio.setStatus("APROVADO");
        } else {
            // Caminho de Exceção: Conteúdo suspeito, retém para moderação humana 🛡️
            desafio.setStatus("PENDENTE");
        }

        // TODO: Futuramente, aqui pegaremos o usuário logado para setar o criador:
        // desafio.setCriador(usuarioLogado);

        return repository.save(desafio);
    }
}