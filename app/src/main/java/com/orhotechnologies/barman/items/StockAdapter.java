package com.orhotechnologies.barman.items;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utilities;
import com.orhotechnologies.barman.models.Items;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private final StockAdapter.ModelcallBack modelcallBack;
    private final List<Items> list;

    public StockAdapter(StockAdapter.ModelcallBack modelcallBack, List<Items> list) {
        this.modelcallBack = modelcallBack;
        this.list = list;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_stock,parent,false);
        return new StockAdapter.StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Items item = list.get(position);
        holder.bindItem(modelcallBack,item);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class StockViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_itemname,tv_stock;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_itemname = itemView.findViewById(R.id.tv_itemname);
            tv_stock = itemView.findViewById(R.id.tv_stock);
        }

        void bindItem(StockAdapter.ModelcallBack modelcallBack, Items item){
            tv_itemname.setText(item.getItemname());
            String unit = item.getUnit().substring(item.getUnit().indexOf("(")+1,item.getUnit().indexOf(")"));
            tv_stock.setText(String.valueOf(item.getStock()).concat(" "+Utilities.getFirstCharacterCapital(unit)));
            tv_itemname.setOnClickListener(v->{modelcallBack.onModelClick(item);});
        }
    }

    interface ModelcallBack{
        void onModelClick(Items item);
    }
}
