package com.vijay.jsonwizard.customviews;

import android.app.Activity;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

/**
 * Created by abdulwahabmemon on 2021-08-10.
 */
public class MaterialSpinnerTest extends BaseTest {

    @Mock
    private Activity activity;

    private MaterialSpinner materialSpinner;
    private AttributeSet attributeSet;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        attributeSet = Robolectric.buildAttributeSet().addAttribute(R.attr.theme, "AppTheme").build();
    }

    @Test
    public void testMaterialSpinnerConstructorInitializationShouldReturnNonNull() {
        //First constructor
        materialSpinner = new MaterialSpinner(RuntimeEnvironment.application);
        Assert.assertNotNull(materialSpinner);

        //Second constructor
        materialSpinner = new MaterialSpinner(RuntimeEnvironment.application, attributeSet);
        Assert.assertNotNull(materialSpinner);

        //third constructor
        materialSpinner = new MaterialSpinner(RuntimeEnvironment.application, attributeSet, R.style.AppTheme);
        Assert.assertNotNull(materialSpinner);
    }

    @Test
    public void testMaterialSpinnerWithDataSet() {

        materialSpinner = new MaterialSpinner(RuntimeEnvironment.application);

        String[] values = new String[3];
        values[0] = "Select Gender";
        values[1] = "Male";
        values[2] = "Female";

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.native_form_simple_list_item_1, values);
        materialSpinner.setAdapter(adapter);
        materialSpinner.setDataList(values);

        Assert.assertEquals(3, materialSpinner.getAdapter().getCount());
    }
}
