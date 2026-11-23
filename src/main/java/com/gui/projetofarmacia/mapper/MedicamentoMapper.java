package com.gui.projetofarmacia.mapper;

import com.gui.projetofarmacia.dto.MedicamentoCadastroDto;
import com.gui.projetofarmacia.model.Medicamento;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MedicamentoMapper {
    private final ModelMapper modelMapper;

    public Medicamento toEntity(MedicamentoCadastroDto medicamentoCadastroDto){
        return modelMapper.map(medicamentoCadastroDto, Medicamento.class);
    }

    public void atualizarEntidade(MedicamentoCadastroDto dto, Medicamento medicamento){
        modelMapper.map(dto, medicamento);
    }
}
