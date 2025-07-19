package com.masprog.controller;

import com.masprog.dto.RecipeDTO;
import com.masprog.dto.RecipeResponseDTO;
import com.masprog.model.Recipe;
import com.masprog.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    public ResponseEntity<RecipeResponseDTO> createRecipe( @RequestBody @Valid RecipeDTO recipeDTO){
       RecipeResponseDTO created = recipeService.createRecipe(recipeDTO);
       return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
