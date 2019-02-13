package com.vijay.jsonwizard.customviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vijay.jsonwizard.R;

import java.util.List;

/**
 * NumberSelectorAdapter {@link NumberSelectorAdapter} The NumberSelectorSpinner {@link NumberSelectorSpinner} Adapter
 *
 * @author kitoto
 */
public class NumberSelectorAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<String> numbers;

    public NumberSelectorAdapter(Context context, List<String> numbers) {
        inflater = LayoutInflater.from(context);
        this.numbers = numbers;
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
        ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.native_form_number_selector_spinner, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.selectedNumber.setText(String.valueOf(getItem(position)));

        return view;
    }

    private static class ViewHolder {
        private TextView selectedNumber;

        ViewHolder(View view) {
            selectedNumber = view.findViewById(R.id.selected_number);
        }
    }
}
