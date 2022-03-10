package com.orhotechnologies.barman.daybook.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.databinding.ModelOfferdaysellBinding;
import com.orhotechnologies.barman.daybook.model.OfferDaySell;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OfferDaySellAdapter extends RecyclerView.Adapter<OfferDaySellAdapter.OfferDaySellViewHolder> {

    private final List<OfferDaySell> list;

    public OfferDaySellAdapter(List<OfferDaySell> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public OfferDaySellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ModelOfferdaysellBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.model_offerdaysell,parent,false);
        return new OfferDaySellViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferDaySellViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<OfferDaySell> list){
        this.list.clear();
        this.list.addAll(list.stream().filter(p->p.getQuantity()>0).collect(Collectors.toList()));
        this.list.sort(Comparator.comparing(OfferDaySell::getName));
        notifyDataSetChanged();
    }

    static class OfferDaySellViewHolder extends RecyclerView.ViewHolder{
        private final ModelOfferdaysellBinding binding;

        public OfferDaySellViewHolder(ModelOfferdaysellBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Object obj){
            binding.setModelofferdaysell((OfferDaySell) obj);
            binding.executePendingBindings();
        }

    }
}
