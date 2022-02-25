package com.orhotechnologies.barman.sell.databinding;

import androidx.databinding.InverseMethod;

import com.google.android.material.textfield.TextInputEditText;
import com.orhotechnologies.barman.R;

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

}
