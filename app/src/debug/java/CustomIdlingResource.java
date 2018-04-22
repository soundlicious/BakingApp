package com.example.pablo.bakingapp.recipes;

import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by pablo on 21/04/2018.
 */

public class CustomIdlingResource implements IdlingResource {

    private AtomicBoolean isIdle = new AtomicBoolean(true);
    private ResourceCallback callback;

    @Override
    public String getName() {
        return "CustomIdlingResource";
    }

    @Override
    public boolean isIdleNow() {
        return isIdle.get();
    }

    public void setIdle(boolean idle) {
        isIdle.set(idle);
        if (idle && callback != null) {
            callback.onTransitionToIdle();
        }
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.callback = resourceCallback;
    }
}
