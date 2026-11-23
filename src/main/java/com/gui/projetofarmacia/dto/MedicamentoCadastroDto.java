package com.gui.projetofarmacia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class MedicamentoCadastroDto {

    @NotBlank
    private String nome;

    @NotBlank
    private String fabricante;

    @NotNull
    @Positive
    private BigDecimal preco;

    @NotNull
    @PositiveOrZero
    private Integer quantidadeEstoque;

    @NotNull
    private LocalDate dataValidade;
}
