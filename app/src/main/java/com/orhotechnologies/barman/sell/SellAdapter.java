package com.orhotechnologies.barman.sell;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utilities;
import com.orhotechnologies.barman.models.SellBills;

import org.joda.time.DateTime;

import java.util.List;

public class SellAdapter extends RecyclerView.Adapter<SellAdapter.SellViewHolder> {

    private final ModelcallBack modelcallBack;
    private final List<SellBills> list;

    public SellAdapter(ModelcallBack modelcallBack, List<SellBills> list) {
        this.modelcallBack = modelcallBack;
        this.list = list;
    }


    @NonNull
    @Override
    public SellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_sbill,parent,false);
        return new SellViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellViewHolder holder, int position) {
        holder.bindItem(list.get(position),modelcallBack);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class SellViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_billnumber, tv_date, tv_totalprice;

        public SellViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_billnumber = itemView.findViewById(R.id.tv_billnumber);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_totalprice = itemView.findViewById(R.id.tv_totalprice);
        }

        void bindItem(SellBills sellBill, ModelcallBack modelcallBack) {
            tv_billnumber.setText("Bill No: "+sellBill.getBillnumber());
            tv_date.setText(new DateTime(sellBill.getDate()).toString("dd MMM yyyy"));
            tv_totalprice.setText(Utilities.getDoubleFormattedValue(sellBill.getBilltotal()).concat(" Rs"));

            itemView.setOnClickListener(v->modelcallBack.onModelClick(sellBill));
        }
    }

    interface ModelcallBack {
        void onModelClick(SellBills sellBills);
    }
}
