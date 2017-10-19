package taras.mushroomer.Fragment;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import taras.mushroomer.R;
import taras.mushroomer.services.GpsService;

public class MapTrackerFragment extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private Circle circle = null;
    private Marker mMarker;

    private boolean recordLocation = true;
    private ArrayList<LatLng> locationsList;

    public final static String BROADCAST_ACTION = "ServiceGpsData";
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("ServiceGpsLongitudeList") && intent.hasExtra("ServiceGpsLatitudeList")){
                ArrayList<Double> longitudeList = (ArrayList<Double>) intent.getSerializableExtra("ServiceGpsLongitudeList");
                ArrayList<Double> latitudeList = (ArrayList<Double>) intent.getSerializableExtra("ServiceGpsLatitudeList");
                addPoint(latitudeList, longitudeList);
            } else {
                double latitude = intent.getDoubleExtra("ServiceGpsLatitude", 0);
                double longitude = intent.getDoubleExtra("ServiceGpsLongitude", 0);
                addPoint(latitude, longitude);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tracker);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        setSupportActionBar(toolbar);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fabStart = (FloatingActionButton) findViewById(R.id.map_fab_start);
        FloatingActionButton fabPause = (FloatingActionButton) findViewById(R.id.map_fab_pause);
        FloatingActionButton fabStop = (FloatingActionButton) findViewById(R.id.map_fab_stop);

        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MapTrackerFragment.this, GpsService.class));
            }
        });
        fabPause.setOnClickListener(this);
        fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MapTrackerFragment.this, GpsService.class));
            }
        });
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void addPoint(double latitude, double longitude){
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),15f));
    }

    private void addPoint(ArrayList<Double> latitudeList, ArrayList<Double> longitudeList){
        for (int i = 0; i < longitudeList.size(); i++){
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitudeList.get(i), longitudeList.get(i))));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeList.get(latitudeList.size() - 1), longitudeList.get(longitudeList.size() - 1)),15f));
        Toast.makeText(this, "Added list of points", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mushroom_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMinZoomPreference(6.0f);
        //mMap.setMaxZoomPreference(20.0f);
        // Add a marker in Sydney and move the camera
        //LatLng sydney = getCurrentLocation();
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map_fab_start:
                //mMap.addMarker(new MarkerOptions().position(getCurrentLocation()).title("Current"));
                //LatLng location = getCurrentLocation();
                //MarkerOptions markerOptions = new MarkerOptions().position(location).title("current position");
                //mMap.addMarker(markerOptions);
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                recordLocation = true;
                break;
            case R.id.map_fab_pause:
                recordLocation = false;
                break;
            case R.id.map_fab_stop:

                Snackbar.make(view, "STOP", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
        }
    }

    private void setUpMapIfNeeded() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            locationsList = new ArrayList<>();

            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @Override
                public void onMyLocationChange(Location arg0) {
                    // TODO Auto-generated method stub
                    LatLng location = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                    locationsList.add(location);
                    System.out.println(location.toString());

                    /*
                    if (circle == null){
                        circle =  mMap.addCircle(new CircleOptions()
                                .center(location)
                                .radius(10)
                                .strokeColor(R.color.brown)
                                .fillColor(R.color.colorAccent));
                    }*/

                    /*
                    if (!isPointInCircle(circle, location)){
                        mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(arg0.getLatitude(), arg0.getLongitude())));
                        circle.remove();
                        circle = mMap.addCircle(new CircleOptions()
                                .center(location)
                                .radius(10)
                                .strokeColor(R.color.brown)
                                .fillColor(R.color.colorAccent));
                    }*/
                }
            });

        }
    }

    private boolean isPointInCircle(Circle circle, LatLng location) {
        float[] distance = new float[2];
        Location.distanceBetween(location.latitude, location.longitude,
                circle.getCenter().latitude, circle.getCenter().longitude, distance);
        if (distance[0] > circle.getRadius()) {
            return false;
        } else {
            return true;
        }
    }


}
