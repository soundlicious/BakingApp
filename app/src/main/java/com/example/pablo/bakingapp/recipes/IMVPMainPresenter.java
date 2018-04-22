package com.example.pablo.bakingapp.recipes;

import com.example.pablo.bakingapp.bases.IMVPPresenter;
import com.example.pablo.bakingapp.data.model.Recipe;

import java.util.ArrayList;

/**
 * Created by pablo on 28/03/2018.
 */

public interface IMVPMainPresenter<E extends IMVPMainView> extends IMVPPresenter<E> {

    ArrayList<Recipe> getRecipeList();
    void fetchRecipeList();
    void setRecipeList(ArrayList<Recipe> list);
}
