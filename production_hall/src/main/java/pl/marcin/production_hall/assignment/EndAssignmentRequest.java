package pl.marcin.production_hall.assignment;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EndAssignmentRequest {
    private UUID machineId;
}
