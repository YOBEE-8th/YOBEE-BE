package com.example.yobee.recipe.repository;

import com.example.yobee.recipe.domain.Recipe;
import com.example.yobee.recipe.dto.PageSearchDto;
import com.example.yobee.recipe.dto.PageSortDto;
import com.example.yobee.recipe.dto.SortDto;
import com.example.yobee.user.domain.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public class JpaRecipeRepository implements RecipeRepository {

    private final EntityManager em;

    public JpaRecipeRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public List<Recipe> sortRecipe(SortDto sortDto) {
        String category = sortDto.getCategory();
        int sortLogic = sortDto.getSort();
        Boolean oredrBoolean = sortDto.getOrder();
        String query = category.equals("전체") ? "SELECT recipe FROM Recipe recipe LEFT JOIN Review review ON recipe.recipeId = review.recipe.recipeId GROUP BY recipe.recipeId "
                :
                "SELECT recipe FROM Recipe recipe LEFT JOIN Review review ON recipe.recipeId = review.recipe.recipeId WHERE recipe.category = '" + category + "' GROUP BY recipe.recipeId ";
        String order = oredrBoolean ? "ASC" : "DESC";
        switch (sortLogic) {
            case 1:
                query += "ORDER BY COUNT(CASE WHEN reviw.content IS NOT NULL THEN 1 END)" + order;
                break;
            case 2:
                query += "order by recipe.difficulty " + order;
                break;
            default:
                query += "order by recipe.recipeLikeCnt " + order;
                break;
        }
        return em.createQuery(query, Recipe.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Recipe findById(Long id) {
        return em.find(Recipe.class, id);
    }

    @Override
    @Transactional
    public void likeIncrease(Long id) {
        em.createQuery("update Recipe r set r.recipeLikeCnt = r.recipeLikeCnt + 1 WHERE r.recipeId = :id")
                .setParameter("id", id)
                .executeUpdate();
        em.clear();
    }

    @Override
    @Transactional
    public void likeDecrease(Long id) {
        em.createQuery("update Recipe r set r.recipeLikeCnt = r.recipeLikeCnt - 1 WHERE r.recipeId = :id")
                .setParameter("id", id)
                .executeUpdate();
        em.clear();
    }

    @Override
    public List<Recipe> findAll() {
        return em.createQuery("SELECT r from Recipe r", Recipe.class).getResultList();
    }

    @Override
    public List<Recipe> likeRecipe(Long id) {
        return em.createQuery("select r from Recipe r left join RecipeLike l on r = l.recipe WHERE l.user.userId = :id order by r.recipeLikeCnt DESC", Recipe.class)
                .setParameter("id", id)
                .getResultList();
    }

    @Transactional
    @Override
    public Recipe save(Recipe recipe) {
        em.persist(recipe);
        return recipe;
    }

    @Override
    public Optional<Recipe> findByName(String name) {
        List<Recipe> result = em.createQuery("select r from Recipe r where r.recipeTitle = :name", Recipe.class)
                .setParameter("name", name)
                .getResultList();
        return result.stream().findAny();
    }

    @Transactional
    @Override
    public void deleteRecipeById(Long id) {
        Recipe recipe = em.find(Recipe.class, id);
        em.remove(recipe);
    }

    @Transactional
    @Override
    public void deleteRecipeByName(String name) {
        List<Recipe> result = em.createQuery("select r from Recipe r where r.recipeTitle = :name", Recipe.class)
                .setParameter("name", name)
                .getResultList();
        em.remove(result.stream().findAny().get());
    }


    @Override
    public List<Recipe> searchRecipe(SortDto sortDto, String keyword) {
        String category = sortDto.getCategory();
        int sortLogic = sortDto.getSort();
        Boolean oredrBoolean = sortDto.getOrder();


        String query = "select recipe from Recipe recipe LEFT JOIN Review r ON recipe.recipeId = r.recipe.recipeId LEFT JOIN HashTag h ON h.recipe.recipeId = recipe.recipeId where h.tag like '%" + keyword + "%' GROUP BY recipe.recipeId ";
        String order = oredrBoolean ? "ASC" : "DESC";
        switch (sortLogic) {
            case 1:
                query += "ORDER BY COUNT(CASE WHEN r.content IS NOT NULL THEN 1 END)";
                break;
            case 2:
                query += "order by recipe.difficulty ";
                break;
            default:
                query += "order by recipe.recipeLikeCnt ";
                break;
        }
        query += order;
        return em.createQuery(query, Recipe.class)
                .getResultList();
    }

    @Override
    public List<Recipe> pagenationSortRecipe(PageSortDto pageSortDto) {
        String category = pageSortDto.getCategory();
        int sortLogic = pageSortDto.getSort();
        Boolean oredrBoolean = pageSortDto.getOrder();
        int page = pageSortDto.getPage();

        String query = category.equals("전체") ? "SELECT recipe FROM Recipe recipe LEFT JOIN Review review ON recipe.recipeId = review.recipe.recipeId GROUP BY recipe.recipeId "
                :
                "SELECT recipe FROM Recipe recipe LEFT JOIN Review review ON recipe.recipeId = review.recipe.recipeId WHERE recipe.category = '" + category + "' GROUP BY recipe.recipeId ";
        String order = oredrBoolean ? "ASC" : "DESC";
        switch (sortLogic) {
            case 1:
                query += "ORDER BY COUNT(CASE WHEN review.content IS NOT NULL THEN 1 END)" + order;
                break;
            case 2:
                query += "order by recipe.difficulty " + order;
                break;
            default:
                query += "order by recipe.recipeLikeCnt " + order;
                break;
        }
        return em.createQuery(query, Recipe.class)
                .setFirstResult((page) * 20)
                .setMaxResults(20)
                .getResultList();
    }

    @Override
    public List<Recipe> pagenationSearchRecipe(PageSearchDto pageSearchDto) {
        int sortLogic = pageSearchDto.getSort();
        Boolean oredrBoolean = pageSearchDto.getOrder();
        int page = pageSearchDto.getPage();
        String keyword = pageSearchDto.getKeyword();


        String query = "select recipe from Recipe recipe LEFT JOIN Review r ON recipe.recipeId = r.recipe.recipeId LEFT JOIN HashTag h ON h.recipe.recipeId = recipe.recipeId where h.tag like '%" + keyword + "%' GROUP BY recipe.recipeId ";
        String order = oredrBoolean ? "ASC" : "DESC";
        switch (sortLogic) {
            case 1:
                query += "ORDER BY COUNT(CASE WHEN r.content IS NOT NULL THEN 1 END)";
                break;
            case 2:
                query += "order by recipe.difficulty ";
                break;
            default:
                query += "order by recipe.recipeLikeCnt ";
                break;
        }
        query += order;
        return em.createQuery(query, Recipe.class)
                .setFirstResult((page) * 20)
                .setMaxResults(20)
                .getResultList();
    }



}