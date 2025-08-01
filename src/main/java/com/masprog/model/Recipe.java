package com.masprog.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tb_recipe")
public class Recipe implements Serializable {

    private static final long serialVersionUID = 1L;

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

   // @Formula("EXTRACT(MONTH FROM received_date)")
    private Integer month;

    //@Formula("EXTRACT(YEAR FROM received_date)")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RecipeOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(RecipeOrigin origin) {
        this.origin = origin;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return Objects.equals(id, recipe.id) && origin == recipe.origin && Objects.equals(value, recipe.value) && Objects.equals(destination, recipe.destination) && Objects.equals(receivedDate, recipe.receivedDate) && Objects.equals(month, recipe.month) && Objects.equals(year, recipe.year) && Objects.equals(createdAt, recipe.createdAt) && Objects.equals(updatedAt, recipe.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, origin, value, destination, receivedDate, month, year, createdAt, updatedAt);
    }
}
