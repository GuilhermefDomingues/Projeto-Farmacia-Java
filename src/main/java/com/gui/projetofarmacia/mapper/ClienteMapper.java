package com.gui.projetofarmacia.mapper;

import com.gui.projetofarmacia.dto.ClienteCadastroDto;
import com.gui.projetofarmacia.model.Cliente;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteMapper {
    private final ModelMapper modelMapper;

    public Cliente toEntity(ClienteCadastroDto dto) {
        return modelMapper.map(dto, Cliente.class);
    }

    public void atualizarEntidade(ClienteCadastroDto dto, Cliente cliente) {
        modelMapper.map(dto, cliente);
    }
}
