package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ServerImagesActivity extends AppCompatActivity {

    private static final String SERVER_URL = "http://104.197.189.16:81/obtener_urls.php";

    private List<String> imageUrls = new ArrayList<>();
    private List<Bitmap> images = new ArrayList<>();
    private int currentIndex = 0;

    private ImageView imageView;
    private Spinner spinnerImages;
    private Button buttonGetImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_images);

        imageView = findViewById(R.id.imageView);
        spinnerImages = findViewById(R.id.spinnerImages);
        buttonGetImage = findViewById(R.id.buttonGetImage);
        Button buttonViewImages = findViewById(R.id.buttonViewImages);

        // Obtener las URL de las imágenes del servidor
        fetchImageUrls();

        // Configurar el botón para obtener y mostrar la imagen seleccionada
        buttonGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedImageIndex = spinnerImages.getSelectedItemPosition();
                if (selectedImageIndex >= 0 && selectedImageIndex < imageUrls.size()) {
                    loadImageFromUrl(imageUrls.get(selectedImageIndex));
                }
            }
        });

        // Configurar el botón para navegar a ViewImagesActivity
        buttonViewImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ServerImagesActivity.this, ViewImagesActivity.class));
            }
        });
    }


    private void fetchImageUrls() {
        new FetchImageUrlsTask().execute(SERVER_URL);
    }

    private void loadImageFromUrl(String imageUrl) {
        new LoadImageTask().execute(imageUrl);
    }

    private void displayImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    private class FetchImageUrlsTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            List<String> urls = new ArrayList<>();
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String response = stringBuilder.toString();

                // Parsear la respuesta como un array JSON de strings
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String imageUrl = jsonArray.getString(i);
                    urls.add(imageUrl);
                }
                bufferedReader.close();
                inputStream.close();
            } catch (IOException | JSONException e) {
                Log.e("FetchImageUrlsTask", "Error fetching image URLs", e);
            }
            return urls;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            if (result != null && !result.isEmpty()) {
                imageUrls.addAll(result);
                setupSpinner();
            } else {
                Toast.makeText(ServerImagesActivity.this, "No se pudieron obtener las URLs de las imágenes", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            String imageUrl = params[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                Log.e("LoadImageTask", "Error loading image from URL: " + imageUrl, e);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
                images.add(result);
                displayImage(result);
            } else {
                Toast.makeText(ServerImagesActivity.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupSpinner() {
        // Creamos una lista para almacenar solo los nombres de las imágenes
        List<String> imageNames = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            // Obtenemos el nombre de archivo de la URL
            String imageName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            // Agregamos el nombre de la imagen a la lista
            imageNames.add(imageName);
        }

        // Creamos un adaptador para el Spinner utilizando la lista de nombres de imágenes
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, imageNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImages.setAdapter(adapter);
    }

}
