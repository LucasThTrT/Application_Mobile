package com.example.bluetooth_api_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnClient;

    private Button btnServeur;

    private Button btnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnClient = findViewById(R.id.Client);
        btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer l'activité ClientActivity
                Intent intent = new Intent(MainActivity.this, ClientActivity.class);
                startActivity(intent);
            }
        });

        btnServeur = findViewById(R.id.Serveur);
        btnServeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer l'activité ServeurActivity
                Intent intent = new Intent(MainActivity.this, ServeurActivity.class);
                startActivity(intent);
            }
        });

        btnTest = findViewById(R.id.Test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer l'activité ServeurActivity
                Intent intent = new Intent(MainActivity.this, ServeurDevicesActivity.class);
                startActivity(intent);
            }
        });
    }
}
