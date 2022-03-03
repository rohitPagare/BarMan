package com.orhotechnologies.barman.item.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.databinding.ModelImportitemBinding;
import com.orhotechnologies.barman.di.FireStoreModule;
import com.orhotechnologies.barman.item.model.Items;

public class ImportItemsFirestoreAdapter extends FirestorePagingAdapter<Items, ImportItemsFirestoreAdapter.ImportItemsViewHolder> {

    private final FireStoreModule fireStoreModule;

    private final OnListImportItemClick onListImportItemClick;

    public ImportItemsFirestoreAdapter(@NonNull FirestorePagingOptions<Items> options,OnListImportItemClick onListImportItemClick,FireStoreModule fireStoreModule) {
        super(options);
        this.onListImportItemClick = onListImportItemClick;
        this.fireStoreModule = fireStoreModule;
    }

    @Override
    protected void onBindViewHolder(@NonNull ImportItemsViewHolder holder, int position, @NonNull Items model) {
        holder.onBind(model,fireStoreModule);
        //check if model users contain userphone then clickable false
        holder.itemView.setOnClickListener(v->{
            if(model.getUsersIds()!=null && model.getUsersIds().contains(fireStoreModule.getFirebaseUser().getPhoneNumber())) return;
            onListImportItemClick.itemAddClick(model);
        });
    }



    @NonNull
    @Override
    public ImportItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ModelImportitemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.model_importitem,parent,false);
        return new ImportItemsViewHolder(binding);
    }

    static class ImportItemsViewHolder extends RecyclerView.ViewHolder{

        ModelImportitemBinding binding;

        public ImportItemsViewHolder(ModelImportitemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void onBind(Object obj,FireStoreModule fireStoreModule){
            binding.setModelimportitem((Items) obj);
            binding.setUserphone(fireStoreModule.getFirebaseUser().getPhoneNumber());
            binding.executePendingBindings();
        }
    }

    public interface OnListImportItemClick{

        void itemAddClick(Items items);
    }
}
