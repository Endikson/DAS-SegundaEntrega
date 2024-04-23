package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewImagesActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private ImageView imageViewCapturedImage;
    private Button buttonCaptureImage;

    // Declarar el lanzador de resultados de la captura de imágenes
    private ActivityResultLauncher<Void> takePictureLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);

        // Vincular elementos de la interfaz de usuario
        imageViewCapturedImage = findViewById(R.id.imageViewCapturedImage);
        buttonCaptureImage = findViewById(R.id.buttonCaptureImage);

        // Inicializar el lanzador de resultados de la captura de imágenes
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(),
                new ActivityResultCallback<Bitmap>() {
                    @Override
                    public void onActivityResult(Bitmap result) {
                        if (result != null) {
                            // La imagen se capturó correctamente
                            imageViewCapturedImage.setImageBitmap(result);
                            imageViewCapturedImage.setVisibility(View.VISIBLE);
                            // Envía la imagen al servidor
                            sendImageToServer(result);
                        } else {
                            // La captura de la imagen falló
                            Toast.makeText(ViewImagesActivity.this, "Error al capturar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Configurar el listener del botón para capturar imágenes
        buttonCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Solicitar permisos de cámara antes de capturar la imagen
                requestCameraPermission();
            }
        });
    }

    // Método para solicitar permisos de cámara
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            // Permiso de cámara ya concedido, proceder con la captura de imagen
            dispatchTakePictureIntent();
        }
    }

    // Método para iniciar la captura de imágenes utilizando el lanzador de resultados
    private void dispatchTakePictureIntent() {
        takePictureLauncher.launch(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de cámara concedido, proceder con la captura de imagen
                dispatchTakePictureIntent();
            } else {
                // Permiso de cámara denegado por el usuario, muestra un mensaje al usuario
                Toast.makeText(this, "El permiso de cámara ha sido denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para enviar la imagen capturada al servidor
    private void sendImageToServer(Bitmap imageBitmap) {
        // Convierte la imagen capturada a bytes en formato PNG
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // URL del servidor donde se enviará la imagen
        String serverUrl = "http://104.197.189.16:81/upload_image.php";

        // Ejecutar una tarea asíncrona para enviar la imagen al servidor
        new SendImageTask().execute(serverUrl, imageBytes);

        // Agregar mensaje de depuración
        Log.d("SendImageToServer", "Enviando imagen al servidor: " + serverUrl);
    }

    // Clase interna para manejar la solicitud HTTP de enviar la imagen al servidor
    private class SendImageTask extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            String serverUrl = (String) params[0];
            byte[] imageBytes = (byte[]) params[1];

            try {
                // Establecer la conexión HTTP
                URL url = new URL(serverUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "image/png"); // Establece el tipo de contenido

                // Enviar la imagen en la solicitud HTTP
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(imageBytes);
                outputStream.flush();
                outputStream.close();

                // Verificar el código de respuesta del servidor
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // La imagen se envió correctamente al servidor
                    return true;
                } else {
                    // Error al enviar la imagen al servidor
                    Log.e("SendImageTask", "Error al enviar la imagen. Código de respuesta: " + responseCode);
                    return false;
                }
            } catch (IOException e) {
                Log.e("SendImageTask", "Excepción al enviar la imagen", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                // La imagen se envió correctamente al servidor
                Toast.makeText(ViewImagesActivity.this, "Imagen enviada al servidor", Toast.LENGTH_SHORT).show();
            } else {
                // Error al enviar la imagen al servidor
                Toast.makeText(ViewImagesActivity.this, "Error al enviar la imagen al servidor", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void backToWelcomeActivity(View view) {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }

    public void showServerImages(View view) {
        Intent intent = new Intent(this, ServerImagesActivity.class);
        startActivity(intent);
    }
}