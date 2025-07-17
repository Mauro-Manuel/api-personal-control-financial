package com.masprog.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipeOrigin origin;

    @Column(nullable = false)
    private BigDecimal value;

    @Column(nullable = false)
    private String destination;

    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate;

    private Integer month;
    private Integer year;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
        if (this.receivedDate != null) {
            this.month = receivedDate.getMonthValue();
            this.year = receivedDate.getYear();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDate.now();
    }

}
