package pl.marcin.production_hall.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "assignments")
public class Assignment {

    @ManyToOne
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "machine_id",nullable = false)
    private Machine machine;

    @Column(name = "operator_name", nullable = false,length = 120)
    private String operatorName;

    @Column(name = "start_time",nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    @PrePersist
    public void prePersist(){
        if(this.startTime==null){
            this.startTime=OffsetDateTime.now();
        }
    }









}
