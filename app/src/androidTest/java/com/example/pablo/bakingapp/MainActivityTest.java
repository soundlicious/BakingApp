package com.example.pablo.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.pablo.bakingapp.data.network.OkHttpProvider;
import com.example.pablo.bakingapp.recipedetail.RecipeStepListActivity;
import com.example.pablo.bakingapp.recipes.CustomIdlingResource;
import com.example.pablo.bakingapp.recipes.IdlingResources;
import com.example.pablo.bakingapp.recipes.MainActivity;
import com.jakewharton.espresso.OkHttp3IdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by pablo on 15/04/2018.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);
    private CustomIdlingResource mIdlingResource;

    @Before
    public void registerIdlingResources() {
        mIdlingResource = main.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Before
    public void initIntent(){
        Intents.init();
    }

    @Test
    public void test_open_activity_in_recipe_list() {
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        intended(hasComponent(RecipeStepListActivity.class.getName()));
        pressBack();
    }

    @After
    public void UnregisterIdling() {
        Espresso.unregisterIdlingResources(mIdlingResource);
    }

    @After
    public void releaseIntents(){
        Intents.release();
    }


}
