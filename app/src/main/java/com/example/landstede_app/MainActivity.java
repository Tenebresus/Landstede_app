package com.example.landstede_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println(getPackageName());
        categorieKiezer("Grondkabels");

        Button grondkabels_button = findViewById(R.id.grondkabels);
        grondkabels_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageGroupDestroyer();
                categorieKiezer("Grondkabels");
            }
        });

        Button luchtkabels_button = findViewById(R.id.luchtkabels);
        luchtkabels_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageGroupDestroyer();
                categorieKiezer("Luchtkabels");
            }
        });

        Button hoogspanningsmasten_button = findViewById(R.id.hoogspanningsmasten);
        hoogspanningsmasten_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageGroupDestroyer();
                categorieKiezer("Hoogspanningsmasten");
            }
        });

        Button schakelkisten_button = findViewById(R.id.schakelskasten);
        schakelkisten_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageGroupDestroyer();
                categorieKiezer("Schakelkasten");
            }
        });

        Button button = findViewById(R.id.startFotoEvent);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fotoOpslaanActivity();
            }
        });
    }

    public void fotoOpslaanActivity(){
        Intent intent = new Intent(this, fotoOpslaan.class);
        startActivity(intent);
    }

    public void categorieKiezer(String categorie){
        List<String> list = new ArrayList<String>();

        File landStedeInformatieFolder = new File(Environment.getExternalStorageDirectory() + "/Documents/LandStedeInformatie");
        File picturesFolder = new File(Environment.getExternalStorageDirectory() + "/Pictures");

        int id_counter = 0;

        for(File picture : picturesFolder.listFiles()){
            String pictureNaam = picture.getName();
            String PictureNaamZonderExtension = pictureNaam.substring(0, pictureNaam.lastIndexOf('.'));

            for(File landStedeInformatieFile : landStedeInformatieFolder.listFiles()){
                String landStedeInformatieFileNaam = landStedeInformatieFile.getName();
                String landStedeInformatieFileNaamZonderExtensie = landStedeInformatieFileNaam.substring(0, landStedeInformatieFileNaam.lastIndexOf('.'));

                if(PictureNaamZonderExtension.equals(landStedeInformatieFileNaamZonderExtensie)){

                    BufferedReader reader;
                    try {
                        reader = new BufferedReader(new FileReader(landStedeInformatieFile.getAbsolutePath()));
                        String line = reader.readLine();
                        while (line != null) {
                            list.add(line);
                            line = reader.readLine();
                        }
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String categorie_van_file = list.get(3);

                    if(categorie_van_file.equals(categorie)){
                        String titel = list.get(0);
                        String longitude = list.get(1);
                        String latitude = list.get(2);
                        Bitmap pictureBitmap = BitmapFactory.decodeFile(picture.getPath());
                        String beschrijving = list.get(4);

                        imageGroupmaker(titel, longitude + " " + latitude, pictureBitmap, beschrijving, id_counter, picture);
                        id_counter++;
                    }
                    list.clear();
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    public void imageGroupmaker(String title, String gps, Bitmap picture, String beschrijving, int id_counter, File picture_file){
        LinearLayout imageScrollLayout = findViewById(R.id.imageScrollLayout);

        LinearLayout firstImageGroup = new LinearLayout(this);
        Toolbar toolbar = new Toolbar(this);
        ImageView imageView = new ImageView(this);
        Button mail_button = new Button(this);
        TextView beschrijvingView = new TextView(this);

        firstImageGroup.setOrientation(LinearLayout.VERTICAL);

        toolbar.setTitle(title);
        toolbar.setSubtitle(gps);
        toolbar.setBackgroundColor(Color.BLACK);

        imageView.setImageBitmap(picture);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth(),800);
        imageView.setLayoutParams(layoutParams);

        mail_button.setId(id_counter);
        mail_button.setText("Verstuur als mail");
        mail_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("message/rfc822");
                email.putExtra(Intent.EXTRA_TEXT, beschrijving);

                Uri photoUri = FileProvider.getUriForFile(
                        getApplication().getBaseContext(),
                        "com.example.landstede_app.fileprovider",
                        picture_file
                );
                email.putExtra(Intent.EXTRA_STREAM, photoUri);

                startActivity(Intent.createChooser(email, "Vertuur mail..."));
            }
        });

        beschrijvingView.setText(beschrijving);

        imageScrollLayout.addView(firstImageGroup);
        firstImageGroup.addView(toolbar);
        firstImageGroup.addView(imageView);
        firstImageGroup.addView(mail_button);
        firstImageGroup.addView(beschrijvingView);

        setSupportActionBar(toolbar);
    }

    public void imageGroupDestroyer(){
        LinearLayout imageScrollLayout = findViewById(R.id.imageScrollLayout);
        imageScrollLayout.removeAllViewsInLayout();
    }
}