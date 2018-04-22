package com.example.pablo.bakingapp.recipedetail;

import com.example.pablo.bakingapp.bases.BasePresenter;
import com.example.pablo.bakingapp.data.model.Recipe;
import com.example.pablo.bakingapp.data.model.Step;

import java.util.List;

/**
 * Created by pablo on 29/03/2018.
 */

public class RecipeStepListPresenter<E extends IMVPRecipeStepListView> extends BasePresenter<E> implements IMVPRecipeStepListPresenter<E> {

    private Recipe recipe;

    @Override
    public void setRecipe(Recipe recipe) {
        if (recipe != null) {
            this.recipe = recipe;
            List<Step> steps = recipe.getSteps();
            getMvpView().setActivityTitle(recipe.getName());
            if (steps != null && steps.size() > 0)
                getMvpView().setList(recipe);
        }
    }

    @Override
    public Recipe getRecipe() {
        return recipe;
    }
}
