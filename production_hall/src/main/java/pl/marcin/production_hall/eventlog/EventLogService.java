package pl.marcin.production_hall.eventlog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.marcin.production_hall.domain.EventLog;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventLogService {

    private final EventLogRepository eventLogRepository;

    public EventLogResponse create(CreateEventLogRequest request){
        EventLog saved=eventLogRepository.save(
                EventLog.builder()
                        .type(request.type())
                        .machineId(request.machineId())
                        .assignmentId(request.assignmentId())
                        .workOrderId(request.workOrderId())
                        .message(request.message())
                        .build()
        );
        return map(saved);
    }

    public void log(EventLogType type, UUID machineId,UUID assignemntId,UUID workOrderId,String message){
        eventLogRepository.save(
                EventLog.builder()
                        .type(type)

                        .machineId(machineId)
                        .assignmentId(assignemntId)
                        .workOrderId(workOrderId)
                        .message(message)
                        .build()
        );
    }

    public List<EventLogResponse> getByMachine(UUID machineId) {
        return eventLogRepository.findByMachineIdOrderByCreatedAtDesc(machineId)
                .stream()
                .map(this::map)
                .toList();
    }

    public List<EventLogResponse> getAll(){
        return eventLogRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::map)
                .toList();
    }
    public List<EventLogResponse> getByAssignment(UUID assignmentId) {
        return eventLogRepository.findByAssignmentIdOrderByCreatedAtDesc(assignmentId)
                .stream()
                .map(this::map)
                .toList();
    }

    public List<EventLogResponse> getByWorkOrder(UUID workOrderId) {
        return eventLogRepository.findByWorkOrderIdOrderByCreatedAtDesc(workOrderId)
                .stream()
                .map(this::map)
                .toList();
    }

    private EventLogResponse map(EventLog eventLog) {
        return new EventLogResponse(
                eventLog.getId(),
                eventLog.getCreatedAt(),
                eventLog.getType(),
                eventLog.getMachineId(),
                eventLog.getAssignmentId(),
                eventLog.getWorkOrderId(),
                eventLog.getMessage()
        );
    }
}
