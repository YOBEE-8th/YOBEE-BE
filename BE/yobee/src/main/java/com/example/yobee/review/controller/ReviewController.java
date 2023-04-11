package com.example.yobee.review.controller;

import com.example.yobee.recipe.dto.HeaderDto;
import com.example.yobee.recipe.dto.MyHistoryDto;
import com.example.yobee.recipe.dto.MyReivewDto;
import com.example.yobee.response.Message;
import com.example.yobee.review.domain.Review;
import com.example.yobee.review.dto.*;
import com.example.yobee.review.service.ReviewService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class ReviewController {
    private final ReviewService reviewService;
    @ApiOperation(value = "getRecipeReview", notes = "레시피에 대한 리뷰리스트를 출력", httpMethod = "GET")
    @GetMapping("/recipe/{id}/review")
    public ResponseEntity<Message> getRecipeReview(@PathVariable("id") Long id, Authentication authentication) {
        HeaderDto headerDto = new HeaderDto((String)authentication.getPrincipal());
        List<RecipeReviewDto> recipeReviewDtos = reviewService.recipeReview(id, headerDto);
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();

        message.setStatus(200);
        message.setMessage("리뷰 리스트 조회 성공");
        message.setData(recipeReviewDtos);

        log.info("{} {}", headerDto.getUserName(), message.getMessage());
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @ApiOperation(value = "getReviewLike", notes = "리뷰 좋아요 변경", httpMethod = "PUT")
    @PutMapping("/review/{id}/like")
    public ResponseEntity<Message> getReviewLike(@PathVariable("id") Long id, Authentication authentication) {
        HeaderDto headerDto = new HeaderDto((String)authentication.getPrincipal());
        reviewService.reviewLike(id, headerDto);

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        message.setStatus(200);
        message.setMessage("리뷰 좋아요 변경 성공");

        log.info("{} {}", headerDto.getUserName(), message.getMessage());
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }



}
