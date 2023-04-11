package com.example.yobee.recipe.controller;



import com.example.yobee.recipe.domain.Recipe;
import com.example.yobee.recipe.dto.*;
import com.example.yobee.recipe.repository.RecipeRepository;
import com.example.yobee.recipe.service.DataService;
import com.example.yobee.recipe.service.RecipeService;
import com.example.yobee.response.Message;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class RecipeController {
    private final RecipeService recipeService;
    private final DataService dataService;

    private final RecipeRepository recipeRepository;

    @ApiOperation(value = "sortedRecipeList", notes = "레시피목록", httpMethod = "POST")
    @PostMapping("/recipe/list")
    public ResponseEntity<Message> sortedRecipeList(@RequestBody SortDto sortDto, Authentication authentication) {
        HeaderDto headerDto = new HeaderDto((String)authentication.getPrincipal());
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        try {
            List<RecipesDto> recipesDtoList = recipeService.sortedRecipe(sortDto, headerDto);
            message.setStatus(200);
            message.setMessage("레시피 목록 조회 성공");
            message.setData(recipesDtoList);
            log.info("레시피 목록 조회 성공 {}", (String)authentication.getPrincipal());

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("레시피 검색  실패:{}",e);
            message.setStatus(400);
            message.setMessage("레시피 검색 실패");
            message.setData(null);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "singleRecipe", notes = "레시피 소개", httpMethod = "GET")
    @GetMapping("/recipe/{id}/intro")
    public ResponseEntity<Message> singleRecipe(@PathVariable("id") Long id, Authentication authentication) {
        HeaderDto headerDto = new HeaderDto((String)authentication.getPrincipal());
        RecipeDto recipeDto = recipeService.findById(id, headerDto);

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        message.setStatus(200);
        message.setMessage("레시피 조회 성공");
        message.setData(recipeDto);
        log.info("레시피 조회 성공 {}", (String)authentication.getPrincipal());
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
   }
    @ApiOperation(value = "sendIngredient", notes = "레시피 재료", httpMethod = "GET")
    @GetMapping("/recipe/{id}/ingredient")
   public ResponseEntity<Message> sendIngredient(@PathVariable("id") Long id, Authentication authentication){
       HeaderDto headerDto = new HeaderDto((String)authentication.getPrincipal());
       IngredientDto ingredientDto = recipeService.ingredientByRecipeId(id);

       Message message = new Message();
       HttpHeaders headers= new HttpHeaders();
       message.setStatus(200);
       message.setMessage("레시피 재료 조회 성공");
       message.setData(ingredientDto);
        log.info("레시피 재료 조회 성공 {}", (String)authentication.getPrincipal());
       return new ResponseEntity<>(message, headers, HttpStatus.OK);
   }

   @ApiOperation(value = "recipeLike", notes = "레시피 좋아요", httpMethod = "GET")
   @GetMapping("/recipe/{id}/like")
   public ResponseEntity<Message> recipeLike(@PathVariable("id") Long id, Authentication authentication) {
       HeaderDto headerDto = new HeaderDto((String)authentication.getPrincipal());
       recipeService.recipeLike(id, headerDto);
       Message message = new Message();
       HttpHeaders headers= new HttpHeaders();
       message.setStatus(200);
       message.setMessage("레시피 좋아요 변경 성공");
       log.info("레시피 좋아요변경 {}", (String)authentication.getPrincipal());
       return new ResponseEntity<>(message, headers, HttpStatus.OK);
   }

    @ApiOperation(value = "searchRecipe", notes = "키워드로 헤쉬태그를 기준으로 요리검색", httpMethod = "POST")
    @PostMapping("/recipe/search")
    public ResponseEntity<Message> searchRecipe(@RequestBody SearchDto searchDto, Authentication authentication) {
        HeaderDto headerDto = new HeaderDto((String)authentication.getPrincipal());
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        try{
            List<RecipesDto> recipesDto = recipeService.recipeSearch(searchDto, headerDto);
            message.setStatus(200);
            message.setMessage("레시피 검색 성공");
            message.setData(recipesDto);

            log.info("레시피검색:{}", searchDto.getKeyword());

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("레시피검색 실패:{}",e);
            message.setStatus(400);
            message.setMessage("레시피 검색 실패");
            message.setData(null);

        return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
    }

    @ApiOperation(value = "recommend", notes = "랜덤으로 레시피추천", httpMethod = "GET")
    @GetMapping("/recipe/recommend")
    public ResponseEntity<Message> recommend(Authentication authentication) {
        HeaderDto headerDto = new HeaderDto((String)authentication.getPrincipal());
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        try {
            List<RecommendRecipeDto> recommendRecipeDtoList = recipeService.recommend();
            message.setStatus(200);
            message.setMessage("레시피 추천 테스트 목록 조회 성공");
            message.setData(recommendRecipeDtoList);
            log.info("레시피 추천 테스트 목록 조회");
            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("레시피 추천 테스트 실패:{}",e);
            message.setStatus(400);
            message.setMessage("레시피 검색 실패");
            message.setData(null);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
    }
    @ApiOperation(value = "recipeStep", notes = "레시피 단계별 정보 제공", httpMethod = "GET")
    @GetMapping("/recipe/{id}/")
    public ResponseEntity<Message> recipeStep(@PathVariable("id") Long id, Authentication authentication) {
        HeaderDto headerDto = new HeaderDto((String)authentication.getPrincipal());
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();

        try {
            List<StepDto> stepDtoList = recipeService.recipeStep(id);
            message.setStatus(200);
            message.setMessage("레시피 과정 조회 성공");
            message.setData(stepDtoList);
            log.info("레시피 과정 조회 email:{} 레시피id:{}",authentication.getPrincipal().toString(), id.toString());

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("레시피 과정 조회 실패:{}", e);
            message.setStatus(400);
            message.setMessage("레시피 과정 조회 실패");
            message.setData(null);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
    }

    @PostMapping("/recipe/crawler/foodsafetykorea")
    public void getRecipeDate(@RequestBody WebclientDto webclientDto, Authentication authentication) throws IOException {
        recipeService.getRecipeData(webclientDto);
    }

    @PostMapping("/recipe/getData2")
    public void getRecipeData2(@RequestBody Recipe10000Dto recipe10000Dto) {
        dataService.getRecipeFrom10000(recipe10000Dto);
    }

    @ApiOperation(value = "sendIngredient", notes = "학습용 레시피 재료", httpMethod = "GET")
    @GetMapping("AI/recipe/{id}/ingredient")
    public ResponseEntity<Message> sendIngredientMark2(@PathVariable("id") Long id, Authentication authentication){
        HeaderDto headerDto = new HeaderDto((String)authentication.getPrincipal());
        Map<String,String> result = recipeService.ingredientByRecipeIdForAI(id);

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        message.setStatus(200);
        message.setMessage("레시피 재료 조회 성공");
        message.setData(result);
        log.info("AI 레시피 재료 조회 성공 {}", (String)authentication.getPrincipal());
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }


    @GetMapping("/recipe/remove/id/{id}")
    public void deleteRecipe(@PathVariable long id) {
        recipeService.deleteRecipeById(id);
    }
    @GetMapping("/recipe/remove/name/{name}")
    public void deleteRecipe(@PathVariable String name) {
        recipeService.deleteRecipeByName(name);
    }

    @PutMapping("/recipe/update/hashtag")
    public void updateHashtag() {
        recipeService.updateHashtag();
    }


    @ApiOperation(value = "searchRecipe v2", notes = "페이지네이션 키워드로 헤쉬태그를 기준으로 요리검색", httpMethod = "POST")
    @PostMapping("/recipe/search/v2")
    public ResponseEntity<Message> searchRecipePage(@RequestBody PageSearchDto pageSearchDto, Authentication authentication) {
        HeaderDto headerDto = new HeaderDto((String)authentication.getPrincipal());
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        try{
            List<RecipesDto> recipesDto = recipeService.pagenationRecipeSearch(pageSearchDto, headerDto);
            message.setStatus(200);
            message.setMessage("레시피 검색 성공");
            message.setData(recipesDto);

            log.info("레시피검색:{}", pageSearchDto.getKeyword());

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("레시피검색 실패:{}",pageSearchDto.getKeyword());
            message.setStatus(400);
            message.setMessage("레시피 검색 실패");
            message.setData(null);

            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        }
    }

    @ApiOperation(value = "sortedRecipeList v2", notes = "페이지네이션 레시피목록", httpMethod = "POST")
    @PostMapping("/recipe/list/v2")
    public ResponseEntity<Message> sortedRecipeListPage(@RequestBody PageSortDto pageSortDto, Authentication authentication) {
        HeaderDto headerDto = new HeaderDto((String)authentication.getPrincipal());
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        try {
            List<RecipesDto> recipesDtoList = recipeService.pagenationSortedRecipe(pageSortDto, headerDto);
            message.setStatus(200);
            message.setMessage("레시피 목록 조회 성공");
            message.setData(recipesDtoList);
            log.info("레시피 목록 조회 성공 {}", (String)authentication.getPrincipal());

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("레시피 검색 실패:{}",e);
            message.setStatus(400);
            message.setMessage("레시피 검색 실패");
            message.setData(null);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
    }

}
