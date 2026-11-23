package com.gui.projetofarmacia.controller;

import com.gui.projetofarmacia.dto.ClienteCadastroDto;
import com.gui.projetofarmacia.model.Cliente;
import com.gui.projetofarmacia.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService clienteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente cadastrar(@Valid @RequestBody ClienteCadastroDto dto) {
        return clienteService.cadastrar(dto);
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public Cliente buscar(@PathVariable("id") Long id) {
        return clienteService.buscar(id);
    }

    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable("id") Long id, @Valid @RequestBody ClienteCadastroDto dto) {
        return clienteService.atualizar(id, dto);
    }
}
