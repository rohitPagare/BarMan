package com.orhotechnologies.barman.item.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.orhotechnologies.barman.BR;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.databinding.ModelItemBinding;
import com.orhotechnologies.barman.item.model.Items;

public class ItemsFirestoreAdapter extends FirestorePagingAdapter<Items, ItemsFirestoreAdapter.ItemViewHolder> {

    private final OnListItemClick onListItemClick;

    public ItemsFirestoreAdapter(@NonNull FirestorePagingOptions<Items> options, OnListItemClick onListItemClick) {
        super(options);
        this.onListItemClick = onListItemClick;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ModelItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.model_item, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Items model) {
        holder.bind(model);
        holder.itemView.setOnClickListener(v->onListItemClick.onItemClick(model));
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ModelItemBinding binding;

        public ItemViewHolder(ModelItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Object obj) {
            binding.setModelitem((Items) obj);
            binding.executePendingBindings();
        }
    }

    public interface OnListItemClick{
        void onItemClick(Items item);
    }
}
