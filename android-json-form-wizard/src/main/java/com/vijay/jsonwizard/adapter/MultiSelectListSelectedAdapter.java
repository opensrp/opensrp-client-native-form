package com.vijay.jsonwizard.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.domain.MultiSelectItem;

import java.util.List;

public class MultiSelectListSelectedAdapter extends RecyclerView.Adapter<MultiSelectListSelectedAdapter.MyViewHolder> {
    private List<MultiSelectItem> data;
    private static ClickListener clickListener;

    public MultiSelectListSelectedAdapter(List<MultiSelectItem> data) {
        this.data = data;
    }

    public List<MultiSelectItem> getData() {
        return data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiselectlistselecteditem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        MultiSelectItem multiSelectItem = data.get(position);
        holder.multiSelectListTextView.setText(multiSelectItem.getKey());
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView multiSelectListTextView;
        private ImageView imgDelete;

        private MyViewHolder(View view) {
            super(view);
            multiSelectListTextView = view.findViewById(R.id.multiSelectListTextView);
            imgDelete = view.findViewById(R.id.multiSelectListDelete);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(view);
            }
        }
    }

    public interface ClickListener {
        void onItemClick(View view);
    }

    public void setOnClickListener(ClickListener onClickListener) {
        MultiSelectListSelectedAdapter.clickListener = onClickListener;
    }
}
