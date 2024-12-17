package com.example.expensemanager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // GitHub profile link TextView
        TextView githubProfileLink = findViewById(R.id.tv_github_profile_link);
        githubProfileLink.setOnClickListener(v -> openUrl("https://github.com/soruprohan"));

        // QR code ImageView (optional action if needed)
        ImageView qrCodeImage = findViewById(R.id.img_qr_code);
        qrCodeImage.setOnClickListener(v -> openUrl("https://github.com/soruprohan"));
    }


    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
