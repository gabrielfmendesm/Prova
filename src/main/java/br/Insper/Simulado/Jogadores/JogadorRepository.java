package br.Insper.Simulado.Jogadores;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JogadorRepository extends MongoRepository<Jogador, String> {
    List<Jogador> findByNome(String nome);
}
