package com.orhotechnologies.barman.traders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.models.Traders;

import java.util.List;

public class TradersAdapter extends RecyclerView.Adapter<TradersAdapter.TradersViewHolder> {


    private final TradersAdapter.ModelcallBack modelcallBack;
    private final List<Traders> list;

    public TradersAdapter(TradersAdapter.ModelcallBack modelcallBack, List<Traders> list) {
        this.modelcallBack = modelcallBack;
        this.list = list;
    }


    @NonNull
    @Override
    public TradersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_trader,parent,false);
        return new TradersAdapter.TradersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TradersViewHolder holder, int position) {
        Traders traders = list.get(position);
        holder.bindItem(modelcallBack,traders);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class TradersViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_name, tv_address;
        public TradersViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_address = itemView.findViewById(R.id.tv_address);
        }

        void bindItem(TradersAdapter.ModelcallBack modelcallBack, Traders traders){
           tv_name.setText(traders.getName());
           tv_address.setText(traders.getAddress());
           itemView.setOnClickListener(v->modelcallBack.onModelClick(traders));
        }
    }

    interface ModelcallBack{
        void onModelClick(Traders traders);
    }
}
