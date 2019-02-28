package com.vijay.jsonwizard.customviews;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.utils.ValidationStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomFormError extends LinearLayoutCompat {

    private Map<String, ValidationStatus> invalidFields;
    HashMap<String, ArrayList<ValidationStatus>> errorsPerStep;

    public CustomFormError(Context context) {
        super(context);
        invalidFields = new HashMap<>();
        errorsPerStep = new HashMap<>();
        this.setOrientation(VERTICAL);
    }


    private Map<String, ValidationStatus> getInvalidFields() {
        return invalidFields;
    }

    private void processInvalidFields() {
        Set<Map.Entry<String, ValidationStatus>> validationStatusEntry = getInvalidFields().entrySet();

        for (Map.Entry<String, ValidationStatus> entry : validationStatusEntry) {
            String key = entry.getKey();
            ValidationStatus value = entry.getValue();
            String[] splitKey = key.split(":");
            ArrayList<ValidationStatus> statusArrayList;
            if (errorsPerStep.containsKey(splitKey[0])) {
                statusArrayList = errorsPerStep.get(splitKey[0]);
            } else {
                statusArrayList = new ArrayList<>();
            }
            statusArrayList.add(value);
            errorsPerStep.put(splitKey[0], statusArrayList);
        }
        addCustomViews();
    }

    private void addCustomViews() {
        List<String> keySet = new ArrayList<>(errorsPerStep.keySet());
        Collections.sort(keySet);
        for (String key : keySet) {
            ArrayList<ValidationStatus> values = errorsPerStep.get(key);
            View view = LayoutInflater.from(this.getContext()).inflate(R.layout.native_form_error_item, null, false);
            FormErrorViewHolder formErrorViewHolder = new FormErrorViewHolder(view);
            String errors;
            StringBuilder sb = new StringBuilder();
            String spacing = values.size() > 1 ? "\n\n" : "";
            for (int i = 0; i < values.size(); i++) {
                ValidationStatus validationStatus = values.get(i);
                sb.append(i + 1).append(". ").append(validationStatus.getErrorMessage()).append(spacing);
            }
            errors = sb.toString();
            String stepName = key.replaceAll("(\\d)([^\\d\\s%])","$1 $2").toUpperCase();
            formErrorViewHolder.bindViews(stepName, errors);
            this.addView(formErrorViewHolder.getItemView());
        }
    }

    public void setInvalidFields(Map<String, ValidationStatus> invalidFields) {
        this.invalidFields = invalidFields;
        processInvalidFields();
    }

    class FormErrorViewHolder {
        private TextView stepNameTextView;
        private TextView errorsTextView;
        private View itemView;

        FormErrorViewHolder(View itemView) {
            this.itemView = itemView;
            initViews();
        }

        private void initViews() {
            stepNameTextView = itemView.findViewById(R.id.error_item_step_name);
            errorsTextView = itemView.findViewById(R.id.error_item_values);
        }

        private void bindViews(String stepName, String errors) {
            stepNameTextView.setText(stepName);
            errorsTextView.setText(errors);
        }

        public View getItemView() {
            return itemView;
        }
    }
}
