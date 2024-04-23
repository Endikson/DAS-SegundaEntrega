package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class RegistroActivity extends AppCompatActivity {

    private static final String TAG = "RegistroActivity";

    private EditText editTextEmail;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonRegister;
    private Button buttonBackToLogin; // También declaramos el botón de volver al inicio de sesión

    private RequestQueue requestQueue;
    private String registerUrl = "http://104.197.189.16:81/register.php"; // Reemplazar con la URL de tu servidor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Vincular los EditText y los botones de la interfaz de usuario
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        TextView textViewLogin = findViewById(R.id.textViewLogin);
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un intent para iniciar LoginActivity
                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                // Iniciar LoginActivity
                startActivity(intent);
                // Finalizar RegistroActivity para que no se quede en la pila de actividades
                finish();
            }
        });


        // Inicializar la cola de solicitudes HTTP
        requestQueue = Volley.newRequestQueue(this);

        // Configurar el listener del botón de registro
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        // Deshabilitar el botón para evitar múltiples intentos
        buttonRegister.setEnabled(false);

        String email = editTextEmail.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Comprobar si los campos están vacíos
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(RegistroActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            buttonRegister.setEnabled(true); // Habilitar el botón nuevamente
            return;
        }

        // Validar el formato del correo electrónico
        if (!isValidEmail(email)) {
            Toast.makeText(RegistroActivity.this, "Formato de correo electrónico no válido", Toast.LENGTH_SHORT).show();
            buttonRegister.setEnabled(true); // Habilitar el botón nuevamente
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                registerUrl,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Habilitar el botón nuevamente después de recibir la respuesta del servidor
                        buttonRegister.setEnabled(true);

                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");
                            Toast.makeText(RegistroActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (success) {
                                // Registro exitoso, redirigir a la siguiente actividad
                                Toast.makeText(RegistroActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                // Pasar el nombre de usuario a la siguiente actividad
                                Intent intent = new Intent(RegistroActivity.this, WelcomeActivity.class);
                                intent.putExtra("username", username);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error en la solicitud HTTP
                        Log.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(RegistroActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; // Expresión regular para validar el formato del correo electrónico
        return email.matches(emailPattern);
    }
}
