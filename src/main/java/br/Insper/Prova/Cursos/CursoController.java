package br.Insper.Prova.Cursos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curso")
public class CursoController {
    @Autowired
    private CursoService cursoService;

    @GetMapping
    public List<Curso> listar(@RequestParam(required = false) String nome) {
        return cursoService.listar(nome);
    }

    @PostMapping
    public Curso salvar(@RequestBody Curso curso) {
        return cursoService.salvar(curso);
    }

    @PostMapping("/{idCurso}/{cpfAluno}")
    public Curso adicionaAluno(@PathVariable String idCurso, @PathVariable String cpfAluno) {
        return cursoService.adicionaAluno(idCurso, cpfAluno);
    }
}
