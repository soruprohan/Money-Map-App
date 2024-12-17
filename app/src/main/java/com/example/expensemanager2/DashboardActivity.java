package com.example.expensemanager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import android.view.MenuInflater;
import android.widget.PopupMenu;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensemanager2.databinding.ActivityDashboardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    ActivityDashboardBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    ArrayList<TransactionModel> transactionModelArrayList;
    TransactionAdapter transactionAdapter;
    int sumExpense=0,sumIncome=0;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        transactionModelArrayList=new ArrayList<>();
        binding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.historyRecyclerView.setHasFixedSize(true);

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    startActivity(new Intent(DashboardActivity.this,MainActivity.class));
                    finish();
                }
            }
        });

       //chartStuff from here
        binding.dropdownBarchart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a PopupMenu
                PopupMenu popupMenu = new PopupMenu(DashboardActivity.this, v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.dropdown_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.income_option) {
                                startActivity(new Intent(DashboardActivity.this, IncomeChartActivity.class));
                                return true;
                            }
                            else if (item.getItemId() == R.id.expense_option) {
                                    startActivity(new Intent(DashboardActivity.this, ExpenseChartActivity.class));
                                    return true;
                                }
                            else if (item.getItemId() == R.id.balance_option){
                                Intent intent = new Intent(DashboardActivity.this, ChartActivity.class);
                                intent.putExtra("income", sumIncome);
                                intent.putExtra("expense", sumExpense);
                                startActivity(intent);
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

        binding.addFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(DashboardActivity.this, AddTransactionActivity.class));
                } catch (Exception e) {

                }
            }
        });

        //new stuff from here
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Set up the ActionBar toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle Navigation Item Clicks
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_logout) {
                    createSignOutDialog();
                } else if (id == R.id.menu_about_us) {
                    startActivity(new Intent(DashboardActivity.this, AboutUsActivity.class));
                } else if (id == R.id.menu_news) {
                    startActivity(new Intent(DashboardActivity.this, NewsActivity.class));
                }else if (id == R.id.menu_analysis) {
                        startActivity(new Intent(DashboardActivity.this, ArticleActivity.class));
                } else if (id == R.id.menu_delete_account) {
                    deleteAccount();
                } else if (id == R.id.menu_share) {
                    shareApp();
                } else if (id == R.id.menu_rate) {
                    showRateAppDialog();
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        // Handle sidenavigation Button Click
        binding.refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    //LOGOUT PART
    private void createSignOutDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(DashboardActivity.this);
        builder.setTitle("Log out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.signOut();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    //DELETE ACCOUNT PART
    private void deleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Ask for password (re-authentication)
                    reAuthenticateAndDelete();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void reAuthenticateAndDelete() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            // User is not logged in
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
            return;
        }

        // Prompt user for re-authentication
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText passwordInput = new EditText(this);
        passwordInput.setHint("Enter your password");
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        builder.setTitle("Delete Account")
                .setMessage("Confirm deletion")
                .setView(passwordInput)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String password = passwordInput.getText().toString().trim();
                    if (password.isEmpty()) {
                        Toast.makeText(this, "Password is required!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Re-authenticate user
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                    user.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Delete user data from Firestore
                                    deleteUserData();
                                } else {
                                    Toast.makeText(this, "An error occured. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void deleteUserData() {
        String userId = firebaseAuth.getUid();

        if (userId != null) {
            firebaseFirestore.collection("Expenses").document(userId).delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Delete user from Firebase Authentication
                            firebaseAuth.getCurrentUser().delete()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Failed to delete account.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Failed to delete user data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    //SHARE APP PART
    private void shareApp() {
        try {
            // Define the Google Drive link to the APK
            String driveLink = "https://drive.google.com/drive/u/0/folders/1jehD5WTEL3NfvB-B6RCXrA0QXP-Obe6J";

            // Create an Intent to share the link
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app!");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey, check out my app! Download it using the link below:\n\n" + driveLink);

            // Start the share activity
            startActivity(Intent.createChooser(shareIntent, "Share App using"));
        } catch (Exception e) {
            Toast.makeText(this, "Failed to share the app: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    //RATE APP PART
    private void showRateAppDialog() {
        // Create a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_rate_app, null);
        builder.setView(view);

        // Initialize the RatingBar and buttons in the dialog
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        Button submitButton = view.findViewById(R.id.btnSubmit);
        Button cancelButton = view.findViewById(R.id.btnCancel);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle submit button click
        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            int rate=(int)rating;
            if (rate > 0) {
                saveRatingToFirebase(rate);
                dialog.dismiss();
            } else {
                Toast.makeText(DashboardActivity.this, "Please select a valid rating!", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle cancel button click
        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void saveRatingToFirebase(int rating) {
        // Get a reference to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ratingsRef = database.getReference("Ratings");

        // Get current user ID
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Create a data object
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("userId", userId);
        ratingData.put("rating", rating);
        ratingData.put("timestamp", System.currentTimeMillis());

        // Save rating to Firebase
        ratingsRef.child(userId).setValue(ratingData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save rating. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //MAIN DASHBOARD ACTIVITY PART

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    public void loadData() {
        firebaseFirestore.collection("Expenses").document(firebaseAuth.getUid()).collection("Notes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        transactionModelArrayList.clear();
                        sumExpense=0;
                        sumIncome=0;
                        for (DocumentSnapshot ds : task.getResult()) {
                            long timestamp = 0L;
                            if(ds.contains("timestamp")){
                                timestamp = ds.getLong("timestamp");
                            }
                            TransactionModel model = new TransactionModel(
                                    ds.getString("id"),
                                    ds.getString("note"),
                                    ds.getString("amount"),
                                    ds.getString("type"),
                                    ds.getString("date"),
                                    timestamp
                            );

                            int amount=Integer.parseInt(ds.getString("amount"));
                            if(ds.getString("type").equals("Expense")){
                                sumExpense+=amount;
                            }else{
                                sumIncome+=amount;
                            }
                            transactionModelArrayList.add(model);
                        }

                        transactionModelArrayList.sort(new Comparator<TransactionModel>() {
                            @Override
                            public int compare(TransactionModel o1, TransactionModel o2) {
                                return Long.compare(o2.timestamp, o1.timestamp);
                            }
                        });
                        binding.totalIncome.setText(String.valueOf(sumIncome));
                        binding.totalExpense.setText(String.valueOf(sumExpense));
                        binding.totalBalance.setText(String.valueOf(sumIncome-sumExpense));

                        transactionAdapter = new TransactionAdapter(DashboardActivity.this, transactionModelArrayList);
                        binding.historyRecyclerView.setAdapter(transactionAdapter);
                    }
                });
    }
}