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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import android.view.View.OnTouchListener;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class Control extends AppCompatActivity {
    ImageButton backCtrBtn;
    ImageButton btnOn, btnOff, connectBtn;

    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
//    ArrayList<BluetoothDevice> pairedDeviceArrayList;
    RelativeLayout layout_joystick;
    ImageView image_joystick, image_border;
    TextView textView1, textView2, textView3, textView4, textView5;
    joystick js;

    byte [] buf_on = {(byte)0xff, 0x55, 0x09, 0x00, 0x02, 0x08, 0x07, 0x02, 0x01, 0x00, (byte)0xff, (byte)0xff,(byte)0xff, 0x55, 0x09, 0x00, 0x02, 0x08, 0x07, 0x02, 0x02, (byte)0xff, (byte)0xf1, (byte)0xff,  };
    byte [] buf_off = {(byte)0xff, 0x55, 0x09, 0x00, 0x02, 0x08, 0x07, 0x02, 0x00, 0x00, 0x00, 0x00 };

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
        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView5 = (TextView)findViewById(R.id.textView5);

        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

        btnOn = (ImageButton)findViewById(R.id.btnOn);
        btnOff = (ImageButton)findViewById(R.id.btnOff);
        backCtrBtn = (ImageButton) findViewById(R.id.btnCtrBack);
        connectBtn = (ImageButton)findViewById(R.id.btnConnectBluetooth);

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
           setup();
            }
        });

//        btnSendData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(myThreadConnected!=null){
////                    byte[] bytesToSend = getData.getText().toString().getBytes();
////                    myThreadConnected.write(bytesToSend);
//              }
//            }
//        });

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myThreadConnected!=null){
                    myThreadConnected.write(buf_on);
                }
            }
        });

//        btnOff.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(myThreadConnected!=null){
//                    myThreadConnected.write(buf_off);
//                }
//            }
//        });
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

        js = new joystick(getApplicationContext(), layout_joystick, R.drawable.vrobox);
        js.setStickSize(180, 180);
        js.setLayoutSize(650, 650);
        js.setLayoutAlpha(255);
        js.setStickAlpha(255);
        js.setOffset(90);
        js.setMinimumDistance(50);

        layout_joystick.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                if(arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    textView1.setText("X : " + String.valueOf(js.getX()));
                    textView2.setText("Y : " + String.valueOf(js.getY()));
                    textView3.setText("Angle : " + String.valueOf(js.getAngle()));
                    textView4.setText("Distance : " + String.valueOf(js.getDistance()));

                    int direction = js.get8Direction();
                    if(direction == joystick.STICK_UP) {
                        textView5.setText("Direction : Up");
                    } else if(direction == joystick.STICK_UPRIGHT) {
                        textView5.setText("Direction : Up Right");
                    } else if(direction == joystick.STICK_RIGHT) {
                        textView5.setText("Direction : Right");
                    } else if(direction == joystick.STICK_DOWNRIGHT) {
                        textView5.setText("Direction : Down Right");
                    } else if(direction == joystick.STICK_DOWN) {
                        textView5.setText("Direction : Down");
                    } else if(direction == joystick.STICK_DOWNLEFT) {
                        textView5.setText("Direction : Down Left");
                    } else if(direction == joystick.STICK_LEFT) {
                        textView5.setText("Direction : Left");
                    } else if(direction == joystick.STICK_UPLEFT) {
                        textView5.setText("Direction : Up Left");
                    } else if(direction == joystick.STICK_NONE) {
                        textView5.setText("Direction : Center");
                    }
                } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
                    textView1.setText("X :");
                    textView2.setText("Y :");
                    textView3.setText("Angle :");
                    textView4.setText("Distance :");
                    textView5.setText("Direction :");
                }
                return true;
            }
        });
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
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("Robox")) {
                    Toast.makeText(Control.this,"Start thread connect to bluetooth device", Toast.LENGTH_LONG).show();
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
//                textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
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
                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            if (success) {
                //connect successful
                final String msgconnected = "connect successful: " + "BluetoothDevice: " + bluetoothDevice;
//                textStatus.setText(msgconnected);
                startThreadConnected(bluetoothSocket);
            }
            else {//fail
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
                    final String msgReceived = String.valueOf(bytes) + " bytes received:\n" + strReceived;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                           textStatus.setText(msgReceived);
                        }});

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    final String msgConnectionLost = "Connection lost:\n"+ e.getMessage();
//                   runOnUiThread(new Runnable(){
//
//                        @Override
//                        public void run() {
//                           textStatus.setText(msgConnectionLost);
//                        }});
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

