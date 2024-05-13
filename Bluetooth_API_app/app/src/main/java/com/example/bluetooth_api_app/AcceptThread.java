package com.example.bluetooth_api_app;

import java.io.IOException;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

class AcceptThread extends Thread {
    private BluetoothServerSocket mServerSocket;
    private BluetoothSocket mBluetoothSocket = null;
    private Context context;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final Handler mHandler;
    public static final UUID APP_UUID = UUID
            .fromString("aeb9f938-a1a3-4947-ace2-9ebd0c67adf1");


    @RequiresApi(api = Build.VERSION_CODES.S)
    public AcceptThread(Handler handler, Context context) {
        this.context = context;
        mHandler = handler;
        try {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
            mServerSocket = mBluetoothAdapter
                    .listenUsingRfcommWithServiceRecord("Bluetooth_API_App",
                            APP_UUID);
        } catch (IOException ignored) {
        }
    }

    public void run() {
        Toast.makeText(null, "Waiting for connection", Toast.LENGTH_SHORT).show();
        while (true) {
            try {
                mBluetoothSocket = mServerSocket.accept();
                manageConnectedSocket();
                mServerSocket.close();
                break;
            } catch (IOException e1) {
                if (mBluetoothSocket != null) {
                    try {
                        mServerSocket.close();
                    } catch (IOException e2) {
                        Log.d(ContentValues.TAG, "Socket Type: " + "Insecure");
                    }
                }
            }
        }
    }

    private void manageConnectedSocket() {
        Toast.makeText(null, "Connected", Toast.LENGTH_SHORT).show();
        /*ConnectionThread conn = new ConnectionThread(mBluetoothSocket, mHandler);
        mHandler.obtainMessage(DataTransferActivity.SOCKET_CONNECTED, conn)
                .sendToTarget();
        conn.start();*/
        Toast.makeText(null, "HANDLER", Toast.LENGTH_SHORT).show();
    }

    public void cancel() {
        try {
            if (null != mServerSocket)
                mServerSocket.close();
        } catch (IOException ignored) {
        }
    }
}