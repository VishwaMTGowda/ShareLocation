package com.example.sharelocation;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    ImageButton btn;
     ImageButton btn2;
    Button turnOnGpsButton ;
    TextView textView6;
   FloatingActionButton refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        turnOnGpsButton = findViewById(R.id.turnOnGpsButton);
        textView6 = findViewById(R.id.textView6);
     refresh  = findViewById(R.id.refresh);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getthatLocation();



//recreate();

       refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
                Toast.makeText(MainActivity.this,"Refreshing....",Toast.LENGTH_LONG).show();
            }
        });




        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, LocSearch.class);
                startActivity(in);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(in);
            }
        });
        turnOnGpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if GPS is enabled
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // GPS is not enabled, prompt user to enable it
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                } else {
                    // GPS is already enabled
                    Toast.makeText(MainActivity.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();


                }
            }
        });
    }

    private void getthatLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGpsEnabled) {
                // GPS is enabled
                textView6.setText("You have enabled your location");
                textView6.setTextColor(getResources().getColor(R.color.green));


            } else {
                // GPS is not enabled
                textView6.setText("You have not enabled your location");
                textView6.setTextColor(getResources().getColor(R.color.red));
            }
        } else {
            // Location manager is null, unable to check GPS status
            textView6.setText("Unable to determine location status");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Check if the "Settings" item is clicked
        if (id == R.id.action_settings) {
            // Start the SettingsActivity
            Intent intent = new Intent(this, pinShow.class);
            startActivity(intent);
            return true;
        }
        // Check if the "About" item is clicked
        else if (id == R.id.action_about) {
            // Call the method to share the APK file
//            shareApkFile();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    private void shareApkFile() {
//        try {
//            // Get the APK file URI
//            Uri apkUri;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                apkUri = FileProvider.getUriForFile(this,
//                        BuildConfig.APPLICATION_ID + ".provider",
//                        new File(getApplicationInfo().sourceDir));
//            } else {
//                apkUri = Uri.fromFile(new File(getApplicationInfo().sourceDir));
//            }
//
//            // Create an intent to share the APK file
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("*/*"); // Set the MIME type to all files
//            shareIntent.putExtra(Intent.EXTRA_STREAM, apkUri);
//            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//            // Launch the intent to share the APK file
//            startActivity(Intent.createChooser(shareIntent, "Share APK via"));
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Error sharing APK", Toast.LENGTH_SHORT).show();
//        }
    }



