package com.example.yobee.user.repository;

import com.example.yobee.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    //    User findOne(UserId id);

    User findByEmail(String email);

    User findByEmailAndPassword(String email,String password);

    User findByEmailAndType(String email, int type);

    User findByRefreshToken(String token);

    List<User> findByProfileImage(String url);

    boolean existsByEmail(String email);

    boolean existsByNickName(String nickName);
}
