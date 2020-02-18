package com.vijay.jsonwizard.widgets;

import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.List;
import java.util.Map;

public class NativeRadioButtonFactoryTest extends BaseTest {
    private NativeRadioButtonFactory factory;
    private  FormUtils formUtils;
    @Mock
    private JsonFormActivity context;

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private Resources resources;

    @Mock
    private CommonListener listener;

    @Mock
    private LinearLayout rootLayout;

    @Mock
    private ConstraintLayout constraintLayout;

    @Mock
    private RadioGroup radioGroup;

    @Mock
    private RelativeLayout radioGroupLayout;

    @Mock
    private RelativeLayout radioGroupLayoutTwo;

    @Mock
    private ImageView imageView;

    @Mock
    private RadioButton radioButton;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new NativeRadioButtonFactory();
        formUtils = new FormUtils();
    }

    @Ignore
    @Test
    public void testNativeRadioButtonFactoryInstantiatesViewsCorrectly() throws Exception {
        String nativeRadioButtonString = "{\"key\":\"respiratory_exam\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Respiratory exam\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"extra_rel\":true,\"has_extra_rel\":\"3\",\"options\":[{\"key\":\"1\",\"text\":\"Not done\",\"openmrs_entity_parent\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"2\",\"text\":\"Normal\",\"openmrs_entity_parent\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1115AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"3\",\"text\":\"Abnormal\",\"specify_info\":\"specify...\",\"specify_widget\":\"check_box\",\"specify_info_color\":\"#8C8C8C\",\"content_form\":\"respiratory_exam_sub_form\",\"openmrs_entity_parent\":\"165367AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1116AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}";
        JSONObject nativeRadioButtonObject = new JSONObject(nativeRadioButtonString);
        Assert.assertNotNull(nativeRadioButtonString);

        Assert.assertNotNull(factory);
        NativeRadioButtonFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        Assert.assertNotNull(formUtils);
        FormUtils formUtilsSpy = Mockito.spy(formUtils);
        Assert.assertNotNull(formUtilsSpy);

        Mockito.doReturn(rootLayout).when(factorySpy).getLinearRootLayout(context);
        Assert.assertNotNull(rootLayout);

        Mockito.doReturn(radioGroup).when(factorySpy).getRadioGroup(ArgumentMatchers.eq(nativeRadioButtonObject), ArgumentMatchers.eq(context), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyString());
        Assert.assertNotNull(radioGroup);

        Mockito.doReturn(radioGroupLayoutTwo).when(factorySpy).getRadioGroupLayout(context);
        Assert.assertNotNull(radioGroupLayoutTwo);

        Mockito.doReturn(radioGroupLayout).when(factorySpy).getRadioGroupLayout(ArgumentMatchers.eq(nativeRadioButtonObject), ArgumentMatchers.eq(context), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(new JSONObject()));
        Assert.assertNotNull(radioGroupLayout);

        Mockito.doReturn(imageView).when(radioGroupLayout).findViewById(R.id.info_icon);
        Assert.assertNotNull(imageView);

        Mockito.doReturn(radioButton).when(radioGroupLayout).findViewById(R.id.mainRadioButton);
        Assert.assertNotNull(radioButton);

        Mockito.doReturn(constraintLayout).when(formUtilsSpy).getRootConstraintLayout(context);
        Assert.assertNotNull(constraintLayout);

        Mockito.doReturn(resources).when(context).getResources();
        Assert.assertNotNull(resources);

        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, nativeRadioButtonObject, listener);
        Assert.assertNotNull(viewList);
        Assert.assertTrue(viewList.size() > 0);

    }

    @Test
    public void testCreateSpecifyTextWhenTextIsSet() throws Exception {
        String result = Whitebox.invokeMethod(new NativeRadioButtonFactory(), "createSpecifyText", "test");
        Assert.assertEquals("(test)", result);
    }

    @Test
    public void testCreateSpecifyTextWhenTextIsEmpty() throws Exception {
        String result = Whitebox.invokeMethod(new NativeRadioButtonFactory(), "createSpecifyText", "");
        Assert.assertEquals("", result);
    }

    @Test
    public void testCreateSpecifyTextWhenTextIsNull() throws Exception {
        String result = Whitebox.invokeMethod(new NativeRadioButtonFactory(), "createSpecifyText", (Object) null);
        Assert.assertEquals("", result);
    }
}
