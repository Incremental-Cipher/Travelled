package com.example.test3;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.Test3.MESSAGE";

    public static final String FILE_NAME = "markers.txt";
    private File file;
    private FileOutputStream fileOutputStream;
    private FileInputStream fileInputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        // Creates a new file at the system default location
        file = new File(this.getFilesDir(), FILE_NAME);

        // read from file and re-add markers
        int length = (int)file.length();
        byte[] bytes = new byte[length];

        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytes);
            fileInputStream.close();

            String data = new String(bytes);
            String[] parts = data.split("\\+");
            System.out.println("partslength: " + parts.length);

            for (int i = 0; i < parts.length; i++) {
                String[] slice = (parts[i].split("\\|"));
                System.out.println("City: " + slice[0]);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onOpenMapsClick(View v) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "");
        startActivity(intent);
    }
}