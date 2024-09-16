package br.Insper.Simulado.Jogadores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jogador")
public class JogadorController {
    @Autowired
    private JogadorService jogadorService;

    @GetMapping
    public List<Jogador> listar(@RequestParam(required = false) String nome) {
        return jogadorService.listar(nome);
    }

    @PostMapping
    public Jogador salvar(@RequestBody Jogador jogador) {
        return jogadorService.salvar(jogador);
    }

    @PostMapping("/{idJogador}/{time}")
    public Jogador adicionaTime(@PathVariable String idJogador, @PathVariable Integer time) {
        return jogadorService.adicionaTime(idJogador, time);
    }
}
