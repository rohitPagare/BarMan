package com.orhotechnologies.barman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
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

        //validate values
        if(name==null || name.isEmpty())
            Utilities.showSnakeBar(this,"Please Enter Name");
        else if(phone==null || phone.isEmpty())
            Utilities.showSnakeBar(this,"Please Enter Phone");
        else if(hotelname==null || hotelname.isEmpty())
            Utilities.showSnakeBar(this,"Please Enter Hotel Name");
        else if (!phone.matches("\\d{10}")) {
            Utilities.showSnakeBar(this, "Enter correct phone number");
        }else {

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            User user = new User(currentUser.getUid(),name,phone,currentUser.getEmail(),hotelname,false);
            FirebaseFirestore.getInstance()
                    .collection(Utilities.DB_USER)
                    .document(currentUser.getEmail())
                    .set(user)
                    .addOnSuccessListener(this, aVoid -> {
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