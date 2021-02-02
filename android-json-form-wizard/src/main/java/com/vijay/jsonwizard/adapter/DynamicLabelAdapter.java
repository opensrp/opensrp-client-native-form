package com.vijay.jsonwizard.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.utils.FormUtils;

import java.io.IOException;
import java.util.ArrayList;

public class DynamicLabelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final ArrayList<String> imagePaths;
    private final ArrayList<String> labelDescriptions;
    private final ArrayList<String> labelTitles;

    public DynamicLabelAdapter(Context context, ArrayList<String> labelTitles, ArrayList<String> labelDescriptions, ArrayList<String> imagePaths) {
        this.context = context;
        this.labelDescriptions = labelDescriptions;
        this.imagePaths = imagePaths;
        this.labelTitles = labelTitles;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_dialog_row_layout, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
        recyclerViewHolder.descriptionTextView.setText(labelDescriptions.get(position));
        recyclerViewHolder.tileTextView.setText(labelTitles.get(position));
        try {
            recyclerViewHolder.imageViewLabel.setImageDrawable(FormUtils.readImageFromAsset(context, imagePaths.get(position)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return labelDescriptions.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView descriptionTextView;
        TextView tileTextView;
        ImageView imageViewLabel;

        private RecyclerViewHolder(View view) {
            super(view);
            descriptionTextView = view.findViewById(R.id.descriptionText);
            tileTextView = view.findViewById(R.id.labelTitle);
            imageViewLabel = view.findViewById(R.id.imageViewLabel);
        }
    }
}
