package com.example.pablo.bakingapp.recipes;

import com.example.pablo.bakingapp.bases.IMVPView;
import com.example.pablo.bakingapp.data.model.Recipe;

import java.util.ArrayList;

/**
 * Created by pablo on 28/03/2018.
 */

public interface IMVPMainView extends IMVPView {
    void updateList(ArrayList<Recipe> recipes);
}
