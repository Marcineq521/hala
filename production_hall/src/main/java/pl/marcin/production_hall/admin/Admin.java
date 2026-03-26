package pl.marcin.production_hall.admin;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "admins")
public class Admin {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "is_active",nullable = false)
    private boolean active=true;

    @Column(name = "created_at",nullable = false, updatable = false)
    private Instant createdAt=Instant.now();

    public Admin() {
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
