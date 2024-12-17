package com.example.expensemanager2;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanager2.NetworkUtil;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Check if there is internet connectivity
        if (NetworkUtil.isConnected(this)) {
            // If there is internet, proceed to MainActivity
            runnable = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            };
            handler.postDelayed(runnable, 2000);
        } else {
            showNoInternetDialog();
        }
    }

    private void showNoInternetDialog() {
        // Create a custom dialog
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_no_internet);
        dialog.setCancelable(false); // Prevent closing dialog unless internet is restored

        // Set up Retry Button
        Button retryButton = dialog.findViewById(R.id.retryButton);
        retryButton.setOnClickListener(v -> {
            if (NetworkUtil.isConnected(SplashActivity.this)) {
                // If internet is back, dismiss the dialog and proceed
                dialog.dismiss();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Otherwise, keep the dialog open
                Toast.makeText(SplashActivity.this, "No internet connection. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        // Show the dialog
        dialog.show();

        // Adjust dialog size
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Remove the runnable when activity is destroyed
    }
}
