package com.orhotechnologies.barman.daybook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utilities;
import com.orhotechnologies.barman.models.DailySellItems;

import java.util.List;

public class DailySellItemAdapter extends RecyclerView.Adapter<DailySellItemAdapter.DailySellItemViewHolder> {

    private final List<DailySellItems> list;

    public DailySellItemAdapter(List<DailySellItems> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public DailySellItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_dailysellitem,parent,false);
        return new DailySellItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailySellItemViewHolder holder, int position) {
        DailySellItems dailySellItems = list.get(position);
        holder.tv_itemname.setText(dailySellItems.getItemname());
        holder.tv_qauntity.setText("Quantity: "+dailySellItems.getSalequantity());
        holder.tv_totalsell.setText("Total Sell: "+ Utilities.getDoubleFormattedValue(dailySellItems.getTotalsaleprice()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class DailySellItemViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_itemname,tv_qauntity,tv_totalsell;
        public DailySellItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_itemname =  itemView.findViewById(R.id.tv_itemname);
            tv_qauntity =  itemView.findViewById(R.id.tv_qauntity);
            tv_totalsell =  itemView.findViewById(R.id.tv_totalsell);
        }
    }
}
