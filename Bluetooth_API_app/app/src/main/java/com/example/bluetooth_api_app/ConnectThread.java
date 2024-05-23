package com.example.bluetooth_api_app;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {
    private BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    private final Context mContext;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter, Context context) {
        mContext = context;
        mmDevice = device;
    }

    @Override
    public void run() {
        try {
            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH_PERMISSION);
                return;
            }
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            mmSocket.connect();
            // Connection successful, perform further actions if needed.
            // On met le socket en variable globale pour pouvoir l'utiliser dans la prochaine activité !
            BluetoothSocketManager.setSocket(mmSocket);
            // START NEW ACTIVITY
            Intent intent = new Intent(mContext, ClientDevicesActivity.class);
            mContext.startActivity(intent);

        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
        }
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}
