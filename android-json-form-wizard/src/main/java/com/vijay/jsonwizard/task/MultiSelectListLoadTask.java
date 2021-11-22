package com.vijay.jsonwizard.task;

import android.app.ProgressDialog;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.domain.MultiSelectListAccessory;
import com.vijay.jsonwizard.utils.AppExecutors;
import com.vijay.jsonwizard.utils.MultiSelectListUtils;
import com.vijay.jsonwizard.widgets.MultiSelectListFactory;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import timber.log.Timber;

public class MultiSelectListLoadTask {

    private MultiSelectListFactory multiSelectListFactory;
    private JSONObject jsonObject;
    private String currentAdapterKey;
    private ProgressDialog progressBar;
    private AppExecutors appExecutors;

    public MultiSelectListLoadTask(final MultiSelectListFactory multiSelectListFactory) {
        this.multiSelectListFactory = multiSelectListFactory;
        this.jsonObject = multiSelectListFactory.jsonObject;
        this.currentAdapterKey = multiSelectListFactory.currentAdapterKey;
        appExecutors = multiSelectListFactory.getJsonFormFragment().getJsonApi().getAppExecutors();

        appExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                progressBar = new ProgressDialog(multiSelectListFactory.getContext());
                progressBar.setMessage(multiSelectListFactory.getContext().getString(R.string.loading_multi_select_list));
                progressBar.show();
            }
        });
        init();
    }

    private void init() {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String source = jsonObject.optString(JsonFormConstants.MultiSelectUtils.SOURCE);
                final List<MultiSelectItem> multiSelectItems = multiSelectListFactory.loadListItems(source);
                if (multiSelectItems == null) {
                    return;
                }
                String strGroupingsArray = jsonObject.optString(JsonFormConstants.MultiSelectUtils.GROUPINGS);
                boolean sort = jsonObject.optBoolean(JsonFormConstants.MultiSelectUtils.SORT);

                if (!StringUtils.isBlank(strGroupingsArray) && sort) {//no grouping without sorting
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(strGroupingsArray);
                    } catch (JSONException e) {
                        Timber.e(e);
                    }
                    MultiSelectListUtils.addGroupings(multiSelectItems, jsonArray);
                }
                if (sort) {
                    String sortClass = jsonObject.optString(JsonFormConstants.MultiSelectUtils.SORTING_CLASS);
                    if (StringUtils.isBlank(sortClass)) {
                        sortClass = JsonFormConstants.MultiSelectUtils.ALPHABET_SORTING;
                    }
                    try {
                        Class<?> aClass = Class.forName(sortClass);
                        Collections.sort(multiSelectItems, (Comparator<? super MultiSelectItem>) aClass.newInstance());
                    } catch (IllegalAccessException e) {
                        Timber.e(e);
                    } catch (InstantiationException e) {
                        Timber.e(e);
                    } catch (ClassNotFoundException e) {
                        Timber.e(e);
                    }
                }

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(multiSelectItems);
                    }
                });
            }
        });
    }

    private void onPostExecute(List<MultiSelectItem> multiSelectItems) {
        if (progressBar != null) {
            progressBar.dismiss();
        }
        if (multiSelectItems != null) {
            MultiSelectListAccessory multiSelectListAccessory = multiSelectListFactory.getMultiSelectListAccessoryHashMap().get(currentAdapterKey);
            multiSelectListAccessory.setItemList(multiSelectItems);
            multiSelectListFactory.updateListData(true, currentAdapterKey);
        }
    }
}
