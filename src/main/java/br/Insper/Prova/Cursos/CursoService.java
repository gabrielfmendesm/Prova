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

        if (curso.getNome() == null || curso.getNome().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro: O nome do curso é obrigatório. Por favor, forneça um nome válido.");
        }

        if (curso.getDescricao() == null || curso.getDescricao().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro: A descrição do curso é obrigatória. Por favor, forneça uma descrição válida.");
        }

        if (curso.getAlunos_maximo() == null || curso.getAlunos_maximo() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro: O número máximo de alunos é obrigatório e deve ser maior que zero.");
        }

        if (curso.getCpf_professor() == null || curso.getCpf_professor().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro: O CPF do professor é obrigatório. Por favor, forneça um CPF válido.");
        }

        if (curso.getAlunos() != null) {
            for (String cpf_aluno : curso.getAlunos()) {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<RetornarPessoaDTO> alunoResponse = restTemplate.getForEntity(
                        "http://184.72.80.215:8080/usuario/" + cpf_aluno, RetornarPessoaDTO.class);

                if (!alunoResponse.getStatusCode().is2xxSuccessful()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro: O aluno com CPF " + cpf_aluno + " não foi encontrado no sistema.");
                }
            }
        }

        // Verificar o professor
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RetornarPessoaDTO> professorResponse = restTemplate.getForEntity(
                "http://184.72.80.215:8080/usuario/" + curso.getCpf_professor(), RetornarPessoaDTO.class);

        if (!professorResponse.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro: O professor com CPF " + curso.getCpf_professor() + " não foi encontrado no sistema.");
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
        Curso curso = cursoRepository.findById(idCurso).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro: O curso com ID " + idCurso + " não foi encontrado.")
        );

        if (curso.getAlunos_maximo() <= curso.getAlunos().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro: O curso " + idCurso + " já atingiu o número máximo de alunos permitidos.");
        }

        if (curso.getAlunos().contains(cpf_aluno)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro: O aluno com CPF " + cpf_aluno + " já está matriculado neste curso.");
        }

        if (cpf_aluno == null || cpf_aluno.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro: O CPF do aluno é obrigatório. Por favor, forneça um CPF válido.");
        }

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RetornarPessoaDTO> alunoResponse = restTemplate.getForEntity(
                "http://184.72.80.215:8080/usuario/" + cpf_aluno, RetornarPessoaDTO.class);

        if (!alunoResponse.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro: O aluno com CPF " + cpf_aluno + " não foi encontrado no sistema.");
        }

        curso.getAlunos().add(cpf_aluno);
        cursoRepository.save(curso);

        return curso;
    }
}