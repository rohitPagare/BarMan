package com.orhotechnologies.barman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.orhotechnologies.barman.daybook.ShowDailybook;
import com.orhotechnologies.barman.items.ShowItems;
import com.orhotechnologies.barman.items.ShowStocks;
import com.orhotechnologies.barman.models.DailyBook;
import com.orhotechnologies.barman.purchase.ShowPurchase;
import com.orhotechnologies.barman.sell.ShowSell;
import com.orhotechnologies.barman.traders.ShowTraders;

import java.util.Arrays;
import java.util.List;

import static com.orhotechnologies.barman.items.Items_Constant.DB_ITEMS;

public class Dashboard extends AppCompatActivity implements OnMenuClickListner {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setUi();
        startlisteners();
    }

    private void setUi(){
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new MenuAdapter(Arrays.asList(Utilities.dashmenus),this));
    }

    private void startlisteners(){
        Utilities.getUserRef().collection("traders").addSnapshotListener((value, error) -> { });
        Utilities.getUserRef().collection(DB_ITEMS).addSnapshotListener((value, error) -> { });
    }

    @Override
    public void openItems() {
        startActivity(new Intent(this, ShowItems.class));
    }

    @Override
    public void openStock() {
        startActivity(new Intent(this, ShowStocks.class));
    }

    @Override
    public void openTraders() {
        startActivity(new Intent(this, ShowTraders.class));
    }

    @Override
    public void openPurchase() {
        startActivity(new Intent(this, ShowPurchase.class));
    }

    @Override
    public void openSell() {
        startActivity(new Intent(this, ShowSell.class));
    }

    @Override
    public void openDailybook() {
        startActivity(new Intent(this, ShowDailybook.class));
    }

    @Override
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,Welcome.class));
        this.finish();
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
                    case 1: onMenuClickListner.openStock();
                        break;
                    case 2: onMenuClickListner.openTraders();
                        break;
                    case 3: onMenuClickListner.openPurchase();
                        break;
                    case 4: onMenuClickListner.openSell();
                        break;
                    case 5: onMenuClickListner.openDailybook();
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                    case 10: onMenuClickListner.signOut();
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

}

interface OnMenuClickListner{
    void openItems();
    void openStock();
    void openTraders();
    void openPurchase();
    void openSell();
    void openDailybook();
    void signOut();
}