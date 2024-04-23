package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    private RequestQueue requestQueue;
    private String loginUrl = "http://104.197.189.16:81/login.php"; // Reemplazar con la URL de tu servidor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Referencias a los elementos de la interfaz de usuario
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

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
    }

    private void loginUser() {
        // Deshabilitar el botón para evitar múltiples intentos durante la solicitud HTTP
        buttonLogin.setEnabled(false);

        // Obtener los datos de los campos de texto
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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
                            } else {
                                // Error en el inicio de sesión
                                Toast.makeText(LoginActivity.this, "Login fallido", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Inicio de sesión fallido: " + response.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error de análisis JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Habilitar el botón nuevamente en caso de error
                        buttonLogin.setEnabled(true);

                        // Error en la solicitud HTTP
                        Log.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Agregar la solicitud a la cola de solicitudes HTTP
        requestQueue.add(jsonObjectRequest);

        // Registro de información sobre el inicio de sesión
        Log.i(TAG, "Solicitud de inicio de sesión enviada para el usuario: " + username);
    }

    // Método para ir a la actividad de registro
    public void irARegistro(View view) {
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }
}
