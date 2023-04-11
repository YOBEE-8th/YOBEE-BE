package com.example.yobee.recipe.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "Ingredient")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientId;
    private String weight;
    @ManyToOne
    @JoinColumn(name="recipe_id")
    @JsonManagedReference
    private Recipe recipe;

    private String ingredientName;


//
//    @JsonBackReference//, orphanRemoval = true
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Gifticon> gifticonList;
//
//    @JsonBackReference
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Favorites> favoriteskList;

    @Builder
    public Ingredient(Long ingredientId,
                      String ingredientName,
                      String weight) {
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.weight = weight;

    }



}
