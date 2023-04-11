package com.example.yobee.user.service;

import com.example.yobee.user.domain.User;
import com.example.yobee.user.dto.ChangePasswordDto;
import com.example.yobee.user.dto.LoginUserDto;
import com.example.yobee.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String encode(String password){
        return passwordEncoder.encode(password);
    }
    public Boolean match(String rawPassword, String Password) { return passwordEncoder.matches(rawPassword, Password); }

    public int changePassword(ChangePasswordDto changePasswordDto, String email){

        String newPassword = changePasswordDto.getPassword();

        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));
        if (!optionalUser.isPresent()){
            throw new EntityNotFoundException("User not present in the database");
        }

        User user = optionalUser.get();
        if(match(newPassword, user.getPassword())) {
            return 2;
        }
        user.setPassword(encode(newPassword));

        try{
            userRepository.save(user);
            return  1;
        }
        catch (Exception e){
            return 0;
        }


    }


    public Boolean isMatch(LoginUserDto loginUserDto){

        String password = loginUserDto.getPassword();
        String email = loginUserDto.getEmail();
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));
        if (!optionalUser.isPresent()){
            return false;
        }

        User user = optionalUser.get();

        return match(password, user.getPassword());
    }
}
