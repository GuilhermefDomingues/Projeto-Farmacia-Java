package com.gui.projetofarmacia.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendas")
@Getter
@Setter
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clienteId;
    private Long medicamentoId;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal total;
    private LocalDateTime dataVenda;
}
