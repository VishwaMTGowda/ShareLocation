package com.example.sharelocation;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private float DEFAULT_ZOOM = 15f;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private DatabaseReference databaseReference;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        button = findViewById(R.id.button3);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveCurrentLocationToDatabase();
            }
        });


        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("locations");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.d("Location Update", "Latitude: " + latitude + ", Longitude: " + longitude);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM));

                    Float latitude = (float) location.getLatitude();
                    Float longitude = (float) location.getLongitude();
//                    text.setText(latitude + " " + longitude);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);

                }
            }
        }
    }

    private void saveCurrentLocationToDatabase() {
        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            String latitude = String.valueOf(location.getLatitude());
                            String longitude = String.valueOf(location.getLongitude());

                            // Generate a random 4-digit pin
                            Random random = new Random();

                            StringBuilder pinBuilder = new StringBuilder();

// Generate 4 characters with mixed-case letters and digits
                            for (int i = 0; i < 6; i++) {
                                // Randomly choose between a letter or a digit
                                boolean isLetter = random.nextBoolean();

                                if (isLetter) {
                                    // Append a random uppercase letter
                                    pinBuilder.append((char) ('A' + random.nextInt(26)));
                                } else {
                                    // Append a random digit
                                    pinBuilder.append(random.nextInt(10));
                                }
                            }

                            String pin = pinBuilder.toString(); // Final PIN string with mixed-case letters and digits


                            String key = databaseReference.push().getKey();
                            if (key != null) {
                                DatabaseReference locationRef = databaseReference.child(key);
                                locationRef.child("latitude").setValue(latitude);
                                locationRef.child("longitude").setValue(longitude);
                                locationRef.child("pin").setValue(pin); // Save the pin
                                Toast.makeText(MapsActivity.this, "Now you can share your Loaction", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MapsActivity.this, pinShow.class);
                                intent.putExtra("pin", pin);
                                startActivity(intent);
                                shownofn(pin);

                            }
                        }
                    }

                });
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }
    public void shownofn(String code) {
        NotificationManager notificationManager = (NotificationManager) MapsActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        // RemoteViews for custom notification layout
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_layout);
        notificationLayout.setTextViewText(R.id.notification_text, "Your pin is " + code);

        // Intent to be triggered when the notification is clicked
        Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MapsActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Intent to be triggered when the copy button is clicked
        Intent copyIntent = new Intent(MapsActivity.this, CopyReceiver.class);
        copyIntent.setAction("COPY_CODE_ACTION");
        copyIntent.putExtra("code", code);
        PendingIntent copyPendingIntent = PendingIntent.getBroadcast(MapsActivity.this, 0, copyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the click action for the copy button in the custom notification layout
        notificationLayout.setOnClickPendingIntent(R.id.copy_button, copyPendingIntent);

        // Build the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MapsActivity.this, channelId)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle("Now you can share your Location")
                .setContentText("Your pin is " + code)
                .setCustomContentView(notificationLayout)
                .setContentIntent(pendingIntent) // Set click action for the notification itself
                .setAutoCancel(true);
        notificationManager.notify(notificationId, mBuilder.build());
    }

    // Inside your MapsActivity class



}
