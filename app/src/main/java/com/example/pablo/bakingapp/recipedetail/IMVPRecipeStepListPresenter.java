package com.example.pablo.bakingapp.recipedetail;

import android.os.Parcelable;

import com.example.pablo.bakingapp.bases.BasePresenter;
import com.example.pablo.bakingapp.bases.IMVPPresenter;
import com.example.pablo.bakingapp.bases.IMVPView;
import com.example.pablo.bakingapp.data.model.Recipe;

/**
 * Created by pablo on 29/03/2018.
 */

public interface IMVPRecipeStepListPresenter<E extends IMVPRecipeStepListView> extends IMVPPresenter<E>{
    void setRecipe(Recipe recipe);
    Recipe getRecipe();
}
