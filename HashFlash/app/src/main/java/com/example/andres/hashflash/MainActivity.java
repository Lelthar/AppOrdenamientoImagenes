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
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static android.os.Environment.getExternalStorageState;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 15;
    private static final String TAG = "No se";
    ImageView mImageView;
    ArrayList<int[]> planos = null;
    HashMap<String, String> hmap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        planos = readPlanos(4);
        hmap = cargarTablaHash();
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

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("On activity result");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap  = cambiarTamabho(imageBitmap,256,256);
            Bitmap imageBitmap2 = toGrayscale(imageBitmap); //Conviertte el bitmap en colores grises
            mImageView.setImageBitmap(imageBitmap2); //Se asiga al imageView, la imagen capturada
            System.out.println("Largo: " + Integer.toString(imageBitmap2.getWidth()) + "\n Ancho: " + Integer.toString(imageBitmap2.getHeight()));
            System.out.println("Pixel color: " + Integer.toString(imageBitmap2.getPixel(0, 0)));
            int[] histograma = calcularLBP(imageBitmap2);
            //imprimirImagen(imageBitmap2);
            String hashValor = calcularValorHash(histograma, planos);
            System.out.println(Arrays.toString(histograma));
            System.out.println("Este es el valor hash: "+hashValor);
            System.out.println("El valor de la tabla hash es: "+hmap.get(hashValor));

        }
    }
    public void imprimirImagen(Bitmap imagen){
        for(int i = 0; i < imagen.getWidth(); i++){
            for(int j = 0; j < imagen.getHeight(); j++){
                System.out.print(Color.green(imagen.getPixel(i,j))+" ");
            }
            System.out.println("");
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
            Bitmap imageBitmap2 = toGrayscale(imageBitmap);
            mImageView.setImageBitmap(imageBitmap2);
            Log.d(TAG, "Largo: " + Integer.toString(mImageView.getWidth()) + "\n Ancho: " + Integer.toString(mImageView.getHeight()));
            Log.d(TAG, "Pixel color: " + Integer.toString(imageBitmap2.getPixel(0, 0)));

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
    public Bitmap cambiarTamabho(Bitmap imagen , int ancho, int largo){
        Bitmap nuevo = Bitmap.createScaledBitmap(imagen,largo,ancho,true);
        return nuevo;
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal) { //Convierte un bitmap de rgb a escala de colores grises
        int width, height;
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

    public String calcularProductoPunto(int[] vector1, int[] vector2){ //Calcula el producto punto entre 2 vectores
        int cantidad = 0;
        for(int i = 0; i < vector1.length; i++){
            cantidad+=(vector1[i]*vector2[i]); //Multiplica el elemento del vector en la posicion i de cada vector y luego se lo suma a cantidad
        }
        if(cantidad > 0){ //Revisa si cantidad es mayor que 0
            System.out.println("Cantidad: "+Integer.toString(cantidad));
            return "1";
        }else { //Si no lo es, retorna 0 como el resultado del producto punto de los 2 vectores
            System.out.println("Cantidad: "+Integer.toString(cantidad));
            return "0";
        }
    }
    public String calcularValorHash(int[] vector, ArrayList<int[]> planos){ //Calcula el valor hash de un histograma con los planos que tiene la lista de planos
        String resultado = "";
        for(int i = 0; i < planos.size(); i++){
            //System.out.println("Hola"+calcularProductoPunto(vector,planos.get(i)));
            //System.out.println(Arrays.toString(vector));
            resultado += calcularProductoPunto(vector,planos.get(i)); //Llama a la funcion de calcular producto punto del histrograma contra un plano de la lista planos
        }
        return resultado; //Retorna el valor hash del histrograma con los planos que le dieron

    }
    public int[] convertirStringAVector(String listaString,int largo){ //Convierte un string como "[2,4,-12,5]" en un vector de int
        int[] lista = new int[largo]; //Inicializa la lista que va a retornar
        int contador = 0; //Es un contador que va a cambiar el indice en el vector
        String numeroString = "";
        for(int i = 0; i < listaString.length(); i++){
            if(listaString.charAt(i) != '[' && listaString.charAt(i) != ']'){ //Revisa que el caracter no sea un [ o ]
                if(listaString.charAt(i) != ','){
                    String stringValueOf = String.valueOf(listaString.charAt(i));
                    numeroString += stringValueOf;
                }else{ //Es si el caracter leido, es una coma
                    int numero = Integer.parseInt(numeroString); //Convierte en int el valor de numeroString
                    lista[contador] = numero; //En el vector en la posicion contador, le pasa el numero que se convirtio arriba
                    contador++;
                    numeroString = "";
                }
            }else if(listaString.charAt(i) == ']'){ //Revisa si el caracter leido es "]", esta comparacion se hace porque al final el ultimo elemento, no se anhade si no se hace estos
                int numero = Integer.parseInt(numeroString); //Convierte en int el valor de numeroString
                lista[contador] = numero; //En el vector en la posicion contador, le pasa el numero que se convirtio arriba
                contador++;
                numeroString = "";
            }
        }
        return lista;

    }

    public ArrayList<int[]> readPlanos(int cantidad){ //Retorna un arraylist con vectores de los planos
        BufferedReader reader = null;
        ArrayList<int[]> lista = new ArrayList<>();
        try {
            System.out.println("En read PLANOS");
            InputStream json=getAssets().open("Planos.txt");
            System.out.println("Se encontro el archivo");

            BufferedReader in = new BufferedReader(new InputStreamReader(json,"UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine = "";
            while (cantidad > 0 && (mLine = in.readLine()) != null) {
                //process line
                System.out.println(mLine);
                lista.add(convertirStringAVector(mLine.replace(" ",""),256)); //Convierte el string en un vector y lo añade al arraylist
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
                    return lista;
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return lista;
    }
    public HashMap<String, String> cargarTablaHash(){
        //Aqui se añade los elementos a la tabla hash
        HashMap<String, String> pHmap = new HashMap<String, String>();

        pHmap.put("0000", "Dio 0");
        pHmap.put("0001", "Dio 1");
        pHmap.put("0010", "Dio 2");
        pHmap.put("0011", "Dio 3");
        pHmap.put("0100", "Dio 4");
        pHmap.put("0101", "Dio 5");
        pHmap.put("0110", "Dio 6");
        pHmap.put("0111", "Dio 7");
        pHmap.put("1000", "Dio 8");
        pHmap.put("1001", "Dio 9");
        pHmap.put("1010", "Dio 10");
        pHmap.put("1011", "Dio 11");
        pHmap.put("1100", "Dio 12");
        pHmap.put("1101", "Dio 13");
        pHmap.put("1110", "Dio 14");
        pHmap.put("1111", "Dio 15");

        return pHmap;
    }
}

//int[] arr = Arrays.stream(str.substring(1, str.length()-1).split(",")) .map(String::trim).mapToInt(Integer::parseInt).toArray();
