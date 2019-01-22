package com.vijay.jsonwizard.customviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vijay.jsonwizard.R;

import java.util.List;

public class NumberSelectorAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<String> numbers;
    private Context context;

    public NumberSelectorAdapter(Context context, List<String> numbers) {
        inflater = LayoutInflater.from(context);
        this.numbers = numbers;
        this.context =context;
    }

    @Override
    public int getCount() {
        return numbers.size();
    }

    @Override
    public Object getItem(int position) {
        return numbers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(numbers.get(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.native_form_number_selector_spinner, parent, false);
        TextView selectedNumber = convertView.findViewById(R.id.selected_number);
        selectedNumber.setText(String.valueOf(getItem(position)));

        return convertView;
    }
}
