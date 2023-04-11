package com.example.yobee.recipe.service;

import com.example.yobee.recipe.domain.*;

import com.example.yobee.recipe.dto.*;
import com.example.yobee.recipe.repository.*;
import com.example.yobee.review.repository.ReviewRepository;
import com.example.yobee.user.domain.User;
import com.example.yobee.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

import static java.lang.Math.min;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final IngredientRepository ingredientRepository;
    private final HashTagRepository hashTagRepository;
    private final ReviewRepository reviewRepository;
    @Value("${foodsafetykorea.apikey}")
    private String foodsafetykoreaApiKey;

    public List<RecipesDto> sortedRecipe(SortDto sortDto, HeaderDto headerDto) {
        Boolean isAI = sortDto.getIsAI();
        List<Recipe> recipes= recipeRepository.sortRecipe(sortDto);
        List<RecipesDto> recipesDtoList = new ArrayList<>();

        for (Recipe recipe : recipes) {
            RecipesDto recipesDto = new RecipesDto(recipe, headerDto);
            recipesDtoList.add(recipesDto);
        }

        return recipesDtoList;
    }

    public RecipeDto findById(Long id, HeaderDto headerDto) {
        Optional<Recipe> optionalRecipe = Optional.ofNullable(recipeRepository.findById(id));
        if (optionalRecipe.isEmpty()){
            throw new EntityNotFoundException("recipe not present in the database");
        }

        Recipe recipe = optionalRecipe.get();

        User user = userRepository.findByEmail(headerDto.getUserName());
        boolean isLike = recipeLikeRepository.existsByUserAndRecipe(user,recipe);
        Long reviwCnt = reviewRepository.countByrecipeAndContentNotNull(recipe);
        return new RecipeDto(recipe, isLike, reviwCnt);
    }

    public IngredientDto ingredientByRecipeId(Long id){
        return new IngredientDto(recipeRepository.findById(id));
    }

    public Map<String, String> ingredientByRecipeIdForAI(Long id){
        Map<String, String> result = new HashMap<String, String>();
        for(Ingredient ingredient: recipeRepository.findById(id).getIngredientList()){
            result.put(ingredient.getIngredientName(), ingredient.getWeight());
        }
        return result;
    }

    @Transactional
    public void recipeLike(Long id, HeaderDto headerDto) {
        Recipe recipe = recipeRepository.findById(id);
        User user = userRepository.findByEmail(headerDto.getUserName());
        Boolean likeBoolean = recipeLikeRepository.existsByUserAndRecipe(user, recipe);

        if (likeBoolean){
            recipeRepository.likeDecrease(id);
            RecipeLike recipeLike = recipeLikeRepository.findByUserAndRecipe(user, recipe);
            Long tmpId = recipeLike.getRecipeLikeId();
            recipeLikeRepository.delete(recipeLikeRepository.findById(tmpId).get());
        }else{
            recipeRepository.likeIncrease(id);
            RecipeLike recipeLike = new RecipeLike();
            recipeLike.setRecipe(recipe);
            recipeLike.setUser(user);
            recipeLikeRepository.save(recipeLike);
        }
    }
    public List<RecipesDto> recipeSearch(SearchDto searchDto, HeaderDto headerDto) {
        String keyword = searchDto.getKeyword();
        SortDto sortDto = new SortDto(searchDto);

        List<RecipesDto> responseRecipesDtoList = new ArrayList<>();
        for(Recipe recipe: recipeRepository.searchRecipe(sortDto, keyword)) {
            responseRecipesDtoList.add(new RecipesDto(recipe, headerDto));
        }
        return responseRecipesDtoList;
    }

    public List<RecommendRecipeDto> recommend() {
        List<Recipe> recipeList = recipeRepository.findAll();
        Collections.shuffle(recipeList);
        List<RecommendRecipeDto> recommendRecipeDtoList = new ArrayList<>();
        for(int i = 0; i < min(16,recipeRepository.findAll().size()); i ++) {
            RecommendRecipeDto recommendRecipeDto = new RecommendRecipeDto(recipeList.get(i));
            recommendRecipeDtoList.add(recommendRecipeDto);
        }
        return recommendRecipeDtoList;
    }

    public List<StepDto> recipeStep(Long id) {
        Recipe recipe = recipeRepository.findById(id);
        List<StepDto> stepDtoList = new ArrayList<>();
        for(RecipeStep recipeStep : recipeStepRepository.findByRecipeOrderByRecipeStepIdAsc(recipe)){
            stepDtoList.add(new StepDto(recipeStep));
        }
        return stepDtoList;
    }

    @Transactional
    public void getRecipeData(WebclientDto webclientDto) throws IOException{

//        String CHNG_DT = "20220101";
//        String apiUrl = "https://openapi.foodsafetykorea.go.kr/api/" + foodsafetykoreaApiKey + "/COOKRCP01/json/1/10/CHNG_DT=" + CHNG_DT + "/";

        String str = "1";
        String end = "1000";
//        String end = "1114";
        String apiUrl = "https://openapi.foodsafetykorea.go.kr/api/" + foodsafetykoreaApiKey + "/COOKRCP01/json/" + str + "/" + end;

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, null, Map.class);
        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
        List<Map<String, String>> rowList = (List<Map<String, String>>) ((Map<String, Object>) body.get("COOKRCP01")).get("row");

        int cnt = 0;
        for (Map<String, String> row : rowList) {
//            if (cnt == 50) break;
//            System.out.println(row.get("MANUAL_IMG01"));
            String imageUrl = row.get("MANUAL_IMG01");

            if (imageUrl.length() == 0) {
                continue;
            }

            URL url = new URL(imageUrl);
            BufferedImage image = ImageIO.read(url);
            int width = image.getWidth();
            int height = image.getHeight();

            if (width < 700 && height < 700) {
                continue;
            } else if (recipeRepository.findByName(row.get("RCP_NM")).isPresent()) {
                continue;
            }

            // 레시피 저장
            Recipe recipe = new Recipe();
//            if (row.get("RCP_PAT2").equals("반찬")) recipe.setCategory("반찬");
            if (row.get("RCP_PAT2").equals("국&찌개")) recipe.setCategory("국/찌개");
            else if (row.get("RCP_PAT2").equals("후식")) recipe.setCategory("디저트");
            else recipe.setCategory(row.get("RCP_PAT2"));
            recipe.setDifficulty(2);
            recipe.setAi(false);
            recipe.setRecipeLikeCnt(0);
            recipe.setRecipeTitle(row.get("RCP_NM"));
            recipe.setResultImage(row.get("ATT_FILE_NO_MAIN"));
            recipe.setServings("1");
            recipe.setTime("60");
            recipeRepository.save(recipe);

            System.out.println(new String(row.get("RCP_SEQ").getBytes("UTF-8"), "UTF-8"));
            cnt++;

            // 재료 저장
            // 재료 문자열 케이스 정리
            // 바지락 30g, 팽이버섯 10g, 홍고추 3g, 쑥갓 1g, 청양고추 3g, 무 8g, 대파 5g, 다시마 5g
            // - 주재료 : 민어 50g, 쇠고기 양지15g, 다진 마늘 1.25g, 후추 0.25g, 참기름 0.5g, 무 40g, 애호박 20g, 애느타리버섯 20g, 풋 고추 5g, 홍고추 2.5g, 쑥갓 10g, 대파 5g, 생강즙 0.5g, 물 300g, 소금 0.5g - 양념장 : 간 홍고추 10g, 간 양파 10g, 다진 마늘 2g, 국간장 2.5g, 간장 2.5g, 맛술 5g, 굵은 고춧가루 5g, 고운 고춧가루 1.25g
            // [ 2인분 ] 마늘(4쪽), 토마토(2개), 양파(1개), 월계수잎(2장), 육수용 멸치(½컵), 다시마(5×5cm, 2장)
            // •필수재료 : 오이고추(40g), 영양부추(5g), 연근(5g), 무(5g)\n•절임물 : 해물육수(8g), 어간장(3g)\n•양념 : 고춧가루(3g), 생강청(3g), 통깨(1g)
            // ●멸치육수 : 국물용 멸치 5g(3마리), 다시마 1장(5×1cm), 양파 10g(2×1cm), 표고버섯 기둥, 국간장 5g(1작은술), 물 300ml(1½컵)\n●채소준비 : 청경채 20g(1개), 표고버섯 20g(2장), 다진 마늘 2g(1/3작은술)

            Recipe target = recipeRepository.findByName(row.get("RCP_NM")).isPresent()?recipeRepository.findByName(row.get("RCP_NM")).get() : null ;
            String input = row.get("RCP_PARTS_DTLS");

            // [ 2인분 ] 마늘(4쪽), 토마토(2개), 양파(1개), 월계수잎(2장), 육수용 멸치(½컵), 다시마(5×5cm, 2장)
            if (input.contains("[")){
                // 대괄호 안의 문자열 : \[ (.*) \]
                Pattern servingsPattern = Pattern.compile("\\[(.*)\\]");
                Matcher servingsMatcher = servingsPattern.matcher(input);
                if (servingsMatcher.find()) {
                    String servingsString = servingsMatcher.group(1).trim();
                    if (servingsString.contains("인분")){
                        char firstChar = servingsString.charAt(0);
                        // recipe의 servings 값
                        String firstString = Character.toString(firstChar);
                        target.setServings(firstString);
                        recipeRepository.save(target);
                        input = input.trim().split("] ")[1];
                    } else {
                        String chkString = "[" + servingsString + "]";
                        input = input.replace(chkString, ", ");
                    }
                }
            }

            // ( 개수 한개인 경우 처리
            // - 주재료 : 토막 낸 닭 170g(닭고기살 무게 90g), 수삼 25g, 건표고버섯 8g, 건표고버섯 불릴 물 150g, 감자 20g, 양파 25g, 당근 10g, 건청양고추 2.5g, 건당면 5g, 대파 10g, 다진 마늘 4g - 양념 : 간장 10g, 황설탕 8g, 올리고당 9g, 노두유 1g, 후추 0.25g
            int chk_cnt = 0;
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) == '(') {
                    chk_cnt++;
                }
                if (chk_cnt >= 2) break;
            }

            // 바지락 30g, 팽이버섯 10g, 홍고추 3g, 쑥갓 1g, 청양고추 3g, 무 8g, 대파 5g, 다시마 5g
            if (chk_cnt < 2) {
                input = input.replaceAll("\\([^\\(\\)]+\\)", "");
                String[] lines = input.split("-");
                for (String line : lines) {
                    // - 주재료 : 민어 50g, 쇠고기 양지15g, 다진 마늘 1.25g, 후추 0.25g, 참기름 0.5g, 무 40g, 애호박 20g, 애느타리버섯 20g, 풋 고추 5g, 홍고추 2.5g, 쑥갓 10g, 대파 5g, 생강즙 0.5g, 물 300g, 소금 0.5g - 양념장 : 간 홍고추 10g, 간 양파 10g, 다진 마늘 2g, 국간장 2.5g, 간장 2.5g, 맛술 5g, 굵은 고춧가루 5g, 고운 고춧가루 1.25g
                    if (line.contains(" : ")) line = line.split(" : ")[1];
                    String[] ingredientText = line.trim().split(", ");
                    for (String text:ingredientText) {
                        String[] arr = text.trim().split(" ");
                        int leng = arr.length;
                        if (leng <= 1) continue;

                        String ingredientName = String.join(" ", Arrays.copyOfRange(arr, 0, leng - 1));
                        String ingredientAmount = arr[leng - 1];

                        Ingredient ingredient = new Ingredient();
                        ingredient.setIngredientName(ingredientName);
                        ingredient.setWeight(ingredientAmount);
                        ingredient.setRecipe(target);
                        ingredientRepository.save(ingredient);
                    }
                }
            }
            // •필수재료 : 오이고추(40g), 영양부추(5g), 연근(5g), 무(5g)\n•절임물 : 해물육수(8g), 어간장(3g)\n•양념 : 고춧가루(3g), 생강청(3g), 통깨(1g)
            // ●멸치육수 : 국물용 멸치 5g(3마리), 다시마 1장(5×1cm), 양파 10g(2×1cm), 표고버섯 기둥, 국간장 5g(1작은술), 물 300ml(1½컵)\n●채소준비 : 청경채 20g(1개), 표고버섯 20g(2장), 다진 마늘 2g(1/3작은술)
            else {
                String[] lines = input.split("\\\\n");
                for (String line : lines) {
                    String[] lineColon = line.trim().split(": ");
                    int leng = lineColon.length;
                    String lineText = lineColon[leng - 1];
                    lineText = ", " + lineText;

                    // 괄호 앞의 모둔 문자열 : ([^\\(]*)
                    // 괄호 안의 문자열 : (\\((.*?)\\))
                    Pattern ingredientsPattern = Pattern.compile("([^\\\\(]*)(\\(.*?\\))");
                    Matcher ingredientsMatcher = ingredientsPattern.matcher(lineText);
                    while (ingredientsMatcher.find()) {
                        String ingredientName = ingredientsMatcher.group(1).replaceFirst(", ", "").trim();
                        String ingredientAmount = ingredientsMatcher.group(2).replaceAll("\\(|\\)", "").trim();

                        Ingredient ingredient = new Ingredient();
                        ingredient.setIngredientName(ingredientName);
                        ingredient.setWeight(ingredientAmount);
                        ingredient.setRecipe(target);
                        ingredientRepository.save(ingredient);
                    }
                }
            }


            // 레시피 조리과정 등록
            for (int i = 1; i < 21; i ++){
                String num = Integer.toString(i);
                if (i < 10) num = "0" + num;
                String manualDescription = row.get("MANUAL" + num);
                String manualImage = row.get("MANUAL_IMG" + num);

                if(manualDescription.length() > 0 ) {
                    RecipeStep recipeStep = new RecipeStep();
                    recipeStep.setRecipe(target);
                    recipeStep.setStepImage(manualImage);
                    recipeStep.setStepDescription(manualDescription);

                    if (manualDescription.contains("℃")) recipeStep.setTemperature(-1);
                    else recipeStep.setTemperature(0);

                    if (manualDescription.contains("불")) recipeStep.setFire(-1);
                    else recipeStep.setFire(0);

                    if (manualDescription.contains("분")) recipeStep.setTimer(-1L);
                    else recipeStep.setTimer(0L);

                    recipeStepRepository.save(recipeStep);
                }
            }

            // 해시태그 등록
            HashTag hashTagName = new HashTag();
            hashTagName.setTag(target.getRecipeTitle());
            hashTagName.setRecipe(target);
            hashTagRepository.save(hashTagName);

            HashTag hashTagCategory = new HashTag();
            hashTagCategory.setTag(target.getCategory());
            hashTagCategory.setRecipe(target);
            hashTagRepository.save(hashTagCategory);
        }

        System.out.println(Integer.toString(cnt) + "개");




//        for (RowDto rowDto : webclientDto.getCOOKRCP01().getRow()) {
//          레시피 등록
//            Recipe recipe = new Recipe();
//            recipe.setAi(false);
//            recipe.setRecipeLikeCnt(0);
//            if (rowDto.getRCP_PAT2().equals("반찬")) recipe.setCategory("side");
//            else if (rowDto.getRCP_PAT2().equals("국&찌개")) recipe.setCategory("soup");
//            else recipe.setCategory(rowDto.getRCP_PAT2());
//            recipe.setDifficulty(2);
//            recipe.setRecipeTitle(rowDto.getRCP_NM());
//            recipe.setResultImage(rowDto.getATT_FILE_NO_MAIN());
//            recipeRepository.save(recipe);
//
////            •필수재료 : 오이고추(40g), 영양부추(5g), 연근(5g), 무(5g)\n•절임물 : 해물육수(8g), 어간장(3g)\n•양념 : 고춧가루(3g), 생강청(3g), 통깨(1g)
//
//            Recipe target = recipeRepository.findByName(rowDto.getRCP_NM()).isPresent()?recipeRepository.findByName(rowDto.getRCP_NM()).get() : null ;
//            String[] ingredientText = rowDto.getRCP_PARTS_DTLS().split(",");
//            for (String text : ingredientText){
//                int leng = text.trim().split(" ").length;
//                String[] purified = text.trim().split(" ")[leng-1].split("\\(");
//                System.out.println(purified[1].substring(0,purified[1].length()-1));
//
//                Ingredient ingredient = new Ingredient();
//                ingredient.setIngredientName(purified[0]);
//                ingredient.setWeight(purified[1].substring(0,purified[1].length()-1));
//                ingredient.setRecipe(target);
//                ingredientRepository.save(ingredient);
//            }

//      recipeStep 등록
//            if(rowDto.getMANUAL01().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG01());
//                recipeStep.setStepDescription(rowDto.getMANUAL01());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL02().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG02());
//                recipeStep.setStepDescription(rowDto.getMANUAL02());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL03().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG03());
//                recipeStep.setStepDescription(rowDto.getMANUAL03());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL04().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG04());
//                recipeStep.setStepDescription(rowDto.getMANUAL04());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL05().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG05());
//                recipeStep.setStepDescription(rowDto.getMANUAL05());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL06().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG06());
//                recipeStep.setStepDescription(rowDto.getMANUAL06());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL07().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG07());
//                recipeStep.setStepDescription(rowDto.getMANUAL07());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL08().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG08());
//                recipeStep.setStepDescription(rowDto.getMANUAL08());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL09().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG09());
//                recipeStep.setStepDescription(rowDto.getMANUAL09());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL10().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG10());
//                recipeStep.setStepDescription(rowDto.getMANUAL10());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL11().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG11());
//                recipeStep.setStepDescription(rowDto.getMANUAL11());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL12().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG12());
//                recipeStep.setStepDescription(rowDto.getMANUAL12());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL13().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG13());
//                recipeStep.setStepDescription(rowDto.getMANUAL13());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL14().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG14());
//                recipeStep.setStepDescription(rowDto.getMANUAL14());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL15().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG15());
//                recipeStep.setStepDescription(rowDto.getMANUAL15());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL16().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG16());
//                recipeStep.setStepDescription(rowDto.getMANUAL16());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL17().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG17());
//                recipeStep.setStepDescription(rowDto.getMANUAL17());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL18().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG18());
//                recipeStep.setStepDescription(rowDto.getMANUAL18());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL19().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG19());
//                recipeStep.setStepDescription(rowDto.getMANUAL19());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }
//            if(rowDto.getMANUAL20().length() > 0 ) {
//                RecipeStep recipeStep = new RecipeStep();
//                recipeStep.setRecipe(target);
//                recipeStep.setStepImage(rowDto.getMANUAL_IMG20());
//                recipeStep.setStepDescription(rowDto.getMANUAL20());
//                recipeStep.setTemperature(0);
//                recipeStep.setFire(0);
//                recipeStep.setTimer(0L);
//                recipeStepRepository.save(recipeStep);
//            }

//        }
    }

    public void deleteRecipeById(Long id) {
        recipeRepository.deleteRecipeById(id);
    }

    public void deleteRecipeByName(String name) {
        recipeRepository.deleteRecipeByName(name);
    }

    public List<RecipesDto> getLikeRecipe(String email) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));
        if (!optionalUser.isPresent()){
            throw new EntityNotFoundException("User not present in the database");
        }
        User user = optionalUser.get();
        HeaderDto headerDto = new HeaderDto();
        headerDto.setUserName(email);

        List<Recipe> recipeList = recipeRepository.likeRecipe(user.getUserId());
        List<RecipesDto> recipesDtoList = new ArrayList<>();
        for (Recipe recipe : recipeList) {
            recipesDtoList.add(new RecipesDto(recipe, headerDto));
        }
        return  recipesDtoList;
    }

    public void updateHashtag() {
        List<Recipe> recipeList = recipeRepository.findAll();

        for (Recipe recipe : recipeList) {
            Long recipeId = recipe.getRecipeId();
            String recipeTitle = recipe.getRecipeTitle();
            String recipeCategory = recipe.getCategory();

            // 해시태그 등록
            HashTag hashTagName = new HashTag();
            hashTagName.setTag(recipeTitle);
            hashTagName.setRecipe(recipe);
            hashTagRepository.save(hashTagName);

            HashTag hashTagCategory = new HashTag();
            hashTagCategory.setTag(recipeCategory);
            hashTagCategory.setRecipe(recipe);
            hashTagRepository.save(hashTagCategory);
        }
    }

    public List<RecipesDto> pagenationSortedRecipe(PageSortDto pageSortDto, HeaderDto headerDto) {
        List<Recipe> recipes= recipeRepository.pagenationSortRecipe(pageSortDto);
        List<RecipesDto> recipesDtoList = new ArrayList<>();

        for (Recipe recipe : recipes) {
            RecipesDto recipesDto = new RecipesDto(recipe, headerDto);
            recipesDtoList.add(recipesDto);
        }

        return recipesDtoList;
    }


    public List<RecipesDto> pagenationRecipeSearch(PageSearchDto PageSearchDto, HeaderDto headerDto) {

        List<RecipesDto> responseRecipesDtoList = new ArrayList<>();
        for(Recipe recipe: recipeRepository.pagenationSearchRecipe(PageSearchDto)) {
            responseRecipesDtoList.add(new RecipesDto(recipe, headerDto));
        }
        return responseRecipesDtoList;
    }

}
