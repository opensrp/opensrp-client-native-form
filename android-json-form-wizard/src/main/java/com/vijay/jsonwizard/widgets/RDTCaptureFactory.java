package com.vijay.jsonwizard.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.washington.cs.ubicomplab.rdt_reader.activity.RDTCaptureActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Vincent Karuri on 24/05/2019
 */
public class RDTCaptureFactory implements FormWidgetFactory {

    private static final String TAG = RDTCaptureFactory.class.getName();

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        WidgetArgs widgetArgs = new WidgetArgs();
        widgetArgs.withStepName(stepName)
                .withContext(context)
                .withFormFragment(formFragment)
                .withJsonObject(jsonObject)
                .withListener(listener)
                .withPopup(popup);

        setUpRDTCaptureActivity(widgetArgs);
        launchRDTCaptureActivity((Activity) context);

        LinearLayout rootLayout = (LinearLayout) LayoutInflater.from(context).inflate(getLayout(), null);
        List<View> views = new ArrayList<>(1);
        views.add(rootLayout);

        return views;
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    public void setUpRDTCaptureActivity(WidgetArgs widgetArgs) {
        Context context = widgetArgs.getContext();
        if (context instanceof JsonApi) {
            JsonApi jsonApi = (JsonApi) context;
            jsonApi.addOnActivityResultListener(JsonFormConstants.RDT_CAPTURE_CODE ,
                    new OnActivityResultListener() {
                        @Override
                        public void onActivityResult(int requestCode, int resultCode, Intent data) {
                            if (requestCode == JsonFormConstants.RDT_CAPTURE_CODE && resultCode == RESULT_OK) {
                                if (data != null) {
                                   // todo: do something
                                } else {
                                    Log.i(TAG, "No result data for RDT capture!");
                                }
                            }
                        }
                    });
        }
    }

    private void launchRDTCaptureActivity(Activity activity) {
        Intent intent = new Intent(activity, RDTCaptureActivity.class);
        activity.startActivityForResult(intent, JsonFormConstants.RDT_CAPTURE_CODE);
    }

    protected int getLayout() {
        return R.layout.native_form_rdt_capture;
    }
}
