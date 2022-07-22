package com.vijay.jsonwizard.fragments;


import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class JsonFormFragmentTest extends BaseTest {

    @Mock
    private JsonFormFragmentPresenter presenter;

    @Test
    public void testOnDestroyInvokesPresenterCleanUpAtleastOnce() {

        JsonFormFragment jsonFormFragment = Mockito.spy(new JsonFormFragment());
        Mockito.doReturn(presenter).when(jsonFormFragment).getPresenter();

        jsonFormFragment.onDestroy();

        Mockito.verify(presenter, Mockito.atLeastOnce()).cleanUp();

    }
}