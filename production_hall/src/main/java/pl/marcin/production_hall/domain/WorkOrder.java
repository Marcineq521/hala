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
@Table(name="work_orders")
public class WorkOrder {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name="order_no",nullable = false,unique = true,length = 30)
    private String orderNo;

    @Column(nullable = false,length = 160)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private WorkOrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name ="machine_id",nullable = false)
    private Machine machine;

    @Column(name = "created_at",nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at",nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
