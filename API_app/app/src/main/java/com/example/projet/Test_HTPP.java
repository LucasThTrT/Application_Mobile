package com.example.projet;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Test_HTPP extends AppCompatActivity {

    // URL pour récupérer les données
    private String url = "https://www.bde.enseeiht.fr/~bailleq/smartHouse/api/v1/devices/29";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_htpp);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest ( Request . Method.POST, url , requestSuccessListener () , volleyErrorListener ()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String , String>();
                params.put("deviceId", "Le device");

                return params;
            }

            @Override
            public  Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x−www−form−urlencoded");

                return params;
            }
        };
        queue.add(sr);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private Response.Listener<String> requestSuccessListener () {
        return new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d(ContentValues.TAG, response);
        } };
    }
    private Response.ErrorListener volleyErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(ContentValues.TAG, Objects.requireNonNull(volleyError.getMessage()));
            }
        };
}
}