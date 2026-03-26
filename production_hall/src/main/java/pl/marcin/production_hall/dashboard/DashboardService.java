package pl.marcin.production_hall.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.marcin.production_hall.assignment.AssignmentRepository;
import pl.marcin.production_hall.domain.Assignment;
import pl.marcin.production_hall.domain.Machine;
import pl.marcin.production_hall.domain.WorkOrder;
import pl.marcin.production_hall.machine.MachineRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final MachineRepository machineRepository;
    private final AssignmentRepository assignmentRepository;

    public List<MachineDashboardResponse> getMachinesDashboard() {
        List<Machine> machines = machineRepository.findAllByOrderByCodeAsc();
        List<Assignment> activeAssignments = assignmentRepository.findByEndTimeIsNull();

        Map<UUID, Assignment> activeAssignmentByMachineId = activeAssignments.stream()
                .collect(Collectors.toMap(
                        assignment -> assignment.getMachine().getId(),
                        Function.identity()
                ));

        return machines.stream()
                .map(machine -> mapToResponse(machine, activeAssignmentByMachineId.get(machine.getId())))
                .toList();
    }

    private MachineDashboardResponse mapToResponse(Machine machine, Assignment assignment) {
        if (assignment == null) {
            return new MachineDashboardResponse(
                    machine.getId(),
                    machine.getCode(),
                    machine.getName(),
                    machine.isActive(),
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        WorkOrder workOrder = assignment.getWorkOrder();

        return new MachineDashboardResponse(
                machine.getId(),
                machine.getCode(),
                machine.getName(),
                machine.isActive(),
                true,
                assignment.getOperatorName(),
                assignment.getStartTime(),
                workOrder != null ? workOrder.getId() : null,
                workOrder != null ? workOrder.getOrderNo() : null,
                workOrder != null ? workOrder.getTitle() : null,
                resolveWorkOrderStatus(workOrder)
        );
    }

    private String resolveWorkOrderStatus(WorkOrder workOrder) {
        if (workOrder == null || workOrder.getStatus() == null) {
            return null;
        }

        return workOrder.getStatus().toString();
    }
}