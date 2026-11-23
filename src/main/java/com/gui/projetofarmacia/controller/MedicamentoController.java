package com.gui.projetofarmacia.controller;

import com.gui.projetofarmacia.dto.MedicamentoCadastroDto;
import com.gui.projetofarmacia.model.Medicamento;
import com.gui.projetofarmacia.service.MedicamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/medicamentos")
@RestController
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    @GetMapping("/{id}")
    public Medicamento buscar(@PathVariable("id") Long medicamentoId){
        return medicamentoService.buscar(medicamentoId);
    }

    @GetMapping
    public List<Medicamento> listar(){
        return medicamentoService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Medicamento registrar(
            @Valid @RequestBody MedicamentoCadastroDto medicamentoCadastroDto){
        return medicamentoService.cadastrar(medicamentoCadastroDto);
    }

    @PutMapping("/{id}")
    public Medicamento atualizar(@PathVariable Long id,
                                 @Valid @RequestBody MedicamentoCadastroDto dto){
        return medicamentoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id){
        medicamentoService.deletar(id);
    }
}
