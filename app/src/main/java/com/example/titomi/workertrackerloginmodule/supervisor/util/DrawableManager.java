package com.example.titomi.workertrackerloginmodule.supervisor.util;


/*
 Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class DrawableManager {
    private static final Map<String, Drawable> drawableMap   = new HashMap<>();
    private static final Map<String, Uri> uriMap   = new HashMap<>();
/*
    public DrawableManager() {
        drawableMap = new HashMap<String, Drawable>();
    }
*/
    public Drawable fetchDrawable(String urlString) {
        if (drawableMap.containsKey(urlString)) {
            return drawableMap.get(urlString);
        }

        Log.d(this.getClass().getSimpleName(), "image url:" + urlString);
        try {
            InputStream is = fetch(urlString);
            Drawable drawable = Drawable.createFromStream(is, "src");


            if (drawable != null) {
                drawableMap.put(urlString, drawable);
                Log.d(this.getClass().getSimpleName(), "got a thumbnail drawable: " + drawable.getBounds() + ", "
                        + drawable.getIntrinsicHeight() + "," + drawable.getIntrinsicWidth() + ", "
                        + drawable.getMinimumHeight() + "," + drawable.getMinimumWidth());
            } else {
                Log.w(this.getClass().getSimpleName(), "could not get thumbnail");
            }

            return drawable;
        } catch (IOException | HttpException | URISyntaxException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        }
    }
    public static final int TEXTVIEW_DRAWABLE_RIGHT = 0;
    public static final int TEXTVIEW_DRAWABLE_LEFT= 1;
    public static final int TEXTVIEW_DRAWABLE_TOP = 2;
    public static final int TEXTVIEW_DRAWABLE_BOTTOM = 3;
    public void fetchTextViewDrawableOnThread(final String urlString, final TextView textView, final int which) {
        if (drawableMap.containsKey(urlString)) {

            switch (which) {
                case TEXTVIEW_DRAWABLE_BOTTOM:

                        textView.setCompoundDrawables(null, null, null, drawableMap.get(urlString));

                    break;
                case TEXTVIEW_DRAWABLE_LEFT:

                        textView.setCompoundDrawables(drawableMap.get(urlString), null, null, null);

                    break;
                case TEXTVIEW_DRAWABLE_RIGHT:

                        textView.setCompoundDrawables(null, null, drawableMap.get(urlString), null);

                    break;
                case TEXTVIEW_DRAWABLE_TOP:

                        textView.setCompoundDrawables(null, drawableMap.get(urlString), null, null);

                    break;
            }
    }


        final  Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (which){
                    case TEXTVIEW_DRAWABLE_BOTTOM:
                          textView.setCompoundDrawables(null, null, null, (Drawable) message.obj);
                        break;
                    case TEXTVIEW_DRAWABLE_LEFT:

                            textView.setCompoundDrawables((Drawable) message.obj,null,null,null);

                        break;
                    case TEXTVIEW_DRAWABLE_RIGHT:

                            textView.setCompoundDrawables(null,null,(Drawable) message.obj,null);

                        break;
                    case TEXTVIEW_DRAWABLE_TOP:

                            textView.setCompoundDrawables(null,(Drawable) message.obj,null,null);

                        break;
                }

            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                //TODO : set imageView to a "pending" image
                Drawable drawable = fetchDrawable(urlString);
                Message message = handler.obtainMessage(1, drawable);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    public void fetchDrawableOnThread(final String urlString, final ImageView imageView) {
        if (drawableMap.containsKey(urlString)) {
            imageView.setImageDrawable(drawableMap.get(urlString));

        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                imageView.setImageDrawable((Drawable) message.obj);


            }
        };

        Thread thread = new Thread() {

            @Override
            public void run() {
                //TODO : set imageView to a "pending" image
                Drawable drawable = fetchDrawable(urlString);
                Message message = handler.obtainMessage(1, drawable);
                handler.sendMessage(message);

            }
        };
        thread.start();
    }


    private InputStream fetch(String urlString) throws MalformedURLException, IOException, URISyntaxException, HttpException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }
}
