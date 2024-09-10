package com.example.sharelocation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocSearch extends AppCompatActivity {
    EditText editText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_search);

        editText = findViewById(R.id.seachText);
        button = findViewById(R.id.searchButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the pin entered by the user
                String enteredPin = editText.getText().toString().trim();

                // Check if the entered pin is empty
                if (enteredPin.isEmpty()) {
                    // Show an alert dialog indicating that the pin is empty
                    new AlertDialog.Builder(LocSearch.this)
                            .setTitle("Empty Pin")
                            .setMessage("Please enter a pin.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    return; // Exit the method
                }

                // Assuming you have a method to retrieve the correct pin from the database
                getCorrectPinFromDatabase(enteredPin);
            }
        });
    }

    // Method to retrieve the correct pin from the database
    private void getCorrectPinFromDatabase(String enteredPin) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("locations");

        // Add a listener to retrieve data from the "locations" node
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Flag to check if pin is found
                boolean pinFound = false;

                // Iterate through each child node under "locations"
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    // Get the pin value from the current location entry
                    String pin = locationSnapshot.child("pin").getValue(String.class);
                    String longitude = locationSnapshot.child("longitude").getValue(String.class);
                    String latitude = locationSnapshot.child("latitude").getValue(String.class);

                    // Compare the entered pin with the pin from the database
                    if (pin != null && pin.equals(enteredPin)) {
                        // Display a toast message indicating access granted
                        Toast.makeText(LocSearch.this, "Access granted", Toast.LENGTH_SHORT).show();
                        pinFound = true;

                        Intent in = new Intent(LocSearch.this,MapsActivity2.class);
                        in.putExtra("longitude",longitude);
                        in.putExtra("latitude",latitude);
                        startActivity(in);
                        break; // Exit the loop if the pin is found
                    }
                }

                // If pin is not found, show an alert dialog
                if (!pinFound) {
                    new AlertDialog.Builder(LocSearch.this)
                            .setTitle("Access Denied")
                            .setMessage("The pin entered is incorrect.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error if database query is canceled
                Toast.makeText(LocSearch.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}