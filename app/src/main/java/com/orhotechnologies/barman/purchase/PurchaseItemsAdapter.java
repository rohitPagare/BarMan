package com.orhotechnologies.barman.purchase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utilities;
import com.orhotechnologies.barman.models.PurchaseItems;

import java.util.List;

public class PurchaseItemsAdapter extends RecyclerView.Adapter<PurchaseItemsAdapter.PurchaseItemViewHolder> {

    private final ModelcallBack modelcallBack;
    private final List<PurchaseItems> list;

    public PurchaseItemsAdapter(ModelcallBack modelcallBack,List<PurchaseItems> list) {
        this.modelcallBack = modelcallBack;
        this.list = list;
    }

    @NonNull
    @Override
    public PurchaseItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_psitem, parent, false);
        return new PurchaseItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseItemViewHolder holder, int position) {
        holder.bindView(modelcallBack,list.get(position),position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class PurchaseItemViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_itemname, tv_qauntity, tv_buyprice, tv_buyxqauntity, tv_totalbuyprice;

        public PurchaseItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_itemname = itemView.findViewById(R.id.tv_itemname);
            tv_qauntity = itemView.findViewById(R.id.tv_qauntity);
            tv_buyprice = itemView.findViewById(R.id.tv_buyprice);
            tv_buyxqauntity = itemView.findViewById(R.id.tv_buyxqauntity);
            tv_totalbuyprice = itemView.findViewById(R.id.tv_totalbuyprice);
        }

        void bindView(ModelcallBack modelcallBack,PurchaseItems item,int position){

            tv_itemname.setText(item.getItemname());
            tv_qauntity.setText("Qauntity: " + item.getQauntity() + " " + item.getUnit());
            tv_buyprice.setText("Buy Price: ".concat(Utilities.getDoubleFormattedValue(item.getBuyprice()) + " Rs."));
            tv_buyxqauntity.setText("Total: " + item.getQauntity() + " X " + item.getBuyprice());
            tv_totalbuyprice.setText(Utilities.getDoubleFormattedValue(item.getTotalprice()).concat(" Rs."));
            itemView.setOnClickListener(v->modelcallBack.onModelClick(item,position));
        }
    }

    interface ModelcallBack{
        void onModelClick(PurchaseItems item,int position);
    }
}
