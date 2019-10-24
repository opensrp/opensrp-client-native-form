package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.widget.Button;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.adapter.MultiSelectListAdapter;
import com.vijay.jsonwizard.adapter.MultiSelectListSelectedAdapter;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.domain.MultiSelectListAccessory;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class MultiSelectListFactory implements FormWidgetFactory {
    private JSONObject jsonObject = new JSONObject();
    private JsonFormFragment jsonFormFragment;
    private HashMap<String, MultiSelectListAccessory> stringAdapterHashMap = new HashMap<>();
    private String currentAdapterKey;

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        return attachJson(stepName, context, formFragment, jsonObject, listener, popup);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return attachJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    private List<View> attachJson(String stepName, final Context context, final JsonFormFragment formFragment, final JSONObject jsonObject, CommonListener listener, boolean popup) {
        Timber.i("stepName %s popup %s listener %s", stepName, popup, listener);
        this.jsonFormFragment = formFragment;
        this.jsonObject = jsonObject;
        this.currentAdapterKey = jsonObject.optString(JsonFormConstants.KEY);
        prepareMultiSelectHashMap(stepName, popup);
        setUpDialog(context);
        List<View> views = new ArrayList<>();
        Button button = createButton(context);
        button.setTag(R.id.key, currentAdapterKey);
        button.setText(jsonObject.optString("buttonText"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentAdapterKey = (String) v.getTag(R.id.key);
                updateListData(true);
                showListDataDialog();
            }
        });
        RecyclerView recyclerView = createSelectedRecylerView(context);
        View underBar = createUnderBar(context);
        views.add(recyclerView);
        views.add(button);
        views.add(underBar);
        return views;
    }

    private void prepareMultiSelectHashMap(String stepName, boolean popup) {
        MultiSelectListAccessory multiSelectListAccessory = new MultiSelectListAccessory(
                new MultiSelectListSelectedAdapter(prepareSelectedData()),
                new MultiSelectListAdapter(prepareListData()),
                null,
                prepareSelectedData(),
                prepareListData());

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JsonFormConstants.STEPNAME, stepName);
            jsonObject.put(JsonFormConstants.IS_POPUP, popup);
            multiSelectListAccessory.setFormAttributes(jsonObject);
        } catch (JSONException e) {
            Timber.e(e);
        }

        updateMultiAccessorHashMap(multiSelectListAccessory);
    }

    private List<MultiSelectItem> prepareSelectedData() {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(JsonFormConstants.VALUE);
            List<MultiSelectItem> multiSelectItems = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                multiSelectItems.add(new MultiSelectItem(jsonObject1.getString(JsonFormConstants.KEY), jsonObject1.has(JsonFormConstants.MultiSelectUtils.PROPERTY) ? jsonObject1.getString(JsonFormConstants.MultiSelectUtils.PROPERTY) : null));
            }
            return multiSelectItems;
        } catch (JSONException e) {
            Timber.e(e);
        }
        return new ArrayList<>();
    }

    private List<MultiSelectItem> prepareListData() {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
            List<MultiSelectItem> multiSelectItems = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                multiSelectItems.add(new MultiSelectItem(jsonObject1.getString(JsonFormConstants.KEY), jsonObject1.has(JsonFormConstants.MultiSelectUtils.PROPERTY) ? jsonObject1.getString(JsonFormConstants.MultiSelectUtils.PROPERTY) : null));
            }
            return multiSelectItems;
        } catch (JSONException e) {
            Timber.e(e);
        }
        return new ArrayList<>();
    }

    public void updateSelectedData(List<MultiSelectItem> selectedData, boolean clearData) {
        if (clearData) {
            getMultiSelectListSelectedAdapter().getData().clear();
        }
        getMultiSelectListSelectedAdapter().getData().addAll(selectedData);
        getMultiSelectListSelectedAdapter().notifyDataSetChanged();
    }

    public void updateListData(boolean clearData) {
        MultiSelectListAccessory multiSelectListAccessory = getStringAdapterHashMap().get(currentAdapterKey);
        if (clearData) {
            getMultiSelectListAdapter().getData().clear();
        }
        getMultiSelectListAdapter().getData().addAll(multiSelectListAccessory.getItemList());
        getMultiSelectListAdapter().notifyDataSetChanged();
    }

    private void showListDataDialog() {
        if (getAlertDialog() != null) {
            getAlertDialog().show();
        }
    }

    private void setUpDialog(final Context context) {
        if (jsonFormFragment == null) {
            return;
        }
        LayoutInflater inflater = jsonFormFragment.getLayoutInflater();
        View view = inflater.inflate(R.layout.multiselectlistdialog, null);
        ImageView imgClose = view.findViewById(R.id.multiSelectListCloseDialog);
        TextView txtMultiSelectListDialogTitle = view.findViewById(R.id.multiSelectListDialogTitle);
        txtMultiSelectListDialogTitle.setText(jsonObject.optString("dialogTitle"));
        SearchView searchViewMultiSelect = view.findViewById(R.id.multiSelectListSearchView);
        searchViewMultiSelect.setQueryHint(jsonObject.optString("searchHint"));
        final RecyclerView recyclerView = view.findViewById(R.id.multiSelectListRecyclerView);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.FullScreenDialogStyle);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        final MultiSelectListAdapter multiSelectListAdapter = getMultiSelectListAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(multiSelectListAdapter);
        searchViewMultiSelect.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                multiSelectListAdapter.getFilter().filter(newText);
                return true;
            }
        });

        multiSelectListAdapter.setOnClickListener(new MultiSelectListAdapter.ClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = recyclerView.getChildLayoutPosition(view);
                MultiSelectItem multiSelectItem = getMultiSelectListAdapter().getItemAt(position);
                handleClickEventOnListData(multiSelectItem);
                Utils.showToast(context, multiSelectItem.getKey() + " " + context.getString(R.string.item_added));
                getAlertDialog().dismiss();
            }
        });

        MultiSelectListAccessory multiSelectListAccessory = getStringAdapterHashMap().get(currentAdapterKey);
        multiSelectListAccessory.setAlertDialog(alertDialog);
        updateMultiAccessorHashMap(multiSelectListAccessory);
    }

    public HashMap<String, MultiSelectListAccessory> getStringAdapterHashMap() {
        return stringAdapterHashMap;
    }

    private void updateMultiAccessorHashMap(MultiSelectListAccessory multiSelectListAccessory) {
        getStringAdapterHashMap().put(currentAdapterKey, multiSelectListAccessory);
    }

    protected void handleClickEventOnListData(MultiSelectItem multiSelectItem) {
        try {
            jsonFormFragment.getJsonApi().writeValue(
                    getStringAdapterHashMap().get(currentAdapterKey).getFormAttributes().optString(JsonFormConstants.STEPNAME), currentAdapterKey,
                    multiSelectItem.toJson().toString(), "", "", "",
                    getStringAdapterHashMap().get(currentAdapterKey).getFormAttributes().optBoolean(JsonFormConstants.IS_POPUP));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateSelectedData(Arrays.asList(multiSelectItem), false);
    }

    protected RecyclerView createSelectedRecylerView(Context context) {
        List<MultiSelectItem> multiSelectItems = prepareSelectedData();
        MultiSelectListSelectedAdapter multiSelectListSelectedAdapter = new MultiSelectListSelectedAdapter(multiSelectItems);
        MultiSelectListAccessory multiSelectListAccessory = getStringAdapterHashMap().get(currentAdapterKey);
        multiSelectListAccessory.setSelectedAdapter(multiSelectListSelectedAdapter);
        updateMultiAccessorHashMap(multiSelectListAccessory);
        RecyclerView recyclerView = new RecyclerView(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(multiSelectListSelectedAdapter);
        return recyclerView;
    }

    protected Button createButton(Context context) {
        Button button = new Button(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(layoutParams);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources()
                .getDimension(R.dimen.button_text_size));
        button.setGravity(Gravity.START);
        button.setTextColor(context.getResources().getColor(R.color.opensrp_accent));
        button.setHeight(context.getResources().getDimensionPixelSize(R.dimen.button_height));
        button.setBackgroundResource(R.color.transparent);
        button.setAllCaps(false);
        button.setTextSize(20);
        button.setTypeface(Typeface.DEFAULT);
        return button;
    }

    protected View createUnderBar(Context context) {
        View underBar = new View(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = context.getResources()
                .getDimensionPixelSize(R.dimen.extra_bottom_margin);
        underBar.setLayoutParams(layoutParams);
        underBar.setMinimumHeight(1);
        underBar.setBackgroundResource(R.color.primary_text);
        return underBar;
    }

    public MultiSelectListSelectedAdapter getMultiSelectListSelectedAdapter() {
        return getStringAdapterHashMap().get(currentAdapterKey).getSelectedAdapter();
    }

    public AlertDialog getAlertDialog() {
        return getStringAdapterHashMap().get(currentAdapterKey).getAlertDialog();
    }

    public MultiSelectListAdapter getMultiSelectListAdapter() {
        MultiSelectListAccessory multiSelectListAccessory = getStringAdapterHashMap().get(currentAdapterKey);
        if (multiSelectListAccessory.getListAdapter() == null) {
            List<MultiSelectItem> multiSelectItems = prepareListData();
            multiSelectListAccessory.setListAdapter(new MultiSelectListAdapter(multiSelectItems));
            getStringAdapterHashMap().put(currentAdapterKey, multiSelectListAccessory);
        }
        return multiSelectListAccessory.getListAdapter();
    }
}