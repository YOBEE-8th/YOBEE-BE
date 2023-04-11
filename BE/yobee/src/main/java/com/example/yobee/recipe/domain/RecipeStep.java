package com.example.yobee.recipe.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "recipe_step")
public class RecipeStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeStepId;

    @ManyToOne
    @JoinColumn(name="recipe_id")
    @JsonManagedReference
    private Recipe recipe;

    private int fire;
    private Long timer;
    private String stepImage;
    private String stepDescription;

    private int temperature;


//
//    @JsonBackReference//, orphanRemoval = true
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Gifticon> gifticonList;
//
//    @JsonBackReference
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Favorites> favoriteskList;

    @Builder
    public RecipeStep(Long recipeStepId,
                      int fire,
                      Long timer,
                      String stepImage,
                      String stepDescription,
                      int temperature) {
        this.recipeStepId = recipeStepId;
        this.fire = fire;
        this.timer = timer;
        this.stepImage = stepImage;
        this.stepDescription = stepDescription;
        this.temperature = temperature;


    }



}
