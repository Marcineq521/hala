package pl.marcin.production_hall.eventlog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateEventLogRequest(
    @NotNull EventLogType type,
    UUID machineId,
    UUID assignmentId,
    UUID workOrderId,
    @NotBlank String message
){

}


