package br.Insper.Prova.Cursos;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CursoRepository extends MongoRepository<Curso, String> {
    List<Curso> findByNome(String nome);
}
