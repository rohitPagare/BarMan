package com.orhotechnologies.barman.sell.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.databinding.ModelSbillBinding;
import com.orhotechnologies.barman.sell.model.Sells;

public class SellsFireStoreAdapter extends FirestorePagingAdapter<Sells, SellsFireStoreAdapter.SellsViewHolder> {

    private final OnListSellClick onListSellClick;

    public SellsFireStoreAdapter(@NonNull FirestorePagingOptions<Sells> options, OnListSellClick onListSellClick) {
        super(options);
        this.onListSellClick = onListSellClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull SellsViewHolder holder, int position, @NonNull Sells model) {
        holder.bind(model);
        holder.itemView.setOnClickListener(v -> onListSellClick.onSellClick(model));
    }

    @NonNull
    @Override
    public SellsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ModelSbillBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.model_sbill, parent, false);
        return new SellsViewHolder(binding);
    }

    static class SellsViewHolder extends RecyclerView.ViewHolder {

        private final ModelSbillBinding binding;

        public SellsViewHolder(ModelSbillBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Object obj) {
            binding.setModelsell((Sells) obj);
            binding.executePendingBindings();
        }
    }

    public interface OnListSellClick {
        void onSellClick(Sells sells);
    }
}
