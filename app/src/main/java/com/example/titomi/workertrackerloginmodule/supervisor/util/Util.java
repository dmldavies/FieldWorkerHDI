package com.example.titomi.workertrackerloginmodule.supervisor.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.example.titomi.workertrackerloginmodule.supervisor.activities.CustomPhotoGalleryActivity;



/**
 * Created by NeonTetras on 28-Sep-17.
 */
public class Util {

    static int imgIndex = 0;
    private static ArrayList<String> images = new ArrayList();
    private static final int MAX_IMAGE = 6;


    public static Uri getResourcePath(Context cxt, int resource) {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                cxt.getResources().getResourcePackageName(resource) +
                "/" + cxt.getResources().getResourceTypeName(resource) +
                "/" + cxt.getResources().getResourceEntryName(resource));
        return imageUri;
    }


    public static String toDateString(Date date) {
        return new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault()).format(date);
    }

    public static void showPlacesPicker(Context cxt) {
        //  int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            ((Activity) cxt).startActivityForResult(builder.build(((Activity) cxt)), PICK_PLACES);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public static void showDatePicker(final Context cxt, final EditText dateEditText) {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        final int day = now.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dialog = new DatePickerDialog(cxt, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                dateEditText.setText(String.format("%02d/%02d/%4d", dayOfMonth, monthOfYear + 1, year));
            }
        }, year, month, day);
        dialog.setTitle("Select Date");
        dialog.show();
        //  DatePicker datePicker = new DatePicker(this);
        //datePicker.
    }

    public static void showTimePicker(Context cxt, final EditText timeEditText) {
        Calendar now = Calendar.getInstance();
        final int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(cxt, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeEditText.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        }, hour, minute, true);
        dialog.setTitle("Select Time");
        dialog.show();

    }

    public static void pickPhoto(Context cxt) {

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity) cxt).startActivityForResult(pickPhoto, PICK_IMAGE_SINGLE);
    }

    public static void pickMultiPhoto(Context cxt) {
        if (!canTakeImages()) {
            Toast.makeText(cxt, String.format("Can't accept more images.\nMaximum number of image is %d", MAX_IMAGE), Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(cxt, CustomPhotoGalleryActivity.class);
        ((Activity) cxt).startActivityForResult(intent, PICK_IMAGE_MULTIPLE);


    }

    public static void clearEditTexts(EditText... editTexts) {
        for (EditText ed : editTexts) {
            ed.setText("");
        }
    }

    public static void clearSpinner(Spinner... spinners) {
        for (Spinner spinner : spinners) {
            spinner.setSelection(0);
        }
    }

    private static boolean canTakeImages() {
        return images.size() < MAX_IMAGE;
    }

    public static void viewImages(final Context cxt, View view, final ArrayList<String> images) {

        final int numImages = images.size();

        View dialogLayoutView = View.inflate(cxt, R.layout.view_images_layout, null);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(cxt);
        alertDialog.setView(dialogLayoutView);
        final AlertDialog alert = alertDialog.create();

        final ImageView imageView = (ImageView) dialogLayoutView.findViewById(R.id.image);
        final ImageView closeButton = (ImageView) dialogLayoutView.findViewById(R.id.close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        final ImageView img = (ImageView) view;
        imageView.setImageDrawable(img.getDrawable());
        imageView.setTag(img.getTag());

        imgIndex = images.indexOf(img.getTag());


        // imgIndex++;


        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);


                //No next image, so return
                //Go back to viewing the first image if we have arrived at the last image
                if (imgIndex >= (numImages - 1)) imgIndex = -1;
                imgIndex++;
                Drawable drw = Drawable.createFromPath(images.get(imgIndex));
                imageView.setImageDrawable(drw);
                imageView.setTag(images.get(imgIndex));

                return false;
            }
        });
        alert.show();


    }

    public static void viewImage(final Context cxt, View view) {


        View dialogLayoutView = View.inflate(cxt, R.layout.view_images_layout, null);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(cxt);
        alertDialog.setView(dialogLayoutView);
        final AlertDialog alert = alertDialog.create();

        final ImageView imageView = (ImageView) dialogLayoutView.findViewById(R.id.image);
        final ImageView closeButton = (ImageView) dialogLayoutView.findViewById(R.id.close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        final ImageView img = (ImageView) view;
        imageView.setImageDrawable(img.getDrawable());


        alert.show();


    }

    public static void viewImagesOverNetwork(final Context cxt, View view, final ArrayList<String> images) {

        final int numImages = images.size();

        View dialogLayoutView = View.inflate(cxt, R.layout.view_images_layout, null);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(cxt);
        alertDialog.setView(dialogLayoutView);
        final AlertDialog alert = alertDialog.create();

        final ImageView imageView = (ImageView) dialogLayoutView.findViewById(R.id.image);
        final ImageView closeButton = (ImageView) dialogLayoutView.findViewById(R.id.close);
        final ImageView img = (ImageView) view;

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        //String imgUrl = cxt.getString(R.string.server_url)+view.getTag().toString();
        imageView.setImageDrawable(img.getDrawable());
        imageView.setTag(img.getTag());

        imgIndex = images.indexOf(img.getTag());

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);


                //No next image, so return
                //Go back to viewing the first image if we have arrived at the last image
                if (imgIndex == (numImages - 1)) imgIndex = -1;
                imgIndex++;
                DrawableManager drm = new DrawableManager();
                drm.fetchDrawableOnThread(cxt.getString(R.string.api_url) + images.get(imgIndex), imageView);
                //  Drawable drw = Drawable.createFromPath(images.get(imgIndex));
                // imageView.setImageDrawable(drw);
                imageView.setTag(images.get(imgIndex));


                return false;
            }
        });
        alert.show();


    }

    public static boolean isMyServiceRunning(Context cxt,Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void requestPermission(Context cxt, int requestCode) {
        // this.video_map_or_image = requestCode;

        switch (requestCode) {

            case PICK_VIDEO:



                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                takeVideoIntent.putExtra("android.intent.extra.durationLimit", 10);
                if (takeVideoIntent.resolveActivity(cxt.getPackageManager()) != null) {
                    ((Activity)cxt).startActivityForResult(takeVideoIntent, PICK_VIDEO);
                }

            break;
            case PICK_IMAGE_MULTIPLE:
            case PICK_IMAGE_SINGLE:

                ActivityCompat.requestPermissions(((Activity) cxt),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
                break;
            case PICK_PLACES:
                if (ContextCompat.checkSelfPermission(cxt,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    //mLocationPermissionGranted = true;
                } else {
                    ActivityCompat.requestPermissions(((Activity) cxt),
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
                break;
        }

    }

    public static String getVideoPath(Context cxt,Uri uri){
        String[] projection = {Images.Media.DATA};
        Cursor cursor = ((Activity)cxt).managedQuery(uri, projection, null, null, null);
        if(cursor != null){
            int columnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        }else return  null;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
       inImage.compress(Bitmap.CompressFormat.JPEG, 0, bytes);
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Field Monitor", "Taken from Field Monitor App");
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Context cxt, Uri uri) {
        Cursor cursor = cxt.getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
   /* public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }*/
    public static Address doReverseGeocode(final Context cxt, final Place place) throws IOException {
        //final double lat, final double lon)
        //   ,
        final Address addr1 = new Address(Locale.getDefault());
        new AsyncTask<Void, Void, String>() {
            ProgressDialog progressDialog;

            URL url = new URL(String.format(
                    "https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&key=%s",
                    place.getLatLng().latitude, place.getLatLng().longitude, cxt.getString(R.string.google_api_key)));

            @Override
            protected String doInBackground(Void... latLngs) {

                try {

                    URLConnection conn = url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()
                            ));
                    StringBuilder sb = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    return sb.toString();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(cxt);
                progressDialog.setMessage("Decoding your location\nPlease wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                progressDialog.cancel();
                if (result == null || result.isEmpty()) {
                    Toast.makeText(cxt,
                            "Error occurred while decoding your location details",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                  /*  stateEditText.setText(result.split(",")[0]);
                    cityEditText.setText(result.split(",")[1]);*/
                try {
                    // JsonParser jsonParser = new JsonParser();
                    JSONObject jsonObject = new JSONObject(result);
                    if (!"OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                        Toast.makeText(cxt,
                                "Error occurred while decoding your location details",
                                Toast.LENGTH_LONG).show();
                        return;

                    }

                    for (int i = 1; i < ((JSONArray) jsonObject.get("results")).length() - 2; i++) {
                        JSONArray addressComp = ((JSONArray) jsonObject.get("results")).getJSONObject(i).getJSONArray("address_components");
                        for (int j = 0; j < addressComp.length(); j++) {
                            String neighborhood = ((JSONArray) ((JSONObject) addressComp.get(j)).get("types")).getString(0);
                            if (neighborhood.compareTo("neighborhood") == 0) {
                                String neighborhood1 = ((JSONObject) addressComp.get(j)).getString("long_name");
                                addr1.setSubThoroughfare(neighborhood1);
                            }
                            String locality = ((JSONArray) ((JSONObject) addressComp.get(j)).get("types")).getString(0);
                            if (locality.compareTo("locality") == 0) {
                                String locality1 = ((JSONObject) addressComp.get(0)).getString("long_name");
                                addr1.setLocality(locality1);
                            }
                            String subAdminArea = ((JSONArray) ((JSONObject) addressComp.get(j)).get("types")).getString(0);
                            if (subAdminArea.compareTo("administrative_area_level_2") == 0) {
                                String subAdminArea1 = ((JSONObject) addressComp.get(0)).getString("long_name");
                                addr1.setSubAdminArea(subAdminArea1);

                            }

                            String adminArea = ((JSONArray) ((JSONObject) addressComp.get(j)).get("types")).getString(0);
                            if (adminArea.compareTo("administrative_area_level_1") == 0) {
                                String adminArea1 = ((JSONObject) addressComp.get(0)).getString("long_name");
                                addr1.setAdminArea(adminArea1);
                            }

                            String postalCode = ((JSONArray) ((JSONObject) addressComp.get(j)).get("types")).getString(0);
                            if (postalCode.compareTo("postal_code") == 0) {
                                String postalCode1 = ((JSONObject) addressComp.get(0)).getString("long_name");
                                addr1.setPostalCode(postalCode1);
                            }

                            String subLocality = ((JSONArray) ((JSONObject) addressComp.get(j)).get("types")).getString(0);
                            if (subLocality.compareTo("sublocality") == 0) {
                                String subLocality1 = ((JSONObject) addressComp.get(0)).getString("long_name");
                                addr1.setSubLocality(subLocality1);
                            }

                            String country = ((JSONArray) ((JSONObject) addressComp.get(j)).get("types")).getString(0);
                            if (country.compareTo("country") == 0) {
                                String country1 = ((JSONObject) addressComp.get(0)).getString("long_name");
                                addr1.setCountryName(country1);
                            }
                        }
                    }

                    String address = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getString("formatted_address");//;.get..getJSONObject(4).getString("long_name");//jsonObject.getJSONArray("results").getJSONArray(1).getJSONArray(0).getString(0);
                    //addr1.setAddressLine();
                    addr1.setAddressLine(0, address);
                   /* cityEditText.setText(addr1.getSubAdminArea());
                    stateEditText.setText(addr1.getAdminArea());


                    venueEditText.setText(String.format("%s, %s", place.getName(), address));*/

                } catch (JSONException e) {
                    Toast.makeText(cxt,
                            e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


            }

        }.execute();

        return addr1;
    }

    /**
     * Capitalizes the first characters of the supplied text
     *
     * @param text
     * @return i love you = I Love You
     */
    public static String toSentenceCase(String text) {
        if (text.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        try {
            String[] strs = text.split("\\s");


            for (String textString : strs) {
                String firstCharacter = textString.substring(0, 1);
                String newS = firstCharacter.toUpperCase() + textString.substring(1);
                sb.append(newS);
                sb.append(" ");
            }
            sb.deleteCharAt(sb.lastIndexOf(" "));
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static boolean confirm(Context cxt) {

        AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
        builder.setMessage("Please confirm you want to continue?");
        builder.setTitle("Confirm");
        builder.setNegativeButton(cxt.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _continue = false;
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(cxt.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _continue = true;
                dialog.dismiss();
            }
        });
        builder.create().show();
        return _continue;
    }

    public static <T> T fromJson(Context cxt, String result, Type typeOfT) {
        Gson gson = new GsonBuilder()
                .setDateFormat(cxt.getString(R.string.date_format))
                .setLenient()
                .create();
        Reader reader = new StringReader(result.trim());
        JsonReader jsonReader = new JsonReader(reader);

        return gson.fromJson(jsonReader, typeOfT);

    }

    public static void disableChildrenViews(Context cxt ,boolean isVisible, int parentLayoutId) {


    ViewGroup layout = (ViewGroup) ((Activity)cxt).findViewById(parentLayoutId);
        for(
    int i = 0; i<layout.getChildCount();i++)

    {
        View child = layout.getChildAt(i);
        child.setEnabled(isVisible);
    }

}
    static boolean _continue = false;
    public static final int PICK_IMAGE_SINGLE = 1;
    public static final int PICK_IMAGE_MULTIPLE = 2;
    public static final int PICK_VIDEO = 3;
    public static final int PICK_PLACES = 4;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;


}
