package com.masprog.dto;

import com.masprog.model.RecipeOrigin;
import io.swagger.v3.oas.annotations.Parameter;

import java.time.LocalDate;

public class RecipeFilterDTO {


    private RecipeOrigin origin;
    private String destination;
    private LocalDate receivedDateFrom;
    private LocalDate receivedDateTo;
    private Integer month;
    private Integer year;

    public RecipeOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(RecipeOrigin origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getReceivedDateFrom() {
        return receivedDateFrom;
    }

    public void setReceivedDateFrom(LocalDate receivedDateFrom) {
        this.receivedDateFrom = receivedDateFrom;
    }

    public LocalDate getReceivedDateTo() {
        return receivedDateTo;
    }

    public void setReceivedDateTo(LocalDate receivedDateTo) {
        this.receivedDateTo = receivedDateTo;
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
}
