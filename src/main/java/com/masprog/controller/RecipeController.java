package com.masprog.controller;

import com.masprog.controller.docs.RecipeControllerDocs;
import com.masprog.dto.RecipeDTO;
import com.masprog.dto.RecipeFilterDTO;
import com.masprog.dto.RecipeResponseDTO;
import com.masprog.service.RecipeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/recipes")
@Tag(name = "Receita", description = "Endpoints para gerenciar receitas")
public class RecipeController implements RecipeControllerDocs {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }


    @PostMapping
    @Override
    public ResponseEntity<RecipeResponseDTO> create(@RequestBody @Valid RecipeDTO recipe) {
        RecipeResponseDTO created = recipeService.createRecipe(recipe);
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

    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponseDTO> updateRecipe(@PathVariable Long id,
                                                          @Valid @RequestBody RecipeDTO recipeDTO) {
        RecipeResponseDTO updatedRecipe = recipeService.updateRecipe(id, recipeDTO);
        return new ResponseEntity<>(updatedRecipe, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
