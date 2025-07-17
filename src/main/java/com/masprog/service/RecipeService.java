package com.masprog.service;

import com.masprog.dto.RecipeDTO;
import com.masprog.exceptions.RequiredObjectIsNullException;
import com.masprog.mapper.ObjectMapper;
import com.masprog.model.Recipe;
import com.masprog.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeService {

    private final Logger logger = LoggerFactory.getLogger(RecipeService.class.getName());

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Transactional
    public Recipe createRecipe(RecipeDTO recipeDTO){

        if (recipeDTO == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one Recipe!");
        Recipe recipe = ObjectMapper.parseObject(recipeDTO, Recipe.class);
        return recipeRepository.save(recipe);
    }
}
