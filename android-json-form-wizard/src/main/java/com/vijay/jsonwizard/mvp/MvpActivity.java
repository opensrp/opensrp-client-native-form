package com.vijay.jsonwizard.mvp;

import android.os.Bundle;

public abstract class MvpActivity<P extends MvpPresenter, V extends ViewState> extends BaseActivity<V> implements
        MvpView {

    protected P presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = createPresenter();
        presenter.attachView(this);
    }

    protected abstract P createPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView(false);
    }
}