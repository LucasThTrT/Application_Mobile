package com.example.projet;


import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Activite2 extends AppCompatActivity {

    // Numéro de la maison donnée
    public int NumeroMaison = 31;

    // URL pour récupérer les données
    private final String url = "https://www.bde.enseeiht.fr/~bailleq/smartHouse/api/v1/devices/" + NumeroMaison;

    // JSONArray récupéré
    private JSONArray data_recup;

    // String des logs
    private String string_recup;

    // Liste des Devices
    private final Map<Integer, Device> ListeDevice = new HashMap<Integer, Device>();

    // Liste des views
    private final Map<Integer, View> Liste_Device_Views = new HashMap<Integer, View>();

    // Liste des boutons
    private final  Map<Integer, Button> Liste_Boutons = new HashMap<Integer, Button>();

    private LinearLayout Global_L_Layout;

    // File d'attente pour les requêtes HTTP
    private RequestQueue queue;

    // Handler pour les requêtes HTTP
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_activity2);

        // Initialisation de la file d'attente
        this.queue = Volley.newRequestQueue(this);

        //on rajoute à la varibale globale le layout
        Global_L_Layout = findViewById(R.id.linearLayout);

        // Récupération depuis requête HTTP des données et rajout dans les views
        // On le fait une première fois pour initialiser les données et ne pas attendre 5 secondes du Handler
        this.RequestDevices();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestDevices(); // Récupération des données depuis une requête HTTP
                handler.postDelayed(this, 5000); // On relance le handler toutes les 10 secondes
                //Toast.makeText(getApplicationContext(), "Mise à jour des données", Toast.LENGTH_SHORT).show();
            }
        }, 5000);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Arrêter le Handler lorsque l'activité est détruite
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    // Récupération des données depuis une requête HTTP
    public void RequestDevices() {
        RequestQueue queue = Volley.newRequestQueue(Activite2.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, requestArraySuccessListener(), requestArrayErrorListener());
        queue.add(jsonArrayRequest);
    }


    public View createDeviceView(Device dev) {

        // Création du layout
        RelativeLayout layout = new RelativeLayout(this );

        // Récupération des information du device
        int autonomy = dev.getAutonomy();
        String nom = dev.getName();
        Boolean etat = dev.getState();
        String modele = dev.getModele();
        String data = dev.getData();
        String type = dev.getType();
        String marque = dev.getBrand();
        int ID = dev.getID();

        String Setat;
        if (etat) {
            Setat = "ON";
        } else {
            Setat = "OFF";
        }

        String letexte = ID + " - [" + marque + "-" + modele + "] " + nom;

        // Paramètres Titre
        RelativeLayout.LayoutParams paramsTitle = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsTitle.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        //paramsTopLeft.addRule(RelativeLayout.ABOVE, RelativeLayout.TRUE);

        TextView titleview = new TextView(this );
        titleview.setText(letexte);
        titleview.setTypeface(null, Typeface.BOLD);
        //paramsTitle.setMargins(20, 20, 20, 20);
        layout.addView(titleview, paramsTitle);

        String texte2;
        boolean b = !(data.isEmpty()) || data.contentEquals("NoData"); // Il y a de l'information associée au device
        if (autonomy != -1) {
            if (b) {
                texte2 = "Type : " + type + " Data : " + " " + data + " " + " Autonomy : " + autonomy + "%";
            } else {
                texte2 = "Type : " + type + " Autonomy : " + autonomy + "%";
            }
        } else {
            if (b) {
                texte2 = "Type : " + type + " Data : " + " " + data + " ";
            } else {
                texte2 = "Type : " + type;
            }
        }


        // Paramètres pour positionner les informations en dessous du titre
        RelativeLayout.LayoutParams paramsInfo = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsInfo.addRule(RelativeLayout.BELOW, titleview.getId());
        paramsInfo.addRule(RelativeLayout.BELOW, RelativeLayout.TRUE);
        paramsInfo.setMargins(0, 70, 0, 100);


        TextView infoView = new TextView(this);
        infoView.setText(texte2);
        layout.addView(infoView, paramsInfo);

        // Création du bouton
        Button bouton_etat = new Button(this);
        bouton_etat.setText(Setat);
        // Définition de la couleur du bouton en fonction de l'état
        if (etat) {
            // Bouton vert pour l'état ON
            bouton_etat.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        } else {
            // Bouton rouge pour l'état OFF
            bouton_etat.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        }

        Liste_Boutons.put(dev.getID(), bouton_etat);

        // Paramètres pour positionner le bouton à droite du texte
        RelativeLayout.LayoutParams paramsTopRight = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsTopRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        paramsTopRight.addRule(RelativeLayout.BELOW, titleview.getId());
        layout.addView(bouton_etat, paramsTopRight);

        // Écouteur de clic pour le bouton
        bouton_etat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ecrire pop up pour voir si le bouton a été cliqué
                Toast.makeText(getApplicationContext(), "Switch enregistré " + String.valueOf(dev.getID()), Toast.LENGTH_SHORT).show();
                SwitchModeDevice(dev.getID());
                // Message de log
                Log.d("Switch Mode Device", "Maj du device numero " + dev.getID());
            }
        });

        return layout ;
    }


    private void SwitchModeDevice(int deviceId) {
        StringRequest sr = new StringRequest(
                Request.Method.POST,
                "https://www.bde.enseeiht.fr/~bailleq/smartHouse/api/v1/devices/" + NumeroMaison + "/" + String.valueOf(deviceId),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Toast.makeText(getApplicationContext(), "Switch Mode du device Réussie !" + String.valueOf(deviceId), Toast.LENGTH_SHORT).show();
                        RequestDevices(); // Récupération des données depuis une requête HTTP car on a changé l'état d'un device
                        // Il se peut que le device ne puisse pas être switché -> par de changement en mode local mais par le serveur !
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Requête POST echouée", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("deviceId", String.valueOf(deviceId));
                params.put("houseId", String.valueOf(NumeroMaison));
                params.put("action", "turnOnOff");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type",
                        "application/x-www-form-urlencoded; charset=utf-8");
                return headers;
            }
        };
        // Ajout de la requête à la file d'attente
        this.queue.add(sr);
        Log.d("SwitchDeviceMode", "queue.add pour le dev num " + String.valueOf(deviceId));
    }


    private Response.Listener<JSONArray> requestArraySuccessListener () {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.d(ContentValues.TAG, jsonArray.toString());
                data_recup = jsonArray;
                string_recup = jsonArray.toString();
                System.out.println(string_recup);

                try {
                    // On supprime l'affichage des devices précédents
                    Global_L_Layout.removeAllViews();

                    for (int i = 0; i < data_recup.length(); i++) {
                        JSONObject device = data_recup.getJSONObject(i);
                        Device device_a_rajouter = new Device();
                        int id = device.getInt("ID");
                        String brand = device.getString("BRAND");
                        String modele = device.getString("MODEL");
                        String name = device.getString("NAME");
                        String type = device.getString("TYPE");
                        int autonomy = device.getInt("AUTONOMY");
                        int state = device.getInt("STATE");
                        String data = device.getString("DATA");

                        Boolean Etat;
                        if (state == 1) {
                            Etat = Boolean.TRUE;
                        } else {
                            Etat = Boolean.FALSE;
                        }

                        // On rajoute les éléments récupérés dans un objet de type device
                        device_a_rajouter.setID(id);
                        device_a_rajouter.setModele(modele);
                        device_a_rajouter.setBrand(brand);
                        device_a_rajouter.setName(name);
                        device_a_rajouter.setType(type);
                        device_a_rajouter.setAutonomy(autonomy);
                        device_a_rajouter.setState(Etat);
                        device_a_rajouter.setData(data);

                        // On rajoute le device à la liste des device
                        ListeDevice.put(device_a_rajouter.getID(), device_a_rajouter);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // Créer les views pour chaque device
                for (Device dev : ListeDevice.values()) {
                    View view_device = createDeviceView(dev);
                    int dev_id = dev.getID();
                    Liste_Device_Views.put(dev_id, view_device);
                    Global_L_Layout.addView(view_device);
                }
            }
        };
    }

    private Response.ErrorListener requestArrayErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(ContentValues.TAG, Objects.requireNonNull(volleyError.getMessage()));
            }
        };
    }
}