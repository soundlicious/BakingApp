package com.example.pablo.bakingapp.recipedetail.recipestep;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pablo.bakingapp.R;
import com.example.pablo.bakingapp.bases.BaseFragment;
import com.example.pablo.bakingapp.data.model.Step;
import com.example.pablo.bakingapp.recipedetail.RecipeStepListActivity;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by pablo on 02/04/2018.
 */

public class RecipeStepFragment extends BaseFragment implements RecipeStepView {
    public static final String ARG_ITEM_ID = "recipe_step";
    public static final String ARG_STEP_IS_LAST = "isLastStep";
    private static final String TAG = RecipeStepFragment.class.getSimpleName();
    private static final String PLAYER_POSITION = "playerPosition";
    private static final String PLAYER_WINDOW = "playerWindow";
    private static final String PLAYER_STATE = "playerState";
    private static final String PLAY_WHEN_READY = "playWhenReady";
    private static final String LIST_STATE = "recyclerViewStatePosition";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private Step step;
    private boolean isLastStep = true;
    private SimpleExoPlayer player;
    private long position;
    private int resumeWindow;
    private int playbackState;
    private boolean playWhenReady;
    private Parcelable listState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearResumePosition();
        if (savedInstanceState != null)
            getPreviousState(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            step = getArguments().getParcelable(ARG_ITEM_ID);
            isLastStep = getArguments().getBoolean(ARG_STEP_IS_LAST, true);
            AppCompatActivity activity = (AppCompatActivity) this.getActivity();
            ActionBar actionBar = activity.getSupportActionBar();
            if (step != null && actionBar != null)
                actionBar.setTitle(step.getShortDescription());
        }
    }

    private void getPreviousState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(PLAYER_POSITION))
            position = savedInstanceState.getLong(PLAYER_POSITION);
        if (savedInstanceState.containsKey(PLAYER_WINDOW))
            resumeWindow = savedInstanceState.getInt(PLAYER_WINDOW);
        if (savedInstanceState.containsKey(PLAYER_STATE))
            playbackState = savedInstanceState.getInt(PLAYER_STATE);
        if (savedInstanceState.containsKey(PLAY_WHEN_READY))
            playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY);
        if (savedInstanceState.containsKey(LIST_STATE))
            listState = savedInstanceState.getParcelable(LIST_STATE);

    }

    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        position = C.TIME_UNSET;
        playbackState = Player.STATE_READY;
        playWhenReady = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new StepAdapter(step));
        if (listState != null && step != null)
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        initPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            position = player.getCurrentPosition();
            resumeWindow = player.getCurrentWindowIndex();
            playbackState = player.getPlaybackState();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    private void initPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        //Initialize the player
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
    }

    public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {
        private SparseArray<StepInfo> layoutPost = new SparseArray<>();

        public StepAdapter(Step step) {
            setSparseArray(step);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(viewType, parent, false);
            ViewHolder viewHolder;
            switch (viewType) {
                case R.layout.step_video_card:
                    viewHolder = new ViewHolderVideo(view);
                    break;
                case R.layout.step_thumbmail_card:
                    viewHolder = new ViewHolderThumbmail(view);
                    break;
                case R.layout.step_navigation_card:
                    viewHolder = new ViewHolderNavigation(view);
                    break;
                default:
                    viewHolder = new ViewHolderDescription(view);
            }
            return viewHolder;
        }

        public class StepInfo {
            public String Str;
            public Integer layout;

            public StepInfo(Integer layout, String str) {
                Str = str;
                this.layout = layout;
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(layoutPost.get(position).Str);
        }

        @Override
        public int getItemViewType(int position) {
            return layoutPost.get(position).layout;
        }

        @Override
        public int getItemCount() {
            return layoutPost.size();
        }

        private int setMapLayout(String str, SparseArray<StepInfo> layoutPost, int pos, int layoutID) {
            if (str != null && !str.isEmpty())
                layoutPost.put(pos++, new StepInfo(layoutID, str));
            return pos;
        }

        public void changeFragment() {
            if (getResources().getBoolean(R.bool.material_responsive_is_tablet)) {
                Step step = ((RecipeStepListActivity) getActivity()).getNextStep();
                if (step != null) {
                    ((RecipeStepListActivity) getActivity()).setFragmentStep(step);
                }
            } else {
                ((RecipeStepActivity) getActivity()).setActivityResult();
                getActivity().finish();
            }
        }

        public void updateList(Step step) {
            setSparseArray(step);
            notifyDataSetChanged();
        }

        private void setSparseArray(Step step) {
            int pos = 0;
            layoutPost = new SparseArray<>();
            if (step != null) {
                pos = setMapLayout(step.getVideoURL(), layoutPost, pos, R.layout.step_video_card);
                pos = setMapLayout(step.getThumbnailURL(), layoutPost, pos, R.layout.step_thumbmail_card);
                pos = setMapLayout(step.getDescription(), layoutPost, pos, R.layout.step_description_card);
                if (isLastStep)
                    Log.d(TAG, "setSparseArray, nextStep is null");
                if (!isLastStep)
                    layoutPost.put(pos, new StepInfo(R.layout.step_navigation_card, null));
            }
        }

        public abstract class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View itemView) {
                super(itemView);
            }

            public abstract void bind(String str);
        }

        public class ViewHolderVideo extends ViewHolder {
            @BindView(R.id.exoPlayerView)
            PlayerView playerView;

            public ViewHolderVideo(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @Override
            public void bind(String str) {
                //Initialize simpleExoPlayerView
                if (player == null)
                    initPlayer();
                playerView.setPlayer(player);

                // Produces DataSource instances through which media data is loaded.
                DataSource.Factory dataSourceFactory =
                        new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), getContext().getString(R.string.app_name)));

                // Produces Extractor instances for parsing the media data.
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

                // This is the MediaSource representing the media to be played.
                Uri videoUri = Uri.parse(str);
                MediaSource videoSource = new ExtractorMediaSource(videoUri,
                        dataSourceFactory, extractorsFactory, null, null);

                // Prepare the player with the source.
                player.prepare(videoSource);
                if (resumeWindow != C.INDEX_UNSET)
                    player.seekTo(resumeWindow, position);
                player.setPlayWhenReady(playWhenReady);
            }
        }

        public class ViewHolderThumbmail extends ViewHolder {
            @BindView(R.id.imageView_card_thumbmail)
            ImageView stepImg;

            public ViewHolderThumbmail(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @Override
            public void bind(String str) {
                if (!TextUtils.isEmpty(str))
                    Picasso.with(getContext())
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

        public class ViewHolderDescription extends ViewHolder {
            @BindView(R.id.textView_step_description)
            TextView desc;

            public ViewHolderDescription(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @Override
            public void bind(String str) {
                desc.setText(str);
            }
        }

        private class ViewHolderNavigation extends ViewHolder implements View.OnClickListener {
            public ViewHolderNavigation(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
            }

            @Override
            public void bind(String str) {

            }

            @Override
            public void onClick(View view) {
                changeFragment();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PLAYER_POSITION, position);
        outState.putInt(PLAYER_WINDOW, resumeWindow);
        outState.putInt(PLAYER_STATE, playbackState);
        outState.putBoolean(PLAY_WHEN_READY, playWhenReady);
        outState.putParcelable(LIST_STATE, recyclerView.getLayoutManager().onSaveInstanceState());

    }
}

