package com.gui.projetofarmacia.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendaCadastroDto {
    @NotNull
    @Positive
    private Long clienteId;

    @NotNull
    @Positive
    private Long medicamentoId;

    @NotNull
    @Positive
    private Integer quantidade;
}
