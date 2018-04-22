package com.example.pablo.bakingapp.data;

import com.example.pablo.bakingapp.data.network.APIHelperFactory;
import com.example.pablo.bakingapp.data.network.BackingAPI;

/**
 * Created by pablo on 28/03/2018.
 */

public class DataManager {

    private static class Holder {
        private static final DataManager INSTANCE = new DataManager();
    }

    public static DataManager getInstance() {
        return Holder.INSTANCE;
    }

    private DataManager() {
    }

    public BackingAPI getBackingAPI(){
        return APIHelperFactory.createRetrofitService(BackingAPI.class, BackingAPI.API_URL);
    }

}
