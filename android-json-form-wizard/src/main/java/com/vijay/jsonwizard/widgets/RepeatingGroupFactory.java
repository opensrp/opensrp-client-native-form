package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vijay.jsonwizard.constants.JsonFormConstants.TYPE;
import static com.vijay.jsonwizard.utils.Utils.hideProgressDialog;
import static com.vijay.jsonwizard.utils.Utils.showProgressDialog;

/**
 * @author Vincent Karuri
 */
public class RepeatingGroupFactory implements FormWidgetFactory {

    private final String REPEATING_GROUP_LAYOUT = "repeating_group_layout";
    private final String TAG = RepeatingGroupFactory.class.getName();
    private final ViewGroup.LayoutParams WIDTH_MATCH_PARENT_HEIGHT_WRAP_CONTENT = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private static Map<Integer, JSONArray> repeatingGroupLayouts = new HashMap<>();

    @Override
    public List<View> getViewsFromJson(final String stepName, final Context context, final JsonFormFragment formFragment, final JSONObject jsonObject, final CommonListener listener, final boolean popup) throws Exception {
        List<View> views = new ArrayList<>(1);

        LinearLayout rootLayout = (LinearLayout) LayoutInflater.from(context).inflate(getLayout(), null);

        final int rootLayoutId = View.generateViewId();
        rootLayout.setId(rootLayoutId);
        views.add(rootLayout);

        JSONArray repeatingGroupLayout = jsonObject.getJSONArray(REPEATING_GROUP_LAYOUT);
        repeatingGroupLayouts.put(rootLayoutId, repeatingGroupLayout);

        final WidgetArgs widgetArgs = new WidgetArgs();
        widgetArgs.withStepName(stepName)
                .withContext(context)
                .withFormFragment(formFragment)
                .withJsonObject(jsonObject)
                .withListener(listener)
                .withPopup(popup);

        EditText editText = rootLayout.findViewById(R.id.reference_edit_text);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addOnDoneAction(rootLayoutId, v, widgetArgs);
                    return true;
                }
                return false;
            }
        });

        return views;
    }

    @Override
    public List<View> getViewsFromJson(final String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    private void addOnDoneAction(int rootLayoutId, TextView textView, WidgetArgs widgetArgs) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) widgetArgs.getFormFragment().getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), 0);
            attachRepeatingGroup(textView.getParent(), Integer.parseInt(textView.getText().toString()), rootLayoutId, widgetArgs);
        } catch (Exception e) {
            Log.e(TAG, e.getStackTrace().toString());
        }
    }

    private void attachRepeatingGroup(final ViewParent parent, final int numRepeatingGroups, final int rootLayoutId, final WidgetArgs widgetArgs) {

        class AttachRepeatingGroupTask extends AsyncTask<Void, Void, List<View>> {
            LinearLayout rootLayout = (LinearLayout) parent;
            List<View> repeatingGroups = new ArrayList<>();
            int diff = 0;

            @Override
            protected void onPreExecute() {
                showProgressDialog(R.string.creating_repeating_group_title, R.string.creating_repeating_group_message, widgetArgs.getFormFragment().getActivity());
            }

            @Override
            protected List<View> doInBackground(Void... objects) {
                int currNumRepeatingGroups = ((ViewGroup) parent).getChildCount() - 1;
                diff = numRepeatingGroups - currNumRepeatingGroups;
                if (diff > 0) {
                    for (int i = currNumRepeatingGroups; i < numRepeatingGroups; i++) {
                        try {
                            repeatingGroups.add(buildRepeatingGroupLayout(rootLayoutId, widgetArgs));
                        } catch (Exception e) {
                            Log.e(TAG, e.getStackTrace().toString());
                        }
                    }
                } else {
                    for (int i = 0; i <= numRepeatingGroups; i++) {
                        repeatingGroups.add(rootLayout.getChildAt(i));
                    }
                }

                return repeatingGroups;
            }

            @Override
            protected void onPostExecute(List<View> result) {
                if (diff < 0) {
                    rootLayout.removeAllViews();
                }
                for (View repeatingGroup : repeatingGroups) {
                    rootLayout.addView(repeatingGroup);
                }
                hideProgressDialog();
            }
        }

        new AttachRepeatingGroupTask().execute();
    }

    private LinearLayout buildRepeatingGroupLayout(int rootLayoutId, WidgetArgs widgetArgs) throws Exception {
        Context context = widgetArgs.getContext();
        JSONArray repeatingGroupJson = repeatingGroupLayouts.get(rootLayoutId);
        LinearLayout repeatingGroup = new LinearLayout(context);
        repeatingGroup.setLayoutParams(WIDTH_MATCH_PARENT_HEIGHT_WRAP_CONTENT);
        repeatingGroup.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < repeatingGroupJson.length(); i++) {
            JSONObject element = repeatingGroupJson.getJSONObject(i);
            String elementType = element.optString(TYPE, null);
            if (elementType != null) {
                FormWidgetFactory factory = JsonFormInteractor.getInstance().map.get(elementType);
                List<View> widgetViews = factory.getViewsFromJson(widgetArgs.getStepName(), context, widgetArgs.getFormFragment(), element, widgetArgs.getListener(), widgetArgs.isPopup());
                for (View view : widgetViews) {
                    view.setLayoutParams(WIDTH_MATCH_PARENT_HEIGHT_WRAP_CONTENT);
                    repeatingGroup.addView(view);
                }
            }
        }
        return repeatingGroup;
    }

    protected int getLayout() {
        return R.layout.native_form_repeating_group;
    }
}