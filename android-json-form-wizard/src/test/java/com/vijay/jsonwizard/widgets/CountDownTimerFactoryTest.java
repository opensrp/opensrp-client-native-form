package com.vijay.jsonwizard.widgets;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.emredavarci.circleprogressbar.CircleProgressBar;
import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.List;

public class CountDownTimerFactoryTest extends BaseTest {
    private CountDownTimerFactory factory;
    @Mock
    private JsonFormActivity context;

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private Resources resources;

    @Mock
    private CommonListener listener;

    @Mock
    private View rootLayout;

    @Mock
    private TextView labelView;
    @Mock
    private CircleProgressBar progressBar;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new CountDownTimerFactory();
    }

    @Test
    public void testCountDownTimerFactoryInstantiatesViewsCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        CountDownTimerFactory factorySpy = Mockito.spy(factory);

        Mockito.doReturn(resources).when(context).getResources();
        Assert.assertNotNull(resources);

        context.setTheme(R.style.NativeFormsAppTheme);
        Mockito.doReturn(rootLayout).when(factorySpy).getRootView(context);
        Mockito.doReturn(labelView).when(rootLayout).findViewById(R.id.timerLabel);
        Mockito.doReturn(progressBar).when(rootLayout).findViewById(R.id.progressCircularBar);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        String countDownTimer = "{\"key\":\"countdown_timer\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"timer\",\"type\":\"countdown_timer\",\"label\":\"Read results in 10 seconds.\",\"label_text_size\":\"8sp\",\"label_text_color\":\"#535F67\",\"countdown_time_unit\":\"seconds\",\"countdown_time_value\":\"10\",\"countdown_interval\":\"1\",\"progressbar_background_color\":\"#e7b330\",\"progressbar_color\":\"#e76130\",\"progressbar_text_color\":\"#f9916b\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-relevance-rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-constraints-rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-calculation-rules.yml\"}}}}";
        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, new JSONObject(countDownTimer), listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());
    }

    @Test
    public void testGetFormattedTimeText() throws Exception {
        Assert.assertNotNull(factory);
        CountDownTimerFactory factorySpy = Mockito.spy(factory);

        String timeText = Whitebox.invokeMethod(factorySpy, "getFormattedTimeText", 20000L);
        Assert.assertEquals("00:20", timeText);
    }
}
