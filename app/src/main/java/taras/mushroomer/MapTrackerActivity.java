package taras.mushroomer;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import taras.mushroomer.DB.DatabaseHelper;
import taras.mushroomer.dialog.DialogInfoMarker;
import taras.mushroomer.dialog.DialogPutMushroom;
import taras.mushroomer.interfaces.DeleteMushroomMarker;
import taras.mushroomer.interfaces.GetMushroomItem;
import taras.mushroomer.model.MarkerMushroomPair;
import taras.mushroomer.model.Mushroom;
import taras.mushroomer.services.GpsService;

public class MapTrackerActivity extends AppCompatActivity implements OnMapReadyCallback,
        View.OnClickListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraMoveListener, DeleteMushroomMarker, GetMushroomItem {

    FloatingActionButton btnStart;
    FloatingActionButton btnStop;
    FloatingActionButton btnClear;

    DialogPutMushroom dialogPutMushroom;

    Toolbar toolbar;

    private ArrayList<LatLng> trackLocationList;
    private ArrayList<MarkerMushroomPair> mMushroomMarkerList;
    ArrayList<ArrayList<Mushroom>> mushroomList;

    private float distance = 0;
    private float zoomParam = 17f;

    private GoogleMap mMap;
    private LatLng onClickPosition;
    private SupportMapFragment mapFragment;

    private Marker mMarkerStart;
    private Marker mMarkerCurrent;
    private Polyline mTrackLine;


    public final static String BROADCAST_ACTION = "ServiceGpsData";
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Double> longitudeList = (ArrayList<Double>) intent.getSerializableExtra("ServiceGpsLongitudeList");
            ArrayList<Double> latitudeList = (ArrayList<Double>) intent.getSerializableExtra("ServiceGpsLatitudeList");
            ArrayList<LatLng> trackList = new ArrayList<>();
            for (int i = 0; i < longitudeList.size(); i++){
                trackList.add(new LatLng(latitudeList.get(i), longitudeList.get(i)));
            }
            if (trackLocationList == null){
                trackLocationList = new ArrayList<>();
            }
            trackLocationList.addAll(trackList);
            if (trackLocationList.size() > 1){
                PolylineOptions options = new PolylineOptions()
                        .width(4)
                        .color(getResources().getColor(R.color.brown))
                        .addAll(trackLocationList);
                mTrackLine = mMap.addPolyline(options);
            }
            addPoint(trackList);
        }
    };

    @Override
    public void onBackPressed() {
        if (isServiceRunning()){
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }


    private boolean isServiceRunning(){
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = i.next();
            if (runningServiceInfo.service.getClassName().equals("taras.mushroomer.services.GpsService")){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tracker);

        toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        setSupportActionBar(toolbar);

        toolbarTextUpdate(getResources().getString(R.string.gps_tracker), null);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnStart = (FloatingActionButton) findViewById(R.id.map_fab_start);
        btnStop = (FloatingActionButton) findViewById(R.id.map_fab_stop);
        btnClear = (FloatingActionButton) findViewById(R.id.map_fab_clear);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void addPoint(ArrayList<LatLng> locationList){
        for (int i = 0; i < locationList.size(); i++){
            calculateDistance();
            if (mMarkerStart == null ){
                mMarkerStart = mMap.addMarker(new MarkerOptions()
                        .title(getResources().getString(R.string.marker_start_text))
                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerIcon(R.drawable.marker_start)))
                        .position(locationList.get(0)));
                getSupportActionBar().setSubtitle("");
            }
            mTrackLine = mMap.addPolyline(new PolylineOptions().add(locationList.get(i)));
        }
        if (mMarkerStart != null && !mMarkerStart.getPosition().equals(locationList.get(locationList.size() - 1))){
            if (mMarkerCurrent != null){
                mMarkerCurrent.remove();
            }
            mMarkerCurrent = mMap.addMarker(new MarkerOptions()
                    .title(getResources().getString(R.string.marker_start_text))
                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerIcon(R.drawable.marker_current)))
                    .position(locationList.get(locationList.size() - 1)));
        }



        Toast.makeText(this, "Distance: " + distance, Toast.LENGTH_SHORT).show();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationList.get(locationList.size() - 1), zoomParam));
    }

    @Override
    protected void onDestroy() {
        Log.e("LoggingApp","MapTrackerActivity - destroyed");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tracker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_tracker_reference) {
            Intent intent = new Intent(this, MushroomListsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map_fab_start:
                btnStart.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);
                btnClear.setVisibility(View.GONE);
                if (trackLocationList == null){
                    trackLocationList = new ArrayList<>();
                }
                toolbarTextUpdate(null, getResources().getString(R.string.wait_location));
                startService(new Intent(MapTrackerActivity.this, GpsService.class));
                break;
            case R.id.map_fab_stop:
                btnStop.setVisibility(View.GONE);
                btnStart.setVisibility(View.VISIBLE);
                btnClear.setVisibility(View.VISIBLE);
                stopService(new Intent(MapTrackerActivity.this, GpsService.class));
                break;
            case R.id.map_fab_clear:
                btnClear.setVisibility(View.GONE);
                mMap.clear();
                mTrackLine = null;
                mMarkerStart = null;
                mMarkerCurrent = null;
                trackLocationList = null;
                distance = 0;
                toolbarTextUpdate(getResources().getString(R.string.gps_tracker), "");
                break;
        }
    }

    private void toolbarTextUpdate(String title, String subtitle){
        if (title == null){}
        else {
            if (title.equals("")){
                getSupportActionBar().setTitle("");
            } else {
                getSupportActionBar().setTitle(title);
            }
        }
        if (subtitle == null){}
        else {
            if (subtitle.equals("")){
                getSupportActionBar().setSubtitle("");
            } else {
                getSupportActionBar().setSubtitle(subtitle);
            }

        }
    }

    private Bitmap getMarkerIcon(int iconId){
        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), iconId), 80, 80, false);
    }

    private void calculateDistance(){
        if (trackLocationList.size() == 1){
        } else {
            double lat2 = trackLocationList.get(trackLocationList.size() - 1).latitude;
            double lat1 = trackLocationList.get(trackLocationList.size() - 2).latitude;
            double lng2 = trackLocationList.get(trackLocationList.size() - 1).longitude;
            double lng1 = trackLocationList.get(trackLocationList.size() - 2).longitude;

            double earthRadius = 6371000; //meters
            double dLat = Math.toRadians(lat2-lat1);
            double dLng = Math.toRadians(lng2-lng1);
            double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                            Math.sin(dLng/2) * Math.sin(dLng/2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            distance = (float) (distance + (earthRadius * c));
            if (distance == 0){
                getActionBar().setSubtitle("");
            } else {
                getActionBar().setSubtitle(getResources().getString(R.string.distance) + roundRate(distance) + "м");
            }

        }
    }

    private float roundRate(float number) {
        int pow = 10;
        for (int i = 1; i < 2; i++)
            pow *= 10;
        double tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5 ? tmp + 1 : tmp) / pow;
    }

    private Marker selectedMarker = null;

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (MarkerMushroomPair marker1: mMushroomMarkerList){
            if (marker1.getMarker().equals(marker)){
                selectedMarker = marker;
                DialogInfoMarker dialogInfoMarker = DialogInfoMarker.newInstance();
                dialogInfoMarker.setMarkerMushroomPair(marker1);
                dialogInfoMarker.show(getFragmentManager(), "InfoMushroom");
                Toast.makeText(this, "Click on mushroom marker", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        onClickPosition = latLng;
        if (mushroomList == null){
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            mushroomList = new ArrayList<>(databaseHelper.getAllMushrooms());
        }
        dialogPutMushroom = DialogPutMushroom.newInstance(mushroomList, null);
        dialogPutMushroom.show(getFragmentManager(), "TakeMushroom");
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(200);
    }

    @Override
    public void onCameraMove() {
        if (mMarkerStart != null){
            zoomParam = mMap.getCameraPosition().zoom;
        }
    }



    @Override
    public void returnMushroom(MarkerMushroomPair markerMushroomPair, Dialog dialog) {
        dialog.dismiss();
        if (mMushroomMarkerList == null){
            mMushroomMarkerList = new ArrayList<>();
        }
        if (markerMushroomPair.getMarker() == null){
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .draggable(true)
                    .position(onClickPosition));
            markerMushroomPair.setMarker(marker);
        }
        for (int i = 0; i < mMushroomMarkerList.size(); i++){
            if (mMushroomMarkerList.get(i).getMarker().equals(markerMushroomPair.getMarker())){
                mMushroomMarkerList.remove(i);
                mMushroomMarkerList.add(i,markerMushroomPair);
                markerMushroomPair = null;
                break;
            }
        }
        if (markerMushroomPair != null){
            mMushroomMarkerList.add(markerMushroomPair);
        }
    }

    @Override
    public void deleteMushroomMarker(boolean isDeleted) {
        if (isDeleted) {
            for (int i = 0; i < mMushroomMarkerList.size(); i++){
                if (mMushroomMarkerList.get(i).getMarker().equals(selectedMarker)){
                    selectedMarker.remove();
                    mMushroomMarkerList.remove(i);
                }
            }

        }
    }
}
