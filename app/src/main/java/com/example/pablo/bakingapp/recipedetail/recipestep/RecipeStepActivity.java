package com.example.pablo.bakingapp.recipedetail.recipestep;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.pablo.bakingapp.R;

/**
 * Created by pablo on 02/04/2018.
 */

public class RecipeStepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(RecipeStepFragment.ARG_ITEM_ID,
                    getIntent().getParcelableExtra(RecipeStepFragment.ARG_ITEM_ID));
            arguments.putBoolean(RecipeStepFragment.ARG_STEP_IS_LAST,
                    getIntent().getBooleanExtra(RecipeStepFragment.ARG_STEP_IS_LAST, true));
            RecipeStepFragment fragment = new RecipeStepFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setActivityResult() {
        Intent intent = new Intent();
        intent.setFlags(0);
        setResult(RESULT_OK, intent);
    }
}
