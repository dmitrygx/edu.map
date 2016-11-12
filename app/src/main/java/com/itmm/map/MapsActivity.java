package com.itmm.map;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itmm.map.xml_handler.DomXmlParser;
import com.itmm.map.xml_handler.DomXmlWriter;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraChangeListener
{

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    LatLng latLng;
    GoogleMap mGoogleMap;
    Marker currLocationMarker;

    Marker highlitedMarker;

    String dbXmlFileName = "locations.xml";

    boolean viewing = false;
    boolean firstClick = false;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000 * 10, 10, locationListener);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                    locationListener);
        }
    }

    private LocationListener locationListener = new LocationListener () {

        @Override
        public void onLocationChanged (Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled (String provider) {

        }

        @Override
        public void onProviderEnabled (String provider) {

        }

        @Override
        public void onStatusChanged (String provider, int status, Bundle extras) {
            try {
                showLocation(locationManager.getLastKnownLocation(provider));
            } catch (SecurityException secexp) {
                secexp.printStackTrace();
            }
        }
    };

    void showLocation(Location location) {
        if (location == null) {
            return;
        }

        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());

        mGoogleMap.addMarker(new MarkerOptions().position(point).title("My Location"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(point));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
        }

        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setOnMapLongClickListener(this);
        mGoogleMap.setOnCameraChangeListener(this);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(0, 0);
        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("New Location"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        buildGoogleApiClient();

        mGoogleApiClient.connect();
    }

    @Override
    public void onMapClick(LatLng point) {

        Log.i("MAP", "tapped, point = " + point);

        List<android.location.Address> addresses;

        try {

            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            addresses = geo.getFromLocation(point.latitude, point.longitude, 1);
            if (!addresses.isEmpty()) {
                if (addresses.size() > 0) {
                    Toast.makeText(getApplicationContext(), "Address:- " +
                            addresses.get(0).getFeatureName() +
                            addresses.get(0).getAdminArea() +
                            addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                }
            }
            mGoogleMap.clear();

            String markerTitle;
            String featureName = addresses.get(0).getFeatureName();
            String locality = addresses.get(0).getLocality();
            String adminArea = addresses.get(0).getAdminArea();
            String countryName = addresses.get(0).getCountryName();

            if (addresses != null) {
                markerTitle = (featureName != null ? featureName : "") +
                        (locality != null ? ", " + locality : "") +
                        (adminArea != null ? ", " + adminArea : "") +
                        (countryName != null ? ", " + countryName : "");
            }
            else {
                markerTitle = "Unknown";
            }

            highlitedMarker = mGoogleMap.addMarker(new MarkerOptions().position(point).title(markerTitle));

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        }
        catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {

        Log.i("MAP", "long pressed, point=" + point);

        List<android.location.Address> addresses;

        try {

            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            addresses = geo.getFromLocation(point.latitude, point.longitude, 1);
            if (!addresses.isEmpty()) {
                if (addresses.size() > 0) {
                    Toast.makeText(getApplicationContext(), "Address:- " +
                            addresses.get(0).getFeatureName() +
                            addresses.get(0).getAdminArea() +
                            addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                }
            }
            mGoogleMap.clear();

            String markerTitle;
            String featureName = addresses.get(0).getFeatureName();
            String locality = addresses.get(0).getLocality();
            String adminArea = addresses.get(0).getAdminArea();
            String countryName = addresses.get(0).getCountryName();

            if (addresses != null) {
                markerTitle = (featureName != null ? featureName : "") +
                        (locality != null ? ", " + locality : "") +
                        (adminArea != null ? ", " + adminArea : "") +
                        (countryName != null ? ", " + countryName : "");
            }
            else {
                markerTitle = "Unknown";
            }

            highlitedMarker = mGoogleMap.addMarker(new MarkerOptions().position(point).title(markerTitle));

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        }
        catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }

    @Override
    public void onCameraChange(final CameraPosition position) {
        Log.i("MAP", position.toString());
    }

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this,"buildGoogleApiClient",Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this,"onConnected",Toast.LENGTH_SHORT).show();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        /*if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"requestLocationUpdates",Toast.LENGTH_SHORT).show();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } */
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    public void onLocationChanged(Location location) {
        Toast.makeText(this,"onLocationChanged",Toast.LENGTH_SHORT).show();
        //place marker at current position
        mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();

        //zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(14).build();

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    public void onClickSave(View view) {

        DomXmlWriter writer = new DomXmlWriter(dbXmlFileName);

        writer.addNewEntry(highlitedMarker.getTitle(),
                highlitedMarker.getPosition().latitude,
                highlitedMarker.getPosition().longitude);
    }

    public void onClickView(View view) {

        if (!viewing) {
            viewing = true;
            Button p1_button = (Button)findViewById(R.id.button2);
            p1_button.setText("Stop Viewing");

            List<com.itmm.map.location.Location> locations;
            DomXmlParser parser = new DomXmlParser(dbXmlFileName);

            locations = parser.parse();

            mGoogleMap.clear();

            for (com.itmm.map.location.Location location : locations) {
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title(location.getTitle()));
            }
        }
        else {
            viewing = false;
            Button p1_button = (Button)findViewById(R.id.button2);
            p1_button.setText("View");

            mGoogleMap.clear();
        }


    }
}
