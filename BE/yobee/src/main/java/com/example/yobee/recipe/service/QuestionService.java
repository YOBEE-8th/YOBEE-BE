package com.example.yobee.recipe.service;

import com.example.yobee.recipe.domain.UnresolvedQuestion;
import com.example.yobee.recipe.repository.RecipeRepository;
import com.example.yobee.recipe.repository.UnresolvedQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final UnresolvedQuestionRepository unresolvedQuestionRepository;

    private final RecipeRepository recipeRepository;

    @Transactional
    public void saveQuestion(Long recipeId, String content) {
        UnresolvedQuestion unresolvedQuestion = new UnresolvedQuestion();
        unresolvedQuestion.setRecipe(recipeRepository.findById(recipeId));
        unresolvedQuestion.setContent(content);

        unresolvedQuestionRepository.save(unresolvedQuestion);
    }
}
