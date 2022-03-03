package com.orhotechnologies.barman.sell.databinding;

import android.view.View;
import android.widget.AutoCompleteTextView;

import androidx.databinding.InverseMethod;

import com.google.android.material.textfield.TextInputEditText;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.ui.ExposedDropdownMenu;

public class Converter {

    @InverseMethod("stringToPrice")
    public static String priceToString(TextInputEditText view, double oldValue, double value){
        return value==0?"":String.valueOf(value);
    }

    public static double stringToPrice(TextInputEditText view,double oldValue,String value){
        try {
            //if string is empty
            if(value.isEmpty())return 0;
            //else return value
            return Double.parseDouble(value);
        }catch (NumberFormatException e){
            //if get exception show error
            view.setError(view.getContext().getString(R.string.valide_price));
            return oldValue;
        }
    }

    @InverseMethod("stringToQuantity")
    public static String quantityToString(TextInputEditText view, long oldValue, long value){
        return value==0?"":String.valueOf(value);
    }

    public static long stringToQuantity(TextInputEditText view, long oldValue, String value){
        try {
            //if string is empty
            if(value.isEmpty())return 0;
            //else return value
            return Long.parseLong(value);
        }catch (NumberFormatException e){
            //if get exception show error
            view.setError(view.getContext().getString(R.string.valide_quantity));
            return oldValue;
        }
    }

    @InverseMethod("stringToInt")
    public static String intToString(View view, int oldValue, int value){
        return value==0?"":String.valueOf(value);
    }

    public static int stringToInt(View view, int oldValue, String value){
        try {
            //if string is empty
            if(value.isEmpty())return 0;
            //else return value
            return Integer.parseInt(value);
        }catch (NumberFormatException e){
            //if get exception show error
            if(view instanceof ExposedDropdownMenu)
                ((ExposedDropdownMenu)view).setError(view.getContext().getString(R.string.valide_number));
            if(view instanceof TextInputEditText)
                ((TextInputEditText)view).setError(view.getContext().getString(R.string.valide_number));
            return oldValue;
        }
    }



}
