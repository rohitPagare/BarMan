package com.orhotechnologies.barman.items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.models.Items;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private final ModelcallBack modelcallBack;
    private final List<Items> list;

    public ItemsAdapter(ModelcallBack modelcallBack,List<Items> list) {
        this.modelcallBack = modelcallBack;
        this.list = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Items item = list.get(position);
        holder.bindItem(modelcallBack,item);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_itemname,tv_saleprice;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_itemname = itemView.findViewById(R.id.tv_itemname);
            tv_saleprice = itemView.findViewById(R.id.tv_sellprice);
        }

        void bindItem(ModelcallBack modelcallBack,Items item){
            tv_itemname.setText(item.getItemname());
            //tv_saleprice.setText(String.valueOf(item.getSellprice()+" Rs"));
            tv_itemname.setOnClickListener(v->{modelcallBack.onModelClick(item);});
        }
    }

    interface ModelcallBack{
        void onModelClick(Items item);
    }
}
