package com.example.yobee.user.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.mapping.ToOne;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "experience")
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="experience_id")
    private Long experienceId;

    @OneToOne
    @JoinColumn(name="user_id") // 1:1 대상 테이블에 외래 키 - 양방향
    private User user;
    private int soupExp;
    private int sideExp;
    private int grilledExp;
    private int noodleExp;
    private int dessertExp;

//
//    @JsonBackReference//, orphanRemoval = true
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Gifticon> gifticonList;
//
//    @JsonBackReference
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Favorites> favoriteskList;

    @Builder
    public Experience(Long experienceId,
                      int soupExp,
                      int sideExp,
                      int grilledExp,
                      int noodleExp,
                      int dessertExp) {
        this.experienceId = experienceId;
        this.soupExp = soupExp;
        this.sideExp = sideExp;
        this.grilledExp = grilledExp;
        this.noodleExp = noodleExp;
        this.dessertExp = dessertExp;

    }





}
