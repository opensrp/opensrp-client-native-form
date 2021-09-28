package com.vijay.jsonwizard.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.widgets.SpinnerFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

/**
 * Created by abdulwahabmemon on 2021-08-10.
 */
public class MaterialSpinnerTest extends BaseTest {

    private AttributeSet attributeSet;

    @Mock
    private MaterialSpinner materialSpinner;

    private SpinnerFactory spinnerFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spinnerFactory = Mockito.spy(new SpinnerFactory());
        attributeSet = Robolectric.buildAttributeSet().addAttribute(R.attr.theme, "AppTheme").build();
    }

    @Test
    public void testMaterialSpinnerConstructorInitializationShouldReturnNonNull() {
        Context context = Mockito.spy(RuntimeEnvironment.application);

        //First constructor
        materialSpinner = new MaterialSpinner(context);
        Assert.assertNotNull(materialSpinner);

        //Second constructor
        materialSpinner = new MaterialSpinner(context, attributeSet);
        Assert.assertNotNull(materialSpinner);

        //third constructor
        materialSpinner = new MaterialSpinner(context, attributeSet, R.style.AppTheme);
        Assert.assertNotNull(materialSpinner);
    }

    @Test
    public void testMaterialSpinnerWithDataSet() {
        Context context = Mockito.spy(RuntimeEnvironment.application);

        String[] values = new String[3];
        values[0] = "Select Gender";
        values[1] = "Male";
        values[2] = "Female";

        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.native_form_item_spinner, null);

        materialSpinner = spinnerFactory.getMaterialSpinner(relativeLayout);
        Assert.assertNotNull(materialSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.native_form_simple_list_item_1, values);
        materialSpinner.setAdapter(adapter);

        Assert.assertEquals(3, materialSpinner.getAdapter().getCount());
        Assert.assertEquals("Male", materialSpinner.getAdapter().getItem(1).toString());
    }
}
