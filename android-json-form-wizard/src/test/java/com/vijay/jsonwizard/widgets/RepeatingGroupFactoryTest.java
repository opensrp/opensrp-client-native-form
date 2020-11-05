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
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class RepeatingGroupFactoryTest extends FactoryTest {

    private RepeatingGroupFactory factory;
    private JSONObject step;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        factory = new RepeatingGroupFactory();
    }

    @Test
    public void testRepeatingGroupFactoryInstantiatesViewsCorrectly() throws Exception {
        List<View> viewList = invokeGetViewsFromJson();
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());

        // invoke repeating group generation when reference edit text loses focus
        MaterialEditText referenceEditText = viewList.get(0).findViewById(R.id.reference_edit_text);
        referenceEditText.requestFocus();
        referenceEditText.clearFocus();
    }

    @Test
    public void testRepeatingGroupCountIsSaved() throws Exception {
        View rootLayout = invokeGetViewsFromJson().get(0);
        ((MaterialEditText) rootLayout.findViewById(R.id.reference_edit_text)).setText("2");

        JSONObject repeatingGroupCountObj = step.getJSONArray(JsonFormConstants.FIELDS).getJSONObject(0);
        Assert.assertEquals("2", repeatingGroupCountObj.getString(JsonFormConstants.VALUE));
    }

    @Test
    public void testParseIntWithDefaultIntegerInput() {
        final String integerString = "1";
        final int integerFromString = 1;

        Assert.assertThat(RepeatingGroupFactory.parseIntWithDefault(integerString), is(integerFromString));
    }

    @Test
    public void testParseIntWithDefaultNullInput() {
        final String emptyString = "";
        final int defaultInteger = 0;

        Assert.assertThat(RepeatingGroupFactory.parseIntWithDefault(emptyString), is(defaultInteger));
    }

    @Test
    public void testSetRepeatingGroupNumLimits() {
        RepeatingGroupFactory factorySpy = Mockito.spy(factory);
        String stepName = "step_name";
        Context context = mock(Context.class);
        JsonFormFragment formFragment = mock(JsonFormFragment.class);
        JSONObject jsonObject = new JSONObject();
        CommonListener listener = mock(CommonListener.class);
        boolean popup = false;

        WidgetArgs widgetArgs = new WidgetArgs();
        widgetArgs.withContext(context)
                .withFormFragment(formFragment)
                .withJsonObject(jsonObject)
                .withListener(listener)
                .withPopup(popup)
                .withStepName(stepName);

        factorySpy.setRepeatingGroupNumLimits(widgetArgs);
        Assert.assertEquals(widgetArgs.getJsonObject().optInt("repeating_group_min", 0), factorySpy.MIN_NUM_REPEATING_GROUPS);
        Assert.assertEquals(widgetArgs.getJsonObject().optInt("repeating_group_max", 35), factorySpy.MAX_NUM_REPEATING_GROUPS);
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        RepeatingGroupFactory factorySpy = Mockito.spy(factory);

        Set<String> editableProperties = factorySpy.getCustomTranslatableWidgetFields();
        Assert.assertEquals(1, editableProperties.size());
    }

    @Test
    public void testUniqueChildElementKeyGenerationShouldContainChildKeyAsComponent() throws JSONException {
        final String parentRepeatingGroupWidgetKey = "parent";
        final String childElementKey = "child";
        final String uniqueId = "unique";

        JSONObject parentRepeatingGroupWidget = new JSONObject();
        parentRepeatingGroupWidget.put(JsonFormConstants.KEY, parentRepeatingGroupWidgetKey);
        WidgetArgs widgetArgs = new WidgetArgs().withJsonObject(parentRepeatingGroupWidget);

        JSONObject childElement = new JSONObject();
        childElement.put(JsonFormConstants.KEY, childElementKey);

        AttachRepeatingGroupTask attachRepeatingGroupTask = new AttachRepeatingGroupTask(Mockito.mock(LinearLayout.class),
                0, new HashMap<Integer, String>(), widgetArgs, Mockito.mock(ImageButton.class));

        ReflectionHelpers.callInstanceMethod(attachRepeatingGroupTask,
                "addUniqueIdentifiers",
                ReflectionHelpers.ClassParameter.from(JSONObject.class, childElement),
                ReflectionHelpers.ClassParameter.from(String.class, uniqueId));

        String[] uniqueIdComponents = childElement.getString(JsonFormConstants.KEY).split("_");
        Assert.assertEquals(childElementKey, uniqueIdComponents[0]);
    }

    private List<View> invokeGetViewsFromJson() throws Exception {
        step = new JSONObject();
        JSONArray fields = new JSONArray();
        step.put(JsonFormConstants.FIELDS, fields);
        Mockito.doReturn(step).when(jsonFormActivity).getStep(ArgumentMatchers.anyString());

        JSONObject repeatingGroupWidget =  new JSONObject();
        repeatingGroupWidget.put(JsonFormConstants.KEY, "key");
        repeatingGroupWidget.put(JsonFormConstants.VALUE, new JSONArray());
        repeatingGroupWidget.put(RepeatingGroupFactory.REFERENCE_EDIT_TEXT_HINT, "text");
        return factory.getViewsFromJson("step1", jsonFormActivity, Mockito.mock(JsonFormFragment.class),
               repeatingGroupWidget, Mockito.mock(CommonListener.class));
    }
}
