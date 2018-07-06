package com.example.titomi.workertrackerloginmodule.supervisor.util;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NeonTetras on 02-Sep-17.
 */
public class InputValidator {
    public static String validateEmail(EditText editText) throws InvalidInputException{
        String email = editText.getText().toString().trim();
        String email_regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        String sampleEmail = "example@domain.com";
        Pattern validEmailRegex = Pattern.compile(email_regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = validEmailRegex.matcher(email);


        if(matcher.find()){
            return email;
        }else{

            editText.requestFocus();

            throw new InvalidInputException(String.format("Email '%s' is invalid.\nSupply email as %s",email,sampleEmail));

        }


    }

    public static String validatePhone(EditText editText) throws InvalidInputException{
        //Accept either 8,11, or 12 numbers long phone number
        String phone = editText.getText().toString().trim();
        String phone_regex = "(\\d{8})|(\\d{11})|(\\d{12})";
        String samplePhone = "1234567890";
        Pattern validEmailRegex = Pattern.compile(phone_regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = validEmailRegex.matcher(phone);
        if(matcher.find()){
            return phone;
        }else{

            editText.requestFocus();

            throw new InvalidInputException(String.format("Phone number '%s' is invalid.\nSupply number as %s",phone,samplePhone));
        }

    }
    /**
     * Validate an input text from editText
     * */
    public static String validateText(EditText editText,int validLength) throws InvalidInputException{
        String text = editText.getText().toString().trim();
        if(!text.isEmpty() && text.length() >= validLength){
            return text;
        }else{

            editText.requestFocus();

            throw new InvalidInputException(String.format("Input '%s' is too short.\n" +
                    "Input should be longer than %d characters",
                    text,
                    validLength));

        }

    }


    /**
     * Validate a  Date from editText
     * */
    public static Date validateDate(EditText editText) throws InvalidInputException, ParseException {

        int validLength = 10;
        String text = editText.getText().toString().trim();
        if(!text.isEmpty() && text.length() >= validLength){
            return new SimpleDateFormat("yyyy-mm-dd").parse(text);
        }else{

            editText.requestFocus();

            throw new InvalidInputException(String.format("Input '%s' is too short.\n" +
                            "Input should be longer than %d characters",
                    text,
                    validLength));

        }

    }
    /**
     * Validate an input text string
     * */
    public static String validateText(String inputText,int validLength) throws InvalidInputException{
        String text = inputText.trim();
        if(!text.isEmpty() && text.length() >= validLength){
            return text;
        }else{

            throw new InvalidInputException(String.format("Input '%s' is too short.\n" +
                            "Input should be longer than %d characters",
                    text,
                    validLength));

        }

    }

    public static String validateWebsite(EditText editText) throws InvalidInputException{
        String website = editText.getText().toString().trim();
        //String email_regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        String website_regex = "^http|https://[A-Z0-9._-]+.[A-Z]{2,6}$";
        String sampleWebsite = "https://example.com or http://example.com";
        Pattern validWebsiteRegex = Pattern.compile(website_regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = validWebsiteRegex.matcher(website);
        if(matcher.find()){
            return website;
        }else{

            editText.requestFocus();

            throw new InvalidInputException(String.format("Website '%s' is invalid.\nSupply website as %s",website,sampleWebsite));

        }

    }
    public static String validatePassword(EditText editText) throws InvalidInputException{
        //Accept either 8,11, or 12 numbers long phone number
        String password = editText.getText().toString().trim();
        String passwordRegex = "\\w+";
        String samplePassword = "abc509";
        Pattern validEmailRegex = Pattern.compile(passwordRegex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = validEmailRegex.matcher(password);
        if(matcher.find()){
            return password;
        }else{

            editText.requestFocus();

            throw new InvalidInputException(String.format("%s not valid.\nPassword should contain alphanumeric characters.\ne.g %s",password,
                    samplePassword));
        }

    }
    public static int validateSpinner(Spinner spinner,int invalidPosition ) throws InvalidInputException{
        int position = spinner.getSelectedItemPosition();
        if(position != invalidPosition){
           return position;

        }else{
            spinner.requestFocus();

            throw new InvalidInputException("Invalid selection");
        }

    }
  public static  class InvalidInputException extends Exception{
        public InvalidInputException(){
            super();
        }
        public InvalidInputException(String message){
            super(message);
        }
        public InvalidInputException(Throwable cause){
            super(cause);
        }
        public InvalidInputException(String message, Throwable cause){
            super(message,cause);
        }
    }

    private static void refreshEditTextBackgroundColor(EditText editText){
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // v.setBackgroundColor(android.R.drawable.editbox_background);
            }
        });
    }
}

