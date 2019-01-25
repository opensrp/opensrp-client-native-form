package com.vijay.jsonwizard.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.utils.NativeViewUtils;

import org.json.JSONObject;

/**
 * Create and renders the native form view.
 *
 * The view returns a {@link org.json.JSONObject} result via {@link #getJsonFrom()}
 * Initialize rendering via {@link #setJsonFrom(String)} or {@link #setJsonObject(JSONObject)}. This will load the form
 *
 */
public class NativeForm extends RelativeLayout {

    private Context context;
    private String jsonFrom;
    private String mStepName = "step1";
    private JSONObject jsonObject;
    private View rootView;
    private JsonFormInteractor nativeViewInteractor;


    public NativeForm(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public NativeForm(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public NativeForm(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NativeForm(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    public void init() {
        // View v = LayoutInflater.from(context).inflate(R.layout.native_form, null);
        rootView = LayoutInflater.from(context).inflate(R.layout.native_form_view, this, true);
    }

    public String getJsonFrom() {
        return jsonFrom;
    }

    public void setJsonFrom(String jsonFrom) {
        this.jsonFrom = jsonFrom;
        setJsonObject(NativeViewUtils.getFormJson(context, jsonFrom));
        loadForm();
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        loadForm();
    }

    private void loadForm(){
        if(getJsonObject() == null || rootView == null){
            throw new RuntimeException("Error processing file");
        }
        // add views
        renderViews();
        recalculate();
    }

    // render/re-render view
    private void renderViews(){

        // List<View> views = nativeViewInteractor.fetchFormElements(mStepName, formFragment, mStepDetails, this, false);

    }

    // reload cals
    private void recalculate(){

    }

}