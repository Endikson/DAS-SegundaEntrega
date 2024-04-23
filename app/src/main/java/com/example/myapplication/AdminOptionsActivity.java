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

public class AdminOptionsActivity extends AppCompatActivity {

    private static final String TAG = "AdminOptionsActivity";

    private EditText editTextUsernameToDelete;
    private EditText editTextNewEmail;
    private EditText editTextNewUsername;
    private EditText editTextNewPassword;

    private RequestQueue requestQueue;
    private String serverUrl = "http://104.197.189.16:81"; // Reemplazar con la URL de tu servidor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_options);

        editTextUsernameToDelete = findViewById(R.id.editTxtUsernameToDelete);
        editTextNewEmail = findViewById(R.id.editTextNewEmail);
        editTextNewUsername = findViewById(R.id.editTextNewUsername);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);

        requestQueue = Volley.newRequestQueue(this);

        Button buttonDeleteUser = findViewById(R.id.buttonDeleteUser);
        buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameToDelete = editTextUsernameToDelete.getText().toString().trim();
                deleteUser(usernameToDelete);
            }
        });

        Button buttonModifyUser = findViewById(R.id.buttonModifyUser);
        buttonModifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEmail = editTextNewEmail.getText().toString().trim();
                String newUsername = editTextNewUsername.getText().toString().trim();
                String newPassword = editTextNewPassword.getText().toString().trim();
                String usernameToModify = editTextUsernameToDelete.getText().toString().trim();
                modifyUser(newEmail, newUsername, newPassword, usernameToModify);
            }
        });

        Button buttonAddUser = findViewById(R.id.buttonAddUser);
        buttonAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEmail = editTextNewEmail.getText().toString().trim();
                String newUsername = editTextNewUsername.getText().toString().trim();
                String newPassword = editTextNewPassword.getText().toString().trim();
                addUser(newEmail, newUsername, newPassword);
            }
        });

        // Botón para volver a WelcomeActivity
        Button buttonBackToWelcome = findViewById(R.id.buttonBackToWelcome);
        buttonBackToWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminOptionsActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void deleteUser(String usernameToDelete) {
        if (usernameToDelete.isEmpty()) {
            Toast.makeText(AdminOptionsActivity.this, "Por favor rellene el usuario a eliminar", Toast.LENGTH_SHORT).show();
            return; // Salir del método si el nombre de usuario está vacío
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", usernameToDelete);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                serverUrl + "/delete_user.php",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");
                            Toast.makeText(AdminOptionsActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(AdminOptionsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void modifyUser(String newEmail, String newUsername, String newPassword, String username) {
        if (newEmail.isEmpty() || newUsername.isEmpty() || newPassword.isEmpty() || username.isEmpty()) {
            Toast.makeText(AdminOptionsActivity.this, "Por favor rellena los campos necesarios", Toast.LENGTH_SHORT).show();
            return; // Salir del método si algún campo está vacío
        }

        // Verificar el formato del correo electrónico
        if (!isValidEmail(newEmail)) {
            Toast.makeText(AdminOptionsActivity.this, "Formato de correo electrónico inválido", Toast.LENGTH_SHORT).show();
            return; // Salir del método si el formato del correo electrónico es inválido
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("newEmail", newEmail);
            jsonObject.put("newUsername", newUsername);
            jsonObject.put("newPassword", newPassword);
            jsonObject.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                serverUrl + "/modify_user.php",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");
                            Toast.makeText(AdminOptionsActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(AdminOptionsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void addUser(String email, String username, String password) {
        // Verificar si algún campo está vacío
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(AdminOptionsActivity.this, "Por favor rellena los campos necesarios", Toast.LENGTH_SHORT).show();
            return; // Salir del método si algún campo está vacío
        }

        // Verificar el formato del correo electrónico
        if (!isValidEmail(email)) {
            Toast.makeText(AdminOptionsActivity.this, "Formato de correo electrónico inválido", Toast.LENGTH_SHORT).show();
            return; // Salir del método si el formato del correo electrónico es inválido
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
                serverUrl + "/add_user.php",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");
                            Toast.makeText(AdminOptionsActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(AdminOptionsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    // Método para verificar el formato del correo electrónico utilizando una expresión regular
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
