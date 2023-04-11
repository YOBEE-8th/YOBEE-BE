package com.example.yobee.push.controller;


import com.example.yobee.push.service.FirebaseCloudMessageService;
import com.example.yobee.recipe.dto.StepDto;
import com.example.yobee.response.Message;
import com.example.yobee.user.domain.User;
import com.example.yobee.user.dto.UserDto;
import com.example.yobee.user.repository.UserRepository;
import com.example.yobee.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@EnableScheduling
@Api(value = "TokenController")
@SwaggerDefinition(tags = {@Tag(name = "TokenController",
        description = "FCM 토큰 컨트롤러")})
@RequestMapping(value = "/api/v1")
@RestController
@Configuration
@CrossOrigin("*")
public class TokenController {
	
	private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    FirebaseCloudMessageService service;

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @ApiOperation(value = "registToken", notes = "토큰을 받는 Method", httpMethod = "POST")
    @PostMapping("/token")
    public String registToken(String token) {
    	logger.info("registToken : token:{}", token);
        service.addToken(token);
        return "'"+token+"'" ;
    }

    @ApiOperation(value = "broadCast", notes = "전체 메세지를 전송하는 Method", httpMethod = "POST")
    @PostMapping("/broadcast")
    public Integer broadCast(String title, String body) throws IOException {
    	logger.info("broadCast : title:{}, body:{}", title, body);
    	return service.broadCastMessage(title, body, null);
    }

//    // test용
//    @PostMapping("/sendMessageTo")
//    public void sendMessageTo(String token, String title, String body) throws IOException {
//    	logger.info("sendMessageTo : token:{}, title:{}, body:{}", token, title, body);
//        service.sendMessageTo(token, title, body);
//    }
//    // test용
//    @GetMapping("/push/{hash}")
//    public ResponseEntity<List<GifticonDto>> sendMessagePerodic(@PathVariable int hash){
//        //List<User> U_list = userService.getAllUser();
//        //List<Gifticon> G_list;
//
//        Optional<User> user = userRepository.findById(hash);
//        User nuser = user.get();
//        int Dday = nuser.getNday();
//        return ResponseEntity.ok(gifticonService.getPushGifticon(hash, Dday));
//    }
//
    @Scheduled(cron = "0 0 18 * * ?")
    @ApiOperation(value = "pushmessage", notes = "fcm 알람", httpMethod = "GET" )
    @GetMapping("/pushmessage")
    public void pushmessage() throws IOException{

        List<User> U_list = userRepository.findAll();
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();


        try {
            for (User user : U_list) {
                if (user.getFcmToken()== null){
                    continue;
                }

                String nickname = user.getNickName();

                String fcmToken = user.getFcmToken();

                Map<String, String> data = new HashMap<String, String>();
                data.put("recommend", "recommend");
                service.sendMessageTo(fcmToken, nickname + "님 식사시간이에요🍳", "오늘의 메뉴를 골라볼까요?",  data);
            }
            message.setStatus(200);
            message.setMessage("fcm에 발송에 성공했습니다.");
            log.info(message.getMessage());

        } catch (Exception e){
            message.setStatus(400);
            message.setMessage("fcm 발송에 실패했습니다.");
            message.setData(e);
            log.error(message.getMessage());
        }

    }
}

