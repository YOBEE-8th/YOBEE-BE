package com.example.yobee.user.repository;

import com.example.yobee.user.domain.Experience;
import com.example.yobee.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    Experience findByUser(User user);
}

