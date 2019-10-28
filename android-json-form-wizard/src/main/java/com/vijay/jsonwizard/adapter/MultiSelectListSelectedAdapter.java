package com.vijay.jsonwizard.adapter;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.domain.MultiSelectItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import timber.log.Timber;

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
        String value = multiSelectItem.getValue();
        try{
            JSONObject jsonObject = new JSONObject(value);
            if(jsonObject.has("meta")){
                holder.multiSelectListTextViewAdditionalInfo.setVisibility(View.VISIBLE);
                holder.multiSelectListTextViewAdditionalInfo.setTypeface(Typeface.DEFAULT);
                holder.multiSelectListTextViewAdditionalInfo.setText(jsonObject.optJSONObject("meta").getString("info"));
            }
        }catch (JSONException e){
            Timber.e(e);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView multiSelectListTextView;
        private TextView multiSelectListTextViewAdditionalInfo;
        private ImageView imgDelete;

        private MyViewHolder(View view) {
            super(view);
            multiSelectListTextView = view.findViewById(R.id.multiSelectListTextView);
            multiSelectListTextViewAdditionalInfo = view.findViewById(R.id.multiSelectListTextViewAdditionalInfo);
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
