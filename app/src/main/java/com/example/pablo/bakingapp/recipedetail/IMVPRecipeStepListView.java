package com.example.pablo.bakingapp.recipedetail;

import com.example.pablo.bakingapp.bases.IMVPView;
import com.example.pablo.bakingapp.data.model.Recipe;

/**
 * Created by pablo on 29/03/2018.
 */

interface IMVPRecipeStepListView extends IMVPView {
    void setActivityTitle(String title);
    void setList(Recipe recipe);
}
