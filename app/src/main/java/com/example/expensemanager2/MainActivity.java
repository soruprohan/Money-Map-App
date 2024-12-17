package com.example.expensemanager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanager2.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in, but only if their email is verified
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            // Redirect to DashboardActivity if the email is verified
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
        }

        binding.goToSignUpScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailLogin.getText().toString().trim();
                String password = binding.passwordLogin.getText().toString().trim();

                if (email.isEmpty()) {
                    binding.emailLogin.setError("Email is required");
                    return;
                }
                if (password.isEmpty()) {
                    binding.passwordLogin.setError("Password is required");
                    return;
                }

                binding.progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user != null && user.isEmailVerified()) {
                                    binding.progressBar.setVisibility(View.GONE);
                                    // Proceed to DashboardActivity only if email is verified
                                    startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                                    finish();
                                } else {
                                    binding.progressBar.setVisibility(View.GONE);
                                    // If email is not verified, show a message
                                    Toast.makeText(MainActivity.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                                    firebaseAuth.signOut(); // Log out the user if email is not verified
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Hide ProgressBar if login fails
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        TextView forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }
}
