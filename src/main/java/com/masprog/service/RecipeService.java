package com.masprog.service;

import com.masprog.dto.RecipeDTO;
import com.masprog.dto.RecipeFilterDTO;
import com.masprog.dto.RecipeResponseDTO;
import com.masprog.exceptions.RequiredObjectIsNullException;
import com.masprog.exceptions.ResourceNotFoundException;
import com.masprog.model.Recipe;
import com.masprog.repository.RecipeRepository;
import com.masprog.repository.RecipeSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.masprog.mapper.ObjectMapper.parseObject;

@Service
public class RecipeService {

    private final Logger logger = LoggerFactory.getLogger(RecipeService.class.getName());

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @CacheEvict(value = {"recipesFilteredCache", "recipesAllCache", "recipeByIdCache"}, allEntries = true)
    @Transactional
    public RecipeResponseDTO createRecipe(RecipeDTO recipeDTO){

        if (recipeDTO == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one Recipe!");
        Recipe recipe = parseObject(recipeDTO, Recipe.class);
        return  parseObject(recipeRepository.save(recipe), RecipeResponseDTO.class);
    }

    @Cacheable(value = "recipesFilteredCache", key = "'filter=' + #filter.toString() + ',page=' + #pageable.pageNumber + ',size=' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<RecipeResponseDTO> getFilteredRecipesPaginated(RecipeFilterDTO filter, Pageable pageable){
        logger.info("Retrieving recipes with filters and pagination");
        Page<Recipe> recipePage = recipeRepository
                .findAll(RecipeSpecification.withFilters(filter), pageable);

        return recipePage.map(recipe -> parseObject(recipe, RecipeResponseDTO.class));

    }

    @Cacheable(value = "recipesAllCache", key = "'page=' + #pageable.pageNumber + ',size=' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<RecipeResponseDTO> getAllRecipesPaginated(Pageable pageable) {
        logger.info("Retrieving all recipes with pagination (no filters)");

        Page<Recipe> recipePage = recipeRepository.findAll(pageable);

        return recipePage.map(recipe -> parseObject(recipe, RecipeResponseDTO.class));
    }


    @Cacheable(value = "recipeByIdCache", key = "#id")
    @Transactional(readOnly = true)
    public RecipeResponseDTO getRecipeById(Long id) {
        if (id == null) throw new RequiredObjectIsNullException();
        logger.info("Retrieving Recipe with ID: {}", id);
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with ID: " + id));
        return parseObject(recipe, RecipeResponseDTO.class);
    }

    @CacheEvict(value = {"recipesFilteredCache", "recipesAllCache", "recipeByIdCache"}, allEntries = true)
    @Transactional
    public RecipeResponseDTO updateRecipe(Long id, RecipeDTO recipeDTO) {
        if (id == null || recipeDTO == null) {
            throw new RequiredObjectIsNullException("Recipe ID and DTO cannot be null");
        }

        logger.info("Updating recipe with ID: {}", id);


        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with ID: " + id));


        Recipe updatedRecipe = parseObject(recipeDTO, Recipe.class);
        updatedRecipe.setId(existingRecipe.getId());
        updatedRecipe.setCreatedAt(existingRecipe.getCreatedAt());
        updatedRecipe.setUpdatedAt(LocalDate.now());
        updatedRecipe.setMonth(recipeDTO.getReceivedDate().getMonthValue());
        updatedRecipe.setYear(recipeDTO.getReceivedDate().getYear());


        Recipe savedRecipe = recipeRepository.save(updatedRecipe);
        return parseObject(savedRecipe, RecipeResponseDTO.class);
    }

    @CacheEvict(value = {"recipesFilteredCache", "recipesAllCache", "recipeByIdCache"}, allEntries = true)
    @Transactional
    public void deleteRecipe(Long id) {
        if (id == null) {
            throw new RequiredObjectIsNullException("Recipe ID cannot be null");
        }
        logger.info("Deleting recipe with ID: {}", id);
        if (!recipeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recipe not found with ID: " + id);
        }
        recipeRepository.deleteById(id);
    }



}
