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
import android.os.CountDownTimer;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import android.view.View.OnTouchListener;
import android.os.Handler;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
public class Control extends AppCompatActivity {
    ImageButton backCtrBtn;
    ImageButton btnDance, btnLed, btnConnect, btnBuzzer, btnLedMatrix, btnRingLed;
    ImageButton btnGetSrf05, btnGetLine, btnGetColor, btnGetSound, btnGetLight, btnGetBtn;
    TextView textSrf05, textLine, textLight, textColor, textSound;
    boolean state_led = false;
    int ledColor = 0;
    int leftSpeed = 0, rightSpeed = 0;
    int buzzerFreq = 0, buzzerDuration = 0;
    int xPos, yPos, volumeSpeed;
    int index = 0;
    boolean state_getSrf05, state_getLightSensor, state_getLine, state_getButton;
    int val = 0;
    int cnt_effect;
    Timer timer;
    byte motion_effect [][] = {
        {0x00, 0x00, 0x00, 0x18, 0x18, 0x00, 0x00, 0x00},
        {0x00, 0x00, 0x3C, 0x3C, 0x3C, 0x3C, 0x00, 0x00},
        {0x00, 0x7E, 0x7E, 0x7E, 0x7E, 0x7E, 0x7E, 0x00},
        {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF},
        {0x18,0x3C,0x66,0x66,0x7E,0x66,0x66,0x66},//A
        {0x78,0x64,0x68,0x78,0x64,0x66,0x66,0x7C},//B
        {0x3C,0x62,0x60,0x60,0x60,0x62,0x62,0x3C},//C
        {0x78,0x64,0x66,0x66,0x66,0x66,0x64,0x78},//D
        {0x7E,0x60,0x60,0x7C,0x60,0x60,0x60,0x7E},//E
        {0x18,0x3c,0x7e,(byte)0xff,0x18,0x18,0x18,0x18}, // Mui ten di len
        {0x18,0x18,0x18,0x18,(byte)0xff,0x7e,0x3c,0x18}, // Mui ten di xuong
        {0x08,0x0c,0x0e,(byte)0xff,(byte)0xff,0x0e,0x0c,0x08}, // Mui ten sang phai
    };

    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
//    ArrayList<BluetoothDevice> pairedDeviceArrayList;
    RelativeLayout layout_joystick;
    ImageView image_joystick, image_border;
    TextView textView1, textView2, textView3, textView4, textView5;
    joystick js;
    int [] hpbdSong= {525, 525, 587, 525, 698, 659, 525,525, 587, 525,784, 698, 525, 525, 987, 880, 698, 659, 587, 932, 932, 880, 698, 784, 698};
    byte [] buf_on = {(byte)0xff, 0x55, 0x09, 0x00, 0x02, 0x08, 0x07, 0x02, 0x01, 0x00, (byte)0xff, (byte)0xff,(byte)0xff, 0x55, 0x09, 0x00, 0x02, 0x08, 0x07, 0x02, 0x02, (byte)0xff, (byte)0xf1, (byte)0xff, };
    byte [] buf_off = {(byte)0xff, 0x55, 0x09, 0x00, 0x02, 0x08, 0x07, 0x02, 0x00, 0x00, 0x00, 0x00 };
    byte [] joyStick = {(byte)0xff, 0x55, 0x07, 0x00, 0x02, 0x05, 0,0,0,0,};
    byte[] ledMatrix= {(byte)0xff, 0x55, 0x0c, 0x00, 0x02, 0x07, 0x18,0x3C,0x66,0x66,0x7E,0x66,0x66,0x66, 0x14};
    byte[] buzzer = {(byte)0xff, 0x55, 0x07, 0x00, 0x02, 0x09, 0,0,0,0,};
    byte[] getSrf05 = {(byte)0xff, 0x55, 0x04, 0x00, 0x01, 0x01, 0,0,0,0,};
    byte[] getLine = {(byte)0xff, 0x55, 0x04, 0x00, 0x01, 0x02, 0,0,0,0,};
    byte[] getLight = {(byte)0xff, 0x55, 0x04, 0x00, 0x01, 0x03, 0,0,0,0,};
    byte[] getBtn = {(byte)0xff, 0x55, 0x04, 0x00, 0x01, 0x06, 0,0,0,0,};
    byte[] getColor = {(byte)0xff, 0x55, 0x04, 0x00, 0x01, 0x01, 0,0,0,0,};
    byte[] getMode = {(byte)0xff, 0x55, 0x04, 0x00, 0x01, 0x01, 0,0,0,0,};
    byte[] byteSrf05 = {0,0,0,0};
    byte[] byteLine = {0,0,0,0};
    byte[] byteBtn = {0,0,0,0};
    byte[] byteLightSensor = {0,0,0,0};

//    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";

    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;

    public void setupMatrixData (byte data[]) {
            for (int j = 0; j <8; j++) {
                ledMatrix[j + 6] = data[j];
        }
    }
    public void getData(String module) {
         TimerTask timerTask = new TimerTask() {
            @Override
             public void run() {
                if (module == "SRF05") {
                    if (myThreadConnected != null) {
                        myThreadConnected.write(getSrf05);
                    }
                }
                else if (module == "LIGHT") {
                    if (myThreadConnected != null) {
                        myThreadConnected.write(getLight);
                    }
                }
                else if (module == "LINE") {
                    if (myThreadConnected != null) {
                        myThreadConnected.write(getLine);
                    }
                }
                else if (module == "BTN") {
                    if (myThreadConnected != null) {
                        myThreadConnected.write(getBtn);
                    }
                }
            }
         };
         if (timer != null)
             timer.cancel();;
         timer = new Timer("Timer");
         if (module != "None") {
             timer.schedule(timerTask, 0, 500);
         }
         else {
             textSrf05.setText("");
             textLight.setText("");
             if (timer != null) {
                 timer.cancel();;
             }
         }
     }

    public static float byteArray2Float(byte[] bytes)  {
        int intBits = (((byte)bytes[3] & 0xFF) << 24) |
                (((byte)bytes[2] & 0xFF) << 16) |
                (((byte)bytes[1] & 0xFF) << 8) |
                ((byte)bytes[0] & 0xFF);
        return Float.intBitsToFloat(intBits);
    }

    void dutySend(long duration){
        new CountDownTimer(6000, duration) {
            @Override
            public void onTick(long i) {
                val++;
                textLine.setText(String.format("%d",val));
                //textLine.setText("try text");
            }
            @Override
            public  void onFinish() {

            }
        }.start();
    }

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

        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

//        btnDance = (ImageButton)findViewById(R.id.btn_dance);
        btnLed = (ImageButton)findViewById(R.id.btn_ledRGB);
        btnLedMatrix = (ImageButton)findViewById(R.id.btn_ledmatrix);
        btnRingLed=(ImageButton)findViewById(R.id.btn_ringled);
        backCtrBtn = (ImageButton) findViewById(R.id.btnCtrBack);
        btnConnect = (ImageButton)findViewById(R.id.btnConnectBluetooth);
        btnBuzzer = (ImageButton)findViewById(R.id.btn_buzzer);

        btnGetSrf05 = (ImageButton)findViewById(R.id.btnIconSrf05);
        btnGetColor = (ImageButton)findViewById(R.id.btnIconColor);
        btnGetLight = (ImageButton)findViewById(R.id.btnIconLight);
        btnGetLine = (ImageButton)findViewById(R.id.btnIconLine);
        btnGetBtn = (ImageButton)findViewById(R.id.btnMode1);

        textSrf05 = (TextView)findViewById(R.id.text_srf05);
        textLine = (TextView)findViewById(R.id.text_line);
        textLight = (TextView)findViewById(R.id.text_light);
        textColor = (TextView)findViewById(R.id.text_color);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 5000);

        /* Handle back button when clicked */
        backCtrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Control.this, MainActivity.class));
            }
        });
        /* Handle connect button when clicked*/
        btnConnect.setOnClickListener(new View.OnClickListener() {
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

//        btnDance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for (int i = 0; i <= 255 ; i = i + 1 ) {
//                    leftSpeed = -(255 - i);
//                    rightSpeed = -(255 - i);
//                    joyStick[6] = (byte) (leftSpeed & (byte) 0xff);
//                    joyStick[7] = (byte) ((leftSpeed >> 8) & (byte) 0xff);
//
//                    joyStick[8] = (byte) (rightSpeed & (byte) 0xff);
//                    joyStick[9] = (byte) ((rightSpeed >> 8) & (byte) 0xff);
//                    if (myThreadConnected != null) {
//                        myThreadConnected.write(joyStick);
//                    }
//                    for (int j = 0; j< 10000; j++) {
//                        for (int k = 0; k < 2000; k++) {}
//                    }
//                }
//            }
//        });

        btnLed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (ledColor) {
                    case 0:
                        btnLed.setBackgroundResource(R.drawable.off);
                        buf_on[9] = buf_on[21] =  0;
                        buf_on[10] = buf_on[22] = 0;
                        buf_on[11] = buf_on[23] =0;
                        break;
                    case 1:
                        btnLed.setBackgroundResource(R.drawable.on);
                        buf_on[9] = buf_on[21] = (byte)0xff;
                        buf_on[10] = buf_on[22] = 0;
                        buf_on[11] = buf_on[23]= 0;
                        break;
                    case 2:
                        btnLed.setBackgroundResource(R.drawable.on);
                        buf_on[9] = buf_on[21]  = 0x33;
                        buf_on[10] = buf_on[22] = (byte)0xff;
                        buf_on[11] = buf_on[23] = 0x33;
                        break;
                    case 3:
                        btnLed.setBackgroundResource(R.drawable.on);
                        buf_on[9] = buf_on[21]  = 0x33;
                        buf_on[10] = buf_on[22] = 0x33;
                        buf_on[11] = buf_on[23] = (byte)0xff;
                        break;
                    case 4:
                        btnLed.setBackgroundResource(R.drawable.on);
                        buf_on[9] = buf_on[21]  = 0x02;
                        buf_on[10] = buf_on[22] = (byte)0xFC;
                        buf_on[11] = buf_on[23] = (byte)0xd7;
                        break;
                    case 5:
                        btnLed.setBackgroundResource(R.drawable.on);
                        buf_on[9] = buf_on[21] = 0x69;
                        buf_on[10] = buf_on[22] = 0x3b;
                        buf_on[11] = buf_on[23] = (byte)0xb3;
                        break;
                    case 6:
                        btnLed.setBackgroundResource(R.drawable.on);
                        buf_on[9] = buf_on[21]  = (byte)0xff;
                        buf_on[10] = buf_on[22] = 0x46;
                        buf_on[11] = buf_on[23]= (byte)0xA0;
                        break;
                }
                if (myThreadConnected != null) {
                    myThreadConnected.write(buf_on);
                }
                ledColor++;
                if (ledColor > 6)
                    ledColor = 0;
            }
        });

        btnLedMatrix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cnt_effect++;
                if (cnt_effect >= 12)
                    cnt_effect = 0;
                setupMatrixData (motion_effect[cnt_effect]);
                if(myThreadConnected != null) {
                    myThreadConnected.write(ledMatrix);
                }
            }
        });
        btnBuzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index >= 25)
                    index = 0;
                buzzerFreq  =  hpbdSong[index];
                buzzerDuration = 250;
                buzzer[6] = (byte) (buzzerFreq  & (byte)0xff);
                buzzer[7] = (byte) ((buzzerFreq  >> 8) & (byte)0xff);

                buzzer[8] = (byte) (buzzerDuration  & (byte)0xff);
                buzzer[9] = (byte) ((buzzerDuration  >> 8) & (byte)0xff);
                if (myThreadConnected != null) {
                    myThreadConnected.write(buzzer);
                }
                index++;
            }
        });
        btnGetSrf05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state_getSrf05 = !state_getSrf05;
                if (state_getSrf05) {
                   getData("SRF05");
                    btnGetSrf05.setBackgroundResource(R.drawable.srf05);
                }
                else {
                   getData("None");
                    btnGetSrf05.setBackgroundResource(R.drawable.srf05_off);
                }
            }
        });

        btnGetLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state_getLine = !state_getLine;
                if (state_getLine) {
                    getData("LINE");
                    btnGetLine.setBackgroundResource(R.drawable.line1);
                }
                else {
                    getData("None");
                    btnGetLine.setBackgroundResource(R.drawable.line_off);
                }
            }
        });
        btnGetLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state_getLightSensor = !state_getLightSensor;
                if (state_getLightSensor) {
                    getData("LIGHT");
                    btnGetLight.setBackgroundResource(R.drawable.light);
                }
                else {
                    getData("None");
                    btnGetLight.setBackgroundResource(R.drawable.light_off);
                }
            }
        });

        btnGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state_getButton = !state_getButton;
                if (state_getButton) {
                    getData("BTN");
                    btnGetBtn.setBackgroundResource(R.drawable.button);
                }
                else {
                    getData("None");
                    btnGetBtn.setBackgroundResource(R.drawable.butto_off);
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

        js = new joystick(getApplicationContext(), layout_joystick, R.drawable.vrobox);
        js.setStickSize(180, 180);
        js.setLayoutSize(650, 650);
        js.setLayoutAlpha(255);
        js.setStickAlpha(255);
        js.setOffset(90);
        js.setMinimumDistance(20);

        layout_joystick.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                if(arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
//                    textView1.setText("X : " + String.valueOf(js.getX() ));
//                    textView2.setText("Y : " + String.valueOf(js.getY() ));
//                    textView3.setText("Angle : " + String.valueOf(js.getAngle()));
                   //textView4.setText("Distance : " + String.valueOf(js.getDistance() ));
                     xPos = (int)js.getX();
                     if (xPos > 255)
                         xPos = 255;

                     yPos = (int)js.getY();
                     if (yPos > 255)
                         yPos = 255;

                    volumeSpeed = (int) js.getDistance();
                    if (volumeSpeed > 255)
                        volumeSpeed = 255;

                    int direction = js.get8Direction();
                    if(direction == joystick.STICK_UP) {
//                        textView5.setText("Direction : Up");
                        leftSpeed =  volumeSpeed - yPos;
                        rightSpeed = -(volumeSpeed - yPos);
                    } else if(direction == joystick.STICK_UPRIGHT) {
//                      textView5.setText("Direction : Up Right");
                        leftSpeed =  volumeSpeed - (-1 * xPos);
                        rightSpeed = -volumeSpeed;
                    } else if(direction == joystick.STICK_RIGHT) {
//                       textView5.setText("Direction : Right");
                        leftSpeed = volumeSpeed + xPos;
                        rightSpeed = volumeSpeed ;
                    } else if(direction == joystick.STICK_DOWNRIGHT) {
//                       textView5.setText("Direction : Down Right");
                        leftSpeed = -(volumeSpeed  - (-1 *  xPos));
                        rightSpeed =volumeSpeed ;
                    } else if(direction == joystick.STICK_DOWN) {
//                       textView5.setText("Direction : Down");
                        leftSpeed = -(volumeSpeed + yPos) ;
                        rightSpeed = volumeSpeed  + yPos;
                    } else if(direction == joystick.STICK_DOWNLEFT) {
//                        textView5.setText("Direction : Down Left");
                        leftSpeed = -volumeSpeed ;
                        rightSpeed =(volumeSpeed- xPos);
                    } else if(direction == joystick.STICK_LEFT) {
//                       textView5.setText("Direction : Left");
                        leftSpeed = -volumeSpeed ;
                        rightSpeed = -(volumeSpeed - xPos);
                    } else if(direction == joystick.STICK_UPLEFT) {
//                        textView5.setText("Direction : Up Left");
                        leftSpeed = volumeSpeed;
                        rightSpeed = -(volumeSpeed  - xPos);
                    } else if(direction == joystick.STICK_NONE) {
//                       textView5.setText("Direction : Center");
                          leftSpeed = 0;
                          rightSpeed = 0;
                    }
                } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
                    leftSpeed = 0;
                    rightSpeed = 0;
//                    textView1.setText("X :");
//                    textView2.setText("Y :");
//                    textView3.setText("Angle :");
//                    textView4.setText("Distance :");
//                    textView5.setText("Direction :");
                }
                joyStick[6] = (byte) (leftSpeed  & (byte)0xff);
                joyStick[7] = (byte) ((leftSpeed  >> 8) & (byte)0xff);

                joyStick[8] = (byte) (rightSpeed  & (byte)0xff);
                joyStick[9] = (byte) ((rightSpeed  >> 8) & (byte)0xff);
                if (myThreadConnected!=null) {
                    myThreadConnected.write(joyStick);
                }
                return true;
            }
        });

    };
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
                btnConnect.setBackgroundResource(R.drawable.bluetooth_on);
//                textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
            } catch (IOException e) {
                btnConnect.setBackgroundResource(R.drawable.bluetooth_off);
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
            float srf05Val = 0;

            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);
                    //Log.d("buffer", "data: " + (byte)buffer[0] + (byte)buffer[1] + buffer[2] + buffer[3] + buffer[4] + buffer[5] + buffer[6] + buffer[7] + buffer[8] + buffer[9]);
                    if ((buffer[1] ==  (byte)0x01)) { // get SRF05
                        for (int i = 0; i < 4; i++) {
                            byteSrf05[i] = buffer[i+2];
                        }
                        textSrf05.setText(Float.toString(byteArray2Float(byteSrf05)));
                    }
                    if ((buffer[1] == (byte)0x02)) { // Get Line Sensor value
                        for (int i = 0; i < 4; i++) {
                            byteLine[i] = buffer[i+2];
                        }
                        textLine.setText(Float.toString(byteArray2Float(byteLine)));
                    }
                    if ((buffer[1] ==  (byte)0x03)) { // get Light Sensor value
                        for (int i = 0; i < 4; i++) {
                            byteLightSensor[i] = buffer[i+2];
                        }
                        textLight.setText(Float.toString(byteArray2Float(byteLightSensor)));
                    }
                    if ((buffer[1] ==  (byte)0x06)) { // get Light Sensor value
                        for (int i = 0; i < 4; i++) {
                            byteBtn[i] = buffer[i+2];
                        }
                        textColor.setText(Float.toString(byteArray2Float(byteBtn)));
//                        textColor.setText("hello");

                    }


                    //String strReceived = new String(buffer, 0, bytes);
//                   final String msgReceived = String.valueOf(bytes) + " bytes received:\n" ;
//                    final String length = String.valueOf(bytes);
                   final String msgReceived = String.format("%02X", buffer[0]) + String.format("%02X", buffer[1]) + String.format("%02X", buffer[2])
                                                                    + String.format("%02X", buffer[3]) + String.format("%02X", buffer[4]) + String.format("%02X", buffer[5])
                                                                  +  String.format("%02X", buffer[6]) + String.format("%02X", buffer[7]) ;
//                    final String msgReceived = String.format("%02X", byteSrf05[0]) + String.format("%02X", byteSrf05[1]) + String.format("%02X", byteSrf05[2])
//                                                                    + String.format("%02X", byteSrf05[3])  ;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            textLine.setText(msgReceived);
//                         textColor.setText(msgReceived);

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