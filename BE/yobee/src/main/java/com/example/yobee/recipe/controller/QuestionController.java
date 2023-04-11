package com.example.yobee.recipe.controller;

import com.example.yobee.recipe.dto.HeaderDto;
import com.example.yobee.recipe.dto.QuestionDto;
import com.example.yobee.recipe.service.QuestionService;
import com.example.yobee.response.Message;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class QuestionController {
    private final QuestionService questionService;


    @ApiOperation(value = "question", notes = "질문 저장", httpMethod = "POST")
    @PostMapping("/recipe/unresolved")
    public ResponseEntity<Message> sendIngredientMark2(@RequestBody QuestionDto questionDto){
//        HeaderDto headerDto = new HeaderDto((String)authentication.getPrincipal());
        questionService.saveQuestion(questionDto.getRecipeId(), questionDto.getContent());

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        message.setStatus(200);
        message.setMessage("질문 저장 성공");
        message.setData(null);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }
}
