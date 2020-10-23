package com.vijay.jsonwizard.widgets;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.task.AttachRepeatingGroupTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.annotation.LooperMode;
import org.robolectric.util.ReflectionHelpers;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static android.os.Looper.getMainLooper;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@LooperMode(PAUSED)
public class RepeatingGroupFactoryTest extends FactoryTest {

    private RepeatingGroupFactory factory;

    private JSONObject step;

    @Mock
    private JsonFormFragment jsonFormFragment;

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
        Mockito.doReturn(jsonFormActivity).when(jsonFormFragment).getContext();
        Mockito.doReturn(jsonFormActivity).when(jsonFormFragment).getJsonApi();
        JsonFormFragmentPresenter jsonFormFragmentPresenter = Mockito.spy(new JsonFormFragmentPresenter(jsonFormFragment));
        Mockito.doReturn("Step title").when(jsonFormFragmentPresenter).getStepTitle();

        JSONObject stepJsonObject = new JSONObject();
        stepJsonObject.put(JsonFormConstants.FIELDS, new JSONArray());
        Mockito.doReturn(stepJsonObject).when(jsonFormFragment).getStep(JsonFormConstants.STEP1);
        Mockito.doReturn(jsonFormFragmentPresenter).when(jsonFormFragment).getPresenter();

        View rootLayout = invokeGetViewsFromJson().get(0);
        ((MaterialEditText) rootLayout.findViewById(R.id.reference_edit_text)).setText("2");


        (rootLayout.findViewById(R.id.btn_repeating_group_done)).performClick();
        shadowOf(getMainLooper()).idle();
        Thread.sleep(TIMEOUT);

        JSONObject repeatingGroupCountObj = stepJsonObject.getJSONArray(JsonFormConstants.FIELDS).getJSONObject(0);
        Assert.assertEquals("2", repeatingGroupCountObj.getString(JsonFormConstants.VALUE));
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

        JSONObject repeatingGroupWidget = new JSONObject();
        repeatingGroupWidget.put(JsonFormConstants.KEY, "key");
        repeatingGroupWidget.put(JsonFormConstants.VALUE, new JSONArray());
        repeatingGroupWidget.put(RepeatingGroupFactory.REFERENCE_EDIT_TEXT_HINT, "text");
        return factory.getViewsFromJson("step1", jsonFormActivity, jsonFormFragment,
                repeatingGroupWidget, Mockito.mock(CommonListener.class));
    }
}
