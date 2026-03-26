package pl.marcin.production_hall.workorder;


import lombok.RequiredArgsConstructor;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Service;
import pl.marcin.production_hall.domain.Machine;
import pl.marcin.production_hall.domain.WorkOrder;
import pl.marcin.production_hall.domain.WorkOrderStatus;
import pl.marcin.production_hall.eventlog.EventLogService;
import pl.marcin.production_hall.eventlog.EventLogType;
import pl.marcin.production_hall.machine.MachineRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final MachineRepository machineRepository;
    private final EventLogService eventLogService;

    public WorkOrderResponse create(CreateWorkOrderRequest request){
        Machine machine=machineRepository.findById(request.machineId())
                .orElseThrow(()-> new RuntimeException("Machine not found"));

        WorkOrder workOrder =WorkOrder.builder().
                orderNo(request.orderNo())
                .title(request.title())
                .status(WorkOrderStatus.NEW)
                .machine(machine)
                .build();

        WorkOrder saved=workOrderRepository.save(workOrder);

        eventLogService.log(EventLogType.WORK_ORDER_CREATED,
                machine.getId(),
                null,
                saved.getId(),
                "Work order "+saved.getOrderNo()+ " created for machine "+ machine.getCode()
        );
        return mapToResponse(saved);
    }

    public List<WorkOrderResponse> getAll() {
        return workOrderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<WorkOrderResponse> getByMachine(UUID machineId) {
        return workOrderRepository.findByMachine_IdOrderByCreatedAtDesc(machineId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public WorkOrderResponse updateStatus(UUID workOrderId, WorkOrderStatus newStatus) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("Work order not found"));

        workOrder.setStatus(newStatus);

        WorkOrder saved = workOrderRepository.save(workOrder);

        eventLogService.log(
                mapEventType(newStatus),
                saved.getMachine().getId(),
                null,
                saved.getId(),
                "Work order " + saved.getOrderNo() + " status changed to " + saved.getStatus()
        );

        return mapToResponse(saved);
    }

    private EventLogType mapEventType(WorkOrderStatus status) {
        return switch (status) {
            case IN_PROGRESS -> EventLogType.WORK_ORDER_STARTED;
            case PAUSED -> EventLogType.WORK_ORDER_PAUSED;
            case DONE -> EventLogType.WORK_ORDER_DONE;
            case CANCELLED -> EventLogType.WORK_ORDER_CANCELLED;
            case NEW -> EventLogType.INFO;
        };
    }

    private WorkOrderResponse mapToResponse(WorkOrder workOrder) {
        return WorkOrderResponse.builder()
                .id(workOrder.getId())
                .orderNo(workOrder.getOrderNo())
                .title(workOrder.getTitle())
                .status(workOrder.getStatus())
                .machineId(workOrder.getMachine().getId())
                .machineCode(workOrder.getMachine().getCode())
                .createdAt(workOrder.getCreatedAt())
                .updatedAt(workOrder.getUpdatedAt())
                .build();
    }
}