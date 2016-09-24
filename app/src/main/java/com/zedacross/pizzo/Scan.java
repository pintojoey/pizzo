package com.zedacross.pizzo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.zedacross.pizzo.wrapper.VisualDetection;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class Scan extends AppCompatActivity {

    public static final int REQUEST_IMAGE_CAPTURE = 1000;

    private static final int PICK_IMAGE_REQUEST =1001 ;
    private int method=0;

    private String currentPhotoPath;
TextView detection;
    /**
     * Provides convenience access to device camera
     * @param activity The current activity
     */

    private static final String TAG = "joey";
    Uri selectedImageUri;
    String  selectedPath;
    ImageView preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Button b = (Button) findViewById(R.id.bGallery);
        Button bCam= (Button) findViewById(R.id.bCamera);
        preview = (ImageView) findViewById(R.id.preview);
        detection = (TextView) findViewById(R.id.Detection);
        bCam.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                method=1;
                dispatchTakePictureIntent();
            }
        });
        Button bGallery= (Button) findViewById(R.id.bGallery);
        bGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                method=2;
                dispatchGalleryIntent();
            }
        });



    }
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    File image=null;
    if(method==1)image=getFile(resultCode);
    else image=getFile(resultCode,data);

    if(image!=null){detectBluemix(image);
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
        preview.setImageBitmap(bitmap);
    }
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void dispatchGalleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (galleryIntent.resolveActivity(this.getPackageManager()) != null) {
            this.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
        }

    }


    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "IOException", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                this.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            }

        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    public void detectBluemix(File file){

new bluemixVisuals(file).execute();


    }
    class bluemixVisuals extends AsyncTask<String,String,String>{
        File image;

        String result;
        bluemixVisuals(File file){
            this.image=file;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);

            service.setApiKey("a6b595b885dcfc7bb04ea06b92ff24d8caf3fff6");

            System.out.println(image.getPath());

            ClassifyImagesOptions options = new ClassifyImagesOptions.Builder().images(image) .build();
            VisualClassification result = service.classify(options).execute();
            this.result=result.toString();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
System.out.println(result);
            try {

                VisualDetection detected_classes = new VisualDetection(result);
                ArrayList<VisualDetection.Detection_class> classes = detected_classes.getDetection_classes();
Iterator<VisualDetection.Detection_class> iterator=classes.iterator();
                System.out.println(classes.size());
                detection.setText("");
   while(iterator.hasNext()){


       detection.setText(detection.getText()+iterator.next().getClass_name());
   }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public File getFile(int resultCode) {
        if(resultCode == this.RESULT_OK) {
            Uri targetUri = Uri.parse(currentPhotoPath);
            return resize(new File(targetUri.getPath()));

        }
        Log.e(TAG, "Result Code was not OK");
        return null;
    }
    public File getFile(int resultCode, Intent data) {
        if(resultCode == this.RESULT_OK) {
            Uri targetUri = data.getData();
            return resize(new File(getPath(targetUri)));
        }
        Log.e(TAG, "Result Code was not OK");
        return null;
    }

    public Bitmap getBitmap(int resultCode) {
        if(resultCode == this.RESULT_OK) {
            Uri targetUri = Uri.parse(currentPhotoPath);
            try {
                return BitmapFactory.decodeStream(this.getContentResolver().openInputStream(targetUri));
            } catch (FileNotFoundException e) {
                Log.e(TAG, "File Not Found", e);
                return null;
            }
        }
        Log.e(TAG, "Result Code was not OK");
        return null;
    }
    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }
    public File resize(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }
}
