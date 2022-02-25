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

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import io.reactivex.rxjava3.exceptions.UndeliverableException;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class Utility {
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    public static final String response_success = "SUCCESS";
    public static final String response_error = "ERROR";

    //tables
    public static final String DB_USER = "users";
    public static final String DB_MEMBER = "members";
    public static final String DB_ITEMS = "items";
    public static final String DB_ITEMTRADE = "itemtrade";
    public static final String DB_SELLS = "sells";
    public static final String DB_DAYBOOK = "daybook";

    //tradetype
    public static final String TRADE_STOCK_ADDED="STOCK ADDED";
    public static final String TRADE_STOCK_REMOVED="STOCK REMOVED";
    public static final String TRADE_SELL_ADDED="SELL ADDED";
    public static final String TRADE_SELL_REMOVED="SELL REMOVED";
    public static final String TRADE_PURCHASE_ADDED="PURCHASE ADDED";
    public static final String TRADE_PURCHASE_REMOVED="PURCHASE REMOVED";



    public static DocumentReference getUserRef() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        return currentUser != null ? db.collection(Utility.DB_USER).document(currentUser.getEmail()) : null;
    }

    //return internet is not availble
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
        return value % 1 == 0 ? String.valueOf((int) value) : String.format(Locale.US, "%.2f", value);
    }

    public static String getRupeeFromDouble(double value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        return formatter.format(value).replace("₹", "");
    }

    // Function to check if a string contains only letters
    public static boolean isLetterString(String s) {
        if (s == null) // checks if the String is null {
            return false;

        int len = s.length();
        for (int i = 0; i < len; i++) {

            // checks whether the character is not a letter
            // if it is not a letter then check for space
            if (!Character.isLetter(s.charAt(i))) {
                //check if it is not space then return false
                if (!Character.isSpaceChar(s.charAt(i)))
                    return false;
            }
        }
        return true;
    }

    // Function to check if a string contains only digits
    public static boolean notDigitsString(String s) {
        if (s == null || s.isEmpty()) // checks if the String is null {
            return true;

        int len = s.length();

        for (int i = 0; i < len; i++) {
            // checks whether the character is not a digit
            // if it is not a digit then return false
            if (!Character.isDigit(s.charAt(i)))
                return true;
        }
        return false;
    }

    public static void handleRXJavaErrors() {
        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                e.getMessage();
            }
            if (e instanceof IOException) {
                return;
            }
            if (e instanceof InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                e.getMessage();
                return;
            }
            if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
                // that's likely a bug in the application
                /* Thread.currentThread().getUncaughtExceptionHandler().handleException(Thread.currentThread(), e);*/
                e.getMessage();
                return;
            }
            if (e instanceof IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                e.getMessage();
            }


        });
    }

}
