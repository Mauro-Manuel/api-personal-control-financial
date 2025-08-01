package com.masprog.controller;


import com.masprog.controller.docs.ExpenseControllerDocs;
import com.masprog.dto.ExpenseDTO;
import com.masprog.dto.ExpenseResponseDTO;
import com.masprog.dto.RecipeDTO;
import com.masprog.dto.RecipeResponseDTO;
import com.masprog.service.ExpenseService;
import com.masprog.service.RecipeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/expenses")
@Tag(name = "Despesas", description = "Endpoints para gerenciar despesas")
public class ExpenseController implements ExpenseControllerDocs {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @Override
    public ResponseEntity<ExpenseResponseDTO> create(@RequestBody @Valid ExpenseDTO expense) {
        ExpenseResponseDTO created = expenseService.createExpense(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
