package com.example.pablo.bakingapp.bases;

import com.example.pablo.bakingapp.data.DataManager;

import java.lang.ref.WeakReference;

/**
 * Created by pablo on 02/03/2018.
 */

public class BasePresenter<E extends IMVPView> implements IMVPPresenter<E> {

    private WeakReference<E> mMvpViewRef;
    private boolean isTablet;

    protected E getMvpView() {
        return mMvpViewRef.get();
    }

    public BasePresenter(){
    }

    public void setIsTablet(boolean isTablet){
        this.isTablet = isTablet;
    }

    public boolean isTablet(){
        return isTablet;
    }

    @Override
    public void onAttach(E mvpView) {
            mMvpViewRef = new WeakReference<>(mvpView);
    }

    @Override
    public void onDetach() {
        mMvpViewRef = null;
    }

    @Override
    public boolean isViewAttached(){
        return mMvpViewRef != null;
    }

    public void checkViewAttached() {
        if (!isViewAttached())
            throw new MvpViewNotAttachedException();
    }

    protected DataManager getDataManager(){
        return DataManager.getInstance();
    }

    public static class NullAPIException extends Exception {

        public NullAPIException() {
            super();
        }

        public NullAPIException(String message) {
            super(message);
        }

        public NullAPIException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
