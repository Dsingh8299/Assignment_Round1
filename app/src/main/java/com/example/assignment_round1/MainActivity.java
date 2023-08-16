package com.example.assignment_round1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    TextView permissionTV;
    private AlertDialog alertDialog;

    // Getting data from the json file


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionTV = findViewById(R.id.permission_tv);
        createAlertDialog();

        // Checking if Accessibility Permissions are granted

        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        boolean isAllowed = accessibilityManager.isEnabled();
        if(isAllowed)
        {
            alertDialog.dismiss();
            permissionTV.setText("Permissions Granted");

        }
        else {
            alertDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        boolean isAllowed = accessibilityManager.isEnabled();
        if(isAllowed) {

            alertDialog.dismiss();
            permissionTV.setText("Permissions Granted");
        }
        else {

            alertDialog.show();
        }
    }

    private void createAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.SystemAlertDialogTheme);

        builder.setTitle("Allow Accessibility Permission")
                .setMessage("To use this app please allow accessibility permission")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);

                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.baseline_add_alert_24);
        //.setCancelable(false);
        alertDialog = builder.create();
    }


}