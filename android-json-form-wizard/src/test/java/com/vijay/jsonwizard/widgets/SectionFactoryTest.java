package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.task.AttachRepeatingGroupTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class SectionFactoryTest extends FactoryTest {

    private SectionFactory factory;
    private JSONObject step;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        factory = new SectionFactory();
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        SectionFactory factorySpy = Mockito.spy(factory);

        Set<String> editableProperty = factorySpy.getCustomTranslatableWidgetFields();
        Assert.assertEquals(new HashSet<>(), editableProperty);
    }
}
