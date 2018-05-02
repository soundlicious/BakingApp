package com.example.pablo.bakingapp.recipedetail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pablo.bakingapp.R;
import com.example.pablo.bakingapp.bases.BaseActivity;
import com.example.pablo.bakingapp.data.model.Ingredient;
import com.example.pablo.bakingapp.data.model.Recipe;
import com.example.pablo.bakingapp.data.model.Step;
import com.example.pablo.bakingapp.recipedetail.recipestep.RecipeStepActivity;
import com.example.pablo.bakingapp.recipedetail.recipestep.RecipeStepFragment;
import com.example.pablo.bakingapp.recipes.MainActivity;
import com.example.pablo.bakingapp.widget.NewAppWidget;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vipulasri.timelineview.LineType;
import com.github.vipulasri.timelineview.TimelineView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepListActivity extends BaseActivity implements IMVPRecipeStepListView {

    private static final String TAG = RecipeStepListActivity.class.getSimpleName();
    private static final int RESULT_NEXT = 100;
    @BindView(R.id.recyclerView)
    RecyclerView stepsView;
    RecipeStepListPresenter presenter;
    private StepsAdapter adapter;
    private Recipe recipe;
    private Menu menu;
    int pos = 0;
    private boolean isBookmarked = false;
    private static final String LIST_STATE = "listState";
    private Parcelable listState;
    private boolean hasSavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps);
        setUnBinder(ButterKnife.bind(this));
        Intent intent = getIntent();

        recipe = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(LIST_STATE))
            listState = savedInstanceState.getParcelable(LIST_STATE);
        if (intent != null)
            recipe = intent.getParcelableExtra(MainActivity.RECIPE);

        if (recipe != null) {
            Log.d(TAG, recipe.getName());
            List<Step> steps;
            steps = recipe.getSteps();
            if (steps != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    steps.removeIf(this::isEmptyStep);
                else {
                    Iterator itr = steps.iterator();
                    while (itr.hasNext())
                        if (isEmptyStep((Step) itr.next()))
                            itr.remove();
                }
            }

        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        hasSavedInstanceState = savedInstanceState != null;
        if (recipe == null && savedInstanceState != null) {
            if (savedInstanceState.containsKey(MainActivity.RECIPE))
                recipe = (Recipe) savedInstanceState.get(MainActivity.RECIPE);
        }
    }

    private boolean isEmptyStep(Step step) {
        return (isStringEmpty(step.getThumbnailURL())
                && isStringEmpty(step.getVideoURL())
                && isStringEmpty(step.getShortDescription()));
    }

    private boolean isStringEmpty(String str) {
        if (str == null || str.isEmpty())
            return true;
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recipe == null)
            recipe = getRecipeFromPref(TAG, MainActivity.RECIPE);
        presenter = new RecipeStepListPresenter();
        presenter.onAttach(this);
        presenter.setIsTablet(getResources().getBoolean(R.bool.material_responsive_is_tablet));
        setView();
        presenter.setRecipe(recipe);
    }

    private Recipe getRecipeFromPref(String tag, String key) {
        SharedPreferences sharedPref = getSharedPreferences(tag, MODE_PRIVATE);
        ObjectMapper mapper = new ObjectMapper();
        if (sharedPref.contains(key)) {
            try {
                return mapper.readValue(sharedPref.getString(key, null), Recipe.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        listState = stepsView.getLayoutManager().onSaveInstanceState();
        saveRecipeInPref(TAG, MainActivity.RECIPE);
    }

    private void saveRecipeInPref(String tag, String key) {
        SharedPreferences sharedPref = getSharedPreferences(tag, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String recipeString = mapper.writeValueAsString(recipe);
            editor.putString(key, recipeString);
            editor.commit();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void deleteRecipeInPref(String tag, String key) {
        SharedPreferences mySPrefs = getSharedPreferences(tag, MODE_PRIVATE);
        SharedPreferences.Editor editor = mySPrefs.edit();
        editor.remove(key);
        editor.apply();
        if (!mySPrefs.contains(key))
            Log.i(TAG, "key doesn't exist anymore");
    }

    private boolean isRecipeInPref(String tag, String key) {
        SharedPreferences prefs = getSharedPreferences(tag, MODE_PRIVATE);
        return prefs.contains(key);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.menu = menu;
        inflater.inflate(R.menu.recipe_star_menu, menu);
        if (isRecipeInPref(NewAppWidget.WIDGET, NewAppWidget.BOOKMARKED_RECIPE)) {
            Recipe rec = getRecipeFromPref(NewAppWidget.WIDGET, NewAppWidget.BOOKMARKED_RECIPE);
            if (rec != null && recipe.getName().equals(rec.getName())) {
                isBookmarked = true;
                setStarIcon(isBookmarked);
            }
        }
        return true;
    }

    public void setStarIcon(Boolean isBookmarked) {
        switch ((isBookmarked) ? 1 : 0) {
            case 1:
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_star_full_white_24dp));
                saveRecipeInPref(NewAppWidget.WIDGET, NewAppWidget.BOOKMARKED_RECIPE);
                break;
            case 0:
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_star_border_white_24dp));
                deleteRecipeInPref(NewAppWidget.WIDGET, NewAppWidget.BOOKMARKED_RECIPE);
                break;
            default:
        }
        updateWidget();
    }

    private void updateWidget() {
        Intent intent = new Intent(this, NewAppWidget.class);
        intent.setAction(NewAppWidget.INTENT_UPDATE_ACTION);
        sendBroadcast(intent);
    }


    private void setView() {
        stepsView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StepsAdapter(this);
        stepsView.setAdapter(adapter);
        if (recipe != null && recipe.getSteps() != null && presenter.isTablet())
            setFragmentStep(recipe.getSteps().get(pos));
    }

    @Override
    public void setActivityTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(title);
    }

    @Override
    public void setList(Recipe recipe) {
        Log.d(TAG, "setList");
        adapter.updateList(recipe.getSteps(), recipe.getIngredients());
        if (listState != null && recipe != null){
            Log.i(TAG, "onResume : Restoring recyclerView State");
            stepsView.getLayoutManager().onRestoreInstanceState(listState);
        } else
            Log.i(TAG, "onResume : nothing to restore stateList is : " +((listState!=null)?"nonNull":"null"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_bookmark:
                isBookmarked = !isBookmarked;
                setStarIcon(isBookmarked);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.RECIPE, presenter.getRecipe());
        outState.putParcelable(LIST_STATE, listState);
    }

    private void openActivity(Step step) {
        pos = recipe.getSteps().indexOf(step);
        RecipeStepListActivity act = RecipeStepListActivity.this;
        List<Ingredient> ingredients = recipe.getIngredients();
        boolean isIngredient = ingredients != null && ingredients.size() > 0;
        boolean isLastStep = (pos == adapter.getItemCount() - 1 - ((isIngredient) ? 1 : 0));
        Intent intent = new Intent(act, RecipeStepActivity.class);
        intent.putExtra(RecipeStepFragment.ARG_ITEM_ID, step);
        intent.putExtra(RecipeStepFragment.ARG_STEP_IS_LAST, isLastStep);
        Log.d(TAG, "setFragmentStep - pos : " + pos + " and is Last Step : " + isLastStep + " and adapterCount : " + adapter.getItemCount());
        act.startActivityForResult(intent, RESULT_NEXT);
    }

    public void setFragmentStep(Step step) {
        pos = recipe.getSteps().indexOf(step);
        List<Ingredient> ingredients = recipe.getIngredients();
        boolean isIngredient = ingredients != null && ingredients.size() > 0;
        boolean isLastStep = (pos == adapter.getItemCount() - 1 - ((isIngredient) ? 1 : 0));
        Bundle arguments = new Bundle();
        arguments.putParcelable(RecipeStepFragment.ARG_ITEM_ID, step);
        arguments.putBoolean(RecipeStepFragment.ARG_STEP_IS_LAST, isLastStep);
        RecipeStepFragment fragment = new RecipeStepFragment();
        fragment.setArguments(arguments);
        Log.d(TAG, "setFragmentStep - pos : " + pos + " and is Last Step : " + isLastStep + " and adapterCount : " + adapter.getItemCount());
        if (!hasSavedInstanceState)
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .commit();

    }


    public Step getNextStep() {
        Log.d(TAG, "getNextStep - pos : " + pos);
        pos += 1;
        if (pos < recipe.getSteps().size()) {
            return recipe.getSteps().get(pos);
        }
        pos = adapter.getItemCount() - 1;
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RecipeStepListActivity.RESULT_NEXT && resultCode == RESULT_OK)
            openActivity(getNextStep());
    }

    @Override
    public void showRefreshButton() {

    }

    @Override
    public void hideRefreshButton() {

    }

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {
        private ArrayList<Step> steps;
        private ArrayList ingredients;

        private final View.OnClickListener mOnClickListener = (View view) -> {
            Step step = (Step) view.getTag();
            if (presenter.isTablet())
                setFragmentStep(step);
            else {
                openActivity(step);
            }
        };
        private boolean isIngredient;
        private boolean isImage;

        public StepsAdapter(RecipeStepListActivity recipeStepListActivity) {
        }

        @NonNull
        @Override
        public StepsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), viewType, null);
            ViewHolder viewHolder;
            if (viewType == R.layout.item_step_recipedetail)
                viewHolder = new ViewHolderStep(view);
            else if (viewType == R.layout.step_thumbmail_card)
                viewHolder = new ViewHolderThumbmail(view);
            else
                viewHolder = new ViewHolderIngredients(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull StepsAdapter.ViewHolder holder, int position) {
            int i = 0;
            if (isIngredient)
                i++;
            if (isImage)
                i++;
            if (holder instanceof ViewHolderIngredients)
                ((ViewHolderIngredients) holder).bind(ingredients);
            else if (holder instanceof ViewHolderThumbmail)
                ((ViewHolderThumbmail) holder).bind(recipe.getImage());
            else {
                int count = getItemCount();
                int linetype = LineType.NORMAL;
                if (position - i == 0)
                    linetype = LineType.BEGIN;
                else if (position == (count - i))
                    linetype = LineType.END;
                ((ViewHolderStep) holder).bind(steps.get(position - i), linetype);
            }
        }

        @Override
        public int getItemCount() {
            isIngredient = ingredients != null && ingredients.size() > 0;
            isImage = !TextUtils.isEmpty(recipe.getImage());
            int listCount = (steps == null) ? 0 : steps.size() + ((isIngredient) ? 1 : 0) + ((isImage) ? 1 : 0);
            Log.d(TAG, "listCount = " + listCount);
            return listCount;
        }

        @Override
        public int getItemViewType(int position) {
            int viewType;
            pos = isImage ? 1 : 0;
            if (position == 0 && (isImage || isIngredient))
                viewType = isImage ? R.layout.step_thumbmail_card : R.layout.ingredients_card;
            else if (position == 1 && isImage && isIngredient)
                viewType = R.layout.ingredients_card;
            else
                viewType = R.layout.item_step_recipedetail;
            return viewType;
        }

        public void updateList(List<Step> steps, List<Ingredient> ingredients) {
            this.steps = new ArrayList<>(steps);
            this.ingredients = new ArrayList<>(ingredients);
            notifyDataSetChanged();
        }

        public abstract class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class ViewHolderStep extends StepsAdapter.ViewHolder {
            @BindView(R.id.time_marker)
            TimelineView timelineView;
            @BindView(R.id.text_timeline_title)
            TextView stepTitle;

            public ViewHolderStep(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(mOnClickListener);
            }

            public void bind(Step step, int lineType) {
                stepTitle.setText(step.getShortDescription());
                itemView.setTag(step);
                timelineView.initLine(lineType);
            }
        }

        public class ViewHolderThumbmail extends StepsAdapter.ViewHolder {
            @BindView(R.id.imageView_card_thumbmail)
            ImageView stepImg;

            public ViewHolderThumbmail(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(String str) {
                if (!TextUtils.isEmpty(str))
                    Picasso.with(getBaseContext())
                            .load(str)
                            .centerCrop()
                            .placeholder(R.drawable.default_picture_recipe)
                            .error(R.drawable.default_picture_recipe)
                            .fit()
                            .into(stepImg);
                else
                    stepImg.setImageResource(R.drawable.default_picture_recipe);
            }
        }

        public class ViewHolderIngredients extends StepsAdapter.ViewHolder {
            @BindView(R.id.linearLayout_ingredients)
            LinearLayout ingredientsView;

            public ViewHolderIngredients(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }


            public void bind(ArrayList<Ingredient> ingredients) {
                for (Ingredient ingredient : ingredients) {
                    View layout = LayoutInflater.from(itemView.getContext()).inflate(R.layout.ingredient_card, null, false);
                    layout.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
                    TextView name = layout.findViewById(R.id.textView_ingredientName);
                    TextView measure = layout.findViewById(R.id.textView_ingredientMeasure);
                    TextView unit = layout.findViewById(R.id.textView_ingredientUnit);
                    name.setText(ingredient.getIngredient());
                    measure.setText("" + ingredient.getQuantity());
                    unit.setText(ingredient.getMeasure());
                    ingredientsView.addView(layout);
                }
            }
        }
    }
}

