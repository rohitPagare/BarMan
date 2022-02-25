package com.orhotechnologies.barman.sell.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.databinding.ModelPsitemBinding;
import com.orhotechnologies.barman.item.model.Itemtrade;

import java.util.ArrayList;
import java.util.List;

public class SellItemTradeAdapter extends RecyclerView.Adapter<SellItemTradeAdapter.SellItemTradeViewHolder> {

    private final List<Itemtrade> list;
    private final OnListItemtradeClickListner clickListner;

    public SellItemTradeAdapter(List<Itemtrade> list,OnListItemtradeClickListner clickListner) {
        this.list = new ArrayList<>(list);
        this.clickListner = clickListner;
    }

    @NonNull
    @Override
    public SellItemTradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ModelPsitemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.model_psitem,parent,false);
        return new SellItemTradeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SellItemTradeViewHolder holder, int position) {
            Itemtrade model = list.get(position);
            model.setPosition(position);
            holder.bind(model);
            holder.itemView.setOnClickListener(v->clickListner.onItemtradeClick(model));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Itemtrade> list){
        this.list.clear();
        this.list.addAll(new ArrayList<>(list));
        notifyDataSetChanged();
    }

    static class SellItemTradeViewHolder extends RecyclerView.ViewHolder{

        private final ModelPsitemBinding binding;

        public SellItemTradeViewHolder(ModelPsitemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Object obj){
            binding.setModelitemtrade((Itemtrade) obj);
            binding.executePendingBindings();
        }
    }

    public interface OnListItemtradeClickListner{
        void onItemtradeClick(Itemtrade itemtrade);
    }
}
