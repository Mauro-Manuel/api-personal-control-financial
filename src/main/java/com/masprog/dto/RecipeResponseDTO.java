package com.masprog.dto;

import com.masprog.model.RecipeOrigin;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class RecipeResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private RecipeOrigin origin;
    private BigDecimal value;
    private String destination;
    private LocalDate receivedDate;
    private Integer month;
    private Integer year;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public RecipeResponseDTO() {}

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
        RecipeResponseDTO that = (RecipeResponseDTO) o;
        return Objects.equals(id, that.id) && origin == that.origin && Objects.equals(value, that.value) && Objects.equals(destination, that.destination) && Objects.equals(receivedDate, that.receivedDate) && Objects.equals(month, that.month) && Objects.equals(year, that.year) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, origin, value, destination, receivedDate, month, year, createdAt, updatedAt);
    }
}
