package com.masprog.repository;

import com.masprog.dto.RecipeFilterDTO;
import com.masprog.model.Recipe;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class RecipeSpecification {

    public static Specification<Recipe> withFilters(RecipeFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getOrigin() != null) {
                predicates.add(criteriaBuilder.equal(root.get("origin"), filter.getOrigin()));
            }

            if (filter.getDestination() != null && !filter.getDestination().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("destination")),
                        "%" + filter.getDestination().toLowerCase() + "%"
                ));
            }

            if (filter.getReceivedDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("receivedDate"), filter.getReceivedDateFrom()
                ));
            }

            if (filter.getMonth() != null) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.function(
                                "TO_CHAR", String.class,
                                root.get("receivedDate"),
                                criteriaBuilder.literal("MM")
                        ),
                        String.format("%02d", filter.getMonth()) // "06" para Junho
                ));
            }

            if (filter.getYear() != null) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.function(
                                "TO_CHAR", String.class,
                                root.get("receivedDate"),
                                criteriaBuilder.literal("YYYY")
                        ),
                        filter.getYear().toString()
                ));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}