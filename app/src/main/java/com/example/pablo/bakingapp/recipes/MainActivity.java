package com.example.pablo.bakingapp.recipes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pablo.bakingapp.R;
import com.example.pablo.bakingapp.bases.BaseActivity;
import com.example.pablo.bakingapp.data.model.Recipe;
import com.example.pablo.bakingapp.data.network.NetworkMonitor;
import com.example.pablo.bakingapp.recipedetail.RecipeStepListActivity;
import com.example.pablo.bakingapp.recipes.CustomIdlingResource;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements IMVPMainView, RecipesAdapter.ListItemClickListener, MainNavigator{

    public static final String RECIPE = "recipe";
    private static final String RECIPE_LIST = "recipList";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private MainPresenter presenter;
    public RecipesAdapter adapter;
    @Nullable
    private CustomIdlingResource mIdlingResource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);
        boolean isTablet = getResources().getBoolean(R.bool.material_responsive_is_tablet);
        setUnBinder(ButterKnife.bind(this));
        setView(isTablet);
        setPresenter(getResources().getBoolean(R.bool.material_responsive_is_tablet));
        getRecipes(savedInstanceState);
    }

    private void setPresenter(boolean isTablet) {
        presenter = new MainPresenter<>();
        presenter.setIsTablet(isTablet);
        presenter.onAttach(this);
    }

    private void setView(boolean isTablet){
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        RecyclerView.LayoutManager lManager;
        if (isTablet)
            lManager = new GridLayoutManager(this, 3);
        else
            lManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lManager);
        adapter = new RecipesAdapter(this, isTablet, width);
        recyclerView.setAdapter(adapter);
    }

    private void getRecipes(Bundle savedInstanceState){
        if (savedInstanceState != null && savedInstanceState.containsKey(RECIPE_LIST)){
            ArrayList<Recipe> list = savedInstanceState.getParcelableArrayList(RECIPE_LIST);
            presenter.setRecipeList(list);
            updateList(list);
        } else {
            CustomIdlingResource customIdling = getIdlingResource();
            if (customIdling != null)
                customIdling.setIdle(false);
            presenter.fetchRecipeList();
        }
    }

    @Override
    public void updateList(ArrayList<Recipe> recipes) {
        CustomIdlingResource customIdling = getIdlingResource();
        if (customIdling != null)
            customIdling.setIdle(true);
        adapter.updateList(recipes);
    }

    @Override
    public boolean isNetworkOn() {
        return NetworkMonitor.isNetworkConnected(this);
    }

    @Override
    public void openRecipe(Recipe recipe) {
        Intent intent = new Intent(this, RecipeStepListActivity.class);
        intent.putExtra(RECIPE, recipe);
        startActivity(intent);
    }

    @Override
    public void onItemClick(Recipe recipe) {
        openRecipe(recipe);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        ArrayList list = presenter.getRecipeList();
        if (list != null)
            outState.putParcelableArrayList(RECIPE_LIST, list);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(int resId) {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void showMessage(int resId) {

    }

    @Override
    public void showMessage(String message) {

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

    /**
     * Only called from test, creates and returns a new {@link CustomIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public CustomIdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new CustomIdlingResource();
        }
        return mIdlingResource;
    }
}

class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private final boolean isTablet;
    private final int width;
    private ArrayList<Recipe> recipes =  new ArrayList<>();
    private Context context;
    private ListItemClickListener listener;

    public RecipesAdapter(MainActivity activity, boolean isTablet, int width) {
        this.isTablet = isTablet;
        this.context = activity;
        this.listener = activity;
        this.width = width;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(viewType, parent, false);
        if (!isTablet)
            view.getLayoutParams().height = width / 2;
        ViewHolder viewHolder;
        switch (viewType){
            case R.layout.recip_header_card :
                viewHolder = new ViewHolderHeader(view);
                break;
            case R.layout.recipe_title_right_card:
                viewHolder = new ViewHolderDouble(view);
                break;
            case R.layout.recipe_title_left_card:
                viewHolder = new ViewHolderDouble(view);
                break;
            default:
                viewHolder = new DefaultViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(recipes.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (position == 0 || isTablet)
            viewType = R.layout.recip_header_card;
        else {
            if (position % 2 == 0)
                viewType = R.layout.recipe_title_left_card;
            else
                viewType = R.layout.recipe_title_right_card;
        }
        return viewType;
    }

    @Override
    public int getItemCount() {
        return (recipes == null)? 0:recipes.size();
    }

    public void updateList(ArrayList<Recipe> recipes){
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    public interface ListItemClickListener {
        void onItemClick(Recipe recipe);
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        abstract void bind(Recipe recipe);

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("ViewHolder", "OnClick Position : " + position);
            listener.onItemClick(recipes.get(position));
        }
    }

    public class ViewHolderHeader extends ViewHolder{

        @BindView(R.id.textView_recipeTitle)
        TextView title;
        @BindView(R.id.textView_viewRecipe)
        TextView viewRecipe;
        @BindView(R.id.imageView_recipePicture)
        ImageView picture;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        void bind(Recipe recipe) {
            title.setText(recipe.getName().toUpperCase());
            viewRecipe.setText(context.getText(R.string.viewRecipe));
            picture.setImageResource(R.drawable.default_picture_recipe);
        }
    }

    public class ViewHolderDouble extends ViewHolder{

        @BindView(R.id.textView_recipeTitle)
        TextView title;
        @BindView(R.id.textView_viewRecipe)
        TextView viewRecipe;
        @BindView(R.id.imageView_recipePicture)
        ImageView picture;

        public ViewHolderDouble(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        void bind(Recipe recipe) {
            title.setText(recipe.getName().toUpperCase());
            viewRecipe.setText(context.getText(R.string.viewRecipe));
            picture.setImageResource(R.drawable.default_picture_recipe);
        }
    }

    public class DefaultViewHolder extends ViewHolder {
        public DefaultViewHolder(View view) {
            super(view);
        }

        @Override
        void bind(Recipe recipe) {

        }
    }
}
