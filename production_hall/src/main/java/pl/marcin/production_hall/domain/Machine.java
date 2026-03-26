package pl.marcin.production_hall.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="machines")
public class Machine {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false,unique = true,length = 10)
    private String code; //D1, D2, D3

    @Column(nullable = false,length = 100)
    private String name;

    @Column(name = "is_active",nullable = false)
    private boolean active=true;
}
