package com.example.bluetooth_api_app;

import java.io.IOException;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class ConnectThread extends Thread {
    private BluetoothSocket mBluetoothSocket;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
            .getDefaultAdapter();
    private final Handler mHandler;
    public static final UUID APP_UUID = UUID
            .fromString("aeb9f938-a1a3-4947-ace2-9ebd0c67adf1");

    @SuppressLint("MissingPermission")
    public ConnectThread(String deviceID, Handler handler) {
        BluetoothDevice mDevice = mBluetoothAdapter.getRemoteDevice(deviceID);
        mHandler = handler;
        try {
            mBluetoothSocket = mDevice
                    .createRfcommSocketToServiceRecord(APP_UUID);
        } catch (IOException e) {
            Log.d(ContentValues.TAG, "Socket's create() method failed", e);
        }
    }

    @SuppressLint("MissingPermission")
    public void run() {
        Toast.makeText(null, "Connecting...", Toast.LENGTH_SHORT).show();
        mBluetoothAdapter.cancelDiscovery();
        try {
            mBluetoothSocket.connect();
            manageConnectedSocket();
        } catch (IOException connectException) {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                Log.d(ContentValues.TAG, "Could not close the client socket", e);
            }
        }
    }

    private void manageConnectedSocket() {
        /*ConnectionThread conn = new ConnectionThread(
                mBluetoothSocket, mHandler);
        mHandler.obtainMessage(
                        DataTransferActivity.SOCKET_CONNECTED, conn)
                .sendToTarget();
        conn.start();*/
        Toast.makeText(null, "Connected", Toast.LENGTH_SHORT).show();
    }

    public void cancel() {
        try {
            mBluetoothSocket.close();
        } catch (IOException ignored) {
        }
    }

}