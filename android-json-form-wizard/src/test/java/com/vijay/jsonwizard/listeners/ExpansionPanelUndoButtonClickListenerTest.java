package com.vijay.jsonwizard.listeners;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.shadow.ShadowUtils;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.widgets.FactoryTest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.util.ReflectionHelpers;

/**
 * Created by Vincent Karuri on 07/09/2020
 */

@Config(shadows = {ShadowUtils.class})
public class ExpansionPanelUndoButtonClickListenerTest extends FactoryTest {

    private ExpansionPanelUndoButtonClickListener expansionPanelUndoButtonClickListener;
    private JSONObject field;
    private JSONObject mJsonObject;

    @Before
    public void setUp() {
        super.setUp();
        bootStrapPanel();
    }

    @Test
    public void testOnClickShouldShowExpansionPanel() {
        callOnClick();

        AlertDialog alertDialog = Mockito.spy(ShadowAlertDialog.getLatestAlertDialog());
        Assert.assertTrue(alertDialog.isShowing());
    }

    @Test
    public void testExpansionPanelButtonsShouldPerformCorrectAction() {
        callOnClick();

        AlertDialog alertDialog = (AlertDialog) Mockito.spy(ShadowAlertDialog.getShownDialogs().get(0));
        alertDialog.findViewById(R.id.undo_button).performClick();

        Mockito.verify(field).remove(ArgumentMatchers.eq(JsonFormConstants.VALUE));
        Mockito.verify(field).remove(ArgumentMatchers.eq(JsonFormConstants.REQUIRED_FIELDS));
        Mockito.verify(jsonFormActivity).setmJSONObject(ArgumentMatchers.eq(mJsonObject));
    }

    private void bootStrapPanel() {
        try {
            expansionPanelUndoButtonClickListener = new ExpansionPanelUndoButtonClickListener();

            mJsonObject = new JSONObject();

            JSONObject global = new JSONObject();
            global.put("key1", "value1");
            global.put("key3", "value3");
            mJsonObject.put(JsonFormConstants.GLOBAL, global);

            Mockito.doReturn(mJsonObject).when(jsonFormActivity).getmJSONObject();

            FormUtils formUtils = Mockito.mock(FormUtils.class);
            JSONArray fields = new JSONArray();
            field = Mockito.spy(new JSONObject());
            field.put(JsonFormConstants.KEY, JsonFormConstants.KEY);
            field.put(JsonFormConstants.VALUE, JsonFormConstants.VALUE);
            field.put(JsonFormConstants.TEXT, "header");

            JSONArray selectedValues = new JSONArray();
            selectedValues.put("key1");
            selectedValues.put("key2");
            field.put(JsonFormConstants.VALUE, selectedValues);

            fields.put(field);
            Mockito.doReturn(fields).when(formUtils).getFormFields(ArgumentMatchers.anyString(),
                    ArgumentMatchers.any(Context.class));

            ReflectionHelpers.setField(expansionPanelUndoButtonClickListener, "formUtils", formUtils);
        } catch (Exception e) {
            // do nothing
        }
    }

    private void callOnClick() {
        View view = new View(RuntimeEnvironment.application);
        view.setTag(R.id.key, JsonFormConstants.KEY);
        view.setTag(R.id.specify_context, jsonFormActivity);
        view.setTag(R.id.specify_step_name, "step1");
        view.setTag(R.id.linearLayout, new LinearLayout(RuntimeEnvironment.application));
        expansionPanelUndoButtonClickListener.onClick(view);
    }
}