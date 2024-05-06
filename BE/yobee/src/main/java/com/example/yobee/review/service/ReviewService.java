package com.example.yobee.review.service;


import com.example.yobee.recipe.domain.Recipe;
import com.example.yobee.recipe.dto.HeaderDto;
import com.example.yobee.recipe.dto.MyHistoryDto;
import com.example.yobee.recipe.dto.MyReivewDto;
import com.example.yobee.recipe.repository.RecipeRepository;

import com.example.yobee.review.domain.Review;
import com.example.yobee.review.domain.ReviewLike;
import com.example.yobee.review.dto.*;
import com.example.yobee.review.repository.ReviewLikeRepository;
import com.example.yobee.review.repository.ReviewRepository;
//import com.example.yobee.s3.service.S3Service;
import com.example.yobee.user.domain.User;
import com.example.yobee.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final RecipeRepository recipeRepository;
    private  final ReviewRepository reviewRepository;
    private  final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    //private final S3Service s3Service;

    public List<RecipeReviewDto> recipeReview(Long id, HeaderDto headerDto) {
        List<RecipeReviewDto> recipeReviewDtos = new ArrayList<>();
        for(Review review : reviewRepository.findByrecipeAndContentNotNullOrderByCreatedAt(recipeRepository.findById(id))){
            User user = userRepository.findByEmail(headerDto.getUserName());
            Boolean isLike = reviewLikeRepository.existsByUserAndReview(user, review);
            recipeReviewDtos.add(new RecipeReviewDto(review, isLike, headerDto));
        }
        return recipeReviewDtos;
    }

    @Transactional
    public void reviewLike(Long id, HeaderDto headerDto) {
        Review review = reviewRepository.findById(id).get();
        User user = userRepository.findByEmail(headerDto.getUserName());
        Boolean likeBoolean = reviewLikeRepository.existsByUserAndReview(user, review);

        if (likeBoolean){
            review.setReviewLikeCnt(review.getReviewLikeCnt() - 1);
            reviewRepository.save(review);

            ReviewLike reviewLike = reviewLikeRepository.findByUserAndReview(user, review);
            reviewLikeRepository.delete(reviewLike);
        }else{
            review.setReviewLikeCnt(review.getReviewLikeCnt() + 1);
            reviewRepository.save(review);

            ReviewLike reviewLike = new ReviewLike();
            reviewLike.setReview(review);
            reviewLike.setUser(user);
            reviewLikeRepository.save(reviewLike);
        }
    }

    public List<MyReivewDto> getMyReviewList(String email){

        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));

        if (!optionalUser.isPresent()){
            throw new EntityNotFoundException("User not present in the database");
        }
        User user = optionalUser.get();

        List<Review> reviewList = reviewRepository.findByUser(user);
        List<MyReivewDto> myReivewDtoList = new ArrayList<>();
        for (Review review : reviewList){
            Recipe recipe = recipeRepository.findById(review.getRecipe().getRecipeId());
            boolean logic = true;
            for(MyReivewDto myReivewDto : myReivewDtoList){
                if(myReivewDto.getTitle().equals(review.getRecipe().getRecipeTitle())){
                    logic = false;
                }
            }
            if(logic) {
                MyReivewDto myReivewDto = new MyReivewDto();

                myReivewDto.setTitle(recipe.getRecipeTitle());
                myReivewDto.setRecipeId(recipe.getRecipeId());
                myReivewDto.setRecipeImage(recipe.getResultImage());
                myReivewDto.setReviewCnt(reviewRepository.findByUserAndRecipeOrderByCreatedAt(user, recipe).size());

                myReivewDtoList.add(myReivewDto);
            }
        }

        return myReivewDtoList;
    }

    public List<MyHistoryDto> getMyHistory(String email, Long id) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));

        if (!optionalUser.isPresent()){
            throw new EntityNotFoundException("User not present in the database");
        }
        User user = optionalUser.get();

        Optional<Recipe> optionalRecipe = Optional.ofNullable(recipeRepository.findById(id));

        if (!optionalRecipe.isPresent()){
            throw new EntityNotFoundException("Recipe not present in the database");
        }
        Recipe recipe = optionalRecipe.get();

        List<MyHistoryDto> myHistoryDtoList = new ArrayList<>();
        List<Review> reviewList = reviewRepository.findByUserAndRecipeOrderByCreatedAt(user, recipe);
        for(Review review : reviewList) {
            MyHistoryDto myHistoryDto = new MyHistoryDto();
            myHistoryDto.setTitle(review.getRecipe().getRecipeTitle());
            myHistoryDto.setCreatedAt(review.getCreatedAt());
            myHistoryDto.setReviewId(review.getReviewId());
            myHistoryDto.setReviewImage(review.getReviewImage());
            myHistoryDto.setIsCompleted(review.getContent() != null);

            myHistoryDtoList.add(myHistoryDto);
        }
        return myHistoryDtoList;


    }


    public MyReviewDetailDto getReview(String email, Long id){
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));

        if (!optionalUser.isPresent()){
            throw new EntityNotFoundException("User not present in the database");
        }
        User user = optionalUser.get();

        Optional<Review> optionalReview = reviewRepository.findById(id);

        if (!optionalReview.isPresent()){
            throw new EntityNotFoundException("Recipe not present in the database");
        }
        Review review = optionalReview.get();
        MyReviewDetailDto myReviewDetailDto = new MyReviewDetailDto();

        myReviewDetailDto.setReviewImage(review.getReviewImage());
        myReviewDetailDto.setContent(review.getContent());

        return myReviewDetailDto;
    }

    @Transactional
    public void updateReview(UpdateReviewDto updateReviewDto, String email){

        Long id = updateReviewDto.getReviewId();
        String content = updateReviewDto.getContent();

        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));

        if (!optionalUser.isPresent()){
            throw new EntityNotFoundException("User not present in the database");
        }
        User user = optionalUser.get();

        Optional<Review> optionalReview = reviewRepository.findById(id);

        if (!optionalReview.isPresent()){
            throw new EntityNotFoundException("Recipe not present in the database");
        }
        Review review = optionalReview.get();
        review.setContent(content);
        reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long id, String email){

        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));

        if (!optionalUser.isPresent()){
            throw new EntityNotFoundException("User not present in the database");
        }
        User user = optionalUser.get();

        Optional<Review> optionalReview = reviewRepository.findById(id);

        if (!optionalReview.isPresent()){
            throw new EntityNotFoundException("Recipe not present in the database");
        }
        Review review = optionalReview.get();
        String imgUrl = review.getReviewImage().split("/")[review.getReviewImage().split("/").length - 1];
        //s3Service.delete(imgUrl);
        reviewRepository.delete(review);
    }

    @Transactional
    public ReviewIdDto makeReview(CreateReviewDto createReviewDto, MultipartFile reviewImage) throws IOException {

        String imgPath = null;

        Long recipeId = createReviewDto.getRecipeId();
        String content = createReviewDto.getContent();
        String email = createReviewDto.getEmail();

        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));
        if (!optionalUser.isPresent()){
            throw new EntityNotFoundException("User not present in the database");
        }
        User user = optionalUser.get();
        String name = reviewImage.getOriginalFilename();
        System.out.println(name);
        System.out.println(name.contains("."));
        int leng = name.split("\\.").length;
        String fileName = "recipe" + String.valueOf(recipeId) + "_"+ String.valueOf(System.currentTimeMillis()) + "." + name.split("\\.")[leng-1];
        //imgPath = s3Service.upload(reviewImage, fileName);

        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setContent(content);
        reviewDto.setReviewImage(imgPath);

        Review review = reviewDto.toEntity();
        review.setUser(user);
        review.setRecipe(recipeRepository.findById(recipeId));
        reviewRepository.save(review);
        ReviewIdDto reviewIdDto = new ReviewIdDto();

        reviewIdDto.setReviewId(reviewRepository.findByreviewImage(imgPath).getReviewId());

        return reviewIdDto;

    }

    public void deleteUserReviews(User user){
        List<Review> reviewList = reviewRepository.findByUser(user);

        for(Review review : reviewList){
            String imgUrl = review.getReviewImage().split("/")[review.getReviewImage().split("/").length - 1];
            //s3Service.delete(imgUrl);
        }
    }
}