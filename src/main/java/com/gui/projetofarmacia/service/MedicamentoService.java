package com.gui.projetofarmacia.service;

import com.gui.projetofarmacia.dto.MedicamentoCadastroDto;
import com.gui.projetofarmacia.mapper.MedicamentoMapper;
import com.gui.projetofarmacia.model.Medicamento;
import com.gui.projetofarmacia.repository.MedicamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final MedicamentoMapper medicamentoMapper;

    public List<Medicamento> listar(){
        return medicamentoRepository.findAll();
    }

    public Medicamento buscar(Long medicamentoId){
        return medicamentoRepository.findById(medicamentoId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Medicamento não encontrado!")
                );
    }

    public Medicamento cadastrar(MedicamentoCadastroDto dto){
        Medicamento medicamento = medicamentoMapper.toEntity(dto);
        return medicamentoRepository.save(medicamento);
    }

    Medicamento buscarParaAlterar(Long id) {
        return medicamentoRepository.findParaAlterarById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicamento não encontrado"));
    }

    @Transactional
    public Medicamento atualizar(Long id, MedicamentoCadastroDto dto){
        Medicamento medicamento = buscarParaAlterar(id);
        medicamentoMapper.atualizarEntidade(dto, medicamento);
        return medicamentoRepository.save(medicamento);
    }

    public void deletar(Long id){
        buscar(id);
        try {
            medicamentoRepository.deleteById(id);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Medicamento vinculado a uma venda");
        }
    }

}
