package com.example.pablo.bakingapp.recipes;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;

import com.jakewharton.espresso.OkHttp3IdlingResource;
import okhttp3.OkHttpClient;

/**
 * Created by pablo on 21/04/2018.
 */

public abstract class IdlingResources {
    private static IdlingResource okhttpResources;

    public static void registerOkHtpp(OkHttpClient client){
        okhttpResources = OkHttp3IdlingResource.create(
                "okhttp", client);
        okhttpResources.isIdleNow();
        IdlingRegistry.getInstance().register(okhttpResources);
    }

    public static void unregisterOkHttp(OkHttpClient client){
        if (okhttpResources != null)
            IdlingRegistry.getInstance().unregister(okhttpResources);
    }
}
