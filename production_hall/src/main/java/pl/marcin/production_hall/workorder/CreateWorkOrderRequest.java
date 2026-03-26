package pl.marcin.production_hall.workorder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateWorkOrderRequest (
    @NotBlank String orderNo,
    @NotBlank String title,
    @NotNull UUID machineId
){
}
