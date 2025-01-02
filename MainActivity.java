package com.example.codmanticheat;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {
    private static final String CODM_PACKAGE = "com.activision.callofduty.shooter";
    private static final String PASSWORD = "OkIstopCheating";
    private boolean isMonitoring = false;
    private LinearLayout mainLayout;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize UI Programmatically
        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setPadding(20, 20, 20, 20);

        Button startButton = new Button(this);
        startButton.setText("Start");
        startButton.setOnClickListener(v -> {
            if (isCODMInstalled()) {
                startMonitoring();
            } else {
                Toast.makeText(this, "You don't have CODM installed!", Toast.LENGTH_LONG).show();
            }
        });

        mainLayout.addView(startButton);
        setContentView(mainLayout);

        // Request device admin permission
        ComponentName compName = new ComponentName(this, MyAdminReceiver.class);
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (!dpm.isAdminActive(compName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This app requires admin privileges to monitor cheats.");
            startActivityForResult(intent, 1);
        }

        // Request file permissions
        if (!Environment.isExternalStorageManager()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(intent);
        }
    }

    private boolean isCODMInstalled() {
        try {
            getPackageManager().getPackageInfo(CODM_PACKAGE, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void startMonitoring() {
        isMonitoring = true;
        Toast.makeText(this, "Monitoring started!", Toast.LENGTH_SHORT).show();

        // Monitor game files and behavior
        new Thread(() -> {
            while (isMonitoring) {
                File codmDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + CODM_PACKAGE);
                if (!codmDir.exists() || !codmDir.isDirectory()) {
                    runOnUiThread(() -> lockGame("Game files are missing!"));
                }

                // Add your cheat detection logic here

                try {
                    Thread.sleep(5000); // Check every 5 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void lockGame(String reason) {
        runOnUiThread(() -> {
            isMonitoring = false;
            Toast.makeText(this, reason, Toast.LENGTH_LONG).show();

            // Clear the UI and show the lock screen
            mainLayout.removeAllViews();

            passwordInput = new EditText(this);
            passwordInput.setHint("Enter Password");
            Button unlockButton = new Button(this);
            unlockButton.setText("Unlock");
            unlockButton.setOnClickListener(v -> {
                String enteredPassword = passwordInput.getText().toString();
                if (PASSWORD.equals(enteredPassword)) {
                    Toast.makeText(this, "Access granted. Please stop cheating!", Toast.LENGTH_SHORT).show();
                    recreate(); // Restart the app
                } else {
                    Toast.makeText(this, "Incorrect password!", Toast.LENGTH_SHORT).show();
                }
            });

            mainLayout.addView(passwordInput);
            mainLayout.addView(unlockButton);
        });
    }
}
