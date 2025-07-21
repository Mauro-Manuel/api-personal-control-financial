package com.masprog.service;

import com.masprog.dto.RecipeDTO;
import com.masprog.dto.RecipeFilterDTO;
import com.masprog.dto.RecipeResponseDTO;
import com.masprog.exceptions.RequiredObjectIsNullException;
import com.masprog.exceptions.ResourceNotFoundException;
import com.masprog.model.Recipe;
import com.masprog.model.RecipeOrigin;
import com.masprog.repository.RecipeRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import static com.masprog.mapper.ObjectMapper.parseObject;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {


    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldSaveRecipe_whenValidDTO_thenReturnResponseDTO() {
        // Entrada (DTO)
        RecipeDTO requestDTO = new RecipeDTO();
        requestDTO.setValue(BigDecimal.valueOf(1000));
        requestDTO.setOrigin(RecipeOrigin.SALARIO);
        requestDTO.setDestination("Banco BAI");
        requestDTO.setReceivedDate(LocalDate.of(2025, 7, 1));

        // Entidade que será salva
        Recipe recipeEntity = new Recipe();
        recipeEntity.setId(1L);
        recipeEntity.setValue(requestDTO.getValue());
        recipeEntity.setOrigin(requestDTO.getOrigin());
        recipeEntity.setDestination(requestDTO.getDestination());
        recipeEntity.setReceivedDate(requestDTO.getReceivedDate());
        recipeEntity.setCreatedAt(LocalDate.now());

        // Simula o comportamento do repository
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipeEntity);

        // Executa o método
        RecipeResponseDTO responseDTO = recipeService.createRecipe(requestDTO);

        // Asserts
        assertNotNull(responseDTO);
        assertEquals(requestDTO.getValue(), responseDTO.getValue());
        assertEquals(requestDTO.getOrigin(), responseDTO.getOrigin());
        assertEquals(requestDTO.getDestination(), responseDTO.getDestination());
        assertEquals(requestDTO.getReceivedDate(), responseDTO.getReceivedDate());
    }

    @Test
    void shouldHaveViolation_whenValueIsNegative() {
        RecipeDTO dto = new RecipeDTO();
        dto.setValue(BigDecimal.valueOf(-50));
        dto.setOrigin(RecipeOrigin.SALARIO);
        dto.setDestination("Banco BAI");
        dto.setReceivedDate(LocalDate.of(2025, 7, 1));

        Set<ConstraintViolation<RecipeDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));
    }

    @Test
    void shouldHaveViolation_whenValueIsNull() {
        RecipeDTO dto = new RecipeDTO();
        dto.setValue(null); // obrigatório
        dto.setOrigin(RecipeOrigin.SALARIO);
        dto.setDestination("Banco BAI");
        dto.setReceivedDate(LocalDate.of(2025, 7, 1));

        Set<ConstraintViolation<RecipeDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldHaveViolation_whenReceivedDateIsInFuture() {
        RecipeDTO dto = new RecipeDTO();
        dto.setValue(BigDecimal.valueOf(100));
        dto.setOrigin(RecipeOrigin.SALARIO);
        dto.setDestination("Banco BAI");
        dto.setReceivedDate(LocalDate.now().plusDays(10)); // futura

        Set<ConstraintViolation<RecipeDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldThrowRuntimeException_whenRepositoryFails() {
        RecipeDTO dto = new RecipeDTO();
        dto.setValue(BigDecimal.valueOf(1000));
        dto.setOrigin(RecipeOrigin.SALARIO);
        dto.setDestination("Banco BAI");
        dto.setReceivedDate(LocalDate.of(2025, 7, 1));

        when(recipeRepository.save(any(Recipe.class)))
                .thenThrow(new RuntimeException("Falha na base de dados"));

        RuntimeException ex = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            recipeService.createRecipe(dto);
        });

        assertEquals("Falha na base de dados", ex.getMessage());
    }

    @Test
    void testGetAllRecipesWithFilter(){
        RecipeFilterDTO filter = new RecipeFilterDTO();
        filter.setOrigin(RecipeOrigin.SALARIO);
        Pageable pageable = PageRequest.of(0, 10);

        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setOrigin(RecipeOrigin.SALARIO);
        recipe.setValue(new BigDecimal("5000.00"));
        recipe.setDestination("Banco BAI");
        recipe.setReceivedDate(LocalDate.of(2025, 6,26));

        Page<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe));
        // Explicitly cast the first argument to Specification<Recipe>
        when(recipeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(recipePage);

        // Act
        Page<RecipeResponseDTO> result = recipeService.getAllRecipes(filter, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(RecipeOrigin.SALARIO, result.getContent().get(0).getOrigin());
    }

    @Test
    void shouldReturnRecipeResponseDTO_whenGetRecipeByIdWithValidId() {
        // Arrange
        Long id = 1L;
        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setOrigin(RecipeOrigin.SALARIO);
        recipe.setValue(new BigDecimal("1000.00"));
        recipe.setDestination("Banco BAI");
        recipe.setReceivedDate(LocalDate.of(2025, 7, 1));
        recipe.setCreatedAt(LocalDate.now());

        RecipeResponseDTO responseDTO = new RecipeResponseDTO();
        responseDTO.setId(id);
        responseDTO.setOrigin(RecipeOrigin.SALARIO);
        responseDTO.setValue(new BigDecimal("1000.00"));
        responseDTO.setDestination("Banco BAI");
        responseDTO.setReceivedDate(LocalDate.of(2025, 7, 1));
        responseDTO.setCreatedAt(LocalDate.now());

        when(recipeRepository.findById(id)).thenReturn(Optional.of(recipe));

        // Mock do método estático parseObject
        try (var mockedStatic = mockStatic(com.masprog.mapper.ObjectMapper.class)) {
            mockedStatic.when(() -> parseObject(recipe, RecipeResponseDTO.class)).thenReturn(responseDTO);

            // Act
            RecipeResponseDTO result = recipeService.getRecipeById(id);

            // Assert
            assertNotNull(result);
            assertEquals(id, result.getId());
            assertEquals(recipe.getOrigin(), result.getOrigin());
            assertEquals(recipe.getValue(), result.getValue());
            assertEquals(recipe.getDestination(), result.getDestination());
            assertEquals(recipe.getReceivedDate(), result.getReceivedDate());
        }
    }

    @Test
    void shouldThrowResourceNotFoundException_whenGetRecipeByIdWithNonExistentId() {
        // Arrange
        Long id = 999L;
        when(recipeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> recipeService.getRecipeById(id));
        assertEquals("Recipe not found with ID: " + id, exception.getMessage());
    }

    @Test
    void shouldThrowIllegalArgumentException_whenGetRecipeByIdWithNullId() {
        // Act & Assert
        RequiredObjectIsNullException exception = assertThrows(RequiredObjectIsNullException.class,
                () -> recipeService.getRecipeById(null));
        assertEquals("It is not allowed to persist a null object!", exception.getMessage());
    }

    @Test
    void shouldReturnRecipeResponseDTO_whenUpdateRecipeWithValidIdAndDTO() {
        // Arrange
        Long id = 1L;
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setOrigin(RecipeOrigin.SALARIO);
        recipeDTO.setValue(new BigDecimal("5000.00"));
        recipeDTO.setDestination("Test");
        recipeDTO.setReceivedDate(LocalDate.now());

        Recipe existingRecipe = new Recipe();
        existingRecipe.setId(id);
        existingRecipe.setOrigin(RecipeOrigin.SALARIO);
        existingRecipe.setValue(new BigDecimal("1000.00"));
        existingRecipe.setDestination("Old Destination");
        existingRecipe.setReceivedDate(LocalDate.now().minusDays(1));
        existingRecipe.setCreatedAt(LocalDate.now().minusDays(2));

        RecipeResponseDTO responseDTO = new RecipeResponseDTO();
        responseDTO.setId(id);
        responseDTO.setOrigin(recipeDTO.getOrigin());
        responseDTO.setValue(recipeDTO.getValue());
        responseDTO.setDestination(recipeDTO.getDestination());
        responseDTO.setReceivedDate(recipeDTO.getReceivedDate());
        responseDTO.setMonth(recipeDTO.getReceivedDate().getMonthValue());
        responseDTO.setYear(recipeDTO.getReceivedDate().getYear());
        responseDTO.setCreatedAt(existingRecipe.getCreatedAt());
        responseDTO.setUpdatedAt(LocalDate.now());

        when(recipeRepository.findById(id)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);

        try (var mockedStatic = mockStatic(com.masprog.mapper.ObjectMapper.class)) {
            mockedStatic.when(() -> parseObject(recipeDTO, Recipe.class)).thenReturn(existingRecipe);
            mockedStatic.when(() -> parseObject(existingRecipe, RecipeResponseDTO.class)).thenReturn(responseDTO);

            // Act
            RecipeResponseDTO result = recipeService.updateRecipe(id, recipeDTO);

            // Assert
            assertNotNull(result);
            assertEquals(id, result.getId());
            assertEquals(recipeDTO.getOrigin(), result.getOrigin());
            assertEquals(recipeDTO.getValue(), result.getValue());
            assertEquals(recipeDTO.getDestination(), result.getDestination());
            assertEquals(recipeDTO.getReceivedDate(), result.getReceivedDate());
            assertEquals(recipeDTO.getReceivedDate().getMonthValue(), result.getMonth());
            assertEquals(recipeDTO.getReceivedDate().getYear(), result.getYear());
            assertEquals(existingRecipe.getCreatedAt(), result.getCreatedAt());
            assertEquals(LocalDate.now(), result.getUpdatedAt());

            verify(recipeRepository).findById(id);
            verify(recipeRepository).save(any(Recipe.class));
        }
    }
    @Test
    void shouldThrowResourceNotFoundException_whenUpdateRecipeWithInvalidId() {
        // Arrange
        Long id = 999L;
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setOrigin(RecipeOrigin.SALARIO);
        recipeDTO.setValue(new BigDecimal("5000.00"));
        recipeDTO.setDestination("Test");
        recipeDTO.setReceivedDate(LocalDate.now());

        when(recipeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> recipeService.updateRecipe(id, recipeDTO));
        verify(recipeRepository).findById(id);
        verify(recipeRepository, never()).save(any(Recipe.class));
    }
    @Test
    void shouldThrowRequiredObjectIsNullException_whenUpdateRecipeWithNullDTO() {
        // Arrange
        Long id = 1L;

        // Act & Assert
        assertThrows(RequiredObjectIsNullException.class, () -> recipeService.updateRecipe(id, null));
        verify(recipeRepository, never()).findById(anyLong());
        verify(recipeRepository, never()).save(any(Recipe.class));
    }
}
