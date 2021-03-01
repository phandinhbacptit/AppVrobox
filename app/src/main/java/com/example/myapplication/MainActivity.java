package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private  ImageView imageMachanical,  imageMusic, imageControl, imageProject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

        imageMachanical = (ImageView)findViewById((R.id.openMechanical));
        imageMusic = (ImageView)findViewById((R.id.openMusic));
        imageControl = (ImageView)findViewById((R.id.openControl));
        imageProject = (ImageView)findViewById((R.id.openProject));
        imageProject.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openProject();
            }
        });
        imageMachanical.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openMechanical();
            }
        });
        imageMusic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openMusic();
            }
        });
        imageControl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openControl();
            }
        });
    }
    public void openMechanical() {
        Intent intent = new Intent(this, Mechanical.class);
        startActivity(intent);
    }
    public void openMusic() {
        Intent intent = new Intent(this, Music.class);
        startActivity(intent);
    }
    public void openProject() {
        Intent intent = new Intent(this, Programming.class);
        startActivity(intent);
    }
    public void openControl() {
        Intent intent = new Intent(this, Control.class);
        startActivity(intent);
    }

}