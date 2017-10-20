package taras.mushroomer.services;


import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import taras.mushroomer.Fragment.MapTrackerFragment;


public class GpsService extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    private ArrayList<Location> backgroundLocationList;


    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider){
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location){
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            if (backgroundLocationList == null){
                backgroundLocationList = new ArrayList<>();
            }
            backgroundLocationList.add(mLastLocation);
            if (isActivityForeground()){
                Intent intent = new Intent(MapTrackerFragment.BROADCAST_ACTION);
                intent.putExtra("ServiceGpsLatitudeList", getDoubleItems(backgroundLocationList, "Latitude"));
                intent.putExtra("ServiceGpsLongitudeList", getDoubleItems(backgroundLocationList, "Longitude"));
                backgroundLocationList = null;
                sendBroadcast(intent);
            }
            /*
            if (isActivityForeground()){
                Intent intent = new Intent(MapTrackerFragment.BROADCAST_ACTION);
                if (backgroundLocationList != null){
                    backgroundLocationList.add(mLastLocation);
                    intent.putExtra("ServiceGpsLatitudeList", getDoubleItems(backgroundLocationList, "Latitude"));
                    intent.putExtra("ServiceGpsLongitudeList", getDoubleItems(backgroundLocationList, "Longitude"));
                    backgroundLocationList = null;
                } else {
                    intent.putExtra("ServiceGpsLatitude", mLastLocation.getLatitude());
                    intent.putExtra("ServiceGpsLongitude", mLastLocation.getLongitude());
                }
                sendBroadcast(intent);
            } else {
                if (backgroundLocationList == null){
                    backgroundLocationList = new ArrayList<>();
                }
                backgroundLocationList.add(mLastLocation);
            }*/
        }

        @Override
        public void onProviderDisabled(String provider){
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider){
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){
            Log.e(TAG, "onStatusChanged: " + provider);
        }

        private ArrayList<Double> getDoubleItems(ArrayList<Location> locationList, String type){
            ArrayList<Double> doubleList = new ArrayList<>();
            if (type.equals("Latitude")){
                for (Location location: locationList){
                    doubleList.add(location.getLatitude());
                }
            } else {
                for (Location location: locationList){
                    doubleList.add(location.getLongitude());
                }
            }
            return doubleList;
        }
    }


    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e("LoggingApp", "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate(){
        Log.e("LoggingApp","GpsService - created");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy(){
        Log.e("GpsService","GpsService - destroyed");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public boolean isActivityForeground(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfos.get(0).topActivity;
        if (componentInfo.getClassName().equals("taras.mushroomer.Fragment.MapTrackerFragment")) return true;
        else return false;
    }
}