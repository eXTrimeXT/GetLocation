package com.extrime.getlocation;

import java.io.*;
import java.util.*;

import android.content.*;
import android.content.pm.*;
import android.location.*;
import android.widget.*;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class GetLocation extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 200;
    final String LOG_TAG = "MY_LOG";
    static String FILENAME = "location.txt";
    String isWork, strCoordinates, strTime;

    TextView textView;

    public double Lat, Lon;
    public static String text;

    WorkWithFiles workWithFiles;
    public LocationManager locationManager;

    public void MyToast(String text){ Toast.makeText(this, text, Toast.LENGTH_SHORT).show(); }

    public void MyLOG(String text, String category, boolean toast) {
        if (category == "e") Log.e(LOG_TAG, text);
        if (category == "d") Log.d(LOG_TAG, text);
        if (toast == true) Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_location);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        textView = findViewById(R.id.textView);
        workWithFiles = new WorkWithFiles();
        UpdateTime();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.onUpdateData:
                onUpdate();
                MyLOG("Обновлено", "e", false);
                break;
            case R.id.StartService:
                startService(new Intent(GetLocation.this, MyService.class));
                break;
            case R.id.StopService:
                stopService(new Intent(GetLocation.this, MyService.class));
                break;
            case R.id.writefile:
                try {
                    String str = (isWork + "\n" + strCoordinates + "\n" + strTime);
                    workWithFiles.WriteFile_InternalStorage(str, FILENAME);
                    MyLOG("Файл записан * onClick", "e", true);
                } catch (Exception e) {
                    MyLOG("Ошибка записи файла!!!", "e", true);
                }
                break;
            case R.id.readfile:
                workWithFiles.ReadFile_InternalStorage(FILENAME);
                break;
            case R.id.btnLocationSettings:
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                break;
            case R.id.deletefile:
                workWithFiles.WriteFile_InternalStorage("", FILENAME);
                MyLOG("Файл очищен", "e", false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onUpdate();
    }

    public void onUpdate() {
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10,10, locationListener);
        checkEnabled();
        strTime = "Время = " + UpdateTime();
    }

    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location, false);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            UpdateTime();
            checkEnabled();
            if (ActivityCompat.checkSelfPermission(GetLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(GetLocation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            showLocation(locationManager.getLastKnownLocation(provider), false);
        }
    };

    public void showLocation(Location location, boolean isWorkFiles) {
        if (location == null) return;
        if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            Lat = location.getLatitude();
            Lon = location.getLongitude();
            text = "Lat = " + Lat + "\nLon = " + Lon + "\nTime = " + UpdateTime();
            if (isWorkFiles) {
                workWithFiles.WriteFile_InternalStorage(text, FILENAME);
                MyToast("getLLD");
            }
        }
        strCoordinates = text;
    }

    public String UpdateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    public void checkEnabled() {
        isWork = "Work: " + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) +"\n" + strCoordinates + "\n" + UpdateTime();
        textView.setText(isWork);
    }
}