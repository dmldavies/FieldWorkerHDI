
package com.example.titomi.workertrackerloginmodule.supervisor.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;



public class ImageUtils {

    public static String getRealPathFromURI(Context cxt, String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = cxt.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath().replace("\"","");
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

            String img = cursor.getString(index);
            cursor.close();
            return img.replace("\"","");

        }

    }
    public static void downloadImages(Entity entity,ArrayList<String> imageUrls) {

        ImageUtils.ImageStorage imageStorage = new ImageUtils.ImageStorage(entity);
        for(String imageUrl : imageUrls) {
            String imageName = ImageUtils.getImageNameFromUrlWithOutExtension(imageUrl);

            if (!imageStorage.imageExists(imageName)) {
                new ImageUtils.GetImages(entity, imageUrl, imageName).execute();
            }
        }
    }

    public static String getImageNameFromUrlWithOutExtension(String imageUrl){
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));
    }

    public static String getImageNameFromUrlWithExtension(String imageUrl){
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }

    /**
     * This method compresses an image if only it does not alread exist in the directory
     * @return ArrayList of String containing the paths to the compressed images
     *
     * @param images (paths to image files)
     * */
    public static ArrayList<String> compressImages(Context cxt, Entity entity, ArrayList<String> images) {
        ImageCompressor imageCompressor = new ImageCompressor(cxt,entity);
        ArrayList<String> compressedImages = new ArrayList<>();
        ImageStorage storage = new ImageStorage(entity);
        for(String img : images) {

            if(!storage.imageExists(ImageUtils.getImageNameFromUrlWithExtension(img))) {
                compressedImages.add(imageCompressor.compressImage(img));
              /*  File file = new File(img);
                file.delete();*/
            }else{
                compressedImages.add(storage.getImage(ImageUtils.getImageNameFromUrlWithExtension(img)).getAbsolutePath());
            }
        }
        return compressedImages;
    }

    /**This method compresses an image if only it does not already exist in the directory
     * @return String containing the path to the compressed image
     * @param image (path to image file)
     * */
    public static String compressImage(Context cxt, Entity entity, String image) {
        ImageCompressor imageCompressor = new ImageCompressor(cxt,entity);
        ImageStorage storage = new ImageStorage(entity);
        String imagePath;
        if(!storage.imageExists(ImageUtils.getImageNameFromUrlWithExtension(image))) {
            imagePath = imageCompressor.compressImage(image);
        }else{
            imagePath = storage.getImage(ImageUtils.getImageNameFromUrlWithExtension(image)).getAbsolutePath();
        }
        return imagePath;
    }

    public static void loadImage(Context cxt, Entity entity, final ImageView imageView){
      ImageStorage  imageStorage = new ImageUtils.ImageStorage(entity);
        String server_url = cxt.getString(R.string.api_url);
        String imageUrl = server_url + entity.getFeaturedImage();
        String imageName = ImageUtils.getImageNameFromUrlWithExtension(imageUrl);
        if(imageStorage.imageExists(imageName)){
            imageView.setImageURI(Uri.parse(imageStorage.getImage(imageName).getAbsolutePath()));
        }else{
            GetImages getImages = new GetImages(entity,imageUrl,imageName){
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(Object obj) {
                    super.onPostExecute(obj);
                    imageView.setImageBitmap((Bitmap)obj);
                }
            };
            getImages.execute();
        }
    }

    public static void loadImage(final Context cxt, Entity entity, final ImageView imageView, String imageUrl, final boolean useProgressDialog){
        ImageStorage  imageStorage = new ImageUtils.ImageStorage(entity);


        String imageName = ImageUtils.getImageNameFromUrlWithExtension(imageUrl);
        if(imageStorage.imageExists(imageName)){
            imageView.setImageURI(Uri.parse(imageStorage.getImage(imageName).getAbsolutePath()));
        }else{
            GetImages getImages = new GetImages(entity,imageUrl,imageName){
                ProgressDialog progressDialog = new ProgressDialog(cxt);
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if(useProgressDialog){
                        progressDialog.setMessage(cxt.getString(R.string.pleaseWait));
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
                }

                @Override
                protected void onPostExecute(Object obj) {
                    super.onPostExecute(obj);

                    if (useProgressDialog) {
                        progressDialog.dismiss();

                    }
                    imageView.setImageBitmap((Bitmap) obj);
                }
            };
            getImages.execute();
        }
    }

    /**
     * Download images supplied in the Arraylist
     * @param entity The entity whose image is to be downloaaded.
     *               This is required so that the image will be saved in its right folder
     * @param cxt Context
     * @param imagesForDownload The ArrayList of images to be downloaded.
     * This list must contain the full url of the images to be downloaded<br>
     * e.g http://example.com/images/users/image.jpg
     * @param useProgressDialog specifies if the dialog should or should not use a Please wait progress dialog
     *
     *
     * */
    public static void downloadImages(final Context cxt,
                                      Entity entity,ArrayList<String>
                                              imagesForDownload, final boolean useProgressDialog){
       ImageStorage imageStorage = new ImageUtils.ImageStorage(entity);
        for(String imageUrl : imagesForDownload) {
            String imageName = ImageUtils.getImageNameFromUrlWithExtension(imageUrl);
            if (!imageStorage.imageExists(imageName)){
                GetImages getImages = new GetImages(entity,imageUrl,imageName){
                    ProgressDialog progressDialog = new ProgressDialog(cxt);
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if(useProgressDialog){
                            progressDialog.setMessage(cxt.getString(R.string.pleaseWait));
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                        }
                    }

                    @Override
                    protected void onPostExecute(Object obj) {
                        super.onPostExecute(obj);

                        if(useProgressDialog){
                            progressDialog.dismiss();
                        }

                    }
                };
                getImages.execute();
            }
        }

    }

    /* public static void updateImage(Context cxt, User user, String imageUri){

         ImageCompressor imageCompressor = new ImageCompressor(cxt,user);
         String compressedImage = imageCompressor.compressImage(imageUri);
         File selectedImgFile = new File(compressedImage);
         //File selectedImgFile = new File(ImageUtils.getRealPathFromURI(this,imageUri.toString()));

         user.setFeaturedImage(selectedImgFile.getName());
         String imageUploadApi = cxt.getString(R.string.server_url)+
                 cxt.getString(R.string.user_image_update_servlet);
         ImageUploader imageUploader = new ImageUploader(cxt,imageUploadApi,user);

         imageUploader.execute(selectedImgFile.getAbsolutePath());


     }*/
    public static class GetImages extends AsyncTask<Object, Object, Object> {

        private String requestUrl, imageName;
        private ImageView view;
        private Bitmap bitmap;
        private FileOutputStream fos;
        private Entity entity;

        /**
         * @param requestUrl
         * @param imageName
         * @param entity
         */
        public GetImages(Entity entity, String requestUrl, String imageName) {
            this.requestUrl = requestUrl;
            this.entity = entity;
            this.imageName = imageName;

        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                URL url = new URL((requestUrl));
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                conn.setDoOutput(true);
                bitmap = BitmapFactory.decodeStream(conn.getInputStream());


            } catch (Exception e) {
                return null;
            }
            return bitmap;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object obj) {
            if (obj == null) return;
            ImageStorage imageStorage = new ImageStorage(entity);
            if (!imageStorage.imageExists(imageName)) {
                imageStorage.saveToSdCard((Bitmap) obj, imageName);
            }
        }


    }

    public static class ImageStorage {
        private static Entity entity;
        private static Context cxt;

        public ImageStorage(Entity entity) {
            this.entity = entity;

        }

        public static <T extends Entity> File getStorageDirectory(T t) {

            File directory = new File(Environment
                    .getExternalStorageDirectory()
                    .getPath(),
                    String.format(".FieldMonitor/Images/%s/",
                            t.getClass().getSimpleName()).toLowerCase());
            if (!directory.exists()) directory.mkdirs();

            return directory;
        }

        public String saveToSdCard(Bitmap bitmap, String fileName) {
            String stored = null;
            File folder = getStorageDirectory(entity);

            File file = new File(folder.getAbsoluteFile(), fileName.endsWith(".jpg") ? fileName : fileName + ".jpg");
            if (file.exists()) return stored;

            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                stored = "success";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stored;
        }

        public File getImage(String imageName) {
            File mediaImage = null;
            try {
                File myDir = getStorageDirectory(entity);

                if (!myDir.exists()) return null;

                mediaImage = new File(myDir.getPath() + "/" + imageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mediaImage;
        }

        public boolean imageExists(String imageName) {
            Bitmap b = null;
            File file = getImage(String.format("/%s%s", imageName, imageName.endsWith(".jpg") ? "" : ".jpg"));
            if (file == null) return false;
            String path = file.getAbsolutePath();


            try {
                //Related to Fixing OutOfMemory Craashes

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inDither = true;

                b = BitmapFactory.decodeFile(path, options);
                if (b == null || b.equals("")) {
                    return false;
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                Toast.makeText(cxt, "" + e.getMessage().trim().toString(), Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }

    static class ImageCompressor extends AppCompatActivity {
        Context cxt;
        /**
         * Directory to store the compressed image*/
        Entity entity;

        public ImageCompressor(Context cxt, Entity entity){
            this.cxt = cxt;
            this.entity = entity;
        }


        public  String compressImage(String imageUri) {

            String filePath = getRealPathFromURI(imageUri);
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            //int actualHeight = options.outHeight;
            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {               imgRatio = maxHeight / actualHeight;                actualWidth = (int) (imgRatio * actualWidth);               actualHeight = (int) maxHeight;             } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original image

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
            //  options.inPurgeable = true;
            //options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
//          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);

            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out = null;
            String filename = getFilename();
            try {
                out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return filename;

        }
        public  String getCompressedImage(String fileName) throws NullPointerException{
            File file = ImageUtils.ImageStorage.getStorageDirectory(entity);
            return new File(file.getAbsolutePath() + "/"+fileName).getAbsolutePath();
        }


        public String getFilename() {

            File file = ImageUtils.ImageStorage.getStorageDirectory(entity);
            if (!file.exists()) {
                file.mkdirs();
            }

            String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
            return uriSting;

        }
        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                final int heightRatio = Math.round((float) height/ (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }

            return inSampleSize;
        }
        public String getRealPathFromURI(String contentURI) {
            Uri contentUri = Uri.parse(contentURI);
            Cursor cursor = cxt.getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) {
                return contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                return cursor.getString(index);
            }
        }

    }


}