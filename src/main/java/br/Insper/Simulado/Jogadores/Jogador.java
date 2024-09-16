package br.Insper.Simulado.Jogadores;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "jogadores")
@Getter
@Setter
public class Jogador {
    @Id
    private String id;
    private String nome;
    private Integer idade;
    private List<Integer> times;
}
