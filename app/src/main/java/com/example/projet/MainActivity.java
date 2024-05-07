package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button bouton = (Button) findViewById((R.id.bouton1ID));
        bouton.setOnClickListener(this);

        Button bouton_http = (Button) findViewById((R.id.bouton_test_HTTP));
        bouton_http.setOnClickListener(this);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onClick(View view) {
        if (view.getId() == R.id.bouton1ID) {
            Button bouton = (Button) findViewById((R.id.bouton1ID));
            bouton.setText(R.string.first_button_reaction);

            // Création d'une intention
            Intent IntentActi2 = new Intent(this, Activite2.class);

            // Ajout d'un paramètre à l'intention
            String name = "ParametreActi2";
            IntentActi2.putExtra("name", name);

            // Démarrage de la nouvelle activité
            startActivity(IntentActi2);
        }
        if (view.getId() == R.id.bouton_test_HTTP) {
            Button bouton = (Button) findViewById((R.id.bouton_test_HTTP));
            bouton.setText(R.string.STR_HTTP);

            // Création d'une intention
            Intent IntentActi_Test_HTTP = new Intent(this, Test_HTPP.class);

            // Ajout d'un paramètre à l'intention
            String name = "Param_Test_HTTP";
            IntentActi_Test_HTTP.putExtra("name", name);


            //Toast.makeText(this, (CharSequence) "Lancement Activité 2", Toast.LENGTH_LONG).show();
            // Démarrage de la nouvelle activité
            startActivity(IntentActi_Test_HTTP);
        }
    }
}