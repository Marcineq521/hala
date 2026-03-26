package pl.marcin.production_hall.assignment;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssignmentResponse {
    private UUID id;
    private UUID machineId;
    private String machineCode;
    private String operatorName;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private UUID workOrderId;
    private String workOrderNo;

}
