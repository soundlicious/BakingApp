package com.example.pablo.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {

    public WidgetService() {
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new NewAppWidget.ListIngredientProvider(this.getApplicationContext(), intent));
    }
}
