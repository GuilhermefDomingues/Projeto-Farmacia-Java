package com.gui.projetofarmacia.service;

import com.gui.projetofarmacia.dto.ClienteCadastroDto;
import com.gui.projetofarmacia.mapper.ClienteMapper;
import com.gui.projetofarmacia.model.Cliente;
import com.gui.projetofarmacia.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public Cliente cadastrar(ClienteCadastroDto dto) {
        return clienteRepository.save(clienteMapper.toEntity(dto));
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente buscar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    public Cliente atualizar(Long id, ClienteCadastroDto dto) {
        Cliente cliente = buscar(id);
        clienteMapper.atualizarEntidade(dto, cliente);
        return clienteRepository.save(cliente);
    }
}
