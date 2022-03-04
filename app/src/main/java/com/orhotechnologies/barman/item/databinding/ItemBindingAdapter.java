package com.orhotechnologies.barman.item.databinding;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.google.android.material.textfield.TextInputEditText;
import com.orhotechnologies.barman.item.ItemConstants;
import com.orhotechnologies.barman.item.model.Items;
import com.orhotechnologies.barman.item.model.OfferPrices;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ItemBindingAdapter {

    @BindingAdapter(value = {"offerpricelist", "offernameforprice"})
    public static void setOfferPrice(TextInputEditText inputEditText, List<OfferPrices> list, String offername) {
        if (list == null || list.isEmpty()) {
            inputEditText.setText("");
            return;
        }
        Optional<OfferPrices> optional = list.stream().filter(o -> o.getName().equals(offername)).findFirst();
        if (optional.isPresent()) {
            inputEditText.setText(String.valueOf(((int) Math.round(optional.get().getPrice()))));
        } else {
            inputEditText.setText("");
        }

    }

    @BindingAdapter("datetime")
    public static void setDateTime(TextView textView, Date date) {
        if (date == null) {
            textView.setText("");
            return;
        }
        String DATE_FORMAT = "dd MMM yyyy   hh:mm a";
        DateTime dateTime = new DateTime(date.getTime());
        textView.setText(dateTime.toString(DATE_FORMAT));
    }

    @BindingAdapter(value = {"stock", "appendStart"})
    public static void setStock(TextView textView, Items item, String extra) {
        //check item
        if (item == null) {
            textView.setText("");
            return;
        }

        //if type food set blank
        switch (item.getType()) {
            case ItemConstants.TYPE_FOOD:
                String s = extra + "Unlimited";
                textView.setText(s);
                break;
            //if type is liquor set bottle box loos
            case ItemConstants.TYPE_LIQUOR:
                textView.setText(extra.concat(getStockForLiquor(item)));
                break;
            //if type is other set single box
            case ItemConstants.TYPE_OTHER:
                textView.setText(extra.concat(getStockForOther(item)));
                break;
        }
    }

    private static String getStockForLiquor(Items item) {
        long box = 0, bottle = 0, loose = 0;
        long stock = item.getStock();
        long boxml = ((long) item.getBob() * item.getBom());
        do {
            if (stock >= boxml) {
                box = stock / boxml;
                stock = stock % boxml;
            } else if (stock >= item.getBom()) {
                bottle = stock / item.getBom();
                stock = stock % item.getBom();
            } else {
                loose = stock;
                stock = 0;
            }
        } while (stock != 0);

        String s = (box != 0 ? "BOX: " + box + ",  " : "")
                .concat(bottle != 0 ? "BOTTLE: " + bottle + ",  " : "")
                .concat(loose != 0 ? "LOOSE: " + loose + "ML" : "");
        s = s.endsWith(",  ") ? s.substring(0, s.lastIndexOf(",  ")) : s;
        return s.isEmpty() ? "No Stock" : s;
    }

    private static String getStockForOther(Items item) {
        long single = 0, box = 0;
        long stock = item.getStock();
        do {
            if (item.getBos()!=0 && stock > item.getBos()) {
                box = stock / item.getBos();
                stock = stock % item.getBos();
            } else {
                single = stock;
                stock = 0;
            }

        } while (stock != 0);

        String s = (box != 0 ? "BOX: " + box + ",  " : "")
                .concat(single != 0 ? "SINGLE: " + single + ",  " : "");
        s = s.endsWith(",  ") ? s.substring(0, s.lastIndexOf(",  ")) : s;
        return s.isEmpty() ? "No Stock" : s;
    }
}
