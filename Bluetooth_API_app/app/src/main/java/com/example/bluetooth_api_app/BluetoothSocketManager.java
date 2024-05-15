package com.example.bluetooth_api_app;

import android.bluetooth.BluetoothSocket;

public class BluetoothSocketManager {
    private static BluetoothSocket socket;

    public static BluetoothSocket getSocket() {
        return socket;
    }

    public static void setSocket(BluetoothSocket bluetoothSocket) {
        socket = bluetoothSocket;
    }
}