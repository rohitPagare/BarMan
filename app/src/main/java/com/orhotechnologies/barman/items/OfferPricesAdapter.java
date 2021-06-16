package com.orhotechnologies.barman.items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utilities;
import com.orhotechnologies.barman.models.OfferPrices;

import java.util.List;

public class OfferPricesAdapter extends RecyclerView.Adapter<OfferPricesAdapter.OfferPriceViewHolder> {

    private final List<OfferPrices> list;
    private final ModelCallback modelCallback;
    private String unit;

    public OfferPricesAdapter(ModelCallback modelCallback,List<OfferPrices> list) {
        this.modelCallback = modelCallback;
        this.list = list;
    }

    @NonNull
    @Override
    public OfferPriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_offersellprice,parent,false);
        return new OfferPriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferPriceViewHolder holder, int position) {
        holder.bindItem(modelCallback,list.get(position),position,unit);
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class OfferPriceViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_offername,tv_qauntity,tv_sellprice;

        public OfferPriceViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_offername = itemView.findViewById(R.id.tv_offername);
            tv_qauntity = itemView.findViewById(R.id.tv_qauntity);
            tv_sellprice = itemView.findViewById(R.id.tv_sellprice);
        }

        void bindItem(ModelCallback modelCallback,OfferPrices offer,int pos,String unit){
            tv_offername.setText(offer.getOffername());
            tv_qauntity.setText("Quantity: "+offer.getQuantity()+" "+unit);
            tv_sellprice.setText("Sell Price: "+ Utilities.getDoubleFormattedValue(offer.getPrice()));
            itemView.setOnClickListener(v->modelCallback.onModelClick(offer,pos));
        }
    }

    interface ModelCallback{
        void onModelClick(OfferPrices offer, int position);
    }
}
