package com.ieti.easywheels.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.ieti.easywheels.R;
import com.ieti.easywheels.model.Trip;
import com.ieti.easywheels.model.TripRequest;
import com.ieti.easywheels.network.Firebase;
import com.ieti.easywheels.util.AdapterUtils;
import com.ieti.easywheels.util.DateUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ieti.easywheels.Constants.ERROR_DIALOG_REQUEST;
import static com.ieti.easywheels.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.ieti.easywheels.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;

    private static final String TAG = "MapActivity";
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    //State Vars
    private boolean mLocationPermissionGranted;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Map vars
    private final LatLng mUniversityLocation = new LatLng(4.782715, -74.042611);
    private static final int DEFAULT_ZOOM = 15;

    //Google API
    private static GeoApiContext mGeoApiContext;
    private PlacesClient mPlacesClient;

    //User Map info
    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private LocationManager locationManager;
    private AutocompleteSupportFragment searchBox;
    private FloatingActionButton confirmationButton;

    private Marker mUserMarker;
    private Marker mUniversityMarker;
    private Polyline polyline;
    private DirectionsLeg directionsLeg;

    private Boolean isMarkerGragged;

    //TripInfo
    private Boolean toUniversity;
    private String day;
    private String hour;
    private Integer availableSeats;
    private Boolean isDriver;
    private LatLng userSelectedPoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        getTripData();
        setContentView(R.layout.activity_maps);

        Places.initialize(getApplicationContext(), getString(R.string.places_key));

        mPlacesClient = Places.createClient(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        initGoogleMap();
        initPlacesSearchBox();
        confirmationButton = findViewById(R.id.floatingActionButton);


        confirmationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isDriver) {
                    setDriverDirectionRoute();
                } else {
                    passengerRequestTravel();
                }
            }
        });

    }

    private void initPlacesSearchBox() {
        searchBox = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.searchBox);
        searchBox.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME));
        if (toUniversity) {
            if (isDriver) {
                searchBox.setHint(getString(R.string.searchBox_hint_toU_as_driver));
            } else {
                searchBox.setHint(getString(R.string.searchBox_hint_toU_as_passenger));
            }
        } else {
            searchBox.setHint(getString(R.string.searchBox_hint_fromU));
        }
        searchBox.setCountry("CO");
        searchBox.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                isMarkerGragged = true;
                Log.i(TAG, place.toString());
                userSelectedPoint = place.getLatLng();
                mUserMarker.setPosition(userSelectedPoint);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        userSelectedPoint, DEFAULT_ZOOM));

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void getTripData() {
        day = (String) getIntent().getExtras().get("day");
        hour = (String) getIntent().getExtras().get("hour");
        toUniversity = (Boolean) getIntent().getExtras().get("toUniversity");
        isDriver = (Boolean) getIntent().getExtras().get("isDriver");
        if (isDriver) {
            availableSeats = (int) getIntent().getExtras().get("availableSeats");
        }
    }

    private void initGoogleMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        isMarkerGragged = false;
        String apiKey = getString(R.string.google_maps_key);

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder().apiKey(apiKey).build();
        }
    }

    private boolean checkMapServices() {
        return isServicesOK() && isGpsEnabled();
    }

    private void getLocationPermission() {
        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            mLocationPermissionGranted = true;
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
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

    /**
     * On Enable GPS listener
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    getDeviceLocation();
                } else {
                    getLocationPermission();
                }
            }
        }

    }

    public boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this.getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isServicesOK() {
        boolean isOk = false;
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            isOk = true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return isOk;
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getDeviceLocation();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();

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

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(this);
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
//                                    .icon()
        );

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap == null) {
            return;
        }
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                getDeviceLocation();
            } else {
                getLocationPermission();
            }
            updateLocationUI();
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
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

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
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
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            position = mUniversityLocation;
                            getGpsLocation();
                        }
                        if (mUserMarker == null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
                            mUserMarker = mMap.addMarker(new MarkerOptions()
                                            .position(position)
                                            .draggable(true)
                                            .title(getString(R.string.user_position_marker_title))
//                                    .icon()
                            );
                        } else {
                            moveMarker(mUserMarker, position, false);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
                        }
                        mUserMarker.showInfoWindow();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
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
                    polyline.setColor(ContextCompat.getColor(MapsActivity.this, R.color.colorAccent));
                    polyline.setClickable(true);
                }else{
                    polyline.setPoints(newDecodedPath);
                }
            }
        });
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

    private void setDriverDirectionRoute() {
        final LatLng origin, destination;
        LatLng userPosition = mUserMarker.getPosition();
        if (toUniversity) {
            origin = userPosition;
            destination = mUniversityLocation;
        } else {
            origin = mUniversityLocation;
            destination = userPosition;
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Date arrivalDate, departureDate;
                Date dateUniversity = DateUtils.getNextDateFromDayAndHour(day, hour);
                final DirectionsResult result = calculateRoute(TravelMode.DRIVING, origin, destination, dateUniversity);
                if (toUniversity) {
                    arrivalDate = dateUniversity;
                    departureDate = DateUtils.getDatePlusSeconds(arrivalDate, (int) -result.routes[0].legs[0].duration.inSeconds);
                } else {
                    departureDate = dateUniversity;
                    arrivalDate = DateUtils.getDatePlusSeconds(departureDate, (int) result.routes[0].legs[0].duration.inSeconds);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addPolylineToMap(result);
                    }
                });
                driverCreateTravel(departureDate, arrivalDate, result);
            }
        });
    }

    private void setPassengerDirectionRoute() {

    }

    //Google Maps API
    public static DirectionsResult calculateRoute(TravelMode travelMode, LatLng origin, LatLng destination, Date dateUniversity) {
        DirectionsApiRequest directionsApiRequest = new DirectionsApiRequest(mGeoApiContext);
        try {
            DirectionsResult result = directionsApiRequest
                    .origin(AdapterUtils.convertLatLngToApiLatLng(origin))
                    .destination(AdapterUtils.convertLatLngToApiLatLng(destination))
                    .mode(travelMode)
                    .departureTime(new org.joda.time.Instant(dateUniversity))
                    .await();
            return result;
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DirectionsResult calculateRoute(TravelMode travelMode, LatLng origin, LatLng destination) {
        DirectionsApiRequest directionsApiRequest = new DirectionsApiRequest(mGeoApiContext);
        try {
            DirectionsResult result = directionsApiRequest
                    .origin(AdapterUtils.convertLatLngToApiLatLng(origin))
                    .destination(AdapterUtils.convertLatLngToApiLatLng(destination))
                    .mode(travelMode)
                    .await();
            return result;
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Firebase functions
    private void driverCreateTravel(Date departureDate, Date arrivalDate, DirectionsResult result) {
        List<com.google.maps.model.LatLng> latLngPoints = result.routes[0].overviewPolyline.decodePath();
        List<GeoPoint> route = new ArrayList<>();
        for (int i = 0; i < latLngPoints.size(); i++) {
            route.add(AdapterUtils.convertLatLngToGeoPoint(latLngPoints.get(i)));
        }
        try {
            String email = Firebase.getFAuth().getCurrentUser().getEmail();
            Trip trip = new Trip(availableSeats, day, departureDate, email, hour, route, toUniversity, arrivalDate);
            Firebase.driverCreateTravel(trip);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void passengerRequestTravel() {
        Date arrivalDate = DateUtils.getNextDateFromDayAndHour(day, hour);
        try {
            String email = Firebase.getFAuth().getCurrentUser().getEmail();
            GeoPoint userPosition = AdapterUtils.convertLocationToGeoPoint(mLastKnownLocation);
            TripRequest tripRequest = new TripRequest(arrivalDate, day, email, hour, false, toUniversity, userPosition);
            Firebase.passengerRequestTravel(tripRequest);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);

        mLastKnownLocation = location;
        Toast.makeText(MapsActivity.this, "Debug latitude:" + mLastKnownLocation.getLatitude() + " longitude:" + mLastKnownLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        getDeviceLocation();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "Listener Working provider.");
        getDeviceLocation();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "Listener Working provider Disabled.");
        isGpsEnabled();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        isMarkerGragged = true;
        try {
            addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        searchBox.setText(address);
    }
}
