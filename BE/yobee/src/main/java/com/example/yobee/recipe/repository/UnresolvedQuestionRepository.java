package com.example.yobee.recipe.repository;

import com.example.yobee.recipe.domain.UnresolvedQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnresolvedQuestionRepository extends JpaRepository<UnresolvedQuestion, Long> {
}
