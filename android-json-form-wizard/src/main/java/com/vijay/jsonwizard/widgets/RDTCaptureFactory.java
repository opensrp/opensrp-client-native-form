package com.vijay.jsonwizard.widgets;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.content.ContextCompat;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.washington.cs.ubicomplab.rdt_reader.activity.RDTCaptureActivity;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
import static edu.washington.cs.ubicomplab.rdt_reader.Constants.SAVED_IMAGE_FILE_PATH;

/**
 * Created by Vincent Karuri on 24/05/2019
 */
public class RDTCaptureFactory implements FormWidgetFactory {

    private static final String TAG = RDTCaptureFactory.class.getName();
    private View rootLayout;

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        WidgetArgs widgetArgs = new WidgetArgs();
        widgetArgs.withStepName(stepName)
                .withContext(context)
                .withFormFragment(formFragment)
                .withJsonObject(jsonObject)
                .withListener(listener)
                .withPopup(popup);

        rootLayout = LayoutInflater.from(context).inflate(getLayout(), null);

        addWidgetTags(jsonObject);
        setUpRDTCaptureActivity(widgetArgs);
        launchRDTCaptureActivity((Activity) context);

        List<View> views = new ArrayList<>(1);
        views.add(rootLayout);

        return views;
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    private void addWidgetTags(JSONObject jsonObject) throws JSONException {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String key = jsonObject.getString(JsonFormConstants.KEY);

        rootLayout.setTag(R.id.key, key);
        rootLayout.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        rootLayout.setTag(R.id.openmrs_entity, openMrsEntity);
        rootLayout.setTag(R.id.openmrs_entity_id, openMrsEntityId);
    }


    private OnActivityResultListener createOnActivityResultListener(final WidgetArgs widgetArgs) {

         OnActivityResultListener resultListener =  new OnActivityResultListener() {

                @Override
                public void onActivityResult(int requestCode, int resultCode, Intent data) {

                    final JsonApi jsonApi = (JsonApi) widgetArgs.getContext();
                    if (requestCode == JsonFormConstants.RDT_CAPTURE_CODE && resultCode == RESULT_OK) {
                        if (data != null) {
                            try {
                                String imageFilePath = data.getExtras().getString(SAVED_IMAGE_FILE_PATH);
                                widgetArgs.getJsonObject().put(VALUE, imageFilePath);
                                // write image path as widget value
                                String key = (String) rootLayout.getTag(R.id.key);
                                String openMrsEntityParent = (String) rootLayout.getTag(R.id.openmrs_entity_parent);
                                String openMrsEntity = (String) rootLayout.getTag(R.id.openmrs_entity);
                                String openMrsEntityId = (String) rootLayout.getTag(R.id.openmrs_entity_id);
                                jsonApi.writeValue(widgetArgs.getStepName(), key, imageFilePath, openMrsEntityParent,
                                        openMrsEntity, openMrsEntityId, widgetArgs.isPopup());
                            } catch (JSONException e) {
                                Log.e(TAG, e.getStackTrace().toString());
                            }
                        } else {
                            Log.i(TAG, "No result data for RDT capture!");
                        }
                        if (!widgetArgs.getFormFragment().next()) {
                            widgetArgs.getFormFragment().save(true);
                        }
                    } else if (resultCode == RESULT_CANCELED) {
                        ((Activity) widgetArgs.getContext()).finish();
                    }
                }
        };

        return resultListener;
    }

    public void setUpRDTCaptureActivity(final WidgetArgs widgetArgs) {
        Context context = widgetArgs.getContext();
        if (context instanceof JsonApi) {
            final JsonApi jsonApi = (JsonApi) context;
            jsonApi.addOnActivityResultListener(JsonFormConstants.RDT_CAPTURE_CODE , createOnActivityResultListener(widgetArgs));
        }
    }

    private void launchRDTCaptureActivity(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(activity, RDTCaptureActivity.class);
            activity.startActivityForResult(intent, JsonFormConstants.RDT_CAPTURE_CODE);
        }
    }

    protected int getLayout() {
        return R.layout.native_form_rdt_capture;
    }
}
