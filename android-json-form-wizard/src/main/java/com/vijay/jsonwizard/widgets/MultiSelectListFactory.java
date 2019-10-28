package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.adapter.MultiSelectListAdapter;
import com.vijay.jsonwizard.adapter.MultiSelectListSelectedAdapter;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.domain.MultiSelectListAccessory;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.task.MultiSelectListLoadTask;
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
    public JSONObject jsonObject = new JSONObject();
    public String currentAdapterKey;
    public Context context;
    private JsonFormFragment jsonFormFragment;
    private HashMap<String, MultiSelectListAccessory> multiSelectListAccessoryHashMap = new HashMap<>();

    @Override
    public List<View> getViewsFromJson(@NonNull String stepName, @NonNull Context context, @NonNull JsonFormFragment formFragment, @NonNull JSONObject jsonObject,
                                       @NonNull CommonListener listener, boolean popup) throws Exception {
        return attachJson(stepName, context, formFragment, jsonObject, listener, popup);
    }

    @Override
    public List<View> getViewsFromJson(@NonNull String stepName, @NonNull Context context, @NonNull JsonFormFragment formFragment, @NonNull JSONObject jsonObject,
                                       @NonNull CommonListener listener) throws Exception {
        return attachJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    private List<View> attachJson(@NonNull String stepName, @NonNull Context context, @NonNull JsonFormFragment formFragment, @NonNull JSONObject jsonObject,
                                  @NonNull CommonListener listener, boolean popup) {
        Timber.i("stepName %s popup %s listener %s", stepName, popup, listener);
        this.jsonFormFragment = formFragment;
        this.jsonObject = jsonObject;
        this.currentAdapterKey = jsonObject.optString(JsonFormConstants.KEY);
        this.context = context;

        prepareMultiSelectHashMap(stepName, popup);

        setUpDialog(context);

        RelativeLayout actionView = createActionView(context);
        RecyclerView recyclerView = createSelectedRecyclerView(context);
        List<View> views = new ArrayList<View>(Arrays.asList(recyclerView, actionView));

        populateTags(actionView, stepName);

        prepareViewChecks(actionView, context);
        return views;
    }

    private void prepareViewChecks(@NonNull RelativeLayout view, @NonNull Context context) {
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        String constraints = jsonObject.optString(JsonFormConstants.CONSTRAINTS);
        String calculation = jsonObject.optString(JsonFormConstants.CALCULATION);

        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
            view.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(view);
        }

        if (!TextUtils.isEmpty(constraints) && context instanceof JsonApi) {
            view.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(view);
        }

        if (!TextUtils.isEmpty(calculation) && context instanceof JsonApi) {
            view.setTag(R.id.calculation, calculation);
            ((JsonApi) context).addCalculationLogicView(view);
        }
    }

    private void populateTags(@NonNull View view, @NonNull String stepName) {
        JSONArray canvasIds = new JSONArray();
        view.setId(ViewUtil.generateViewId());
        canvasIds.put(view.getId());
        String openMrsEntityParent = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID);
        view.setTag(R.id.canvas_ids, canvasIds.toString());
        view.setTag(R.id.key, jsonObject.optString(JsonFormConstants.KEY));
        view.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        view.setTag(R.id.openmrs_entity, openMrsEntity);
        view.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        view.setTag(R.id.type, jsonObject.optString(JsonFormConstants.TYPE));
        view.setTag(R.id.address, stepName + ":" + jsonObject.optString(JsonFormConstants.KEY));
    }

    private void prepareMultiSelectHashMap(@NonNull String stepName, boolean popup) {
        MultiSelectListAccessory multiSelectListAccessory = new MultiSelectListAccessory(
                new MultiSelectListSelectedAdapter(new ArrayList<MultiSelectItem>()),
                new MultiSelectListAdapter(prepareListData()),
                null,
                new ArrayList<MultiSelectItem>(),
                new ArrayList<MultiSelectItem>());

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JsonFormConstants.STEPNAME, stepName);
            jsonObject.put(JsonFormConstants.IS_POPUP, popup);
            multiSelectListAccessory.setFormAttributes(jsonObject);
        } catch (JSONException e) {
            Timber.e(e);
        }

        updateMultiSelectListAccessoryHashMap(multiSelectListAccessory);
    }

    protected List<MultiSelectItem> prepareSelectedData() {
        try {
            String strJsonArray = jsonObject.has(JsonFormConstants.VALUE) ? jsonObject.getString(JsonFormConstants.VALUE) : null;
            if (strJsonArray != null) {
                JSONArray jsonArray = new JSONArray(strJsonArray);
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
        return new ArrayList<>();
    }

    protected List<MultiSelectItem> prepareListData() {
        new MultiSelectListLoadTask(this).execute();
        return new ArrayList<>();
    }

    public void updateSelectedData(@NonNull List<MultiSelectItem> selectedData, boolean clearData) {
        if (clearData) {
            getMultiSelectListSelectedAdapter().getData().clear();
        }
        getMultiSelectListSelectedAdapter().getData().addAll(selectedData);
        getMultiSelectListSelectedAdapter().notifyDataSetChanged();
    }

    public void updateListData(boolean clearData) {
        MultiSelectListAccessory multiSelectListAccessory = getMultiSelectListAccessoryHashMap().get(currentAdapterKey);
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
                handleClickEventOnListData(getMultiSelectListAdapter().getItemAt(position), context);
            }
        });

        MultiSelectListAccessory multiSelectListAccessory = getMultiSelectListAccessoryHashMap().get(currentAdapterKey);
        multiSelectListAccessory.setAlertDialog(alertDialog);
        updateMultiSelectListAccessoryHashMap(multiSelectListAccessory);
    }

    public HashMap<String, MultiSelectListAccessory> getMultiSelectListAccessoryHashMap() {
        return multiSelectListAccessoryHashMap;
    }

    private void updateMultiSelectListAccessoryHashMap(@NonNull MultiSelectListAccessory multiSelectListAccessory) {
        getMultiSelectListAccessoryHashMap().put(currentAdapterKey, multiSelectListAccessory);
    }

    protected void handleClickEventOnListData(@NonNull MultiSelectItem multiSelectItem, @NonNull Context context) {
        updateSelectedData(Arrays.asList(multiSelectItem), false);
        writeToForm(multiSelectItem);
        Utils.showToast(context, multiSelectItem.getKey() + " " + context.getString(R.string.item_added));
        getAlertDialog().dismiss();
    }

    protected void writeToForm(@NonNull MultiSelectItem multiSelectItem) {
        try {
            jsonFormFragment.getJsonApi().writeValue(
                    getMultiSelectListAccessoryHashMap().get(currentAdapterKey).getFormAttributes().optString(JsonFormConstants.STEPNAME), currentAdapterKey,
                    multiSelectItem.toJson(getMultiSelectListSelectedAdapter().getData()).toString(), "", "", "",
                    getMultiSelectListAccessoryHashMap().get(currentAdapterKey).getFormAttributes().optBoolean(JsonFormConstants.IS_POPUP));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public MultiSelectListSelectedAdapter getMultiSelectListSelectedAdapter() {
        MultiSelectListAccessory multiSelectListAccessory = getMultiSelectListAccessoryHashMap().get(currentAdapterKey);
        if (multiSelectListAccessory != null) {
            return multiSelectListAccessory.getSelectedAdapter();
        }
        return null;
    }

    public AlertDialog getAlertDialog() {
        MultiSelectListAccessory multiSelectListAccessory = getMultiSelectListAccessoryHashMap().get(currentAdapterKey);
        if (multiSelectListAccessory != null) {
            return multiSelectListAccessory.getAlertDialog();
        }
        return null;
    }

    public MultiSelectListAdapter getMultiSelectListAdapter() {
        MultiSelectListAccessory multiSelectListAccessory = getMultiSelectListAccessoryHashMap().get(currentAdapterKey);
        if (multiSelectListAccessory != null) {
            return multiSelectListAccessory.getListAdapter();
        }
        return null;
    }

    protected RecyclerView createSelectedRecyclerView(@NonNull Context context) {
        List<MultiSelectItem> multiSelectItems = prepareSelectedData();
        MultiSelectListSelectedAdapter multiSelectListSelectedAdapter = new MultiSelectListSelectedAdapter(multiSelectItems);

        MultiSelectListAccessory multiSelectListAccessory = getMultiSelectListAccessoryHashMap().get(currentAdapterKey);
        multiSelectListAccessory.setSelectedAdapter(multiSelectListSelectedAdapter);
        updateMultiSelectListAccessoryHashMap(multiSelectListAccessory);
        final RecyclerView recyclerView = new RecyclerView(context);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.multi_select_list_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(multiSelectListSelectedAdapter);
        return recyclerView;
    }

    protected RelativeLayout createActionView(@NonNull Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final RelativeLayout relativeLayout = (RelativeLayout) layoutInflater.inflate(R.layout.multi_select_list_action_layout, null);
        relativeLayout.setTag(R.id.key, currentAdapterKey);
        Button btn_multi_select_action = relativeLayout.findViewById(R.id.btn_multi_select_action);
        btn_multi_select_action.setTypeface(Typeface.DEFAULT);
        btn_multi_select_action.setTag(R.id.maxSelectable, jsonObject.optString(JsonFormConstants.MultiSelectUtils.MAX_SELECTABLE));
        btn_multi_select_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strMaxSelectable = (String) v.getTag(R.id.maxSelectable);
                currentAdapterKey = (String) relativeLayout.getTag(R.id.key);
                int maxSelectable;
                if (!TextUtils.isEmpty(strMaxSelectable)) {
                    maxSelectable = Integer.parseInt(strMaxSelectable);
                    List<MultiSelectItem> multiSelectItems = getMultiSelectListSelectedAdapter().getData();
                    if ((multiSelectItems.size() >= maxSelectable) && !multiSelectItems.isEmpty()) {
                        return;
                    }
                }
                updateListData(true);
                showListDataDialog();
            }
        });

        return relativeLayout;
    }
}