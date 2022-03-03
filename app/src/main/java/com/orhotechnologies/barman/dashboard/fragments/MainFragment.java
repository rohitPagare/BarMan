package com.orhotechnologies.barman.dashboard.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.welcome.Welcome;
import com.orhotechnologies.barman.databinding.FragmentMainBinding;
import com.orhotechnologies.barman.daybook.Activity_Daybook;
import com.orhotechnologies.barman.di.FireStoreModule;
import com.orhotechnologies.barman.item.Activity_Items;
import com.orhotechnologies.barman.sell.Activity_Sell;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainFragment extends Fragment implements OnMenuClickListner {

    private FragmentMainBinding binding;

    @Inject
    FireStoreModule fireStoreModule;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUi();
    }

    private void setUi(){
        RecyclerView recyclerView = binding.recyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new MenuAdapter(Arrays.asList(getResources().getStringArray(R.array.DashMenus)),this));
    }

      private static class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder>{
        private final List<String> menus;
        private final OnMenuClickListner onMenuClickListner;

        MenuAdapter(List<String> menus,OnMenuClickListner onMenuClickListner){
            this.menus = menus;
            this.onMenuClickListner = onMenuClickListner;
        }

        @NonNull
        @Override
        public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_dashmenu,parent,false);
            return new MenuViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
            holder.tv_menuname.setText(menus.get(position));
            holder.tv_menuname.setOnClickListener(v -> {
                switch (position){
                    case 0: onMenuClickListner.openItems();
                        break;
                    case 1: onMenuClickListner.openTraders();
                        break;
                    case 2: onMenuClickListner.openCustomers();
                        break;
                    case 3: onMenuClickListner.openPurchase();
                        break;
                    case 4: onMenuClickListner.openNewSell();
                        break;
                    case 5: onMenuClickListner.openSell();
                        break;
                    case 6: onMenuClickListner.openDailybook();
                        break;
                    case 7: onMenuClickListner.signOut();
                        break;
                }
            });
        }

        @Override
        public int getItemCount() {
            return menus.size();
        }

        private static class MenuViewHolder extends RecyclerView.ViewHolder{
            private final TextView tv_menuname;

            public MenuViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_menuname = itemView.findViewById(R.id.tv_menuname);
            }
        }

    }

    @Override
    public void openItems() {
        startActivity(new Intent(requireActivity(), Activity_Items.class));
    }

    @Override
    public void openTraders() {
        //startActivity(new Intent(requireActivity(), ShowTraders.class));
    }

    @Override
    public void openCustomers(){
        //
    }

    @Override
    public void openPurchase() {
        //startActivity(new Intent(requireActivity(), ShowPurchase.class));
    }

    @Override
    public void openNewSell(){
        Intent intent = new Intent(requireActivity(),Activity_Sell.class);
        intent.putExtra("action","newSell");
        startActivity(intent);
    }

    @Override
    public void openSell() {
        startActivity(new Intent(requireActivity(), Activity_Sell.class));
    }

    @Override
    public void openDailybook() {
        startActivity(new Intent(requireActivity(), Activity_Daybook.class));
    }

    @Override
    public void signOut() {
       fireStoreModule.getFirebaseAuth().signOut();
       startActivity(new Intent(requireActivity(), Welcome.class));
       requireActivity().finish();
    }

}

interface OnMenuClickListner{
    void openItems();
    void openTraders();
    void openCustomers();
    void openPurchase();
    void openNewSell();
    void openSell();
    void openDailybook();
    void signOut();
}


