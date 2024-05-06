package com.example.yobee.recipe.domain;

import com.example.yobee.review.domain.Review;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "recipe")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeId;
    private String recipeTitle;
    private String resultImage;

    private String time;

    private int difficulty;

    private String servings;

    private boolean isAi;

    private String category;

    private int recipeLikeCnt;

    private int recipe10000Id;
    private long view;
    @JsonBackReference
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviewList;

    @JsonBackReference
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RecipeLike> recipeLikeList;

    @JsonBackReference
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UnresolvedQuestion> unresolvedQuestionList;
    @JsonBackReference
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RecipeStep> recipeStepList;
    @JsonBackReference
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Ingredient> ingredientList;
    @JsonBackReference
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HashTag> hashTagList;

    @Builder
    public Recipe(Long recipeId,
                  String recipeTitle,
                  String resultImage,
                  String time,
                  int difficulty,
                  String servings,
                  boolean isAi,
                  String category,
                  int recipeLikeCnt) {
        this.recipeId = recipeId;
        this.recipeTitle = recipeTitle;
        this.resultImage = resultImage;
        this.time = time;
        this.difficulty =  difficulty;
        this.servings = servings;
        this.isAi = isAi;
        this.category = category;
        this.recipeLikeCnt = recipeLikeCnt;

    }



}
