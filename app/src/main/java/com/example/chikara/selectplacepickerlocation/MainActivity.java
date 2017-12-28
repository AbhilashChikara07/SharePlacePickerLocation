package com.example.chikara.selectplacepickerlocation;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class MainActivity extends AppCompatActivity {

    private int PLACE_PICKER_REQUEST = 22;
    private LocationManager mLocationManager;
    private boolean gps_enabled = false;
    private ProgressDialog progress;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        progress = new ProgressDialog(this);
        checkLocationPermission();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dialog.isShowing())
            dialog.dismiss();
        if (checkGps()) {
            try {
                PlacePicker.IntentBuilder intentBuilder =
                        new PlacePicker.IntentBuilder();
//            intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                Intent intent = intentBuilder.build(MainActivity.this);
                startActivityForResult(intent, PLACE_PICKER_REQUEST);

            } catch (GooglePlayServicesRepairableException
                    | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        } else {
            showAlertDialog();
            dialog.show();
        }
    }

    private void showAlertDialog() {
        dialog = new AlertDialog.Builder(this)
                .setTitle("GPS is disabled")
                .setMessage("Show location settings?")
                .setPositiveButton("ENABLE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                        dialog.dismiss();
                    }
                })
                .create();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    private boolean checkGps() {
        gps_enabled = false;
        try {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return gps_enabled;
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
