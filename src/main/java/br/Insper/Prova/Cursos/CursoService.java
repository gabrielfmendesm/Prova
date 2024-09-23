package br.Insper.Prova.Cursos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class CursoService {
    @Autowired
    private CursoRepository cursoRepository;

    public Curso salvar(Curso curso) {
        curso.setId(UUID.randomUUID().toString());

        if (curso.getNome() == null) {
            throw new IllegalArgumentException("Nome do curso é obrigatório");
        } else if (curso.getDescricao() == null) {
            throw new IllegalArgumentException("Descrição do curso é obrigatória");
        } else if (curso.getAlunos_maximo() == null) {
            throw new IllegalArgumentException("Número máximo de alunos do curso é obrigatório");
        } else if (curso.getCpf_professor() == null) {
            throw new IllegalArgumentException("CPF do professor do curso é obrigatório");
        }

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RetornarPessoaDTO> professorResponse = restTemplate.getForEntity(
                "http://184.72.80.215:8080/usuario/" + curso.getCpf_professor(), RetornarPessoaDTO.class);

        if (!professorResponse.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("Professor " + curso.getCpf_professor() + " não encontrado");
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
        Curso curso = cursoRepository.findById(idCurso).orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));

        if (curso.getAlunos_maximo() <= curso.getAlunos().size()) {
            throw new IllegalArgumentException("Número máximo de alunos já foi atingido");
        }

        if (curso.getAlunos().contains(cpf_aluno)) {
            throw new IllegalArgumentException("Aluno " + cpf_aluno + " já está na lista de alunos do curso");
        }

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RetornarPessoaDTO> alunoResponse = restTemplate.getForEntity(
                "http://184.72.80.215:8080/usuario/" + cpf_aluno, RetornarPessoaDTO.class);

        if (!alunoResponse.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("Aluno " + cpf_aluno + " não encontrado");
        }

        curso.getAlunos().add(cpf_aluno);
        cursoRepository.save(curso);

        return curso;
    }
}
