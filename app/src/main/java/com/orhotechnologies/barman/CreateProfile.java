package com.orhotechnologies.barman;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.orhotechnologies.barman.models.Member;
import com.orhotechnologies.barman.models.User;
import com.orhotechnologies.barman.models.Utility;

public class CreateProfile extends AppCompatActivity {

    private EditText edt_name,edt_phone,edt_hotelname;
    private String name,phone,hotelname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createprofile);

        edt_name = findViewById(R.id.edt_name);
        edt_phone = findViewById(R.id.edt_phone);
        edt_hotelname = findViewById(R.id.edt_hotelname);
    }


    public void onSubmit(View view) {
        name = edt_name.getText().toString();
        phone = edt_phone.getText().toString();
        hotelname = edt_hotelname.getText().toString();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //validate values
        if(name.isEmpty())
            Utilities.showSnakeBar(this,"Please Enter Name");
        else if(phone.isEmpty())
            Utilities.showSnakeBar(this,"Please Enter Phone");
        else if(hotelname.isEmpty())
            Utilities.showSnakeBar(this,"Please Enter Hotel Name");
        else if (!phone.matches("\\d{10}")) {
            Utilities.showSnakeBar(this, "Enter correct phone number");
        }else if(currentUser==null){
            FirebaseAuth.getInstance().signOut();
        }else {

            User user = new User(currentUser.getEmail(),hotelname,false);

            DocumentReference userRef = FirebaseFirestore.getInstance()
                    .collection(Utilities.DB_USER).document(currentUser.getEmail());
            DocumentReference memberRef = userRef.collection("members").document(currentUser.getUid());
            Member member = new Member();
            member.setDesignation("SuperAdmin");
            member.setName(name);
            member.setPhone("+91"+phone);
            member.setLockmode(false);
            member.setUid(currentUser.getUid());

            FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    transaction.set(userRef,user);
                    transaction.set(memberRef,member);
                    return null;
                }
            }).addOnSuccessListener(this, aVoid -> {
                Intent intent = new Intent(CreateProfile.this,Dashboard.class);
                intent.putExtra(Utilities.KEY_USER,user);
                startActivity(intent);
                //animate
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                //finish this activity
                CreateProfile.this.finish();
            }).addOnFailureListener(this, e -> {
                Utilities.showSnakeBar(CreateProfile.this,"Sorry, Try Later!");
            });

        }

    }
}