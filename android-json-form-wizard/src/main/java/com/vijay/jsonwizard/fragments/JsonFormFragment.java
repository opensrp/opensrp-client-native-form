package com.vijay.jsonwizard.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.mvp.MvpFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.views.JsonFormFragmentView;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vijay on 5/7/15.
 */
public class JsonFormFragment extends MvpFragment<JsonFormFragmentPresenter, JsonFormFragmentViewState> implements
        CommonListener, JsonFormFragmentView<JsonFormFragmentViewState> {
    private static final String TAG = "JsonFormFragment";
    private static String CONST_REAL_TIME_VALIDATION = "RealtimeValidation";
    private static String CONST_FRAGMENT_WRITEVALUE_CALLED = "Fragment write value called";
    protected LinearLayout mMainView;
    protected ScrollView mScrollView;
    private Menu mMenu;
    private JsonApi mJsonApi;
    private Map<String, List<View>> lookUpMap = new HashMap<>();

    public static JsonFormFragment getFormFragment(String stepName) {
        JsonFormFragment jsonFormFragment = new JsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stepName", stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        mJsonApi = (JsonApi) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.native_form_fragment_json_wizard, null);
        mMainView = (LinearLayout) rootView.findViewById(R.id.main_layout);
        mScrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mJsonApi.clearFormDataViews();
        presenter.addFormElements();
        mJsonApi.refreshSkipLogic(null, null);
        mJsonApi.refreshConstraints(null, null);
    }

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return new JsonFormFragmentViewState();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        menu.clear();
        inflater.inflate(R.menu.menu_toolbar, menu);
        presenter.setUpToolBar();
    }

    @Override
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            presenter.onBackClick();
            return true;
        } else if (item.getItemId() == R.id.action_next) {
            return next();
        } else if (item.getItemId() == R.id.action_save) {
            try {
                Boolean skipValidation = ((JsonFormActivity) mMainView.getContext()).getIntent().getBooleanExtra(JsonFormConstants.SKIP_VALIDATION,
                        false);
                return save(skipValidation);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return save(false);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean save(boolean skipValidation) {
        try {
            mMainView.setTag(R.id.skip_validation, skipValidation);
            presenter.onSaveClick(mMainView);
            return true;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return false;
    }

    public boolean next() {
        try {
            presenter.onNextClick(mMainView);
            return true;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        presenter.onClick(v);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    @Override
    public void onDetach() {
        mJsonApi = null;
        super.onDetach();
    }

    @Override
    public void updateRelevantImageView(Bitmap bitmap, String imagePath, String currentKey) {
        int childCount = mMainView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mMainView.getChildAt(i);
            if (view instanceof ImageView) {
                ImageView imageView = (ImageView) view;
                String key = (String) imageView.getTag(R.id.key);
                if (key.equals(currentKey)) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setTag(R.id.imagePath, imagePath);
                }
            }
        }
    }

    @Override
    public void writeValue(String stepName, String key, String selectedValue, String openMrsEntityParent,
                           String openMrsEntity, String openMrsEntityId) {
        try {
            mJsonApi.writeValue(stepName, key, selectedValue, openMrsEntityParent, openMrsEntity, openMrsEntityId);
        } catch (JSONException e) {
            // TODO - handle
            e.printStackTrace();
        }
    }

    @Override
    public void writeValue(String stepName, String prentKey, String childObjectKey, String childKey,
                           String value, String openMrsEntityParent, String openMrsEntity,
                           String openMrsEntityId, Boolean popup) {
        Log.d(CONST_REAL_TIME_VALIDATION, CONST_FRAGMENT_WRITEVALUE_CALLED);
        try {
            mJsonApi.writeValue(stepName, prentKey, childObjectKey, childKey, value,
                    openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
        } catch (JSONException e) {
            // TODO - handle
            e.printStackTrace();
        }
    }

    @Override
    public void writeMetaDataValue(String metaDataKey, Map<String, String> values) {
        Log.d(CONST_REAL_TIME_VALIDATION, CONST_FRAGMENT_WRITEVALUE_CALLED);
        try {
            mJsonApi.writeMetaDataValue(metaDataKey, values);
        } catch (JSONException e) {
            // TODO - handle
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject getStep(String stepName) {
        return mJsonApi.getStep(stepName);
    }

    @Override
    public String getCurrentJsonState() {
        return mJsonApi.currentJsonState();
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new JsonFormFragmentPresenter(this);
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public CommonListener getCommonListener() {
        return this;
    }

    @Override
    public void addFormElements(List<View> views) {
        for (View view : views) {
            mMainView.addView(view);
        }
        mJsonApi.refreshHiddenViews();
        mJsonApi.resetFocus();
    }

    @Override
    public ActionBar getSupportActionBar() {
        return ((JsonFormActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public Toolbar getToolbar() {
        return ((JsonFormActivity) getActivity()).getToolbar();
    }

    @Override
    public void setToolbarTitleColor(int colorId) {
        getToolbar().setTitleTextColor(getContext().getResources().getColor(colorId));
    }

    @Override
    public void updateVisibilityOfNextAndSave(boolean next, boolean save) {
        mMenu.findItem(R.id.action_next).setVisible(next);
        mMenu.findItem(R.id.action_save).setVisible(save);
    }

    @Override
    public void hideKeyBoard() {
        super.hideSoftKeyboard();
    }

    @Override
    public void backClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void unCheckAllExcept(String parentKey, String childKey) {
        int childCount = mMainView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mMainView.getChildAt(i);

            if (view instanceof RadioButton) {
                RadioButton radio = (RadioButton) view;
                String parentKeyAtIndex = (String) radio.getTag(R.id.key);
                String childKeyAtIndex = (String) radio.getTag(R.id.childKey);
                if (radio.isChecked() && parentKeyAtIndex.equals(parentKey) && !childKeyAtIndex.equals(childKey)) {
                    radio.setChecked(false);
                }
            } else if (view instanceof ViewGroup && ((ViewGroup) view).getChildCount() > 0 && ((ViewGroup) view).getChildAt(0) instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) ((ViewGroup) view).getChildAt(0);
                String parentKeyAtIndex = (String) checkBox.getTag(R.id.key);
                String childKeyAtIndex = (String) checkBox.getTag(R.id.childKey);
                if (checkBox.isChecked() && parentKeyAtIndex.equals(parentKey) && !childKeyAtIndex.equals(childKey)) {
                    checkBox.setChecked(false);
                }
            }
        }
    }

    @Override
    public void unCheck(String parentKey, String exclusiveKey) {
        int childCount = mMainView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mMainView.getChildAt(i);

            if (view instanceof RadioButton) {
                RadioButton radio = (RadioButton) view;
                String parentKeyAtIndex = (String) radio.getTag(R.id.key);
                String childKeyAtIndex = (String) radio.getTag(R.id.childKey);
                if (radio.isChecked() && parentKeyAtIndex.equals(parentKey) && childKeyAtIndex.equals(exclusiveKey)) {
                    radio.setChecked(false);
                    break;
                }
            } else if (view instanceof ViewGroup && ((ViewGroup) view).getChildCount() > 0 && ((ViewGroup) view).getChildAt(0) instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) ((ViewGroup) view).getChildAt(0);
                String parentKeyAtIndex = (String) checkBox.getTag(R.id.key);
                String childKeyAtIndex = (String) checkBox.getTag(R.id.childKey);
                if (checkBox.isChecked() && parentKeyAtIndex.equals(parentKey) && childKeyAtIndex.equals(exclusiveKey)) {
                    checkBox.setChecked(false);
                    break;
                }
            }
        }
    }

    @Override
    public String getCount() {
        return mJsonApi.getCount();
    }

    @Override
    public void onFormStart() {
        mJsonApi.onFormStart();
    }

    @Override
    public void onFormFinish() {
        mJsonApi.onFormFinish();
    }

    @Override
    public void scrollToView(final View view) {
        view.requestFocus();
        if (!(view instanceof MaterialEditText)) {
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    int y = view.getBottom() - view.getHeight();
                    if (y < 0) {
                        y = 0;
                    }
                    mScrollView.scrollTo(0, y);
                }
            });
        }
    }

    @Override
    public void finishWithResult(Intent returnIntent) {
        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }

    @Override
    public void setUpBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void transactThis(JsonFormFragment next) {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left,
                        R.anim.exit_to_right).replace(R.id.container, next)
                .addToBackStack(next.getClass().getSimpleName()).commit();
    }

    public Menu getMenu() {
        return mMenu;
    }

    public JsonApi getJsonApi() {
        return mJsonApi;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        presenter.onCheckedChanged(buttonView, isChecked);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        presenter.onItemSelected(parent, view, position, id);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("JsonFormFragment", "onNothingSelected called");
    }

    public LinearLayout getMainView() {
        return mMainView;
    }

    public Map<String, List<View>> getLookUpMap() {
        return lookUpMap;
    }

}
