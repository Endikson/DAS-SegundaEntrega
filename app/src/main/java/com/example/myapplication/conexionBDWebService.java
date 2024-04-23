package com.example.myapplication;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class conexionBDWebService extends Worker {

    public conexionBDWebService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // URL del servicio web
        String direccion = "http://104.197.189.16/parametro.php";

        HttpURLConnection urlConnection = null;

        try {
            // Crear la URL y la conexión HttpURLConnection
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();

            // Establecer tiempo de conexión y lectura máximos
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            // Resto de configuraciones de la conexión si es necesario...

            // Realizar la conexión y leer la respuesta del servidor
            InputStream in = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Convertir la respuesta a String
            String resultado = response.toString();

            // Construir el objeto Data con los datos recuperados
            Data outputData = new Data.Builder()
                    .putString("datos", resultado)
                    .build();

            // Retornar el resultado exitoso con los datos
            return Result.success(outputData);
        } catch (IOException e) {
            // Log de errores en la conexión
            e.printStackTrace();
            // Retornar un resultado fallido
            return Result.failure();
        } finally {
            // Cerrar la conexión HttpURLConnection
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}

