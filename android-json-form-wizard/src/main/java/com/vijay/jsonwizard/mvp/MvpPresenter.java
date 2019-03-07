package com.vijay.jsonwizard.mvp;

public interface MvpPresenter<V extends MvpView> {

    void attachView(V view);

    void detachView(boolean retainInstance);
}
