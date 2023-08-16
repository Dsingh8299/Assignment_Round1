package com.example.assignment_round1;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MyAccessibilityService extends AccessibilityService {

    private Context appContext;

    HashMap<String, Integer> adultList = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

        // Getting the content from the json file

        InputStream inputStream = getResources().openRawResource(R.raw.profanewords);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stringBuilder.append(line);
        }
        String jsonString = stringBuilder.toString();

        // Parsing the adult words from the json array and storing them into hashmap

        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            // Iterate through the array elements
            for (int i = 0; i < jsonArray.length(); i++) {
                String arrayItem = jsonArray.getString(i);
                // Do something with arrayItem, such as displaying it or processing it
                adultList.put(arrayItem, 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        appContext = getApplicationContext();

        Log.d("AccessibiltyEventStart", "");

        int eventType = event.getEventType();

        if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {

            Log.d("AccessibilityService", "TextChanged ");

            List<CharSequence> textList = event.getText();
            if (textList != null && !textList.isEmpty()) {

                // Iterate through the list of text and process each CharSequence
                for (CharSequence text : textList) {

                    if (text != null && text.length() > 0) {

                        // Process or log the text
                        Log.d("AccessibilityService", "Text: " + text);

                        if (adultList.get(text + "") != null)
                            if (Objects.equals(adultList.get(text + ""), 1)) {

                                pushNotification(text + "");
                            }
                    }

                }
            }
        }
    }

    @Override
    public void onInterrupt() {

        Log.d("ErrorAaya", "Error aa gaya");

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        Log.d("Kaam Hua", "Kaam ho gaya");

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();

        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
                AccessibilityEvent.TYPE_VIEW_FOCUSED | AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED;

        // If you only want this service to work with specific applications, set their
        // package names here. Otherwise, when the service is activated, it will listen
        // to events from all applications.

        // Set the type of feedback your service will provide.
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;

        // Default services are invoked only if no package-specific ones are present
        // for the type of AccessibilityEvent generated. This service *is*
        // application-specific, so the flag isn't necessary. If this was a
        // general-purpose service, it would be worth considering setting the
        // DEFAULT flag.

        // info.flags = AccessibilityServiceInfo.DEFAULT;

        info.notificationTimeout = 100;

        this.setServiceInfo(info);


    }

    private void pushNotification(String text) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManagerCompat = getSystemService(NotificationManager.class);
            notificationManagerCompat.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "My Notification")
                .setSmallIcon(R.drawable.baseline_block_24)
                .setContentTitle("Alert")
                .setContentText(text + " is an adult keyword.")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Directing to youtube app to play video

        String videoUrl = "https://www.youtube.com/watch?v=rdtBudmc03M";
        Uri uri = Uri.parse(videoUrl);
        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,uri);
        youtubeIntent.setPackage("com.google.android.youtube");

        // Checking if youtube app is installed in phone

        try
        {
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,youtubeIntent,PendingIntent.FLAG_MUTABLE);
            builder.setContentIntent(pendingIntent);
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(appContext, "YouTube not installed", Toast.LENGTH_SHORT).show();
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);

                return;
            }
        }
        notificationManager.notify(1, builder.build());

    }




}
