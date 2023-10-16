package com.vijay.jsonwizard.fragments;


import android.view.View;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

public class JsonFormFragmentTest extends BaseTest {

    @Mock
    private JsonFormFragmentPresenter presenter;

    @Mock
    private LinearLayout mView;

    @Test
    public void testOnDestroyInvokesPresenterCleanUpAtleastOnce() {

        JsonFormFragment jsonFormFragment = Mockito.spy(new JsonFormFragment());
        Mockito.doReturn(presenter).when(jsonFormFragment).getPresenter();

        jsonFormFragment.onDestroy();

        Mockito.verify(presenter, Mockito.atLeastOnce()).cleanUp();

    }

    @Test
    public void testSave()
    {
        JsonFormFragment jsonFormFragment = Mockito.spy(new JsonFormFragment());
        jsonFormFragment.mMainView= mView;
        Mockito.doReturn(presenter).when(jsonFormFragment).getPresenter();
        Mockito.doNothing().when(mView).setTag(ArgumentMatchers.any());
        Mockito.doNothing().when(presenter).onSaveClick(ArgumentMatchers.any());

        boolean result =  jsonFormFragment.save(true);
        Assert.assertEquals(result,false);




    }
}