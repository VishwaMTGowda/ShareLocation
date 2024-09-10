package com.example.sharelocation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class pinShow extends AppCompatActivity {
    TextView textView5;
    Button DelteBtn;
    ImageButton sharemsg;
    DatabaseReference locationsRef = FirebaseDatabase.getInstance().getReference("locations");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinshow);
        textView5 = findViewById(R.id.textView5);
        DelteBtn = findViewById(R.id.deletePin);
        sharemsg =findViewById(R.id.msgBtn);





        Intent intent = getIntent();
        String pin = intent.getStringExtra("pin");
        textView5.setText(pin);

        sharemsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareContent();

            }
        });

        DelteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alert();

            }
        });
    }

    private void deleteLocation() {
        String pinToDelete = textView5.getText().toString().trim();

        if (TextUtils.isEmpty(pinToDelete)) {
            Toast.makeText(this, "Please enter a PIN to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        Query query = locationsRef.orderByChild("pin").equalTo(pinToDelete);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    locationSnapshot.getRef().removeValue();
                    Toast.makeText(pinShow.this, "Location deleted successfully", Toast.LENGTH_SHORT).show();
                    return; // Stop after deleting the first location with the matching PIN
                }
                // If no location with the given PIN is found
                Toast.makeText(pinShow.this, "No location found with the given PIN", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(pinShow.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void alert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set dialog title, message, and buttons
        builder.setTitle("Alert")
                .setMessage("Are you sure you want to stop the tracker")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ok button clicked
                        deleteLocation();
                        Toast.makeText(pinShow.this, "Stopped", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel button clicked
      Toast.makeText(pinShow.this, "Canceled", Toast.LENGTH_SHORT).show();
                    }
                });

        // Create AlertDialog object
        AlertDialog alertDialog = builder.create();

        // Show the dialog
        alertDialog.show();
    }
    private void shareContent() {
        String pin = textView5.getText().toString().trim();
        String boldPin = "*Pin:* " + pin;
        String message = "Hi, See my location on Maps\n" + boldPin + "\nJust pasting this code";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    }