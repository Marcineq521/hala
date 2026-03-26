package pl.marcin.production_hall.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import pl.marcin.production_hall.eventlog.EventLogType;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="event_logs")
public class EventLog {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name="created_at",nullable = false)
    private OffsetDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name="type",nullable = false,length = 40)
    private EventLogType type;

    @Column(name="machine_id")
    private UUID machineId;

    @Column(name="assignment_id")
    private UUID assignmentId;

    @Column(name="work_order_id")
    private UUID workOrderId;

    @Column(name="message",nullable = false,columnDefinition = "TEXT")
    private String message;

    @PrePersist
    public void prePersist(){
        if(createdAt==null){
            createdAt=OffsetDateTime.now();
        }
    }

}
