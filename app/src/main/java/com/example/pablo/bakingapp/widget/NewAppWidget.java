package com.example.pablo.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.pablo.bakingapp.R;
import com.example.pablo.bakingapp.data.model.Ingredient;
import com.example.pablo.bakingapp.data.model.Recipe;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    public static final String WIDGET = "widget";
    public static final String BOOKMARKED_RECIPE = "BookmarkedRecipe";
    public static final String INTENT_UPDATE_ACTION = "updateWidget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.d(TAG, "update App Widget");
        Intent intent = new Intent(context, WidgetService.class);
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        rv.setRemoteAdapter(R.id.listView, intent);
        rv.setEmptyView(R.id.listView, R.id.empty_view);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
        Log.d(TAG, "update App Widget end");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent != null) {
            if (INTENT_UPDATE_ACTION.equals(intent.getAction())) {
                Log.d(TAG, "update Intent Action");
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName thisWidget = new ComponentName(context.getApplicationContext(), NewAppWidget.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
                for (int appWidgetId : appWidgetIds)
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listView);
            } else {
                Log.d(TAG, "no update Intent Action");
            }
        } else
            Log.d(TAG, "no intent");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        for (int appWidgetId : appWidgetIds){
            Log.d(TAG, "onUpdate ID : " + appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        Log.d(TAG, "onUpdate end");
    }

    public static class ListIngredientProvider implements RemoteViewsService.RemoteViewsFactory {
        private ArrayList<Ingredient> ingredients = new ArrayList<>();
        private Context context = null;

        public ListIngredientProvider(Context context, Intent intent) {
            this.context = context;
        }

        private void getIngredients() {
            Log.d(TAG, "getIngredients begin");
            SharedPreferences pref = context.getSharedPreferences(WIDGET, Context.MODE_PRIVATE);
            if (pref.contains(NewAppWidget.BOOKMARKED_RECIPE)) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Recipe recipe = mapper.readValue(pref.getString(BOOKMARKED_RECIPE, null), Recipe.class);
                    ingredients = new ArrayList<>(recipe.getIngredients());
                    Log.d(TAG, "getIngredients size : "+ ingredients.size());

                } catch (IOException e) {
                    e.printStackTrace();
                    ingredients = null;
                }
            } else
                ingredients.clear();
            Log.d(TAG, "getIngredients end");
        }

        @Override
        public void onCreate() {
            Log.d(TAG, "oncreate");
            getIngredients();
        }

        @Override
        public void onDataSetChanged() {
            Log.d(TAG, "ondataSetChanged");
            getIngredients();
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return (ingredients != null) ? ingredients.size() : 0;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        /*
        *Similar to getView of Adapter where instead of View
        *we return RemoteViews
        *
        */
        @Override
        public RemoteViews getViewAt(int position) {
            Log.d(TAG, "getViewAt");
            final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.ingredient_card);
            Ingredient ingredient = ingredients.get(position);
            remoteView.setTextViewText(R.id.textView_ingredientName, ingredient.getIngredient());
            remoteView.setTextViewText(R.id.textView_ingredientMeasure, "" + ingredient.getQuantity());
            remoteView.setTextViewText(R.id.textView_ingredientUnit, ingredient.getMeasure());
            Log.d(TAG, "getViewAt end");
            return remoteView;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }
    }
}

