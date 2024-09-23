package br.Insper.Prova.Cursos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "cursos")
@Getter
@Setter
public class Curso {
    @Id
    private String id;
    private String nome;
    private String descricao;
    private Integer alunos_maximo;
    private String cpf_professor;
    private List<String> alunos;
}
