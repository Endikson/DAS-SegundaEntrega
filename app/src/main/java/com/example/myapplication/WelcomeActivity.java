package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Obtener el nombre de usuario del Intent
        String username = getIntent().getStringExtra("username");
        boolean isAdmin = username.equals("admin");

        // Mostrar el nombre de usuario en un TextView
        TextView textViewWelcomeMessage = findViewById(R.id.textViewWelcomeMessage);
        textViewWelcomeMessage.setText("Hola, " + username + ". Bienvenido a la aplicación de ver y capturar imágenes");

        // Obtener una referencia al botón
        Button buttonViewImages = findViewById(R.id.buttonViewImages);
        Button buttonAdminOptions = findViewById(R.id.buttonAdminOptions); // Nuevo botón para opciones de administrador

        // Configurar un listener para el botón de ver imágenes
        buttonViewImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir la actividad para ver las imágenes
                Intent intent = new Intent(WelcomeActivity.this, ViewImagesActivity.class);
                startActivity(intent);
            }
        });

        Button buttonOpenCalendar = findViewById(R.id.buttonOpenCalendar);
        buttonOpenCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir el calendario de Google
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("content://com.android.calendar/time"));
                startActivity(intent);
            }
        });

        // Configurar un listener para el botón de opciones de administrador
        if (isAdmin) {
            buttonAdminOptions.setVisibility(View.VISIBLE);
            buttonAdminOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Abrir la actividad para opciones de administrador
                    Intent intent = new Intent(WelcomeActivity.this, AdminOptionsActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            buttonAdminOptions.setVisibility(View.GONE);
        }
    }
}
