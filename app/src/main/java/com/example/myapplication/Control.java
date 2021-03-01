package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class Control extends AppCompatActivity {
    ImageButton backCtrBtn;
    Button connectBtn, btnSendData;

    private static final int REQUEST_ENABLE_BT = 1;


    BluetoothAdapter bluetoothAdapter;

//    ArrayList<BluetoothDevice> pairedDeviceArrayList;

    TextView textStatus;
    ListView listViewPairedDevice;
    LinearLayout inputPane;
    EditText inputField, getData;
    Button btnSend;


//    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";

    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }

        btnSendData = (Button)findViewById(R.id.btnSend);
        getData = (EditText)findViewById(R.id.inputData);
        backCtrBtn = (ImageButton) findViewById(R.id.btnCtrBack);
        connectBtn = (Button)findViewById(R.id.btnConnect);
        textStatus = (TextView)findViewById(R.id.info);

        /* Handle back button when clicked */
        backCtrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Control.this, MainActivity.class));
            }
        });
        /* Handle connect button when clicked*/
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()) {
//                    Toast.makeText(Control.this,"Bluetooth is diable", Toast.LENGTH_LONG).show();
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }
//            setup();
            }
        });

        btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myThreadConnected!=null){
                    byte[] bytesToSend = getData.getText().toString().getBytes();
                    myThreadConnected.write(bytesToSend);
              }
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            Toast.makeText(Control.this,
                    "FEATURE_BLUETOOTH NOT support",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(Control.this,
                    "Bluetooth is not supported on this hardware platform",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // Create a BroadcastReceiver for ACTION_FOUND
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            // When discovery finds a device
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                // Get the BluetoothDevice object from the Intent
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                // Add the name and address to an array adapter to show in a ListView
//                textStatus.append("\n unpair" + device.getName() + device.getAddress() );
//                Log.i("ctr", "hello" );
//                if (device.getName() == "ESP32_LED_Control") {
//                        textStatus.append(" get Device ");
//                        try {
//                            Method method = device.getClass().getMethod("createBond,", (Class[]) null);
//                            method.invoke(device, (Object[]) null);
//                        } catch  (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//    };

    private void setup( ) {

        /*bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //check to see if there is BT on the Android device at all
        if (bluetoothAdapter == null){
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(this, "No Bluetooth on this handset", duration).show();
        }
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        //re-start discovery
        bluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        */
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
//            pairedDeviceArrayList = new ArrayList<BluetoothDevice>();

            for (BluetoothDevice device : pairedDevices) {
//                pairedDeviceArrayList.add(device);

                if (device.getName().equals("ESP32_LED_Control")) {
                    //Toast.makeText(Control.this,"Start thread connect to bluetooth device", Toast.LENGTH_LONG).show();
                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                    myThreadConnectBTdevice.start();
                }
            }
       }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
//        if (myThreadConnectBTdevice!=null) {
//            myThreadConnectBTdevice.cancel();
//        }
//        unregisterReceiver(mReceiver, filter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                setup();
            }
            else {
                Toast.makeText(this, "BlueTooth NOT enabled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    //Called in ThreadConnectBTdevice once connect successed
    //to start ThreadConnected
    private void startThreadConnected(BluetoothSocket socket)
    {
        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }
    private class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;

        private ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device;
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
                textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
//                        textStatus.setText("something wrong bluetoothSocket.connect(): \n" + eMessage);
                    }
                });

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if(success) {
                //connect successful
                final String msgconnected = "connect successful:\n"
                        + "BluetoothSocket: " + bluetoothSocket + "\n"
                        + "BluetoothDevice: " + bluetoothDevice;

                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
//                        textStatus.setText(msgconnected);
//
//                        listViewPairedDevice.setVisibility(View.GONE);
//                       inputPane.setVisibility(View.VISIBLE);
                    }});

                startThreadConnected(bluetoothSocket);
            }else{
                //fail
            }
        }

        public void cancel()
        {
            Toast.makeText(getApplicationContext(), "close bluetoothSocket", Toast.LENGTH_LONG).show();
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /*
    ThreadConnected:
    Background Thread to handle Bluetooth data communication
    after connected
     */
    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);
                    String strReceived = new String(buffer, 0, bytes);
//                    final String msgReceived = String.valueOf(bytes) +
//                            " bytes received:\n"
//                            + strReceived;
//                    Log.i("ctr", strReceived);
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
//                            textStatus.setText(msgReceived);
                        }});

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

//                    final String msgConnectionLost = "Connection lost:\n"
//                            + e.getMessage();
                   runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
//                            textStatus.setText(msgConnectionLost);
                        }});
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
//        pieChartView = findViewById(R.id.chart);
//
//        List pieData = new ArrayList<>();
//        pieData.add(new SliceValue(10, Color.parseColor("#00A2E8")).setLabel(""));
//        pieData.add(new SliceValue(10, Color.parseColor("#00A2E8")).setLabel(""));
//        pieData.add(new SliceValue(10, Color.parseColor("#00A2E8")).setLabel(""));
//        pieData.add(new SliceValue(10, Color.parseColor("#00A2E8")).setLabel(""));
//        pieData.add(new SliceValue(10, Color.parseColor("#00A2E8")).setLabel(""));
//        pieData.add(new SliceValue(10, Color.parseColor("#00A2E8")).setLabel(""));
//        pieData.add(new SliceValue(10, Color.parseColor("#00A2E8")).setLabel(""));
//        pieData.add(new SliceValue(10, Color.parseColor("#00A2E8")).setLabel(""));
//
//        PieChartData pieChartData = new PieChartData(pieData);
//        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
//        pieChartData.setHasCenterCircle(true).setCenterText1("").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
//        pieChartView.setPieChartData(pieChartData);

//    };

