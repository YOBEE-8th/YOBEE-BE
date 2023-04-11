package com.example.yobee.recipe.repository;

import com.example.yobee.recipe.domain.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {
}
