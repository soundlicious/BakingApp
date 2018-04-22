package com.example.pablo.bakingapp.bases;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import butterknife.Unbinder;

/**
 * Created by pablo on 12/03/2018.
 */

public class BaseFragment extends android.support.v4.app.Fragment implements IMVPView {

    private BaseActivity activity;
    private Unbinder unBinder;
    private OnFragmentInteractionListener mListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        activity = null;
    }

    public BaseActivity getBaseActivity() {
        return activity;
    }

    public void setUnBinder(Unbinder unBinder) {
        unBinder = unBinder;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(String message) {
        if (activity != null) {
            activity.onError(message);
        }
    }

    @Override
    public void onError(@StringRes int resId) {
        if (activity != null) {
            activity.onError(resId);
        }
    }

    @Override
    public void showMessage(String message) {
        if (activity != null) {
            activity.showMessage(message);
        }
    }

    @Override
    public void showMessage(@StringRes int resId) {
        if (activity != null) {
            activity.showMessage(resId);
        }
    }


    @Override
    public boolean isNetworkOn() {
        if (activity != null) {
            return activity.isNetworkOn();
        }
        return false;
    }

    @Override
    public void showRefreshButton() {

    }

    @Override
    public void hideRefreshButton() {

    }

    @Override
    public void onDestroy() {
        if (unBinder != null) {
            unBinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if (unBinder != null) {
            unBinder.unbind();
        }
        super.onDestroyView();
    }

    public interface OnFragmentInteractionListener {

        void onFragmentAttached();

        void onFragmentDetached(String tag);
    }
}
