package com.masprog.service;

import com.masprog.dto.ExpenseDTO;
import com.masprog.dto.ExpenseResponseDTO;
import com.masprog.dto.RecipeDTO;
import com.masprog.dto.RecipeResponseDTO;
import com.masprog.exceptions.RequiredObjectIsNullException;
import com.masprog.model.Expense;
import com.masprog.model.Recipe;
import com.masprog.repository.ExpenseRepository;
import com.masprog.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.masprog.mapper.ObjectMapper.parseObject;

@Service
public class ExpenseService {

    private final Logger logger = LoggerFactory.getLogger(RecipeService.class.getName());

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Transactional
    public ExpenseResponseDTO createExpense(ExpenseDTO expenseDTO){

        if (expenseDTO == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one Expense!");
        Expense expense = parseObject(expenseDTO, Expense.class);
        return  parseObject(expenseRepository.save(expense), ExpenseResponseDTO.class);
    }
}
