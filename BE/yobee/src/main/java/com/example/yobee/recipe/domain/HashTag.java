package com.example.yobee.recipe.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "HashTag")
public class HashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long HashTagId;

    @ManyToOne
    @JoinColumn(name="recipe_id")
    @JsonManagedReference
    private Recipe recipe;

    private String tag;



//
//    @JsonBackReference//, orphanRemoval = true
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Gifticon> gifticonList;
//
//    @JsonBackReference
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Favorites> favoriteskList;

    @Builder
    public HashTag(Long HashTagId,
                   String tag) {
        this.HashTagId = HashTagId;
        this.tag = tag;


    }



}
