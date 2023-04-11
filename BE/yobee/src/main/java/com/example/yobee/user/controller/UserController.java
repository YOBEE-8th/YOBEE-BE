package com.example.yobee.user.controller;

import com.example.yobee.recipe.dto.MyHistoryDto;
import com.example.yobee.recipe.dto.MyReivewDto;
import com.example.yobee.recipe.dto.RecipesDto;
import com.example.yobee.recipe.dto.EmailDto;
import com.example.yobee.recipe.service.RecipeService;
import com.example.yobee.review.dto.*;
import com.example.yobee.review.service.ReviewService;
import com.example.yobee.user.dto.ResponseUpdateExperienceDto;
import com.example.yobee.user.dto.UpdateExperienceDto;
import com.example.yobee.response.Message;
import com.example.yobee.user.dto.*;
import com.example.yobee.user.repository.UserRepository;
import com.example.yobee.user.service.EmailService;
import com.example.yobee.user.service.ExperienceService;
import com.example.yobee.user.service.PasswordService;
import com.example.yobee.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Api(value = "UserController")
@SwaggerDefinition(tags = {@Tag(name = "UserController",
        description = "유저 컨트롤러")})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user")
public class UserController {

    private final UserService userservice;

    private final ExperienceService experienceService;

    private final EmailService emailService;

    private final ReviewService reviewService;

    private final RecipeService recipeService;
    private final PasswordService passwordService;
    private final UserRepository userRepository;
    private boolean refreshFlag = true;


    @ApiOperation(value = "signup",
            notes = "회원가입",
            httpMethod = "POST")
    @PostMapping(value="/signup",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            })
    public ResponseEntity<Message> signup(@RequestPart(value="email") String email, @RequestPart(value="password",required = false) String password, @RequestPart(value="type",required = false) String type, @RequestPart(value="nickname",required = false) String nickname, @RequestPart(value="profileImageUrl",required = false) String profileImageUrl,@RequestPart(value="fcmToken") String fcmToken, @RequestPart(value="profileImage",required = false) MultipartFile profileImage) {

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));


        CreateUserDto createUserDto = new CreateUserDto();

        createUserDto.setEmail(email);

        if (password == null){
            createUserDto.setPassword(password);
        }else{
            createUserDto.setPassword(passwordService.encode(password));
        }

        createUserDto.setType(type);
        createUserDto.setNickname(nickname);
        createUserDto.setFcmToken(fcmToken);
        createUserDto.setProfileImageUrl(profileImageUrl);

        if (nickname.length() <2 || nickname.length() > 8) {
            message.setStatus(400);
            message.setMessage("닉네임은 2 ~ 8자리 입력해 주세요.");
            log.info("{} {}", message.getMessage(),nickname);

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }


        try {
            if (profileImage==null) {
                ResponseCreateUserDto result = userservice.createUser(createUserDto, null);

                message.setStatus(200);
                message.setMessage("회원가입 완료");
                message.setData(result);

                log.info("회원 가입 완료 email:{} type:{}", createUserDto.getEmail(), createUserDto.getType());
            }
            else {
                ResponseCreateUserDto result = userservice.createUser(createUserDto, profileImage);

                message.setStatus(200);
                message.setMessage("회원가입 완료");
                message.setData(result);

                log.info("회원 가입 완료 email:{} type:{}", createUserDto.getEmail(), createUserDto.getType());
            }


        } catch (IOException e) {
            String tmpMsg = e.toString();

            message.setMessage(tmpMsg);
            message.setStatus(400);
            message.setData(null);

            log.info("회원가입 실패 IOExecption: {}", e);

        } catch (Exception e) {
            System.out.println(createUserDto.getEmail());
            System.out.println(createUserDto.getFcmToken());
            String tmpMsg = e.toString();

            message.setMessage(tmpMsg);
            message.setStatus(400);
            message.setData(null);

            log.info("회원가입 실패 Exception: {}", e);
        }

        return new ResponseEntity<>(message, headers, HttpStatus.OK);


    }

    @ApiOperation(value = "checkSendEmail",
            notes = "이메일 중복 체크 후 인증번호 전송",
            httpMethod = "POST")
    @PostMapping("/email/duplicate")
    public ResponseEntity<Message> checkSendEmail(@RequestBody CheckSendEmailDto checkSendEmailDto) {

        String email = checkSendEmailDto.getEmail();

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));


        //ResponseCreateUserDto result = userservice.createUser(createUserDto);

        boolean isDuplicateEmail = userservice.emailCheck(email);



        if (isDuplicateEmail==false) {
            try{
                String code = emailService.sendSimpleMessage(email);

                log.info("인증번호 전송 email:{} code:{}", email, code);

                message.setStatus(200);
                message.setMessage("인증번호를 전송했습니다");
                message.setData(null);

                //return new ResponseEntity<>(message, headers, HttpStatus.OK);

            }
            catch (Exception e ) {
                log.info("인증번호 발송 실페: {}", e);
                message.setStatus(200);
                message.setMessage("인증번호 발송에 실패했습니다");
                message.setData(null);
            }


        }
        else if (isDuplicateEmail==true) {

            message.setStatus(400);
            message.setMessage("이미 사용중인 이메일입니다");
            message.setData(null);

            log.info("이미사용중인 이메일 입니다: {}", email);
            //return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @ApiOperation(value = "SendEmailForPasswordChange",
            notes = "임시비밀번호 발급 String email",
            httpMethod = "POST")
    @PostMapping("/email/issue/password")
    public ResponseEntity<Message> SendEmailForTempPassword(@RequestBody EmailDto emailDto) {
        String email = emailDto.getEmail();
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();

        boolean isDuplicateEmail = userservice.emailCheck(email);

        if (isDuplicateEmail==false) {

            message.setStatus(400);
            message.setMessage("존재하지 않는 계정입니다");
            message.setData(null);

            log.info("존재하지 않는 계정입니다: {}", email);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }

        int isSocial = userRepository.findByEmail(email).getType();
        if (isSocial > 0){
            message.setStatus(400);
            message.setMessage("소셜 로그인 계정입니다.");
            message.setData(null);

            log.info("임시 비밀번호 발급 성공: {}", email);
        }

        try{
            String code = emailService.sendTemporaryPassword(email);
            changePasswordDto.setPassword(code);
            passwordService.changePassword(changePasswordDto,email);

            message.setStatus(200);
            message.setMessage("임시비밀번호 발급 성공했습니다");
            message.setData(null);

            log.info("임시 비밀번호 발급 성공: {} {}", email,code);


        }
        catch (Exception e ) {
            message.setStatus(400);
            message.setMessage("임시비밀번호 발급에 실패했습니다");
            message.setData(null);
            log.error("임시비밀번호 발급 실패: {}", e);
        }


        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }



    @ApiOperation(value = "SendEmailForPasswordChange",
            notes = "비밀번호 변경 위해 인증번호 전송",
            httpMethod = "POST")
    @GetMapping("/email/auth")
    public ResponseEntity<Message> SendEmailForPasswordChange(Authentication authentication) {

        String email = authentication.getPrincipal().toString();

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));


        try{
            String code = emailService.sendSimpleMessage(email);

            log.info("인증번호 발송 성공 email:{} code:{}",email, code);

            message.setStatus(200);
            message.setMessage("인증번호를 전송했습니다");
            message.setData(null);

            //return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
        catch (Exception e ) {
            message.setStatus(400);
            message.setMessage("인증번호를 발송에 실패했습니다.");
            message.setData(null);

            log.info("{} {}", message.getMessage(), email);
        }


        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }


    @ApiOperation(value = "chagePassword",
            notes = "비밀번호 변경",
            httpMethod = "PUT")
    @PutMapping("/password/change")
    public ResponseEntity<Message> changePassword(@RequestBody ChangePasswordDto changePasswordDto, Authentication authentication) {


        String email = authentication.getPrincipal().toString();

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
                int result = passwordService.changePassword(changePasswordDto,email);

                if (result == 1) {

                    message.setStatus(200);
                    message.setMessage("비밀번호 변경이 완료되었습니다");
                    message.setData(null);

                    return new ResponseEntity<>(message, headers, HttpStatus.OK);
                }
                else if (result == 0){

                    message.setStatus(400);
                    message.setMessage("비밀번호 변경에 실패했습니다");
                    message.setData(null);

                    return new ResponseEntity<>(message, headers, HttpStatus.OK);
                }
                else {

                    message.setStatus(400);
                    message.setMessage("이전에 사용하던 비밀번호입니다.");
                    message.setData(null);

                    return new ResponseEntity<>(message, headers, HttpStatus.OK);
                }

        }
        catch (EntityNotFoundException | NullPointerException  e) {

            message.setStatus(400);
            message.setMessage(e.toString());
            message.setData(null);

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
        catch (Exception e) {
            message.setStatus(400);
            message.setMessage(e.toString());
            message.setData(null);

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }

    }



    @ApiOperation(value = "verifyEmail",
            notes = "이메일 인증번호 확인",
            httpMethod = "POST")
    @PostMapping("/email/auth/check")
    public ResponseEntity<Message> verifyEmail(@RequestBody VerifyEmailDto verifyEmailDto) {

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        String key = verifyEmailDto.getEmailToken();

        try {
            String code = emailService.verifyEmail(key);
            System.out.println(code);

            message.setStatus(200);
            message.setMessage("인증되었습니다");
            message.setData(null);

        }
        catch (ChangeSetPersister.NotFoundException e) {

            message.setStatus(400);
            message.setMessage("인증번호가 일치하지 않습니다");
            message.setData(null);

        }
        log.info("{}", message.getMessage());
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
        
    }



    @ApiOperation(value = "login",
            notes = "로그인",
            httpMethod = "POST")
    @PostMapping("/login") // 로그인
    public ResponseEntity<Message> login(@RequestBody LoginUserDto loginUserDto) {

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        Boolean isMatch = passwordService.isMatch(loginUserDto);

        if (isMatch) {
            ResponseCreateUserDto result = userservice.login(loginUserDto);
            message.setStatus(200);
            message.setMessage("로그인 성공");
            message.setData(result);

        }
        else {
            message.setStatus(400);
            message.setMessage("계정 정보가 일치하지 않습니다");
            message.setData(null);
        }

        log.info("{} {}", loginUserDto.getEmail(), message.getMessage());
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }


    @ApiOperation(value = "socialLogin",
            notes = "소셜로그인",
            httpMethod = "POST")
    @PostMapping("/login/social") // 로그인
    public ResponseEntity<Message> socialLogin(@RequestBody SocialLoginUserDto socialLoginUserDto) {

        ResponseCreateUserDto result = userservice.socialLogin(socialLoginUserDto);

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        if (result == null) {
            message.setStatus(700);
            message.setMessage("계정 정보가 일치하지 않습니다");
            message.setData(result);

        }
        else {
            message.setStatus(200);
            message.setMessage("로그인 성공");
            message.setData(result);
        }
        log.info("{} 소셜{}", socialLoginUserDto.getEmail(), message.getMessage());
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @ApiOperation(value = "refresh",
            notes = "리프레시토큰발급",
            httpMethod = "POST")
    @PostMapping("/refresh") // 리프레시하기
    public ResponseEntity<Message> refresh(@RequestBody ValidateTokenDto validateTokenDto){

        ResponseValidateTokenDto result = userservice.refresh(validateTokenDto);

        String accessToken = result.getAccessToken();
        String refreshToken = result.getRefreshToken();

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        if (accessToken==null&&refreshToken==null) {

            message.setStatus(400);
            message.setMessage("다시 로그인해주세요");
            message.setData(null);

        }else{

            message.setStatus(200);
            message.setMessage("리프레시 성공");
            message.setData(result);

        }

        log.info("{}", message.getMessage());
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

//    @ApiOperation(value = "refresh",
//            notes = "리프레시토큰발급",
//            httpMethod = "GET")
//    @GetMapping("/refresh") // 리프레시하기
//    public ResponseEntity<Message> refresh(HttpServletRequest request){// 필터에서 안걸러지면 유효기간 남아있는거임
//        if (refreshFlag){
//            refreshFlag = false;
//            String token = request.getHeader(HttpHeaders.AUTHORIZATION).split(" ")[1];
//
//            ResponseToken result = userservice.refresh(token);
//
//            Message message = new Message();
//            HttpHeaders headers= new HttpHeaders();
//            headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
//
//            message.setStatus(200);
//            message.setMessage("리프레시 성공");
//            message.setData(result);
//
//            return new ResponseEntity<>(message, headers, HttpStatus.OK);
//        }else{
//
//            Message message = new Message();
//            HttpHeaders headers= new HttpHeaders();
//            headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
//
//            message.setStatus(400);
//            message.setMessage("리프레시 토큰이 만료되지 않았습니다.");
//            message.setData(null);
//
//            return new ResponseEntity<>(message, headers, HttpStatus.OK);
//        }
//    }

    @Scheduled(cron = "0/5 * * * * *")
    public void Flag_reset () {
        refreshFlag = true;
    }


//    @ApiOperation(value = "emailCheck",
//            notes = "이메일 중복 확인",
//            httpMethod = "POST")
//    @PostMapping("/email-check")
//    public ResponseEntity<Message> emailCheck(@RequestBody Map<String, String> emailMap) {
//
//        String email = emailMap.get("email");
//
//        System.out.println(email);
//
//        boolean result = userservice.emailCheck(email);
//
//        Message message = new Message();
//        HttpHeaders headers= new HttpHeaders();
//        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
//
//
//        if (result==false) {
//
//            message.setStatus(200);
//            message.setMessage("이메일 사용 가능");
//            message.setData(true);
//
//            return new ResponseEntity<>(message, headers, HttpStatus.OK);
//        }
//        else if (result==true) {
//
//            message.setStatus(400);
//            message.setMessage("이메일 중복");
//            message.setData(false);
//
//            return new ResponseEntity<>(message, headers, HttpStatus.OK);
//        }
//
//        return null;
//    }


    @ApiOperation(value = "nickNameCheckAtInfo",
            notes = "회원정보 수정시 닉네임 중복 확인",
            httpMethod = "POST")
    @PostMapping("/nickname/duplicate/auth")
    public ResponseEntity<Message> nickNameCheckAtInfo(@RequestBody NickNameCheckDto nickNameCheckDto, Authentication authentication) {

        String email = authentication.getPrincipal().toString();

        String nickName = nickNameCheckDto.getNickname();


        boolean result = userservice.nickNameCheckAtInfo(nickName, email);

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        if (nickName == null || nickName.length() <2 || nickName.length() > 8) {
            message.setStatus(400);
            message.setMessage("닉네임은 2 ~ 8자리 입력해 주세요.");
            log.info("{} {}", message.getMessage(),nickName);

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }

        if (result==false) {

            message.setStatus(200);
            message.setMessage("사용 가능한 닉네임입니다");
            message.setData(null);
        }
        else if (result==true) {

            message.setStatus(400);
            message.setMessage("이미 사용중인 닉네임입니다");
            message.setData(null);
        }
        log.info("{} {}", nickName, message.getMessage());
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @ApiOperation(value = "nickNameCheck",
            notes = "닉네임 중복 확인",
            httpMethod = "POST")
    @PostMapping("/nickname/duplicate")
    public ResponseEntity<Message> nickNameCheck(@RequestBody NickNameCheckDto nickNameCheckDto) {

        String nickName = nickNameCheckDto.getNickname();

        System.out.println(nickName);

        boolean result = userservice.nickNameCheck(nickName);

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        if (nickName == null || nickName.length() <2 || nickName.length() > 8) {
            message.setStatus(400);
            message.setMessage("닉네임은 2 ~ 8자리 입력해 주세요.");
            log.info("{} {}", message.getMessage(),nickName);

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }

        if (result==false) {

            message.setStatus(200);
            message.setMessage("사용 가능한 닉네임입니다");
            message.setData(null);
        }
        else if (result==true) {

            message.setStatus(400);
            message.setMessage("이미 사용중인 닉네임입니다");
            message.setData(null);
        }

        log.info("{} {}",nickName, message.getMessage());
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }


    @ApiOperation(value = "logout",
            notes = "로그아웃",
            httpMethod = "POST")
    @PostMapping("/logout") // 로그아웃
    public ResponseEntity<Message> logout(Authentication authentication) {

        String userEmail = authentication.getPrincipal().toString();

        //UserDto us= (UserDto)authentication.getPrincipal();


        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {

            UserTypeDto userTypeDto = userservice.logout(userEmail);

            message.setStatus(200);
            message.setMessage("로그아웃 되었습니다.");
            message.setData(userTypeDto);

            log.info("로그아웃 성공 email:{}", userEmail);

            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
        catch (EntityNotFoundException e) {

            message.setStatus(400);
            message.setMessage("로그아웃에 실패했습니다.");
            message.setData(null);

            log.error("로그아웃 실패:{}", e);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }


    }

    @ApiOperation(value = "withdrawal",
            notes = "회원탈퇴",
            httpMethod = "DELETE")
    @DeleteMapping("/withdrawal") // 로그아웃
    public ResponseEntity<Message> withdrawal(Authentication authentication) {

        String userEmail = authentication.getPrincipal().toString();

        //UserDto us= (UserDto)authentication.getPrincipal();


        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            EmailDto result = userservice.withdrawal(userEmail);
            message.setStatus(200);
            message.setMessage("회원 탈퇴되었습니다");
            message.setData(result);

            log.info("회원 탈퇴 성공 email:{}", userEmail);

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
        catch (EntityNotFoundException e) {

            message.setStatus(400);
            message.setMessage("회원 탈퇴에 실패했습니다.");
            message.setData(null);

            log.info("회원탈퇴 실패 email:{}", userEmail);

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }


    }


    @ApiOperation(value = "updateExperience",
            notes = "경험치 업데이트",
            httpMethod = "POST")
    @PostMapping("/exp")
    public ResponseEntity<Message> updateExperience(@RequestBody UpdateExperienceDto updateExperienceDto, Authentication authentication) {

        String email = authentication.getPrincipal().toString();


        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            ResponseUpdateExperienceDto result = experienceService.updateExperience(updateExperienceDto,email);
            message.setStatus(200);
            message.setMessage("경험치 업데이트 성공");
            message.setData(result);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
        catch (Exception e) {
            message.setStatus(400);
            message.setMessage("경험치 업데이트 실패");
            message.setData(null);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }


    }

    @ApiOperation(value = "getExperience",
            notes = "경험치 조회",
            httpMethod = "GET")
    @GetMapping("/exp")
    public ResponseEntity<Message> getExperience(Authentication authentication) {

        String email = authentication.getPrincipal().toString();


        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            ResponseGetExperienceDto result = experienceService.getExperience(email);
            message.setStatus(200);
            message.setMessage("경험치 조회 성공");
            message.setData(result);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
        catch (Exception e) {
            message.setStatus(400);
            message.setMessage("경험치 조회 실패");
            message.setData(null);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }


    }



    @ApiOperation(value = "chageUserInfo",
            notes = "회원정보수정",
            httpMethod = "POST")
    @PostMapping("/info/change")
    public ResponseEntity<Message> updateUserInfo(@RequestPart(value="nickname",required = false) String nickname, @RequestPart(value="profileImage",required = false) MultipartFile profileImage, Authentication authentication) {


        String userEmail = authentication.getPrincipal().toString();

        //UserDto us= (UserDto)authentication.getPrincipal();


        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            if (profileImage==null) {

                ResponseUpdateUserInfoDto result = userservice.updateUserInfo(nickname, null, userEmail);

                message.setStatus(200);
                message.setMessage("회원정보가 수정되었습니다.");
                message.setData(result);

                return new ResponseEntity<>(message, headers, HttpStatus.OK);
            }
            else {
                ResponseUpdateUserInfoDto result = userservice.updateUserInfo(nickname, profileImage, userEmail);

                message.setStatus(200);
                message.setMessage("회원정보가 수정되었습니다.");
                message.setData(result);

                return new ResponseEntity<>(message, headers, HttpStatus.OK);
            }

        }
        catch (EntityNotFoundException | NullPointerException | IOException e) {

            message.setStatus(400);
            message.setMessage("회원정보 수정에  실패했습니다.");
            message.setData(null);

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
        catch (Exception e) {
            message.setStatus(400);
            message.setMessage("회원정보 수정에 실패했습니다.");
            message.setData(null);

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }




    }



    @ApiOperation(value = "getLikeRecipe",
            notes = "즐겨찾기 레시피 조회",
            httpMethod = "GET")
    @GetMapping("/like/list")
    public ResponseEntity<Message> getLikeRecipe(Authentication authentication) {

        String email = authentication.getPrincipal().toString();


        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            List<RecipesDto> result = recipeService.getLikeRecipe(email);
            message.setStatus(200);
            message.setMessage("즐겨찾기 레시피 조회 성공");
            message.setData(result);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
        catch (Exception e) {
            message.setStatus(400);
            message.setMessage("즐겨찾기 레시피 조회 실패");
            message.setData(null);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }


    }


    @ApiOperation(value = "getMyReview" ,notes = "레시피정보와 레시피별 리뷰 갯수를 조회합니다.", httpMethod = "GET")
    @GetMapping("/review/recipe")
    public ResponseEntity<Message> getMyReview(Authentication authentication) {

        String email = authentication.getPrincipal().toString();


        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            List<MyReivewDto> result = reviewService.getMyReviewList(email);
            message.setStatus(200);
            message.setMessage("레시피정보, 리뷰갯수 조회 성공");
            message.setData(result);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
        catch (Exception e) {
            message.setStatus(400);
            message.setMessage("레시피정보, 리뷰갯수 조회 실패");
            message.setData(null);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
    }

    @ApiOperation(value = "getMyHistory" ,notes = "해당레시피의 자신의 리뷰를 조회합니다.", httpMethod = "POST")
    @PostMapping("/review/history")
    public ResponseEntity<Message> getMyHistory(@RequestBody RecipeIdDto recipeIdDto, Authentication authentication) {

        String email = authentication.getPrincipal().toString();
        Long id = recipeIdDto.getRecipeId();


        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            List<MyHistoryDto> result = reviewService.getMyHistory(email, id);
            message.setStatus(200);
            message.setMessage("리뷰 조회 성공");
            log.info("리뷰 조회 성공 {} recipe{}", email, id);
            message.setData(result);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
        catch (Exception e) {
            message.setStatus(400);
            message.setMessage("리뷰 조회 실패");
            log.info("리뷰 조회 실패 {} recipe{}", email, id);
            message.setData(null);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
    }

    @ApiOperation(value = "getMyReview" ,notes = "리뷰id로 리뷰상세조회: 리뷰이미지와 컨텐츠를 조회 합니다.", httpMethod = "GET")
    @GetMapping("/review/{id}")
    public ResponseEntity<Message> getReview(@PathVariable("id") Long id, Authentication authentication) {

        String email = authentication.getPrincipal().toString();


        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            MyReviewDetailDto result = reviewService.getReview(email, id);
            message.setStatus(200);
            message.setMessage("리뷰 상세 조회 성공");
            log.info("리뷰 상세 조회 {} reviewId{}", email, id);
            message.setData(result);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
        catch (Exception e) {
            message.setStatus(400);
            message.setMessage("리뷰 상세 조회 실패");
            log.info("리뷰 조회 실패 {} reviewId{}", email, id);
            message.setData(null);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
    }

    @ApiOperation(value = "updateReview" ,notes = "리뷰를 업데이트 합니당.", httpMethod = "PUT")
    @PutMapping("/review")
    public ResponseEntity<Message> updateReview(@RequestBody UpdateReviewDto updateReviewDto, Authentication authentication) {

        String email = authentication.getPrincipal().toString();
        Long id = updateReviewDto.getReviewId();

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            reviewService.updateReview(updateReviewDto, email);

            message.setStatus(200);
            message.setMessage("리뷰 업데이트 성공");
            log.info("리뷰 업데이트 성공 {} {}", email, id);
            message.setData(null);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
        catch (Exception e) {
            message.setStatus(400);
            message.setMessage("리뷰 업데이트 실패");
            message.setData(null);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
    }

    @ApiOperation(value = "deleteReview" ,notes = "리뷰를  삭제 합니다.", httpMethod = "DELETE")
    @DeleteMapping("/review/{id}")
    public ResponseEntity<Message> deleteReview(@PathVariable("id") Long id, Authentication authentication) {

        String email = authentication.getPrincipal().toString();

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        try {
            reviewService.deleteReview(id, email);

            message.setStatus(200);
            message.setMessage("리뷰 삭제 성공");
            log.info("리뷰 삭제 성공 {} {}", email, id);
            message.setData(null);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
        catch (Exception e) {
            message.setStatus(400);
            message.setMessage("리뷰 삭제 조회 실패");
            message.setData(null);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
    }

    @ApiOperation(value = "makeReview", notes = "리뷰생성 reviewImage의 CONTENT TYPE multipart/form-data, 나머지는 application/json #415원인#", httpMethod = "POST")
    @PostMapping(value="/review", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Message> makeReview(@RequestPart(value="recipeId") String id,  @RequestPart(value="reviewImage") MultipartFile reviewImage, @RequestPart(value = "content",required = false) String content, Authentication authentication) {
        String email = authentication.getPrincipal().toString();

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        CreateReviewDto createReviewDto = new CreateReviewDto();
        createReviewDto.setEmail(email);
        createReviewDto.setRecipeId(Long.parseLong(id));
        createReviewDto.setContent(content);

        try {
            ReviewIdDto result = reviewService.makeReview(createReviewDto,reviewImage);

            message.setStatus(200);
            message.setMessage("리뷰 생성 성공");
            message.setData(result);
            log.info("리뷰 생성 성공 {} reviewId:{}", email, result.getReviewId());
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
        catch (Exception e) {
            message.setStatus(400);
            message.setMessage("리뷰 생성 실패");
            message.setData(null);
            log.info("리뷰 생성 실패 {} {}", email, e);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
    }

}
