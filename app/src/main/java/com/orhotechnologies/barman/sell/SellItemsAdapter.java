package com.orhotechnologies.barman.sell;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utilities;
import com.orhotechnologies.barman.models.SellItems;

import java.util.List;

public class SellItemsAdapter extends RecyclerView.Adapter<SellItemsAdapter.SellItemViewHolder> {

    private final ModelcallBack modelcallBack;
    private final List<SellItems> list;

    public SellItemsAdapter(ModelcallBack modelcallBack, List<SellItems> list) {
        this.modelcallBack = modelcallBack;
        this.list = list;
    }


    @NonNull
    @Override
    public SellItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_psitem, parent, false);
        return new SellItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellItemViewHolder holder, int position) {
        holder.bindView(modelcallBack, list.get(position), position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class SellItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_itemname, tv_qauntity, tv_buyprice, tv_buyxqauntity, tv_totalbuyprice;

        public SellItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_itemname = itemView.findViewById(R.id.tv_itemname);
            tv_qauntity = itemView.findViewById(R.id.tv_qauntity);
            tv_buyprice = itemView.findViewById(R.id.tv_buyprice);
            tv_buyxqauntity = itemView.findViewById(R.id.tv_buyxqauntity);
            tv_totalbuyprice = itemView.findViewById(R.id.tv_totalbuyprice);
        }

        void bindView(ModelcallBack modelcallBack, SellItems item, int position) {

            tv_itemname.setText(item.getItemname()+" "+item.getOffer().getOffername());
            tv_qauntity.setText("Qauntity: " + item.getQauntity());
            tv_buyprice.setText("Sell Price: ".concat(Utilities.getDoubleFormattedValue(item.getSellprice()) + " Rs."));
            tv_buyxqauntity.setText("Total: " + item.getQauntity() + " X " + Utilities.getDoubleFormattedValue(item.getSellprice()));
            tv_totalbuyprice.setText(Utilities.getDoubleFormattedValue(item.getTotalprice()).concat(" Rs."));
            itemView.setOnClickListener(v -> modelcallBack.onModelClick(item, position));
        }
    }

    interface ModelcallBack {
        void onModelClick(SellItems item, int position);
    }
}
