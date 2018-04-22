package com.example.pablo.bakingapp.data.network;

import com.example.pablo.bakingapp.BuildConfig;
import com.example.pablo.bakingapp.data.model.Recipe;

import java.util.ArrayList;
import io.reactivex.Single;
import retrofit2.http.GET;

/**
 * Created by pablo on 28/03/2018.
 */

public interface BackingAPI {

    String API_URL = BuildConfig.RECIPES_ENDPOINT;

    @GET("baking.json")
    Single<ArrayList<Recipe>> getRecipeList();
}
