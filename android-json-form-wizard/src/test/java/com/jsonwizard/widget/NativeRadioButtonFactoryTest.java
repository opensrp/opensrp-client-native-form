package com.jsonwizard.widget;

import android.content.Context;
import android.view.View;

import com.jsonwizard.BaseTest;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.widgets.NativeRadioButtonFactory;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class NativeRadioButtonFactoryTest extends BaseTest {

    @Mock
    private List<View> views;

    @Mock
    private Context context;

    private String json = "{" +
            "\"key\": \"native_radio\"," +
            "\"openmrs_entity_parent\": \"\"," +
            " \"openmrs_entity\": \"\"," +
            "\"openmrs_entity_id\": \"\"," +
            "\"type\": \"native_radio\"," +
            "\"label\": \"Highest Level of School\"," +
            "\"label_text_size\": \"20sp\"," +
            "\"label_text_color\":\"#FF9800\"," +
            "\"options\": [" +
            "{\"key\": \"primary_school\"," +
            "\"text\": \"Primary school\"," +
            "\"text_color\":\"#000000\"}," +
            "{\"key\": \"high_school\"," +
            "\"text\": \"High School\"," +
            "\"text_size\":\"30sp\"}," +
            "{\"key\": \"higher_education\"," +
            " \"text\": \"College/University\"," +
            "\"text_color\":\"#358CB7\"}" +
            "],\"value\": \"primary_school\"}";

    @Mock
    private CommonListener commonListener;

    @Mock
    private JsonFormFragment jsonFormFragment;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        context = RuntimeEnvironment.application;
    }

    @Test
    public void testGetViewsFromJson() throws Exception {
        NativeRadioButtonFactory nativeRadioButtonFactory = Mockito.spy(NativeRadioButtonFactory.class);

        JSONObject jsonObject = new JSONObject(json);

        Mockito.doReturn(views).when(nativeRadioButtonFactory).getViewsFromJson("", context, jsonFormFragment, jsonObject,
                commonListener);
        Assert.assertNotNull(views);

        nativeRadioButtonFactory.getViewsFromJson("",context,jsonFormFragment,jsonObject,commonListener);
    }

}
