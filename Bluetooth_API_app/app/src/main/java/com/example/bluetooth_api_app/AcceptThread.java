package com.example.bluetooth_api_app;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.UUID;

public class AcceptThread extends Thread {
    public BluetoothServerSocket mmServerSocket = null;
    // Socket communication
    public static BluetoothSocket socket = null;
    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final String NAME = "ConnectThread";
    private final Context mContext;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;


    public AcceptThread(Context context) {
        mContext = context;
    }


    public void run() {
        // Initialize the BluetoothServerSocket.
        // SI AVANT LE RUN IL PEUT Y AVOIR UN PROBLEME DE PERMISSION, IL FAUT METTRE L'INITIALISATION DANS LE RUN
        // POUR NE PAS AVOIR UN SERVER SOCKET NULL
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                // Request Bluetooth permission
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH_PERMISSION);
                return;
            }
            mmServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) {
            Log.e(ContentValues.TAG, "Socket's listen() method failed", e);
        }
        // BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
                BluetoothSocketManager.setSocket(socket);
            } catch (IOException e) {
                Log.e(ContentValues.TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                // The connection attempt succeeded. Perform work associated with

                // START THE NEW ACTIVITY
                Intent intent = new Intent(mContext, ServeurDevicesActivity.class);
                // Add the socket to the intent
                mContext.startActivity(intent);

                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(ContentValues.TAG, "Could not close the connect socket", e);
        }
    }
}