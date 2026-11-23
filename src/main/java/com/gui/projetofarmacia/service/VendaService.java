package com.gui.projetofarmacia.service;

import com.gui.projetofarmacia.dto.VendaCadastroDto;
import com.gui.projetofarmacia.model.Medicamento;
import com.gui.projetofarmacia.model.Venda;
import com.gui.projetofarmacia.repository.MedicamentoRepository;
import com.gui.projetofarmacia.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendaService {
    private final VendaRepository vendaRepository;
    private final ClienteService clienteService;
    private final MedicamentoService medicamentoService;
    private final MedicamentoRepository medicamentoRepository;

    @Transactional
    public Venda registrar(VendaCadastroDto dto) {
        clienteService.buscar(dto.getClienteId());
        Medicamento medicamento = medicamentoService.buscarParaAlterar(dto.getMedicamentoId());

        LocalDateTime agora = LocalDateTime.now();
        if (medicamento.getDataValidade().isBefore(agora.toLocalDate())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Medicamento vencido");
        }

        int quantidade = dto.getQuantidade();
        if (medicamento.getQuantidadeEstoque() < quantidade) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente");
        }

        Venda venda = new Venda();
        venda.setClienteId(dto.getClienteId());
        venda.setMedicamentoId(medicamento.getId());
        venda.setQuantidade(quantidade);
        venda.setPrecoUnitario(medicamento.getPreco());
        venda.setTotal(medicamento.getPreco().multiply(BigDecimal.valueOf(quantidade)));
        venda.setDataVenda(agora);

        medicamento.setQuantidadeEstoque(medicamento.getQuantidadeEstoque() - quantidade);
        medicamentoRepository.save(medicamento);
        return vendaRepository.save(venda);
    }

    public List<Venda> listar() {
        return vendaRepository.findAll();
    }

    public Venda buscar(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venda não encontrada"));
    }
}
