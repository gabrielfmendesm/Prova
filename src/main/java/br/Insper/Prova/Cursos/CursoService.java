package br.Insper.Prova.Cursos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class CursoService {
    @Autowired
    private CursoRepository cursoRepository;

    public Curso salvar(Curso curso) {
        curso.setId(UUID.randomUUID().toString());

        if (curso.getNome() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do curso é obrigatório");
        } else if (curso.getDescricao() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descrição do curso é obrigatória");
        } else if (curso.getAlunos_maximo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Número máximo de alunos do curso é obrigatório");
        } else if (curso.getCpf_professor() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF do professor é obrigatório");
        } else if (curso.getAlunos() != null) {
            for (String cpf_aluno : curso.getAlunos()) {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<RetornarPessoaDTO> alunoResponse = restTemplate.getForEntity(
                        "http://184.72.80.215:8080/usuario/" + cpf_aluno, RetornarPessoaDTO.class);

                if (!alunoResponse.getStatusCode().is2xxSuccessful()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno " + cpf_aluno + " não encontrado");
                }
            }
        }

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RetornarPessoaDTO> professorResponse = restTemplate.getForEntity(
                "http://184.72.80.215:8080/usuario/" + curso.getCpf_professor(), RetornarPessoaDTO.class);

        if (!professorResponse.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Professor " + curso.getCpf_professor() + " não encontrado");
        }

        cursoRepository.save(curso);
        return curso;
    }

    public List<Curso> listar(String nome) {
        if (nome != null) {
            return cursoRepository.findByNome(nome);
        }
        return cursoRepository.findAll();
    }

    public Curso adicionaAluno(String idCurso, String cpf_aluno) {
        Curso curso = cursoRepository.findById(idCurso).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Curso não encontrado"));

        if (curso.getAlunos_maximo() <= curso.getAlunos().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Curso " + idCurso + " já atingiu o número máximo de alunos");
        }

        if (curso.getAlunos().contains(cpf_aluno)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno " + cpf_aluno + " já está matriculado no curso");
        }

        if (cpf_aluno == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF do aluno é obrigatório");
        }

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RetornarPessoaDTO> alunoResponse = restTemplate.getForEntity(
                "http://184.72.80.215:8080/usuario/" + cpf_aluno, RetornarPessoaDTO.class);

        if (!alunoResponse.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno " + cpf_aluno + " não encontrado");
        }

        curso.getAlunos().add(cpf_aluno);
        cursoRepository.save(curso);

        return curso;
    }
}
