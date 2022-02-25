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
import com.orhotechnologies.barman.databinding.ModelItemtradeBinding;
import com.orhotechnologies.barman.item.model.Itemtrade;

public class ItemTradeFirestoreAdapter extends FirestorePagingAdapter<Itemtrade, ItemTradeFirestoreAdapter.ItemTradeViewHolder> {

    public ItemTradeFirestoreAdapter(@NonNull FirestorePagingOptions<Itemtrade> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemTradeViewHolder holder, int position, @NonNull Itemtrade model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public ItemTradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ModelItemtradeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.model_itemtrade,parent,false);
        return new ItemTradeViewHolder(binding);
    }

    static class ItemTradeViewHolder extends RecyclerView.ViewHolder{
        ModelItemtradeBinding binding;
        public ItemTradeViewHolder(ModelItemtradeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Object obj) {
            binding.setModelitemtrade((Itemtrade) obj);
            binding.executePendingBindings();
        }
    }
}
