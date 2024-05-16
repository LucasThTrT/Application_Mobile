package com.example.bluetooth_api_app;

import static android.content.ContentValues.TAG;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClientDevicesActivity extends AppCompatActivity {

    // Liste des Devices
    private final Map<Integer, Device> ListeDevice = new HashMap<Integer, Device>();

    // Liste des views
    private final Map<Integer, View> Liste_Device_Views = new HashMap<Integer, View>();

    // Liste des boutons
    private final  Map<Integer, Button> Liste_Boutons = new HashMap<Integer, Button>();

    // JSONArray récupéré
    private JSONArray data_recup;
    private LinearLayout Global_L_Layout;

    private final Handler handler = new Handler();

    // Récupération de la socket
    private final BluetoothSocket socket = BluetoothSocketManager.getSocket();
    private InputStream mmInStream;
    private OutputStream mmOutStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_devices);

        // Récupération des flux d'entrée et de sortie
        try {
            mmInStream = socket.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            mmOutStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Création du Thread pour la réception des données
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getApplicationContext(), "Thread de réception démarré", Toast.LENGTH_SHORT).show();
                // AUGMENTATION DE LA TAILLE DU BUFFER SINON ON PERD DES DONNEES
                // pas possible de changer le buffer comme ça
                byte[] buffer = new byte[1024];
                int bytes;
                while (true) {
                    try {
                        // Il faut lire tout le buffer
                        // Tout mettre dans une liste
                        // mais n'extraire qu'un seul Json à la fois
                        // Pour ensuite faire updateDeviceViews


                        bytes = mmInStream.read(buffer);

                        String json = new String(buffer, 0, bytes);
                        JSONObject jsonObject = new JSONObject(json);

                        // Mise à jour des vues
                        handler.post(() -> updateDeviceViews(jsonObject));

                        /*String json = new String(buffer, 0, bytes);
                        JSONArray jsonArray = new JSONArray(json);
                        //JSONObject jsonObject = new JSONObject(json);

                        // Mise à jour des vues
                        handler.post(() -> updateDeviceViews(jsonArray));*/

                    } catch (IOException e) {
                        break;
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        // Démarrage du thread
        thread.start();

        //on rajoute à la varibale globale le layout
        Global_L_Layout = findViewById(R.id.linearLayout);

        // On récupère les devices par handler read bluetooth

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
                try {
                    SwitchModeDevice(dev.getID());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // Message de log
                Log.d("Switch Mode Device", "Maj du device numero " + dev.getID());
            }
        });

        return layout ;
    }


    public void SwitchModeDevice(int id) throws IOException {
        // Convertir int en string puis en byte !
        byte[] buffer = String.valueOf(id).getBytes();
        // ENVOI PAR SOCKET
        mmOutStream.write(buffer);
    }


    /*private Response.Listener<JSONArray> requestArraySuccessListener () {
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
    }*/

    private void updateDeviceViews(JSONObject jsonObject) {
    /*private void updateDeviceViews(JSONArray jsonArray) {

        Log.d(TAG, jsonArray.toString());
        data_recup = jsonArray;
        String string_recup = jsonArray.toString();
        System.out.println(string_recup);*/

        try {
            // On supprime l'affichage des devices précédents
            //Global_L_Layout.removeAllViews();

            //for (int i = 0; i < jsonArray.length(); i++) {
                //JSONObject deviceJson = jsonArray.getJSONObject(i);
                JSONObject deviceJson = jsonObject;
                Device device = new Device();
                int id = deviceJson.getInt("ID");
                String brand = deviceJson.getString("BRAND");
                String modele = deviceJson.getString("MODEL");
                String name = deviceJson.getString("NAME");
                String type = deviceJson.getString("TYPE");
                int autonomy = deviceJson.getInt("AUTONOMY");
                int state = deviceJson.getInt("STATE");
                String data = deviceJson.getString("DATA");

                Boolean etat;
                if (state == 1) {
                    etat = Boolean.TRUE;
                } else {
                    etat = Boolean.FALSE;
                }

                // On crée un objet Device avec les données récupérées
                device.setID(id);
                device.setModele(modele);
                device.setBrand(brand);
                device.setName(name);
                device.setType(type);
                device.setAutonomy(autonomy);
                device.setState(etat);
                device.setData(data);

                // On crée la vue correspondante pour ce device
                View deviceView = createDeviceView(device);

                // On ajoute la vue au layout global
                Global_L_Layout.addView(deviceView);
           // }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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