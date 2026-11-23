package com.gui.projetofarmacia.repository;

import com.gui.projetofarmacia.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
