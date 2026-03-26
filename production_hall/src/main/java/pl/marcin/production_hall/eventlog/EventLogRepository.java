package pl.marcin.production_hall.eventlog;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.marcin.production_hall.domain.EventLog;

import java.util.List;
import java.util.UUID;

public interface EventLogRepository extends JpaRepository<EventLog, UUID>{

    List<EventLog> findAllByOrderByCreatedAtDesc();

    List<EventLog> findByMachineIdOrderByCreatedAtDesc(UUID machineId);

    List<EventLog> findByAssignmentIdOrderByCreatedAtDesc(UUID assignmentId);

    List<EventLog> findByWorkOrderIdOrderByCreatedAtDesc(UUID workOrderId);
}

