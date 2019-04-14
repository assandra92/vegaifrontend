package com.example.veg;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Vision vision = null;
    private TextAnnotation text = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(intent);

                Vision.Builder visionBuilder = new Vision.Builder(
                        new NetHttpTransport(),
                        new AndroidJsonFactory(),
                        null);

                visionBuilder.setVisionRequestInitializer(
                        new VisionRequestInitializer("AIzaSyAhDhzybE59gHjc13BT3DNjQBhnGt6CNEU"));


                vision = visionBuilder.build();

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {

                        // Convert photo to byte array
                        InputStream inputStream =
                                getResources().openRawResource(R.raw.photo);
                        byte[] photoData = new byte[0];
                        try {
                            photoData = IOUtils.toByteArray(inputStream);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Image inputImage = new Image();
                        inputImage.encodeContent(photoData);

                        Feature desiredFeature = new Feature();
                        desiredFeature.setType("LOGO_DETECTION");

                        AnnotateImageRequest request = new AnnotateImageRequest();
                        request.setImage(inputImage);
                        request.setFeatures(Arrays.asList(desiredFeature));

                        BatchAnnotateImagesRequest batchRequest =
                                new BatchAnnotateImagesRequest();

                        batchRequest.setRequests(Arrays.asList(request));

                        BatchAnnotateImagesResponse batchResponse =
                                null;
                        try {
                            batchResponse = vision.images().annotate(batchRequest).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        final String text = batchResponse.getResponses()
                                .get(0).getLogoAnnotations().get(0).getDescription();

                        Log.d("CREATION", text);
                    }
                });


                Toast.makeText(MainActivity.this, text.getText(), Toast.LENGTH_LONG).show();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
