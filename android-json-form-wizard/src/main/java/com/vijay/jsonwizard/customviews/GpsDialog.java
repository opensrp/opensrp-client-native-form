package com.vijay.jsonwizard.customviews;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.rey.material.widget.TextView;
import com.vijay.jsonwizard.R;

/**
 * Created by Jason Rogena - jrogena@ona.io on 11/24/17.
 */
public class GpsDialog extends Dialog implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final double MIN_ACCURACY = 4d;
    private final View dataView;
    private final TextView latitudeTV, longitudeTV, altitudeTV, accuracyTV;
    private TextView providerTV, dialogAccuracyTV;
    private final Context context;
    private GoogleApiClient googleApiClient;
    private Button okButton, cancelButton;
    private Location lastLocation;
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_ALTITUDE = "altitude";
    public static final String KEY_ACCURACY = "accuracy";

    public GpsDialog(Context context, View dataView, TextView latitudeTV, TextView longitudeTV, TextView altitudeTV, TextView accuracyTV) {
        super(context);
        this.context = context;
        this.dataView = dataView;
        this.latitudeTV = latitudeTV;
        this.longitudeTV = longitudeTV;
        this.altitudeTV = altitudeTV;
        this.accuracyTV = accuracyTV;
        init();
    }

    private void init() {
        this.setContentView(R.layout.dialog_gps);
        setTitle(R.string.loading_location);
        this.setCancelable(false);
        this.lastLocation = null;
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                disconnectGoogleApiClient();
            }
        });
        okButton = (Button) this.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndDismiss();
            }
        });
        cancelButton = (Button) this.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GpsDialog.this.dismiss();
            }
        });

        this.providerTV = (TextView) this.findViewById(R.id.provider);
        this.dialogAccuracyTV = (TextView) this.findViewById(R.id.accuracy);

        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                initGoogleApiClient();
            }
        });
    }

    private void saveAndDismiss() {
        updateLocationViews(lastLocation);
        GpsDialog.this.dismiss();
    }

    private void initGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        googleApiClient.connect();
    }

    private void disconnectGoogleApiClient() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    private void updateLocationViews(Location location) {
        if (location != null) {
            location.getProvider();

            latitudeTV.setText(String.format(context.getString(R.string.latitude), String.valueOf(location.getLatitude())));
            longitudeTV.setText(String.format(context.getString(R.string.longitude), String.valueOf(location.getLongitude())));
            altitudeTV.setText(String.format(context.getString(R.string.altitude), String.valueOf(location.getAltitude()) + " m"));
            accuracyTV.setText(String.format(context.getString(R.string.accuracy), String.valueOf(location.getAccuracy()) + " m"));
            dataView.setTag(R.id.raw_value, constructString(location));
        }
    }

    private String constructString(Location location) {
        if (location != null) {
            return String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude());
        }

        return null;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        this.dismiss();
        Toast.makeText(context, R.string.could_not_get_your_location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            dialogAccuracyTV.setText(String.format(context.getString(R.string.accuracy), String.valueOf(location.getAccuracy()) + " m"));
            providerTV.setText(String.format(context.getString(R.string.using_provider), location.getProvider()));
        }

        lastLocation = location;
        if (lastLocation != null && lastLocation.getAccuracy() <= MIN_ACCURACY) {
            saveAndDismiss();
        }
    }
}
