package com.gymplus.backend.dto.venta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaRequestDto {

    @NotNull
    private Long idGimnasio;

    private Long idSucursal;

    @NotNull
    private Long idUsuario;

    @NotNull
    private BigDecimal total;

    private String estado;

    @Valid
    @NotEmpty
    private List<VentaDetalleRequestDto> detalles;
}
