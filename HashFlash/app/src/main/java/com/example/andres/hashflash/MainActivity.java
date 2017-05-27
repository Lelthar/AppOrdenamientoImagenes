package com.example.andres.hashflash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
<<<<<<< HEAD
=======
import java.util.ArrayList;
>>>>>>> e35208befecb0eb60c4a79820d3b508fd4197419
import java.util.Arrays;
import java.util.Date;

import static android.os.Environment.getExternalStorageState;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 15;
    private static final String TAG = "No se";
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button click = (Button) findViewById(R.id.button);
        click.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                dispatchTakePictureIntent();    // Tomar Foto
            }
        });
        mImageView = (ImageView) findViewById(R.id.imageView);
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
                System.out.println("startActivityForResult . . .");
                readPlanos(10);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("On activity result");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            /*int redValue = Color.red(imageBitmap.getPixel(0, 0));
            System.out.println("REEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDD:" + redValue);
            int greenValue = Color.green(imageBitmap.getPixel(0, 0));
            System.out.println("GREEEEEEEEEEEEEEEENNNNNNNNNNNNN:" + greenValue);
            int blueValue = Color.blue(imageBitmap.getPixel(0, 0));
            System.out.println("BLUUUUUUUUUUUUUUUUUUUUUEEEEE:" + blueValue);*/
            Bitmap imageBitmap2 = toGrayscale(imageBitmap);
            mImageView.setImageBitmap(imageBitmap2);
            System.out.println("Largo: " + Integer.toString(imageBitmap2.getWidth()) + "\n Ancho: " + Integer.toString(imageBitmap2.getHeight()));
            System.out.println("Pixel color: " + Integer.toString(imageBitmap2.getPixel(0, 0)));
            int[] histograma = calcularLBP(imageBitmap2);
            System.out.println(Arrays.toString(histograma));

        }
    }

    /*
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
            /*int redValue = Color.red(imageBitmap.getPixel(0, 0));
            System.out.println("REEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDD:" + redValue);
            int greenValue = Color.green(imageBitmap.getPixel(0, 0));
            System.out.println("GREEEEEEEEEEEEEEEENNNNNNNNNNNNN:" + greenValue);
            int blueValue = Color.blue(imageBitmap.getPixel(0, 0));
            System.out.println("BLUUUUUUUUUUUUUUUUUUUUUEEEEE:" + blueValue);*/
            Bitmap imageBitmap2 = toGrayscale(imageBitmap);
            mImageView.setImageBitmap(imageBitmap2);
            System.out.println("Largo: " + Integer.toString(imageBitmap2.getWidth()) + "\n Ancho: " + Integer.toString(imageBitmap2.getHeight()));
            System.out.println("Pixel color: " + Integer.toString(imageBitmap2.getPixel(0, 0)));
            int[]histograma =  calcularLBP(imageBitmap2);
            System.out.println(Arrays.toString(histograma));

        }
    }
    */

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

    public Bitmap toGrayscale(Bitmap bmpOriginal) { //Convierte un bitmap de rgb a escala de colores grises
        int width, height;
        /*height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();*/
        height = 256; //Le pasa 256 para que ese sea la altura de la imagen
        width = 256; //Le pasa 256 para que ese sea el ancho de la imagen
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public int[] inicializarVector(int largo) { //Inicializa un arraylist con la cantidad que le ponga en la entrada
        int[] lista = new int[largo];
        for (int i = 0; i < largo; i++) {
            lista[i] = 0; //Le asigna en cada posicion un 0 para representar el histograma
        }
        return lista;
    }

    public int[] calcularLBP(Bitmap imageNew) {
        int[] lista = inicializarVector(256);
        for (int i = 1; i < 255; i++) {
            for (int j = 1; j < 255; j++) {
                String valorBinario = "";
<<<<<<< HEAD
                if (Color.green(imageNew.getPixel(i - 1, j - 1)) >= Color.green(imageNew.getPixel(i, j))) {
                    valorBinario += "1";
                } else {
                    valorBinario += "0";
                }
                if (Color.green(imageNew.getPixel(i - 1, j)) >= Color.green(imageNew.getPixel(i, j))) {
                    valorBinario += "1";
                } else {
                    valorBinario += "0";
                }
                if (Color.green(imageNew.getPixel(i - 1, j + 1)) >= Color.green(imageNew.getPixel(i, j))) {
                    valorBinario += "1";
                } else {
                    valorBinario += "0";
                }
                if (Color.green(imageNew.getPixel(i, j + 1)) >= Color.green(imageNew.getPixel(i, j))) {
                    valorBinario += "1";
                } else {
                    valorBinario += "0";
                }
                if (Color.green(imageNew.getPixel(i + 1, j + 1)) >= Color.green(imageNew.getPixel(i, j))) {
                    valorBinario += "1";
                } else {
                    valorBinario += "0";
                }
                if (Color.green(imageNew.getPixel(i + 1, j)) >= Color.green(imageNew.getPixel(i, j))) {
                    valorBinario += "1";
                } else {
                    valorBinario += "0";
                }
                if (Color.green(imageNew.getPixel(i + 1, j - 1)) >= Color.green(imageNew.getPixel(i, j))) {
                    valorBinario += "1";
                } else {
                    valorBinario += "0";
                }
                if (Color.green(imageNew.getPixel(i, j - 1)) >= Color.green(imageNew.getPixel(i, j))) {
                    valorBinario += "1";
                } else {
                    valorBinario += "0";
=======
                if(Color.green(imageNew.getPixel(i-1,j-1))>=Color.green(imageNew.getPixel(i,j))){
                    valorBinario+="1";
                }else{
                    valorBinario+="0";
                }
                if(Color.green(imageNew.getPixel(i-1,j))>=Color.green(imageNew.getPixel(i,j))){
                    valorBinario+="1";
                }else{
                    valorBinario+="0";
                }
                if(Color.green(imageNew.getPixel(i-1,j+1))>=Color.green(imageNew.getPixel(i,j))){
                    valorBinario+="1";
                }else{
                    valorBinario+="0";
                }
                if(Color.green(imageNew.getPixel(i,j+1))>=Color.green(imageNew.getPixel(i,j))){
                    valorBinario+="1";
                }else{
                    valorBinario+="0";
                }
                if(Color.green(imageNew.getPixel(i+1,j+1))>=Color.green(imageNew.getPixel(i,j))){
                    valorBinario+="1";
                }else{
                    valorBinario+="0";
                }
                if(Color.green(imageNew.getPixel(i+1,j))>=Color.green(imageNew.getPixel(i,j))){
                    valorBinario+="1";
                }else{
                    valorBinario+="0";
                }
                if(Color.green(imageNew.getPixel(i+1,j-1))>=Color.green(imageNew.getPixel(i,j))){
                    valorBinario+="1";
                }else{
                    valorBinario+="0";
                }
                if(Color.green(imageNew.getPixel(i,j-1))>=Color.green(imageNew.getPixel(i,j))){
                    valorBinario+="1";
                }else{
                    valorBinario+="0";
>>>>>>> e35208befecb0eb60c4a79820d3b508fd4197419
                }
                lista[convertirBinarioDecimal(valorBinario)] += 1;

            }
        }
        return lista;

    }

    public int convertirBinarioDecimal(String numero) {
        int salida = 0;
        int potencia = numero.length() - 1;
        for (int i = 0; i < numero.length(); i++) {
            int var = (numero.charAt(i) - 48);
            salida += (var * Math.pow(2, potencia)); //Sacarle la potencia de 2 elevador a la variable de potencia
            potencia--; //Disminuye la potencia para que el numero de 2 se eleve menor, hasta llevar a 0
        }
        return salida;
    }

    public void readPlanos(int cantidad){
        BufferedReader reader = null;
        try {
            System.out.println("En read PLANOS");
            InputStream json=getAssets().open("Planos.txt");
            System.out.println("Se encontro el archivo");

            BufferedReader in = new BufferedReader(new InputStreamReader(json,"UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine;
            mLine = "";
            while (cantidad > 0 && (mLine = in.readLine()) != null) {
                //process line
                System.out.println(mLine);
                cantidad --;
            }
        } catch (IOException e) {
            //log the exception
            System.out.println(e);
        } finally {
            if (reader != null) {
                try {
                    System.out.println("En read PLANOS CLOSE");
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }
}

//int[] arr = Arrays.stream(str.substring(1, str.length()-1).split(",")) .map(String::trim).mapToInt(Integer::parseInt).toArray();
