package com.vijay.jsonwizard.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.domain.MultiSelectItem;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private List<MultiSelectItem> data;
    private List<MultiSelectItem> origData;
    private static ClickListener clickListener;

    public MultiSelectListAdapter(List<MultiSelectItem> data) {
        this.data = data;
        this.origData = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mutiselectlistitem, parent, false);
            return new SectionViewHolder(itemView);
        } else if (viewType == 1) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mutiselectlistitem_head, parent, false);
            return new SectionTitleViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MultiSelectItem multiSelectItem = data.get(position);
        if (multiSelectItem.getValue() != null) {
            SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
            sectionViewHolder.txtMultiSelectItem.setText(multiSelectItem.getKey());
        } else {
            SectionTitleViewHolder sectionViewHolder = (SectionTitleViewHolder) holder;
            sectionViewHolder.txtMultiSelectHeader.setText(multiSelectItem.getKey());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<MultiSelectItem> filteredMultiSelectItems = null;
                if (constraint.length() == 0) {
                    filteredMultiSelectItems = origData;
                } else {
                    filteredMultiSelectItems = getFilteredResults(constraint.toString());
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredMultiSelectItems;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data = (List<MultiSelectItem>) results.values;
                notifyDataSetChanged();
            }

            protected List<MultiSelectItem> getFilteredResults(String constraint) {
                List<MultiSelectItem> results = new ArrayList<>();

                for (MultiSelectItem item : origData) {
                    if (item.getKey().toLowerCase().contains(constraint)) {
                        results.add(item);
                    }
                }
                return results;
            }
        };
    }

    public class SectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtMultiSelectItem;

        private SectionViewHolder(View view) {
            super(view);
            txtMultiSelectItem = view.findViewById(R.id.txtMultiSelectItem);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(view);
            }
        }
    }

    public class SectionTitleViewHolder extends RecyclerView.ViewHolder {
        TextView txtMultiSelectHeader;

        private SectionTitleViewHolder(View view) {
            super(view);
            txtMultiSelectHeader = view.findViewById(R.id.txtMultiSelectHeader);
        }
    }

    @Override
    public int getItemViewType(int position) {
        MultiSelectItem multiSelectItem = data.get(position);
        if (multiSelectItem.getValue() != null) {
            return 0;
        } else {
            return 1;
        }
    }

    public MultiSelectItem getItemAt(int position){
        return data.get(position);
    }

    public interface ClickListener {
        void onItemClick(View view);
    }

    public void setOnClickListener(ClickListener onClickListener) {
        MultiSelectListAdapter.clickListener = onClickListener;
    }
}
