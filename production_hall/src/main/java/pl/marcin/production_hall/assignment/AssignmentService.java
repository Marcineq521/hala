package pl.marcin.production_hall.assignment;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.marcin.production_hall.domain.Assignment;
import pl.marcin.production_hall.domain.Machine;
import pl.marcin.production_hall.domain.WorkOrder;
import pl.marcin.production_hall.eventlog.EventLogService;
import pl.marcin.production_hall.eventlog.EventLogType;
import pl.marcin.production_hall.machine.MachineRepository;
import pl.marcin.production_hall.workorder.WorkOrderRepository;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final MachineRepository machineRepository;
    private final EventLogService eventLogService;
    private final WorkOrderRepository workOrderRepository;

    public List<AssignmentResponse> getActiveAssignments(){
        return assignmentRepository.findByEndTimeIsNull().stream().map(this::mapToResponse).toList();
    }

    public AssignmentResponse startAssignment(UUID machineId, String operatorName, UUID workOrderId) {
        Assignment existing = assignmentRepository.findByMachine_IdAndEndTimeIsNull(machineId)
                .orElse(null);

        if (existing != null) {
            throw new RuntimeException("Machine already occupied");
        }

        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new RuntimeException("Machine not found"));

        WorkOrder workOrder = null;

        if (workOrderId != null) {
            workOrder = workOrderRepository.findById(workOrderId)
                    .orElseThrow(() -> new RuntimeException("Work order not found"));

            if (!workOrder.getMachine().getId().equals(machine.getId())) {
                throw new RuntimeException("Work order does not belong to this machine");
            }
        }

        Assignment assignment = Assignment.builder()
                .machine(machine)
                .operatorName(operatorName)
                .workOrder(workOrder)
                .build();

        Assignment saved = assignmentRepository.save(assignment);

        eventLogService.log(
                EventLogType.ASSIGNMENT_STARTED,
                machine.getId(),
                saved.getId(),
                workOrder != null ? workOrder.getId() : null,
                "Operator " + saved.getOperatorName()
                        + " started assignment on machine " + machine.getCode()
                        + (workOrder != null ? " for work order " + workOrder.getOrderNo() : "")
        );

        return mapToResponse(saved);
    }


    public AssignmentResponse endAssignment(UUID machineId){
        Assignment assignment=assignmentRepository.findByMachine_IdAndEndTimeIsNull(machineId)
                .orElseThrow(()->new RuntimeException("Active assignment not found for this machine"));
            assignment.setEndTime(OffsetDateTime.now());

         Assignment saved=assignmentRepository.save(assignment);

        eventLogService.log(
                EventLogType.ASSIGNMENT_ENDED,
                saved.getMachine().getId(),
                saved.getId(),
                saved.getWorkOrder() != null ? saved.getWorkOrder().getId() : null,
                "Operator " + saved.getOperatorName() + " ended assignment on machine " + saved.getMachine().getCode()
        );

          return mapToResponse(saved);
    }

    public List<AssignmentResponse> getAssignmentsForMachine(UUID machineId){
        return assignmentRepository.findByMachine_Id(machineId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AssignmentResponse mapToResponse(Assignment assignment) {
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .machineId(assignment.getMachine().getId())
                .machineCode(assignment.getMachine().getCode())
                .operatorName(assignment.getOperatorName())
                .startTime(assignment.getStartTime())
                .endTime(assignment.getEndTime())
                .workOrderId(assignment.getWorkOrder() != null ? assignment.getWorkOrder().getId() : null)
                .workOrderNo(assignment.getWorkOrder() != null ? assignment.getWorkOrder().getOrderNo() : null)
                .build();
    }
}
