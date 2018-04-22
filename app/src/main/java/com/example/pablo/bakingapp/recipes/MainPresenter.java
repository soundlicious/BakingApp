package com.example.pablo.bakingapp.recipes;


import android.util.Log;

import com.example.pablo.bakingapp.bases.BasePresenter;
import com.example.pablo.bakingapp.data.DataManager;
import com.example.pablo.bakingapp.data.model.Recipe;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pablo on 28/03/2018.
 */

public class MainPresenter<E extends IMVPMainView> extends BasePresenter<E> implements IMVPMainPresenter<E> {

    private ArrayList<Recipe> recipeList = new ArrayList<>();

    @Override
    public ArrayList<Recipe> getRecipeList() {
        return recipeList;
    }

    @Override
    public void fetchRecipeList() {
        Single<ArrayList<Recipe>> singleObs = DataManager.getInstance().getBackingAPI().getRecipeList();
        singleObs.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recipes ->  {
                    Log.i("test", "recipes received");
                    MainPresenter.this.recipeList = recipes;
                    getMvpView().updateList(recipes);
                }, throwable -> Log.i("test", throwable.getMessage()));
    }

    @Override
    public void setRecipeList(ArrayList<Recipe> list) {
        this.recipeList = list;
    }
}
