package br.Insper.Prova.Jogadores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class JogadorService {
    @Autowired
    private JogadorRepository jogadorRepository;

    public Jogador salvar(Jogador jogador) {
        jogador.setId(UUID.randomUUID().toString());

        if (jogador.getNome() == null) {
            throw new IllegalArgumentException("Nome do jogador é obrigatório");
        } else if (jogador.getIdade() == null) {
            throw new IllegalArgumentException("Idade do jogador é obrigatória");
        }

        for (Integer time : jogador.getTimes()) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<RetornarTimeDTO> timeResponse = restTemplate.getForEntity(
                    "http://3.81.161.81:8081/time/" + time, RetornarTimeDTO.class);

            if (!timeResponse.getStatusCode().is2xxSuccessful()) {
                throw new IllegalArgumentException("Time " + time + " não encontrado");
            }
        }

        jogadorRepository.save(jogador);
        return jogador;
    }

    public List<Jogador> listar(String nome) {
        if (nome != null) {
            return jogadorRepository.findByNome(nome);
        }
        return jogadorRepository.findAll();
    }

    public Jogador adicionaTime(String idJogador, Integer time) {
        Jogador jogador = jogadorRepository.findById(idJogador).orElseThrow(() -> new IllegalArgumentException("Jogador não encontrado"));

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RetornarTimeDTO> timeResponse = restTemplate.getForEntity(
                "http://3.81.161.81:8081/time/" + time, RetornarTimeDTO.class);

        if (!timeResponse.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("Time " + time + " não encontrado");
        }

        if (jogador.getTimes().contains(time)) {
            throw new IllegalArgumentException("Time " + time + " já está na lista de times do jogador");
        }

        jogador.getTimes().add(time);
        jogadorRepository.save(jogador);
        return jogador;
    }
}
