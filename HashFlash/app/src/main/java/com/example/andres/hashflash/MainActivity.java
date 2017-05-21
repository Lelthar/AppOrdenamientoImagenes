package com.example.andres.hashflash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 15;
    private static final String TAG = "No se";
    ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button click = (Button)findViewById(R.id.button);
        click.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                dispatchTakePictureIntent();    // Tomar Foto
            }
        });
        mImageView = (ImageView)findViewById(R.id.imageView);
    }

    private void dispatchTakePictureIntent() {
        System.out.println("Camera Activity Initiated");
        if (ContextCompat.checkSelfPermission(getApplicationContext(),  // Revisa si no hay permiso para accesar a memoria
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Needs Permission");
            ActivityCompat.requestPermissions(this,                     // Pide permiso
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            System.out.println("Permission Requested");
        }
        else {
            System.out.println("Has Permission");
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Intento para tomar foto despues
            System.out.println("Intent Lanched");
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {   // Revisa si los datos del intento son correctos
                System.out.println("Data is Readable");
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);   // Captura la imagen con el intento
                System.out.println("HELOOOOOOOOOOOOOOOOOOO");
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
            Log.d(TAG, "Largo: "+Integer.toString(mImageView.getWidth())+"\n Ancho: "+Integer.toString(mImageView.getHeight()));

        }
    }


    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}


