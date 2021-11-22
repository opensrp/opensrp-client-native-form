package com.vijay.jsonwizard.mvp;

import com.vijay.jsonwizard.activities.BaseActivityTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class MvpActivityTest extends BaseActivityTest {

    private TestMvpActivity mvpActivity;

    private ActivityController<TestMvpActivity> activityController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activityController = Robolectric.buildActivity(TestMvpActivity.class);
    }

    @Test
    public void testOnCreateShouldInvokeExpectedMethods() {
        mvpActivity = spy(activityController.create().get());
        MvpPresenter mvpPresenter = ReflectionHelpers.getField(mvpActivity, "presenter");
        verify(mvpPresenter).attachView(any(MvpView.class));
        mvpActivity.finish();
        activityController.destroy();
    }

    @Test
    public void testOnDestroyShouldDetachView() {
        mvpActivity = spy(activityController.create().destroy().get());
        MvpPresenter mvpPresenter = ReflectionHelpers.getField(mvpActivity, "presenter");
        verify(mvpPresenter).detachView(eq(false));
    }

    public static class TestMvpActivity extends MvpActivity {

        @Override
        protected MvpPresenter createPresenter() {
            return mock(MvpBasePresenter.class);
        }

        @Override
        protected ViewState createViewState() {
            return mock(ViewState.class);
        }
    }
}