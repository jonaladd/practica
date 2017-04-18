/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;
import info.androidhive.loginandregistration.helper.SQLiteHandler;
import info.androidhive.loginandregistration.helper.SessionManager;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputUser;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputUser = (EditText) findViewById(R.id.usuario);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // dialogo de progreso
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Administrador de sesión
        session = new SessionManager(getApplicationContext());

        // Controlador de base de datos SQLite
        db = new SQLiteHandler(getApplicationContext());

        // Compruebe si el usuario ya está conectado o no
        if (session.isLoggedIn()) {
            // El usuario ya está identificado. Tómelo a la actividad principal
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Botón de registro Haga clic en evento
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String usuario = inputUser.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!name.isEmpty() && !usuario.isEmpty() && !password.isEmpty()) {
                    registerUser(name, usuario, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Por favor ingrese los datos!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Enlace a la pantalla de inicio de sesión
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }


       //Función para almacenar el usuario en la base de datos MySQL publicará params (etiqueta, nombre,
 //Correo electrónico, contraseña) para registrar url

    private void registerUser(final String name, final String usuario,
                              final String password) {
        // Etiqueta utilizada para cancelar la solicitud
        String tag_string_req = "req_register";

        pDialog.setMessage("Registrando...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {


            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // Usuario almacenado correctamente en MySQL
                        // Ahora almacena el usuario en sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String usuario = user.getString("usuario");
                        String created_at = user
                                .getString("created_at");

                        // Insertar fila en la tabla de usuarios
                        db.addUser(name, usuario, uid, created_at);

                        Toast.makeText(getApplicationContext(), "El registro fue exitoso!", Toast.LENGTH_LONG).show();

                        // Iniciar actividad de inicio de sesión
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Se ha producido un error en el registro. Obtener el error
                     // mensaje
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {


            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {


            protected Map<String, String> getParams() {
                // Publicación de parámetros para registrar url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("usuario", usuario);
                params.put("password", password);

                return params;
            }

        };

        // Añadir solicitud a la cola de solicitudes
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
