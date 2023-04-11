package com.example.yobee.user.service;

import com.example.yobee.recipe.domain.Recipe;
import com.example.yobee.recipe.domain.RecipeLike;
import com.example.yobee.recipe.dto.HeaderDto;
import com.example.yobee.recipe.dto.RecipesDto;
import com.example.yobee.recipe.dto.EmailDto;
import com.example.yobee.recipe.repository.RecipeLikeRepository;
import com.example.yobee.recipe.repository.RecipeRepository;
import com.example.yobee.review.domain.Review;
import com.example.yobee.review.domain.ReviewLike;
import com.example.yobee.review.dto.CreateReviewDto;
import com.example.yobee.review.dto.ReviewDto;
import com.example.yobee.review.dto.ReviewIdDto;
import com.example.yobee.review.repository.ReviewLikeRepository;
import com.example.yobee.review.repository.ReviewRepository;
import com.example.yobee.review.service.ReviewService;
import com.example.yobee.s3.service.S3Service;
import com.example.yobee.user.domain.User;
import com.example.yobee.user.dto.*;
import com.example.yobee.user.repository.UserRepository;
import com.example.yobee.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class  UserService {

    private final UserRepository userRepository;

    private final ExperienceService experienceService;

    private final RecipeRepository recipeRepository;

    private final S3Service s3Service;

    private final RecipeLikeRepository recipeLikeRepository;

    private final ReviewLikeRepository reviewLikeRepository;

    private final ReviewRepository reviewRepository;

    @Value("${jwt.secret}")
    private String secretkey;
    @Value("${app.sec}")
    private String appkey; // 회원가입, 로그인할때 외부 요청 막기위한 키
    private Long expiredMs = 1000 * 60 * 60 * 24l; //하루
//    private Long expiredMs = 1000 * 60l;    // 1분
    private Long expiredMsRe= expiredMs*24*30;

    String default_img = "https://yobee.s3.ap-northeast-2.amazonaws.com/default_profile.png";
//    private Long expiredMsRe= expiredMs*2;
    public ResponseCreateUserDto createUser (CreateUserDto createUserDto, MultipartFile profileImage) throws IOException {

        String imgPath = null;

        String email = createUserDto.getEmail();
        String password = createUserDto.getPassword();
        String fcmToken = createUserDto.getFcmToken();
        String nickName = createUserDto.getNickname();
        int type = Integer.parseInt(createUserDto.getType());

        String profileImageUrl = createUserDto.getProfileImageUrl();

        //MultipartFile profileImage = createUserDto.getProfileImage();

        System.out.println(type==0);
        //System.out.println(profileImage.isEmpty());
        System.out.println(profileImage==null);
        System.out.println(profileImageUrl==null||profileImageUrl.isEmpty());


        if (!(profileImage==null)) {
            String fileName = "profile_" + String.valueOf(System.currentTimeMillis()) + "." + profileImage.getOriginalFilename().split("\\.")[profileImage.getOriginalFilename().split("\\.").length-1];
            try {
                imgPath = s3Service.upload(profileImage, fileName);
            }
            catch (NullPointerException e) {
                System.out.println(e);
            }
        } else {
            imgPath = profileImageUrl;
        }


//        if (!createUserDto.getSecret().equals(appkey)){
//            return null;
//        }

        UserDto user = new UserDto();

        user.setEmail(email);
        user.setPassword(password);
        user.setType(type);
        user.setFcmToken(fcmToken);
        user.setNickName(nickName);
        if (imgPath == null){
            user.setProfileImage(default_img);
        }else {
            user.setProfileImage(imgPath);
        }
        user.setLevel(0);



        String token = JwtUtil.createJwt(createUserDto.getEmail(), secretkey,expiredMs);//토큰생성
        String refreshToken = JwtUtil.createRefreshToken(expiredMsRe,secretkey);

        user.setRefreshToken(refreshToken);// Refresh 토큰은 db에 저장

        userRepository.save(user.toEntity());

        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(createUserDto.getEmail()));

        ResponseCreateUserDto ruser = new ResponseCreateUserDto();

        ruser.setAccessToken(token); //엑세스 토큰 응답값에 넣어주고
        ruser.setRefreshToken(optionalUser.get().getRefreshToken());
        ruser.setNickname(optionalUser.get().getNickName());
        ruser.setProfileImage(optionalUser.get().getProfileImage());
        ruser.setUserId(optionalUser.get().getUserId());
        ruser.setEmail(optionalUser.get().getEmail());
        ruser.setPassword(optionalUser.get().getPassword());
        ruser.setType(optionalUser.get().getType());
        ruser.setFcmToken(optionalUser.get().getFcmToken());
        ruser.setLevel(optionalUser.get().getLevel());



        experienceService.createExperience(optionalUser.get());


        return ruser;

    }


    public ResponseCreateUserDto login (LoginUserDto loginUserDto){

        String token = JwtUtil.createJwt(loginUserDto.getEmail(), secretkey, expiredMs);//토큰생성

        String refreshToken = JwtUtil.createRefreshToken(expiredMsRe, secretkey);

        ResponseCreateUserDto ruser = new ResponseCreateUserDto();
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(loginUserDto.getEmail()));

        if (optionalUser.isEmpty()) {
            return null;
        }
        else {
            UserDto joinedUser = new UserDto();
            BeanUtils.copyProperties(optionalUser.get(),joinedUser);//이미 db에 있는 값들 복사해주고
            joinedUser.setRefreshToken(refreshToken);//재로그인 했을때 refresh 토큰 새로 저장
            joinedUser.setFcmToken(loginUserDto.getFcmToken());//fcm 토큰 새로 저장해주고
            joinedUser.setType(0);  //일반 로그인은 타입0
            userRepository.save(joinedUser.toEntity());//db 업데이트해주고

            ruser.setAccessToken(token); //엑세스 토큰 응답값에 넣어주고
            ruser.setRefreshToken(joinedUser.getRefreshToken());
            ruser.setNickname(joinedUser.getNickName());
            ruser.setProfileImage(joinedUser.getProfileImage());
            ruser.setUserId(joinedUser.getUserId());
            ruser.setEmail(joinedUser.getEmail());
            ruser.setPassword(joinedUser.getPassword());
            ruser.setType(joinedUser.getType());
            ruser.setFcmToken(joinedUser.getFcmToken());
            ruser.setLevel(joinedUser.getLevel());

            return ruser;
        }

    }

    public ResponseCreateUserDto socialLogin (SocialLoginUserDto socialLoginUserDto){

        String token = JwtUtil.createJwt(socialLoginUserDto.getEmail(), secretkey,expiredMs);//토큰생성

        System.out.println(token);

        String refreshToken = JwtUtil.createRefreshToken(expiredMsRe,secretkey);

        ResponseCreateUserDto ruser = new ResponseCreateUserDto();
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(socialLoginUserDto.getEmail()));

        if (optionalUser.isEmpty()) {
            return null;
        }
        else {
            UserDto joinedUser = new UserDto();
            BeanUtils.copyProperties(optionalUser.get(),joinedUser);//이미 db에 있는 값들 복사해주고
            joinedUser.setRefreshToken(refreshToken);//재로그인 했을때 refresh 토큰 새로 저장
            joinedUser.setFcmToken(socialLoginUserDto.getFcmToken());//fcm 토큰 새로 저장해주고
            joinedUser.setType(socialLoginUserDto.getType()); // 카카오는 2 구글은 1
            userRepository.save(joinedUser.toEntity());//db 업데이트해주고

            ruser.setAccessToken(token); //엑세스 토큰 응답값에 넣어주고
            ruser.setRefreshToken(joinedUser.getRefreshToken());
            ruser.setNickname(joinedUser.getNickName());
            ruser.setProfileImage(joinedUser.getProfileImage());
            ruser.setUserId(joinedUser.getUserId());
            ruser.setEmail(joinedUser.getEmail());
            ruser.setPassword(joinedUser.getPassword());
            ruser.setType(joinedUser.getType());
            ruser.setFcmToken(joinedUser.getFcmToken());
            ruser.setLevel(joinedUser.getLevel());

            return ruser;
        }

    }

    public boolean tokenCheck(String token) { // valid: true / expired: false
        boolean result = true;
        try {
            result = !JwtUtil.isExpired(token,secretkey);
        }
        catch (ExpiredJwtException e) {
            System.out.println(e);
            result = false;
        }
        catch (Exception e) {
            System.out.println(e);
        }

        return result;
    }

    public ResponseValidateTokenDto refresh(ValidateTokenDto validateTokenDto){// 리프레시 토큰 오면 보내는거

        String accessToken = validateTokenDto.getAccessToken();
        String refreshToken = validateTokenDto.getRefreshToken();

        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByRefreshToken(refreshToken));
        if (!optionalUser.isPresent()){
            throw new EntityNotFoundException("User not present in the database");
        }
        User user = optionalUser.get();

        String email = user.getEmail();

        ResponseValidateTokenDto responseValidateTokenDto = new ResponseValidateTokenDto();


        if (!tokenCheck(accessToken) && tokenCheck(refreshToken)) {
            //String email = JwtUtil.getUserName(refreshToken,secretkey);
            //System.out.println(email);
            System.out.println("User by refreshToken : " + email);
            String newAccessToken = JwtUtil.createJwt(email, secretkey,expiredMs);
            System.out.println("User by newAccessToken : " + JwtUtil.getUserName(newAccessToken,secretkey));

            responseValidateTokenDto.setAccessToken(newAccessToken);
            responseValidateTokenDto.setRefreshToken(refreshToken);

        } else if (!tokenCheck(accessToken) && !tokenCheck(refreshToken)) {
            responseValidateTokenDto.setAccessToken(null);
            responseValidateTokenDto.setRefreshToken(null);
        }
        else {
            responseValidateTokenDto.setAccessToken(accessToken);
            responseValidateTokenDto.setRefreshToken(refreshToken);
        }


        return responseValidateTokenDto;
    }

//    public ResponseToken refresh(String refreshToken){// 리프레시 토큰 오면 보내는거
//        User optionalUser = userRepository.findByRefreshToken(refreshToken);
//        ResponseToken responseToken = new ResponseToken();
//        if(optionalUser == null){ //없으면 그냥 보내라
//            return responseToken;// 널값 들어가있음 없으면
//        }
//        User user = optionalUser;
//        UserDto dto = new UserDto();
//        BeanUtils.copyProperties(user,dto);
//
//        String token = JwtUtil.createJwt(user.getEmail(), secretkey,expiredMs);
//        String Refreshtoken = JwtUtil.createRefreshToken (expiredMsRe,secretkey);
//
//        responseToken.setAccessToken(token);
//        responseToken.setRefreshToken(Refreshtoken);
//        //user에 refreshtoken 저장하기
//        dto.setRefreshToken(Refreshtoken);
//        userRepository.save(dto.toEntity());
//
//        return responseToken;
//    }



    public boolean emailCheck (String email){
        System.out.println(userRepository.existsByEmail(email));
        return userRepository.existsByEmail(email);

    }

    public boolean nickNameCheck (String nickName){
        System.out.println(userRepository.existsByNickName(nickName));
        return userRepository.existsByNickName(nickName);

    }

    public boolean nickNameCheckAtInfo (String nickName, String email){
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));
        if (!optionalUser.isPresent()){
            throw new EntityNotFoundException("User not present in the database");
        }
        User user = optionalUser.get();
        if ( !(userRepository.existsByNickName(nickName)) || userRepository.findByEmail(email).getNickName().equals(nickName) ){
            return false;
        }else{
            return true;
        }
    }

    public UserTypeDto logout(String email){

        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));
        if (!optionalUser.isPresent()){
            throw new EntityNotFoundException("User not present in the database");
        }
        User user = optionalUser.get();

        user.setFcmToken(null);
        UserTypeDto userTypeDto = new UserTypeDto();
        userTypeDto.setType(user.getType());

        userRepository.save(user);

        return userTypeDto;
    }


    public EmailDto withdrawal(String email){

        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));
        if (!optionalUser.isPresent()){
            throw new EntityNotFoundException("User not present in the database");
        }
        User user = optionalUser.get();
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail(user.getEmail());
        String url = user.getProfileImage();

        if (!(url.equals(default_img)) && userRepository.findByProfileImage(url).size() == 1) {
            s3Service.delete(user.getProfileImage().split("/")[user.getProfileImage().split("/").length - 1]);
        }

        List<RecipeLike> recipeLikeList = recipeLikeRepository.findByUser(user);
        for (RecipeLike recipeLike : recipeLikeList){
            Recipe recipe = recipeLike.getRecipe();
            int cnt = recipe.getRecipeLikeCnt();
            recipe.setRecipeLikeCnt(cnt - 1);
            recipeRepository.save(recipe);
        }

        List<ReviewLike> reviewLikeList = reviewLikeRepository.findByUser(user);
        for(ReviewLike reviewLike : reviewLikeList){
            Review review = reviewLike.getReview();
            int cnt = review.getReviewLikeCnt();
            review.setReviewLikeCnt(cnt - 1);
            reviewRepository.save(review);
        }

        userRepository.delete(user);

        return emailDto;
    }


    public ResponseUpdateUserInfoDto updateUserInfo(String nickname,MultipartFile profileImage ,String email) throws IOException{

        ResponseUpdateUserInfoDto responseUpdateUserInfoDto = new ResponseUpdateUserInfoDto();


        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));
        if (!optionalUser.isPresent()){
            throw new EntityNotFoundException("User not present in the database");
        }
        User user = optionalUser.get();


        if (!(profileImage==null)) {
            try {
                String url = user.getProfileImage();
                if (!(url.equals(default_img)) && userRepository.findByProfileImage(url).size() == 1) {
                    s3Service.delete(user.getProfileImage().split("/")[user.getProfileImage().split("/").length - 1]);
                }
                String fileName = "profile_" + String.valueOf(System.currentTimeMillis()) + "." + profileImage.getOriginalFilename().split("\\.")[profileImage.getOriginalFilename().split("\\.").length-1];
                String imgPath = s3Service.upload(profileImage, fileName);
                user.setProfileImage(imgPath);
            }
            catch (NullPointerException e) {
                System.out.println(e);
            }
        }


        if (!(nickname==null||nickname.isEmpty())) {
            user.setNickName(nickname);
        }

        userRepository.save(user);

        String nowNickname = user.getNickName();
        String nowProfileImage = user.getProfileImage();

        responseUpdateUserInfoDto.setNickname(nowNickname);
        responseUpdateUserInfoDto.setProfileImage(nowProfileImage);

        return responseUpdateUserInfoDto;
    }








}
