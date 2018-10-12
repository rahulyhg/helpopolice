package com.rvsoftlab.helpopolice.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Helper {
    public static String getCurrentDateTime(){
        Calendar now = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        return dateFormat.format(now.getTime());
    }
}
