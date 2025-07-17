package com.masprog.dto;

import com.masprog.model.RecipeOrigin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class RecipeDTO {
    private RecipeOrigin origin;
    private BigDecimal value;
    private String destination;
    private LocalDate receivedDate;

    public RecipeDTO(){}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeDTO recipeDTO = (RecipeDTO) o;
        return origin == recipeDTO.origin && Objects.equals(value, recipeDTO.value) && Objects.equals(destination, recipeDTO.destination) && Objects.equals(receivedDate, recipeDTO.receivedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, value, destination, receivedDate);
    }
}
