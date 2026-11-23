package com.gui.projetofarmacia.controller;

import com.gui.projetofarmacia.dto.VendaCadastroDto;
import com.gui.projetofarmacia.model.Venda;
import com.gui.projetofarmacia.service.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
public class VendaController {
    private final VendaService vendaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Venda registrar(@Valid @RequestBody VendaCadastroDto dto) {
        return vendaService.registrar(dto);
    }

    @GetMapping
    public List<Venda> listar() {
        return vendaService.listar();
    }

    @GetMapping("/{id}")
    public Venda buscar(@PathVariable("id") Long id) {
        return vendaService.buscar(id);
    }
}
