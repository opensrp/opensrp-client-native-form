package com.vijay.jsonwizard.presenters;

import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.TestConstants;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormErrorFragment;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.OnFieldsInvalid;
import com.vijay.jsonwizard.shadow.ShadowContextCompat;
import com.vijay.jsonwizard.shadow.ShadowPermissionUtils;
import com.vijay.jsonwizard.utils.AppExecutors;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import org.robolectric.shadows.ShadowToast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.os.Looper.getMainLooper;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter.RESULT_LOAD_IMG;
import static com.vijay.jsonwizard.utils.PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

/**
 * Created by samuelgithengi on 3/3/20.
 */
@LooperMode(PAUSED)
public class JsonFormFragmentPresenterRoboElectricTest extends BaseTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private JsonFormInteractor jsonFormInteractor;
    @Mock
    private JsonFormActivity jsonFormActivity;

    @Captor
    private ArgumentCaptor<JSONObject> jsonArgumentCaptor;

    @Captor
    private ArgumentCaptor<Intent> intentArgumentCaptor;

    @Mock
    private JsonFormErrorFragment errorFragment;

    @Mock
    private OnFieldsInvalid onFieldsInvalid;

    private JsonFormFragmentPresenter presenter;

    private JSONObject mStepDetails;

    private View textView;

    private Context context = RuntimeEnvironment.application;

    private AppExecutors appExecutors;


    @Before
    public void setUp() throws JSONException {
        when(formFragment.getJsonApi()).thenReturn(jsonFormActivity);
        formFragment.onFieldsInvalid = onFieldsInvalid;
        presenter = new JsonFormFragmentPresenter(formFragment, jsonFormInteractor);
        Whitebox.setInternalState(presenter, "viewRef", new WeakReference<>(formFragment));
        textView = new TextView(context);
        JSONObject jsonForm = new JSONObject(TestConstants.PAOT_TEST_FORM);
        mStepDetails = jsonForm.getJSONObject(STEP1);
        when(jsonFormActivity.getmJSONObject()).thenReturn(jsonForm);
        when(formFragment.getContext()).thenReturn(context);
        AppExecutors myAppExecutors = new AppExecutors();
        appExecutors = new AppExecutors(myAppExecutors.mainThread(), myAppExecutors.mainThread(), myAppExecutors.mainThread());
        when(jsonFormActivity.getAppExecutors()).thenReturn(appExecutors);
    }

    private void initWithActualForm() throws InterruptedException {
        Intent intent = new Intent();
        intent.putExtra("json", TestConstants.BASIC_FORM);
        jsonFormActivity = spy(Robolectric.buildActivity(JsonFormActivity.class, intent).create().resume().get());
        when(jsonFormActivity.getAppExecutors()).thenReturn(appExecutors);
        formFragment = spy(JsonFormFragment.getFormFragment("step1"));
        jsonFormActivity.getSupportFragmentManager().beginTransaction().add(formFragment, null).commit();
        shadowOf(getMainLooper()).idle();
        formFragment.onFieldsInvalid = this.onFieldsInvalid;
        presenter = formFragment.getPresenter();
        shadowOf(getMainLooper()).idle();
        Thread.sleep(TIMEOUT);
    }

    @Test
    public void testAddFormElements() {
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, STEP1);
        when(formFragment.getArguments()).thenReturn(bundle);
        when(formFragment.getStep(STEP1)).thenReturn(mStepDetails);
        List<View> views = Collections.singletonList(textView);
        when(jsonFormInteractor.fetchFormElements(anyString(), any(JsonFormFragment.class), any(JSONObject.class), isNull(CommonListener.class), anyBoolean())).thenReturn(views);
        presenter.addFormElements();
        Shadows.shadowOf(getMainLooper()).idle();
        verify(jsonFormInteractor, timeout(TIMEOUT)).fetchFormElements(eq(STEP1), eq(formFragment), jsonArgumentCaptor.capture(), isNull(CommonListener.class), eq(false));
        assertEquals(mStepDetails.toString(), jsonArgumentCaptor.getValue().toString());
        verify(formFragment, timeout(TIMEOUT)).addFormElements(views);
    }

    @Test
    public void testSetUpToolBarForBottomNavigation() throws JSONException {
        Whitebox.setInternalState(presenter, "mStepDetails", mStepDetails);
        mStepDetails.put("bottom_navigation", true);
        presenter.setUpToolBar();
        verify(formFragment).updateVisibilityOfNextAndSave(false, false);

    }

    @Test
    public void testSetUpToolBarForStep2AndMore() {
        Whitebox.setInternalState(presenter, "mStepDetails", mStepDetails);
        Whitebox.setInternalState(presenter, "mStepName", "step2");
        presenter.setUpToolBar();
        verify(formFragment).setUpBackButton();

    }


    @Test
    public void testSetUpToolBarIfStepHasNext() throws JSONException {
        Whitebox.setInternalState(presenter, "mStepDetails", mStepDetails);
        Whitebox.setInternalState(presenter, "mStepName", "step2");
        mStepDetails.put("next", true);
        presenter.setUpToolBar();
        verify(formFragment).updateVisibilityOfNextAndSave(true, false);

    }


    @Test
    public void testSetUpToolBarForLastLastStep() {
        Whitebox.setInternalState(presenter, "mStepDetails", mStepDetails);
        Whitebox.setInternalState(presenter, "mStepName", "step2");
        presenter.setUpToolBar();
        verify(formFragment).updateVisibilityOfNextAndSave(false, true);

    }

    @Test
    public void testOnBackClick() {
        presenter.onBackClick();
        verify(formFragment).hideKeyBoard();
        verify(formFragment).backClick();

    }

    @Test
    public void testGetIncorrectlyFormattedFields() {
        assertEquals(new ArrayList<>(), presenter.getIncorrectlyFormattedFields());
        Stack<String> stack = new Stack<>();
        stack.push("field1");
        Whitebox.setInternalState(presenter, "incorrectlyFormattedFields", stack);
        assertEquals(stack, presenter.getIncorrectlyFormattedFields());
    }


    @Test
    public void testSetErrorFragment() {
        presenter.setErrorFragment(errorFragment);
        assertEquals(errorFragment, presenter.getErrorFragment());
    }

    @Test
    public void testOnNextClickReturnsFalseIfFormIsInvalid() {
        Whitebox.setInternalState(presenter, "mStepDetails", mStepDetails);
        assertFalse(presenter.onNextClick(null));
    }

    @Test
    public void testValidateAndWriteValuesWithInvalidFields() throws InterruptedException {
        initWithActualForm();
        presenter.validateAndWriteValues();
        shadowOf(getMainLooper()).idle();
        assertEquals(4, presenter.getInvalidFields().size());
        assertEquals("Please enter the last name", presenter.getInvalidFields().get("step1#Basic Form One:user_last_name").getErrorMessage());
        assertEquals("Please enter user age", presenter.getInvalidFields().get("step1#Basic Form One:user_age").getErrorMessage());
        assertEquals("Please enter the first name", presenter.getInvalidFields().get("step1#Basic Form One:user_first_name").getErrorMessage());
        assertEquals("Please enter the sex", presenter.getInvalidFields().get("step1#Basic Form One:user_spinner").getErrorMessage());
        shadowOf(getMainLooper()).idle();
        verify(formFragment, times(6)).writeValue(anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyBoolean());
        verify(onFieldsInvalid).passInvalidFields(presenter.getInvalidFields());
    }


    @Test
    public void testValidateAndWriteValues() throws InterruptedException {
        initWithActualForm();
        presenter.validateAndWriteValues();
        shadowOf(getMainLooper()).idle();
        assertEquals(4, presenter.getInvalidFields().size());


        setTextValue("step1:user_last_name", "Doe");
        setTextValue("step1:user_first_name", "John");
        setTextValue("step1:user_age", "21");
        ((AppCompatSpinner) formFragment.getJsonApi().getFormDataView("step1:user_spinner")).setSelection(1,false);
        presenter.validateAndWriteValues();
        shadowOf(getMainLooper()).idle();
        assertEquals(0, presenter.getInvalidFields().size());
        verify(formFragment, times(12)).writeValue(anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyBoolean());
        verify(onFieldsInvalid, times(2)).passInvalidFields(presenter.getInvalidFields());
    }

    private void setTextValue(String address, String value) {
        TextView view = (TextView) formFragment.getJsonApi().getFormDataView(address);
        view.setTag(R.id.raw_value, value);
        view.setText(value);

    }


    @Test
    public void testOnSaveClickDisplaysErrorFragmentAndDisplaysToast() throws InterruptedException {
        initWithActualForm();
        formFragment.getMainView().setTag(R.id.skip_validation, false);
        presenter.onSaveClick(formFragment.getMainView());
        shadowOf(getMainLooper()).idle();
        assertEquals(4, presenter.getInvalidFields().size());
        assertEquals("Please enter the last name", presenter.getInvalidFields().get("step1#Basic Form One:user_last_name").getErrorMessage());
        assertEquals("Please enter user age", presenter.getInvalidFields().get("step1#Basic Form One:user_age").getErrorMessage());
        assertEquals("Please enter the first name", presenter.getInvalidFields().get("step1#Basic Form One:user_first_name").getErrorMessage());
        assertEquals("Please enter the sex", presenter.getInvalidFields().get("step1#Basic Form One:user_spinner").getErrorMessage());
        verify(formFragment, times(6)).writeValue(anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyBoolean());
        assertTrue(presenter.getErrorFragment().isVisible());
        Toast toast = ShadowToast.getLatestToast();
        assertEquals(Toast.LENGTH_SHORT, toast.getDuration());
        assertEquals(context.getString(R.string.json_form_error_msg, 4), ShadowToast.getTextOfLatestToast());
    }


    @Test
    public void testOnSaveClickFinishesForm() throws JSONException, InterruptedException {
        initWithActualForm();
        formFragment.getMainView().setTag(R.id.skip_validation, false);
        setTextValue("step1:user_last_name", "Doe");
        setTextValue("step1:user_first_name", "John");
        setTextValue("step1:user_age", "21");
        ((AppCompatSpinner) formFragment.getJsonApi().getFormDataView("step1:user_spinner")).setSelection(1,false);
        presenter.onSaveClick(formFragment.getMainView());
        shadowOf(getMainLooper()).idle();
        assertEquals(0, presenter.getInvalidFields().size());
        verify(formFragment, times(6)).writeValue(anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyBoolean());
        assertNull(presenter.getErrorFragment());
        assertNull(ShadowToast.getLatestToast());
        verify(formFragment).onFormFinish();
        verify(formFragment).finishWithResult(intentArgumentCaptor.capture());
        assertFalse(intentArgumentCaptor.getValue().getBooleanExtra(JsonFormConstants.SKIP_VALIDATION, true));
        JSONObject json = new JSONObject(intentArgumentCaptor.getValue().getStringExtra("json"));
        assertNotNull(json);
        assertEquals("Doe", FormUtils.getFieldFromForm(json, "user_last_name").getString(JsonFormConstants.VALUE));
        assertEquals("John", FormUtils.getFieldFromForm(json, "user_first_name").getString(JsonFormConstants.VALUE));
        assertEquals("21", FormUtils.getFieldFromForm(json, "user_age").getString(JsonFormConstants.VALUE));

    }


    @Test
    public void testOnSaveClickErrorFragmentDisabledAndDisplaysSnackbar() throws JSONException, InterruptedException {
        initWithActualForm();
        formFragment.getMainView().setTag(R.id.skip_validation, false);
        formFragment.getJsonApi().getmJSONObject().put(JsonFormConstants.SHOW_ERRORS_ON_SUBMIT, false);
        formFragment = spy(formFragment);
        doNothing().when(formFragment).showSnackBar(anyString());
        Whitebox.setInternalState(presenter, "viewRef", new WeakReference<>(formFragment));
        presenter.onSaveClick(formFragment.getMainView());
        assertEquals(4, presenter.getInvalidFields().size());
        assertEquals("Please enter the last name", presenter.getInvalidFields().get("step1#Basic Form One:user_last_name").getErrorMessage());
        assertEquals("Please enter user age", presenter.getInvalidFields().get("step1#Basic Form One:user_age").getErrorMessage());
        assertEquals("Please enter the first name", presenter.getInvalidFields().get("step1#Basic Form One:user_first_name").getErrorMessage());
        assertEquals("Please enter the sex", presenter.getInvalidFields().get("step1#Basic Form One:user_spinner").getErrorMessage());
        verify(formFragment, times(6)).writeValue(anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyBoolean());
        assertNull(presenter.getErrorFragment());
        assertNull(ShadowToast.getLatestToast());
        verify(formFragment, never()).onFormFinish();
        verify(formFragment).showSnackBar(context.getString(R.string.json_form_error_msg, 4));
    }

    @Test
    public void testOnActivityResult() {
        when(formFragment.getContext()).thenReturn(context);
        presenter.onActivityResult(RESULT_LOAD_IMG, RESULT_CANCELED, null);
        verify(formFragment).getJsonApi();
        verifyNoMoreInteractions(formFragment);
        presenter.onActivityResult(RESULT_LOAD_IMG, RESULT_OK, null);
        verify(formFragment).updateRelevantImageView(null, null, null);
    }

    @Test
    @Config(shadows = {ShadowContextCompat.class, ShadowPermissionUtils.class})
    public void testOnRequestPermissionsResultDisplaysCameraIntent() {
        Whitebox.setInternalState(presenter, "key", "user_image");
        Whitebox.setInternalState(presenter, "type", "choose_image");
        when(formFragment.getContext()).thenReturn(context);
        presenter.onRequestPermissionsResult(CAMERA_PERMISSION_REQUEST_CODE, new String[]{permission.CAMERA, permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE}, new int[5]);
        verify(formFragment).hideKeyBoard();
        assertEquals("user_image", Whitebox.getInternalState(presenter, "mCurrentKey"));
    }


}
