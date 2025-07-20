package com.masprog.controller;

import com.masprog.dto.RecipeDTO;
import com.masprog.dto.RecipeFilterDTO;
import com.masprog.dto.RecipeResponseDTO;
import com.masprog.model.Recipe;
import com.masprog.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<Page<RecipeResponseDTO>> getAllRecipes(RecipeFilterDTO filter,
                                                                 Pageable pageable) {
        Page<RecipeResponseDTO> recipes = recipeService.getAllRecipes(filter, pageable);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponseDTO> getRecipeById(@PathVariable Long id) {
        RecipeResponseDTO recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(recipe);
    }
}
