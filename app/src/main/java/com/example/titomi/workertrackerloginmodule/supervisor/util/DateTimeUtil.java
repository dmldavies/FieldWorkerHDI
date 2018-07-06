package com.example.titomi.workertrackerloginmodule.supervisor.util;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by NeonTetras on 13-Feb-18.
 */
public class DateTimeUtil {
    public static void showDatePicker(final Context cxt,final EditText dateEditText){
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        final int day = now.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(cxt, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
               // Toast.makeText(cxt,String.format("%02d/%02d/%4d",dayOfMonth,monthOfYear+1,year),Toast.LENGTH_LONG).show();
                dateEditText.setText(String.format(Locale.ENGLISH,"%d/%02d/%02d",year,monthOfYear+1,dayOfMonth));
            }
        },year,month,day);
        dialog.setTitle("Select Date");
        dialog.show();
        //  DatePicker datePicker = new DatePicker(this);
        //datePicker.
    }

    public static void showTimePicker(Context cxt, final EditText timeEditText){
        Calendar now = Calendar.getInstance();
        final int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(cxt, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeEditText.setText(String.format("%02d:%02d",hourOfDay,minute));
            }
        },hour,minute,true);
        dialog.setTitle("Select Time");
        dialog.show();

    }
}
