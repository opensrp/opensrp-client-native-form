package com.vijay.jsonwizard.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.domain.MultiSelectListAccessory;
import com.vijay.jsonwizard.widgets.MultiSelectListFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MultiSelectListLoadTask extends AsyncTask<Void, Void, List<MultiSelectItem>> {

    private MultiSelectListFactory multiSelectListFactory;
    private JSONObject jsonObject;
    private String currentAdapterKey;
    private ProgressDialog progressBar;

    public MultiSelectListLoadTask(MultiSelectListFactory multiSelectListFactory) {
        this.multiSelectListFactory = multiSelectListFactory;
        this.jsonObject = multiSelectListFactory.jsonObject;
        this.currentAdapterKey = multiSelectListFactory.currentAdapterKey;
        this.progressBar = new ProgressDialog(multiSelectListFactory.context);
        this.progressBar.setMessage(multiSelectListFactory.context.getString(R.string.loading_multi_select_list));
    }

    @Override
    protected void onPreExecute() {
        try {
            progressBar.show();
        }catch (Exception e){
            Timber.e(e);
        }
    }

    @Override
    protected List<MultiSelectItem> doInBackground(Void... voids) {
        try {
            JSONArray jsonArray = jsonObject.has(JsonFormConstants.OPTIONS_FIELD_NAME) ? jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME) : null;
            if (jsonArray != null) {
                List<MultiSelectItem> multiSelectItems = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    multiSelectItems.add(new MultiSelectItem(jsonObject1.getString(JsonFormConstants.KEY), jsonObject1.has(JsonFormConstants.MultiSelectUtils.PROPERTY) ? jsonObject1.getString(JsonFormConstants.MultiSelectUtils.PROPERTY) : null));
                }
                return multiSelectItems;
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<MultiSelectItem> multiSelectItems) {
        progressBar.dismiss();
        MultiSelectListAccessory multiSelectListAccessory = multiSelectListFactory.getMultiSelectListAccessoryHashMap().get(currentAdapterKey);
        multiSelectListAccessory.setItemList(multiSelectItems);
        multiSelectListFactory.updateListData(true);
    }
}
