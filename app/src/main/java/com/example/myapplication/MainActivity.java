package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.messaging.FirebaseMessaging;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar el SDK de FCM
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        setContentView(R.layout.activity_main);

        // Iniciar la actividad de inicio de sesión
        startActivity(new Intent(this, LoginActivity.class));

        // Finalizar la actividad principal para que el usuario no pueda volver atrás
        finish();
    }
}
