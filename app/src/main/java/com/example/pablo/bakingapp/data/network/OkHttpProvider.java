package com.example.pablo.bakingapp.data.network;

import okhttp3.OkHttpClient;

/**
 * Created by pablo on 19/04/2018.
 */

public class OkHttpProvider {
    private static OkHttpClient INSTANCE = null;

    public static OkHttpClient getOkHttpInstance() {
        if (INSTANCE == null)
            INSTANCE = new OkHttpClient();
        return INSTANCE;
    }
}
