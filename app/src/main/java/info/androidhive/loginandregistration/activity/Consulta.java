package info.androidhive.loginandregistration.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.helper.SQLiteHandler;
import info.androidhive.loginandregistration.helper.SessionManager;

public class Consulta extends AppCompatActivity {
    private SessionManager session;
    private SQLiteHandler db;
    private ImageButton volver;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);


        session = new SessionManager(getApplicationContext());
        // Controlador de base de datos SQLite
        db = new SQLiteHandler(getApplicationContext());

        volver = (ImageButton)findViewById(R.id.volver);
        // volvemos al main
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Consulta.this, MainActivity.class);
                startActivity(intent);

            }
        });

    }
}