package com.example.yobee.recipe.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "unresolvedQuestion")
public class UnresolvedQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @ManyToOne
    @JoinColumn(name="recipe_id")
    @JsonManagedReference
    private Recipe recipe;

    private String content;


//
//    @JsonBackReference//, orphanRemoval = true
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Gifticon> gifticonList;
//
//    @JsonBackReference
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Favorites> favoriteskList;

    @Builder
    public UnresolvedQuestion(Long questionId,
                              String content) {
        this.questionId = questionId;
        this.content = content;


    }



}
