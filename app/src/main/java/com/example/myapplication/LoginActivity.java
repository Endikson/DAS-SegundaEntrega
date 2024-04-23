package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewSignup;

    private RequestQueue requestQueue;
    private String loginUrl = "http://104.197.189.16:81/login.php"; // Reemplazar con la URL de tu servidor

    private static final int PERMISSION_REQUEST_CODE = 12; // Cambia el número de código si es necesario
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.SYSTEM_ALERT_WINDOW}; // Agrega más permisos si es necesario


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Solicitar permisos al iniciar la actividad
        requestNotificationPermission();

        // Referencias a los elementos de la interfaz de usuario
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignup = findViewById(R.id.textViewSignup);

        // Inicializar la cola de solicitudes HTTP
        requestQueue = Volley.newRequestQueue(this);

        // Configurar el listener del botón de login
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Método para intentar iniciar sesión
                loginUser();
            }
        });

        // Configurar el listener del texto de registro
        textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irARegistro();
            }
        });
    }

    // Método para solicitar permiso de notificaciones
    private void requestNotificationPermission() {
        // Verificar si el permiso ya está otorgado
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) !=
                    PackageManager.PERMISSION_GRANTED) {
                // Si el permiso no está otorgado, solicitarlo
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
            }
        }
    }

    // Manejar el resultado de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        handlePermissionRequestResult(requestCode, grantResults);
    }

    // Método para manejar el resultado de la solicitud de permisos
    private void handlePermissionRequestResult(int requestCode, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado
                Toast.makeText(this, "Permiso otorgado para mostrar notificaciones", Toast.LENGTH_SHORT).show();
            } else {
                // Permiso denegado
                Toast.makeText(this, "Para recibir notificaciones, por favor, habilita el permiso desde la configuración de la aplicación.", Toast.LENGTH_LONG).show();

            }
        }
    }

    // Método para iniciar sesión de usuario
    private void loginUser() {
        // Deshabilitar el botón para evitar múltiples intentos durante la solicitud HTTP
        buttonLogin.setEnabled(false);

        // Obtener los datos de los campos de texto
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validar los datos de inicio de sesión
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            buttonLogin.setEnabled(true);
            return;
        }

        // Crear un objeto JSON con los datos de inicio de sesión
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Crear la solicitud JSON para iniciar sesión
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                loginUrl,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Habilitar el botón nuevamente después de recibir la respuesta del servidor
                        buttonLogin.setEnabled(true);

                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                // Login exitoso, redirigir a la actividad de bienvenida
                                Toast.makeText(LoginActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                                intent.putExtra("username", username); // Pasar el nombre de usuario a la actividad de bienvenida
                                startActivity(intent);
                                finish(); // Cerrar la actividad actual
                            } else {
                                // Error en el inicio de sesión
                                Toast.makeText(LoginActivity.this, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error de análisis JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Habilitar el botón nuevamente en caso de error
                        buttonLogin.setEnabled(true);

                        // Error en la solicitud HTTP
                        Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Agregar la solicitud a la cola de solicitudes HTTP
        requestQueue.add(jsonObjectRequest);
    }

    // Método para ir a la actividad de registro
    private void irARegistro() {
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }
}
