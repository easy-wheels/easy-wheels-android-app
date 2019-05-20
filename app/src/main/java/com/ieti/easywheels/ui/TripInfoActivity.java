package com.ieti.easywheels.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.GeoApiContext;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.ieti.easywheels.R;
import com.ieti.easywheels.model.PassengerInfo;
import com.ieti.easywheels.model.Trip;
import com.ieti.easywheels.model.TripRequest;
import com.ieti.easywheels.network.Firebase;
import com.ieti.easywheels.util.AdapterUtils;
import com.ieti.easywheels.util.MemoryUtil;
import com.ieti.easywheels.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ieti.easywheels.Constants.ERROR_DIALOG_REQUEST;
import static com.ieti.easywheels.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.ieti.easywheels.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class TripInfoActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        LocationListener {

    private Trip trip;
    private TripRequest tripRequest;

    private TextView rolTripInfo;
    private TextView dateTripInfo;
    private TextView toUniversityTripInfo;
    private TextView infoTripInfo;
    private TextView infoDepartureTripInfo;

    private Polyline polyline;

    private GoogleMap mMap;

    private LocationManager locationManager;

    private boolean mLocationPermissionGranted;

    private static final String TAG = "TripInfoActivity";
    private Marker mUniversityMarker;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private Boolean isMarkerGragged;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;

    private CameraPosition mCameraPosition;

    private DirectionsLeg directionsLeg;

    //Google API
    private static GeoApiContext mGeoApiContext;
    private PlacesClient mPlacesClient;


    private Marker mUserMarker;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Map vars
    private final LatLng mUniversityLocation = new LatLng(4.782715, -74.042611);
    private static final int DEFAULT_ZOOM = 15;

    private DirectionsResult route;

    private DirectionsRoute directionsRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);
        //GOOGLE MAP
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        initGoogleMap();

        //Else

        Toolbar mToolbar = findViewById(R.id.toolbarTripInfo);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rolTripInfo = findViewById(R.id.rol_trip_info);
        dateTripInfo = findViewById(R.id.date_trip_info);
        toUniversityTripInfo = findViewById(R.id.toUniversity_trip_info);
        infoTripInfo = findViewById(R.id.info_trip_info);
        infoDepartureTripInfo = findViewById(R.id.info_departure_trip_info);
        if (MemoryUtil.TRIP != null) {
            setTripInfo();
        }
        if (MemoryUtil.TRIPREQUEST != null) {
            setTripRequestInfo();
        }


    }

    private void setTripInfo() {
        trip = MemoryUtil.TRIP;
        rolTripInfo.setText(getString(R.string.driver));
        dateTripInfo.setText(trip.dayInSpanish() + " " + trip.getHour());
        if (trip.getToUniversity()) {
            toUniversityTripInfo.setText("Hacia la universidad");
        } else {
            toUniversityTripInfo.setText("Desde la universidad");
        }
        if (trip.getPassengers() == null) {
            infoTripInfo.setText("Quedan " + (trip.getAvailableSeats()) + " cupos");
            infoTripInfo.setTextColor(Color.DKGRAY);
        } else {
            if (trip.getAvailableSeats() - trip.getPassengers().size() == 0) {
                infoTripInfo.setText("¡El carro se lleno!");
                infoTripInfo.setTextColor(Color.BLACK);
            } else {
                infoTripInfo.setText("Quedan " + (Integer.toString(trip.getAvailableSeats() - trip.getPassengers().size())) + " cupos");
                infoTripInfo.setTextColor(Color.DKGRAY);
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("EEEE dd MMMM 'del' yyyy 'a las' HH:mm", new Locale("es", "CO"));
        infoDepartureTripInfo.setText("Fecha de salida: " + format.format(trip.getDepartureDate()));
    }

    private void setTripRequestInfo() {
        tripRequest = MemoryUtil.TRIPREQUEST;
        rolTripInfo.setText(getString(R.string.passenger));
        dateTripInfo.setText(tripRequest.dayInSpanish() + " " + tripRequest.getHour());
        if (tripRequest.getToUniversity()) {
            toUniversityTripInfo.setText("Hacia la universidad");
        } else {
            toUniversityTripInfo.setText("Desde la universidad");
        }
        if (tripRequest.getMatched()) {
            SimpleDateFormat format = new SimpleDateFormat("EEEE dd MMMM 'del' yyyy 'a las' HH:mm", new Locale("es", "CO"));
            infoTripInfo.setText("¡Se ha encontrado un viaje!");
            infoDepartureTripInfo.setText("Fecha de salida: " + format.format(tripRequest.getDepartureDate()));
            infoTripInfo.setTextColor(Color.BLACK);
        } else {
            infoTripInfo.setText("Pendiente");
            infoTripInfo.setTextColor(Color.DKGRAY);
        }
    }

    private void setIndependentTexts() {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                getDeviceLocation();
            } else {
                getLocationPermission();
            }
            updateLocationUI();
        }
        mUniversityMarker = mMap.addMarker(new MarkerOptions()
                .position(mUniversityLocation)
                .title(getString(R.string.university_position_marker_title))
        );
    }

    private boolean checkMapServices() {
        return isServicesOK() && isGpsEnabled();
    }

    private boolean isServicesOK() {
        boolean isOk = false;
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(TripInfoActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            isOk = true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(TripInfoActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return isOk;
    }

    @SuppressWarnings("MissingPermission")
    private void getGpsLocation() {
        updateLocationUI();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
    }

    public boolean isGpsEnabled() {
        boolean isEnabled = true;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            isEnabled = false;
        }
        return isEnabled;
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted && !isMarkerGragged) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        LatLng position;
                        if (location != null) {
                            mLastKnownLocation = location;
                            position = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                            mMap.setMyLocationEnabled(true);
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            mMap.setMyLocationEnabled(false);
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            position = mUniversityLocation;
                            getGpsLocation();
                        }

                        if (trip != null) {
                            driverDrawLineAndInfo();
                        }
                        if (tripRequest != null) {
                            passengerDrawLineAndInfo();
                        }
                        //if(isDriver){
                        //   drawPolyLine();
                        //}
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            mLocationPermissionGranted = true;
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Necesitamos saber donde estas para ubicarte en el mapa, deseas habilitar el GPS?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void moveMarker(final Marker marker, final LatLng toPosition, final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    public boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this.getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void initGoogleMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapTripInfo);
        mapFragment.getMapAsync(this);
        isMarkerGragged = false;
        String apiKey = getString(R.string.google_maps_key);

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder().apiKey(apiKey).build();
        }
    }

    //Map UI
    private void addPolylineToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                DirectionsRoute route = result.routes[0];
                directionsLeg = route.legs[0];
                Log.d(TAG, "run: leg: " + directionsLeg.toString());
                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                List<LatLng> newDecodedPath = new ArrayList<>();

                // This loops through all the LatLng coordinates of ONE polyline.
                for (com.google.maps.model.LatLng latLng : decodedPath) {
                    newDecodedPath.add(new LatLng(latLng.lat, latLng.lng));
                }
                if (polyline == null) {
                    polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(TripInfoActivity.this, R.color.colorAccent));
                    polyline.setClickable(true);
                } else {
                    polyline.setPoints(newDecodedPath);
                }
            }
        });
    }

    //GOOGLE API with INFO
    private void driverDrawLineAndInfo() {
        LatLng position = new LatLng(trip.getRoute().get(0).getLatitude(), trip.getRoute().get(0).getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(position)
                .draggable(false)
                .title(getString(R.string.departure))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
        List<LatLng> newDecodedPath = new ArrayList<>();
        for (GeoPoint geoPoint : trip.getRoute()) {
            newDecodedPath.add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
        }
        if (polyline == null) {
            polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
            polyline.setColor(ContextCompat.getColor(TripInfoActivity.this, R.color.colorAccent));
            polyline.setClickable(true);
        } else {
            polyline.setPoints(newDecodedPath);
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(trip.getPassengers());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (trip.getPassengersWithInfo() != null && trip.getPassengersWithInfo().size() > 0) {
                            SimpleDateFormat format = new SimpleDateFormat("EEEE dd 'a las' HH:mm", new Locale("es","CO"));
                            for (PassengerInfo passenger : trip.getPassengersWithInfo()) {
                                String name = StringUtils.getNameFromEmail(passenger.getPassengerEmail());
                                String snippet = "Recoger el " + format.format(passenger.getMeetingDate());
                                mMap.addMarker(new MarkerOptions()
                                        .position(AdapterUtils.convertGeoPointToLatLng(passenger.getMeetingPoint()))
                                        .title(name)
                                        .snippet(snippet)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                );
                            }
                        }
                        getWindow().getDecorView().invalidate();
                    }
                });
            }
        });

    }

    private void passengerDrawLineAndInfo() {
        if (tripRequest.getMatched()) {
            List<LatLng> newDecodedPath = new ArrayList<>();
            for (GeoPoint geoPoint : tripRequest.getRouteWalking()) {
                newDecodedPath.add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
            }
            if (polyline == null) {
                polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                polyline.setColor(ContextCompat.getColor(TripInfoActivity.this, R.color.colorAccent));
                polyline.setClickable(true);
            } else {
                polyline.setPoints(newDecodedPath);
            }
            LatLng position = new LatLng(tripRequest.getMeetingPoint().getLatitude(), tripRequest.getMeetingPoint().getLongitude());
            Marker destiny = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .draggable(false)
                    .title(getString(R.string.walk_to_here))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
            destiny.showInfoWindow();

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(tripRequest.getRouteWalking().get(0).getLatitude(), tripRequest.getRouteWalking().get(0).getLongitude()))
                    .draggable(false)
                    .title(getString(R.string.departure)));
        } else {
            LatLng position = new LatLng(tripRequest.getUserPosition().getLatitude(), tripRequest.getUserPosition().getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .draggable(false)
                    .title(getString(R.string.departure))
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
            marker.showInfoWindow();
        }
    }

}
