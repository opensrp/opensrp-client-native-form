package com.vijay.jsonwizard.listeners;

import android.content.Context;
import android.view.View;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.widgets.FactoryTest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.util.ReflectionHelpers;

/**
 * Created by Vincent Karuri on 07/09/2020
 */
public class ExpansionPanelUndoButtonClickListenerTest extends FactoryTest {

    private ExpansionPanelUndoButtonClickListener expansionPanelUndoButtonClickListener;

    @Before
    public void setUp() {
        super.setUp();
        expansionPanelUndoButtonClickListener = new ExpansionPanelUndoButtonClickListener();
    }

    @Test
    public void onClickShouldSetUpExpansionPanel() throws JSONException {
        final String KEY = "key";
        final String VALUE = "value";

        View view = new View(RuntimeEnvironment.application);
        view.setTag(R.id.key, KEY);
        view.setTag(R.id.specify_context, jsonFormActivity);
        view.setTag(R.id.specify_step_name, "step1");

        FormUtils formUtils = Mockito.mock(FormUtils.class);
        JSONArray fields = new JSONArray();
        JSONObject field = new JSONObject();
        field.put(JsonFormConstants.KEY, KEY);
        field.put(JsonFormConstants.VALUE, VALUE);
        field.put(JsonFormConstants.TEXT, "header");
        fields.put(field);
        Mockito.doReturn(fields).when(formUtils).getFormFields(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Context.class));

        ReflectionHelpers.setField(expansionPanelUndoButtonClickListener, "formUtils", formUtils);
        expansionPanelUndoButtonClickListener.onClick(view);

        Assert.assertNotNull(ShadowAlertDialog.getLatestAlertDialog());
    }
}