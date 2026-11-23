package com.gui.projetofarmacia.repository;

import com.gui.projetofarmacia.model.Medicamento;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Medicamento> findParaAlterarById(Long id);
}
