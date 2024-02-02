package com.example.app_image_retrieve;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Document document =  null;
    Bitmap bitmap;
    String title, imageUrl, packageName;
    List<String> imag = new ArrayList<>();
    int size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText edit = (EditText) findViewById(R.id.text_view_id);
        Button validate = findViewById(R.id.button_id);
        validate.setOnClickListener(v -> {
            imag.clear();
            imageUrl = null;
            packageName = edit.getText().toString();
            if(!packageName.isEmpty()){
                new CheckAppImage().execute();
                edit.setText("");
            }else{
                Toast.makeText(getApplicationContext(), "Empty input", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public class CheckAppImage extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                document = Jsoup.connect("https://play.google.com/store/apps/details?id="+packageName).get();
            }catch (IOException e){
                e.printStackTrace();
            }

            try {
                if(document != null){
                    Elements media = document.select("[src]");
                    for (Element src:
                         media) {
                        if(src.attr("abs:src") != null && src.attr("abs:src").contains("play-lh.googleusercontent.com")){
                               imag.add(src.attr("abs:src"));
                               break;
                        }
                    }
                    title = document.title();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            TextView test = findViewById(R.id.testText);

            test.setText(String.valueOf(size));
            System.out.println(imageUrl);
            if(imag.size() > 0){
                imageUrl = imag.get(0);
                new downloadOnline().execute();
            }
            //System.out.println("[" + String.join(", ", imag) + "]");
            //testImg.setImageBitmap(bitmap);
        }
    }

    public class downloadOnline extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String imageURL = imageUrl;
            bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            ImageView testImg = findViewById(R.id.testImage);
            testImg.setImageBitmap(bitmap);
        }
    }

}