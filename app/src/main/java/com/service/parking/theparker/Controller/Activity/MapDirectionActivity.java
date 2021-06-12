package com.service.parking.theparker.Controller.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.service.parking.theparker.Model.LocationPin;
import com.service.parking.theparker.Model.ParkingBooking;
import com.service.parking.theparker.R;
import com.service.parking.theparker.Utils.IPositiveNegativeListener;

import java.util.ArrayList;

public class MapDirectionActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 10;
    private static final int ZOOM_LEVEL = 18;
    private static final int TILT_LEVEL = 25;
    Boolean mLocationPermissionGranted = false;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap googleMap;
    private MapView mMapView;
    private Location mLastKnownLocation;
    private ParkingBooking parkingBooking;
    private Polyline polyline;

    public static boolean checkGPSStatus(Context context) {
        LocationManager manager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_direction);
        parkingBooking = (ParkingBooking) getIntent().getSerializableExtra("object");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {

        setupMapView();
    }

    private void setupMapView() {
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;
//            NetworkServices.ParkingPin.getParkingAreas(googleMap);

            getLocationPermission();
//            mLocationPermissionGranted = true;
            updateLocationUI();
            getDeviceLocation();


            googleMap.setOnMarkerClickListener(marker -> {

                return false;
            });
        });
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
//                mLastKnownLocation = null;
                getLocationPermission();
//                mLocationPermissionGranted = true;
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        if (checkGPSStatus(this)) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                animateCamera(mLastKnownLocation);
                            }
                        } else {
                            turnGPSOn();
                        }

                    } else {
                        Log.d("XYZ", "Current location is null. Using defaults.");
                        Log.e("XYZ", "Exception: %s", task.getException());
                        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private MaterialDialog buildDialog(Activity activity, String title, String content) {
        return new MaterialDialog.Builder(activity)
                .title(title)
                .content(content)
                .build();
    }

    public void showPositiveDialogWithListener(@NonNull Activity activity, String title, String content, final IPositiveNegativeListener positiveNegativeDialogListener, String positiveText, boolean cancelable) {
        buildDialog(activity, title, content)
                .getBuilder()
                .positiveText(positiveText)
                .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .onPositive((dialog, which) -> positiveNegativeDialogListener.onPositive())
                .cancelable(cancelable)
                .show();
    }

    /**
     * @param latLng in which position to Zoom the camera.
     * @return the [CameraUpdate] with Zoom and Tilt level added with the given position.
     */

    public CameraUpdate buildCameraUpdate(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .tilt(TILT_LEVEL)
                .zoom(ZOOM_LEVEL)
                .build();
        return CameraUpdateFactory.newCameraPosition(cameraPosition);
    }

    private void animateCamera(Location location) {
        CameraUpdate cameraUpdate = buildCameraUpdate(new LatLng(location.getLatitude(), location.getLongitude()));
        googleMap.animateCamera(cameraUpdate);


        FirebaseDatabase.getInstance().getReference().child("GlobalPins").child(parkingBooking.getParkingArea()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {
                LocationPin value = snapshot.getValue(LocationPin.class);
                String pinkey = snapshot.getKey();
                if (parkingBooking != null && googleMap != null) {
                    if (value != null && pinkey.equalsIgnoreCase(parkingBooking.getParkingId())) {
                        if (polyline != null) {
                            polyline.remove();
                        }
                        LatLng latLngFrom = new LatLng(value.getPinloc().get("lat"), value.getPinloc().get("long"));
                        LatLng latLngEnd = new LatLng(location.getLatitude(), location.getLongitude());
                        // Creating a marker
                        MarkerOptions markerOptionFrom = new MarkerOptions()
                                .position(latLngFrom);
                        // Setting the title for the marker.
                        // This will be displayed on taping the marker
                        markerOptionFrom.title(parkingBooking.getParkingArea());
                        googleMap.addMarker(markerOptionFrom);

                        GoogleDirection.withServerKey(getString(R.string.google_maps_api_key))
                                .from(latLngFrom)
                                .to(latLngEnd)
                                .transportMode(TransportMode.DRIVING)
                                .execute(new DirectionCallback() {
                                    @Override
                                    public void onDirectionSuccess(Direction direction, String rawBody) {
                                        if (direction != null && direction.isOK()) {
//                                if (this != null) {
                                            Route route = direction.getRouteList().get(0);
                                            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                            polyline = googleMap.addPolyline(DirectionConverter.createPolyline(MapDirectionActivity.this, directionPositionList, 12, ContextCompat.getColor(MapDirectionActivity.this, R.color.colorPrimaryDark)));
                                            setCameraWithCoordinationBounds(route);


                                            // Creating a marker
//                                MarkerOptions markerOptionEnd = new MarkerOptions()
//                                        .position(latLngEnd);
//                                // Setting the title for the marker.
//                                // This will be displayed on taping the marker
//                                markerOptionEnd.title("Current Location");
//                                googleMap.addMarker(markerOptionEnd);
//                                }
                                        } else {
                                            // Do something
                                        }
                                    }

                                    @Override
                                    public void onDirectionFailure(Throwable t) {
                                        // Do something
                                        Toast.makeText(MapDirectionActivity.this, "Somthing went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    /**
     * update camera on map
     *
     * @param route
     */
    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }
    }

    private void turnGPSOn() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) {
            showPositiveDialogWithListener(this, getResources().getString(R.string.need_location), getResources().getString(R.string.location_content), () -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)), "Turn On", false);
        }
    }
}