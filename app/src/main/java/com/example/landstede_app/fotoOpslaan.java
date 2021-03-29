package com.example.landstede_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class fotoOpslaan extends AppCompatActivity implements LocationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_opslaan);

        dispatchTakePictureIntent();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        Button wegwerpen_button = findViewById(R.id.Wegwerpen);
        wegwerpen_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickWegwerpen();
            }
        });

        Button opslaan_button = findViewById(R.id.opslaanButton);
        opslaan_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    onClickOpslaan();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onClickWegwerpen(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickOpslaan() throws IOException {
        TextView monteur_naam = findViewById(R.id.monteur_naam);
        TextView gps = findViewById(R.id.gps);
        ImageView thumbnail = findViewById(R.id.thumbnail);
        Spinner categorie = findViewById(R.id.spinner);
        TextView beschrijving = findViewById(R.id.Beschrijving);

        Bitmap thumbnail_bitmap = ((BitmapDrawable)thumbnail.getDrawable()).getBitmap();

        MediaStore.Images.Media.insertImage(
                getContentResolver(),
                thumbnail_bitmap,
                monteur_naam.getText().toString(),
                "test"
        );

        File documentsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        File root = new File(documentsFolder, "LandStedeInformatie");
        if(!root.exists()){
            root.mkdirs();
        }

        File imageInfoFile = new File(root, monteur_naam.getText().toString() + ".txt");
        if(!imageInfoFile.exists()){
            imageInfoFile.createNewFile();
        }
        FileWriter writer = new FileWriter(imageInfoFile);
        writer.append(monteur_naam.getText().toString());
        writer.append("\n");
        writer.append(gps.getText().toString());
        writer.append("\n");
        writer.append(categorie.getSelectedItem().toString());
        writer.append("\n");
        writer.append(beschrijving.getText().toString());
        writer.flush();
        writer.close();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        ImageView imageView = findViewById(R.id.thumbnail);

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        TextView gps = findViewById(R.id.gps);
        gps.setText(location.getLatitude() + "," + "\n" + location.getLongitude());
    }
}