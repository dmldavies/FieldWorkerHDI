package com.example.titomi.workertrackerloginmodule.supervisor.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVWriter;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Font;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


/**
 * Created by NeonTetras on 23-Feb-18.
 */


public class ExcelExporter extends android.os.AsyncTask<String ,String, String>{
    private  final ProgressDialog dialog;
    private String fileName;
    private final Context cxt;
    private String[] header;
    private ArrayList<String[]> data;

        public ExcelExporter(Context cxt, String[] header, ArrayList<String[]> data){
            this.cxt = cxt;
            dialog = new ProgressDialog(cxt);
            SimpleDateFormat dtf = new SimpleDateFormat("yyyymmddhhmmss");
            String dateText = dtf.format(new Date());
            fileName =  cxt.getString(R.string.app_name)
                    .replace(" ","_")+""+dateText+"_Export.xls";
            this.header = header;
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting XlS...");
            this.dialog.show();
        }

        protected String doInBackground(final String... args){
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file = new File(exportDir, fileName);
            try {

                file.createNewFile();
                WorkbookSettings wbSettings = new WorkbookSettings();
                wbSettings.setLocale(new Locale("en", "EN"));
                WritableWorkbook workbook;
                workbook = Workbook.createWorkbook(file, wbSettings);
                //Excel sheet name. 0 represents first sheet
                WritableSheet sheet = workbook.createSheet("Report", 0);
                // column and row
                WritableFont arial10font = new WritableFont(WritableFont.ARIAL, 11, Font.BOLD);
                WritableCellFormat arial10format = new WritableCellFormat(arial10font);


                try {
                    int k = 0;
                for(String heading : header) {
                    sheet.addCell(new Label(k,0,heading.toUpperCase(),arial10format));
                    k++;
                }





                //write the data

                for(int i = 0; i< data.size(); i++){
                    String[] da = data.get(i);

                    for(int j = 0; j<da.length; j++){
                        sheet.addCell(new Label(j,(i+1),da[j]));
                    }
                }

                    CellView cell;
                    for(int i = 0; i<sheet.getColumns(); i++){
                        cell = sheet.getColumnView(i);
                        cell.setAutosize(true);
                        sheet.setColumnView(i, cell);
                    }

                    workbook.write();
                    workbook.close();

                } catch (WriteException e) {
                    e.printStackTrace();
                }

                return file.getAbsolutePath();
            }
            catch (IOException e){
                Log.e(this.getClass().getName(), e.getMessage(), e);
                return "";
            }
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(final String success) {

            if (this.dialog.isShowing()){
                this.dialog.dismiss();
            }
            if (!success.isEmpty()){
                AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"View file",(v,which)->{
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(success), "application/vnd.ms-excel");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                        cxt.startActivity(intent);
                    }
                    catch (ActivityNotFoundException e) {
                        Toast.makeText(cxt, "No Application Available to View Excel",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Dismiss",(v,which)->{
                    alertDialog.dismiss();
                });

                alertDialog.setMessage("Export successful!\r\nFile saved to: "+success+" Open?");

                alertDialog.show();
            }
            else {
                Toast.makeText(cxt, "Export failed!", Toast.LENGTH_SHORT).show();
            }
        }


}

