package com.example.orgaapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    // Constantes para la conexion BT
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int REQUEST_ENABLE_BT = 1;

    // Variable del txt
    private EditText character;
    private Button btn;
    private Button btnCloseConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializamos variable txt
        character = findViewById(R.id.txtChar);
        btn = findViewById(R.id.btnChar);
        btnCloseConn = findViewById(R.id.btnCloseConn);

        // Creamos adaptador de bt
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            System.out.println("El dispositivo no soporta Bluetooth");
        }
        // System.out.println(btAdapter.getBondedDevices());

        // Verificamos si el adaptador bluethooth esta activo
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Creamos adaptador puente entre mobil y hc05
        BluetoothDevice hc05 = btAdapter.getRemoteDevice("E4:02:9B:84:EA:3F");
        System.out.println(hc05.getName());
        BluetoothSocket btSocket = null;

        // Conexion entre telefono y HC05
        int counter = 0;
        do {
            try {
                    btSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
                    btSocket.connect();
                    System.out.println("La conexion es: " + btSocket.isConnected());
            } catch (IOException e) {
                e.printStackTrace();
            }
            counter++;
        } while (!btSocket.isConnected() && counter < 3);

        BluetoothSocket finalBtSocket1 = btSocket;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendChar(finalBtSocket1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        BluetoothSocket finalBtSocket = btSocket;
        btnCloseConn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeConn(finalBtSocket);
            }
        });
    }

    public void sendChar (BluetoothSocket btSocket) throws IOException {
        OutputStream outputStream = btSocket.getOutputStream();
        char charToAscii = character.getText().toString().charAt(0);
        outputStream.write(charToAscii);
    }

    public void closeConn (BluetoothSocket btSocket) {
        // Conexion cerrada
        try {
            btSocket.close();
            System.out.println("La conexion es: " + btSocket.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}