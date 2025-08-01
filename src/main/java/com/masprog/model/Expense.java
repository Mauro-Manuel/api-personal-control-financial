package com.masprog.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_expense")
public class Expense {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ExpenseCategory category;

    @ManyToOne(optional = false)
    private ExpenseDetail detail;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

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
        if (this.expenseDate != null) {
            this.month = expenseDate.getMonthValue();
            this.year = expenseDate.getYear();
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

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public ExpenseDetail getDetail() {
        return detail;
    }

    public void setDetail(ExpenseDetail detail) {
        this.detail = detail;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
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
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id) && Objects.equals(category, expense.category) && Objects.equals(detail, expense.detail) && Objects.equals(expenseDate, expense.expenseDate) && Objects.equals(month, expense.month) && Objects.equals(year, expense.year) && Objects.equals(createdAt, expense.createdAt) && Objects.equals(updatedAt, expense.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, category, detail, expenseDate, month, year, createdAt, updatedAt);
    }
}
