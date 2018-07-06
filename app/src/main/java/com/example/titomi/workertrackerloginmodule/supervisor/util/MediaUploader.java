package com.example.titomi.workertrackerloginmodule.supervisor.util;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by NeonTetras on 12-Sep-17.
 */
public class MediaUploader extends AsyncTask<ArrayList<String>,Integer,List<String>> {
    String uploadHandler;
    Context cxt;

    public MediaUploader(Context cxt, String uploadApiUrl){
        this.cxt = cxt;
        uploadHandler =uploadApiUrl;

    }
    private List<String> uploadFile(ArrayList<String> imgPaths) {

        String charset = "UTF-8";


        File sourceFile[] = new File[imgPaths.size()];
        for (int i=0;i<imgPaths.size();i++){
            sourceFile[i] = new File(imgPaths.get(i));

        }

        String requestURL = uploadHandler;

        try {
            FileUploader multipart = new FileUploader(requestURL, charset);

            multipart.addHeaderField("User-Agent", "CodeJava");
            multipart.addHeaderField("Test-Header", "Header-Value");

            multipart.addFormField("description", "Cool Pictures");
            multipart.addFormField("keywords", "Java,upload,Spring");

            for (int i=0;i<imgPaths.size();i++){
                multipart.addFilePart("uploaded_file[]", sourceFile[i]);
                publishProgress(i);
            }



            List<String> response = multipart.finish();

            System.out.println("SERVER REPLIED:");

            for (String line : response) {
                System.out.println(line);
            }
            return response;
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return null;
    }
    @Override
    protected void onPreExecute(){

    }
    @Override
    protected void onProgressUpdate(Integer... progress){
        super.onProgressUpdate(progress);


    }
    @Override
    protected void onCancelled(){

    }

    @Override
    protected List<String> doInBackground(ArrayList<String>... params) {
        return uploadFile(params[0]);
    }


}
