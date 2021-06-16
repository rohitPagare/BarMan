package com.orhotechnologies.barman;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Locale;

public class Utilities {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String[] dashmenus = {"Items","Stock","Traders","Purchase","Sell","DayBook","Counter Cash","Expenses","Profile","Settings","SignOut"};

    public static final String KEY_USER="user";

    public static final String DB_USER = "users";
    public static final String DB_PURCHASEBILL = "purchasebills";

    public static DocumentReference getUserRef() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        return currentUser != null ? db.collection(Utilities.DB_USER).document(currentUser.getEmail()) : null;
    }

    public static boolean isInternetNotAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork == null || !activeNetwork.isConnectedOrConnecting();
    }

    public static void showSnakeBar(Activity activity, String error) {
        View parentView = activity.findViewById(android.R.id.content);
        Snackbar.make(parentView, error, 2000).show();
    }

    public static String getDoubleFormattedValue(double value) {
        return value % 1 == 0 ? String.valueOf((int) value) : String.format(Locale.US,"%.2f",value);
    }

    public static String getRupeeFromDouble(double value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        return formatter.format(value).replace("₹", "");
    }

    public static String getFirstCharacterCapital(String str){
        // get First letter of the string
        String firstLetStr = str.substring(0, 1);
        // Get remaining letter using substring
        String remLetStr = str.substring(1);
        // convert the first letter of String to uppercase
        firstLetStr = firstLetStr.toUpperCase();
        //convert the other letter of string to lowercase
        remLetStr = remLetStr.toLowerCase();
        // concantenate the first letter and remaining string
        return firstLetStr + remLetStr;
    }

}
