package com.orhotechnologies.barman.daybook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.models.DailyBook;

import org.joda.time.DateTime;

import java.util.List;

public class DailybookAdapter extends RecyclerView.Adapter<DailybookAdapter.DailybookViewHolder> {

    private final List<DailyBook> list;
    private final ModelcallBack modelcallBack;

    public DailybookAdapter( ModelcallBack modelcallBack,List<DailyBook> list) {
        this.list = list;
        this.modelcallBack = modelcallBack;
    }

    @NonNull
    @Override
    public DailybookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_dailybook,parent,false);
        return new DailybookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailybookViewHolder holder, int position) {
        holder.tv_date.setText(new DateTime(list.get(position).getDate()).toString("dd MMMM yyyy"));
        holder.itemView.setOnClickListener(v->modelcallBack.onModelClick(list.get(position)));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class DailybookViewHolder extends RecyclerView.ViewHolder{
        private final TextView tv_date;

        public DailybookViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }

    interface ModelcallBack {
        void onModelClick(DailyBook dailyBook);
    }
}
