package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.helper.SQLiteHandler;
import info.androidhive.loginandregistration.helper.SessionManager;

public class MainActivity extends Activity {

	private TextView txtName;
	private TextView txtUsuario;
	private Button btnLogout;
    private ImageButton entrega;
    private ImageButton nuevo;
    private ImageButton consulta;
    private ImageButton estadisticas;
    private ImageButton retiro;

	private SQLiteHandler db;
	private SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtName = (TextView) findViewById(R.id.name);
		txtUsuario = (TextView) findViewById(R.id.usuario);
		btnLogout = (Button) findViewById(R.id.btnLogout);
        entrega =(ImageButton) findViewById(R.id.entrega);
        nuevo =(ImageButton) findViewById(R.id.nuevo);
        consulta =(ImageButton) findViewById(R.id.consulta);
        estadisticas =(ImageButton) findViewById(R.id.estadisticas);
        retiro =(ImageButton) findViewById(R.id.retiro);


		consulta.setOnClickListener(new View.OnClickListener()
		{ @Override public void onClick(View v) {
			Intent intent = new Intent (MainActivity.this, Consulta.class);
			startActivity(intent); } });

		nuevo.setOnClickListener(new View.OnClickListener()
		{ @Override public void onClick(View v) {
			Intent intent = new Intent (MainActivity.this, Nuevo.class);
			startActivity(intent); } });

		// Controlador de bases de datos SqLite
		db = new SQLiteHandler(getApplicationContext());

		// Administrador de sesiones
		session = new SessionManager(getApplicationContext());

		if (!session.isLoggedIn()) {
			logoutUser();
		}

		// Recuperación de detalles de usuario desde SQLite
		HashMap<String, String> user = db.getUserDetails();

		String name = user.get("name");
		String usuario = user.get("usuario");

		// Visualización de los detalles del usuario en la pantalla
		txtName.setText(name);
		txtUsuario.setText(usuario);

		// Botón de cierre de sesión haga clic en evento
		btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutUser();
			}
		});
	}


	  //Cerrar la sesión del usuario. Se establecerá el indicador isLoggedIn en false en shared
	// preferences Borra los datos de usuario de la tabla sqlite users

	private void logoutUser() {
		session.setLogin(false);

		db.deleteUsers();

		// Lanzamiento de la actividad de inicio de sesión
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}


}
