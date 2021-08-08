package com.vijay.jsonwizard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private String[] itemList;
    private LayoutInflater layoutInflater;
    private int rowItemResourceId;

    public CustomSpinnerAdapter(Context context, int rowItemResourceId, String[] itemList) {
        super(context, rowItemResourceId);
        this.context = context;
        this.itemList = itemList;
        this.layoutInflater = LayoutInflater.from(context);
        this.rowItemResourceId = rowItemResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = layoutInflater.inflate(rowItemResourceId, parent, false);
        TextView itemView = row.findViewById(android.R.id.text1);
        itemView.setSingleLine(false);
        itemView.setLines(3);
        itemView.setOnEditorActionListener(null);
        itemView.setText(itemList[position]);


        final TextView finalItem = itemView;
        itemView.post(new Runnable() {
            @Override
            public void run() {
                finalItem.setSingleLine(false);
            }
        });

        return row;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = layoutInflater.inflate(rowItemResourceId, parent, false);
        TextView itemView = row.findViewById(android.R.id.text1);
        itemView.setSingleLine(false);
        itemView.setLines(3);
        itemView.setOnEditorActionListener(null);
        itemView.setText(itemList[position]);

        final TextView finalItem = itemView;
        itemView.post(new Runnable() {
            @Override
            public void run() {
                finalItem.setSingleLine(false);
            }
        });

        return row;
    }
}
