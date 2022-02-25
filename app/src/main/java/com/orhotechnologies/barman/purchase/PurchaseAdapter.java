package com.orhotechnologies.barman.purchase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.models.PurchaseBills;

import org.joda.time.DateTime;

import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder> {

    private final ModelcallBack modelcallBack;
    private final List<PurchaseBills> list;

    public PurchaseAdapter(ModelcallBack modelcallBack, List<PurchaseBills> list) {
        this.modelcallBack = modelcallBack;
        this.list = list;
    }


    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_pbill,parent,false);
        return new PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        PurchaseBills purchasebill = list.get(position);
        holder.bindItem(purchasebill,modelcallBack);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class PurchaseViewHolder extends RecyclerView.ViewHolder{
        private final TextView tv_billnumber,tv_tradername,tv_date;

        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_billnumber = itemView.findViewById(R.id.tv_billnumber);
            tv_tradername = itemView.findViewById(R.id.tv_tradername);
            tv_date = itemView.findViewById(R.id.tv_date);
        }

        void bindItem(PurchaseBills purchasebill, ModelcallBack modelcallBack){
            tv_billnumber.setText("Bill No: "+purchasebill.getBillnumber());
            tv_tradername.setText(purchasebill.getTradername());
            DateTime dateTime = new DateTime(purchasebill.getDate());
            String formatDate = dateTime.toString(Utility.DATE_FORMAT);
            tv_date.setText(formatDate);
            itemView.setOnClickListener(v->modelcallBack.onModelClick(purchasebill));
        }


    }

    interface ModelcallBack{
        void onModelClick(PurchaseBills purchasebill);
    }
}
