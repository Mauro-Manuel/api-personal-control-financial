package com.masprog.service;

import com.masprog.dto.RecipeDTO;
import com.masprog.dto.RecipeFilterDTO;
import com.masprog.dto.RecipeResponseDTO;
import com.masprog.exceptions.RequiredObjectIsNullException;
import com.masprog.model.Recipe;
import com.masprog.repository.RecipeRepository;
import com.masprog.repository.RecipeSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.masprog.mapper.ObjectMapper.parseObject;

@Service
public class RecipeService {

    private final Logger logger = LoggerFactory.getLogger(RecipeService.class.getName());

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Transactional
    public RecipeResponseDTO createRecipe(RecipeDTO recipeDTO){

        if (recipeDTO == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one Recipe!");
        Recipe recipe = parseObject(recipeDTO, Recipe.class);
        return  parseObject(recipeRepository.save(recipe), RecipeResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public Page<RecipeResponseDTO> getAllRecipes(RecipeFilterDTO filter, Pageable pageable){
        logger.info("Retrieving recipes with filters and pagination");
        Page<Recipe> recipePage = recipeRepository
                .findAll(RecipeSpecification.withFilters(filter), pageable);

        return recipePage.map(recipe -> parseObject(recipe, RecipeResponseDTO.class));

    }
}
