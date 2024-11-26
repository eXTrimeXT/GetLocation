package com.extrime.getlocation;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class MyService extends Service {
    final String LOG_TAG = "MY_LOG";
    static String FILENAME = "location.txt";
    public LocationManager locationManager;
    public double Lat, Lon;
    public String date;
    public static String text;
    String isWork, strCoordinates, strTime;

    WorkWithFiles workWithFiles;
    GetLocation getLocation;

    public void MyToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        workWithFiles = new WorkWithFiles();
        getLocation = new GetLocation();
        super.onCreate();
        MyToast("Служба создана!");
    }

    public void onUpdate() {
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10,
//                10, locationListener);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10,
                10, locationListener);

        // todo: СТРОКА 103-104 в Service.java   !!!!!!!!!

        checkEnabled();
//        MyLOG("Обновлено...", "e", false);
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
            onUpdate();
            UpdateTime();
            checkEnabled();
            if (ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
            {
                ActivityCompat.requestPermissions(null, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            showLocation(locationManager.getLastKnownLocation(provider), false);
        }
    };

    public void checkEnabled() {
        isWork = "Work: " + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) + "\n" + strCoordinates + "\n" + UpdateTime();
        UpdateTime();
    }

    public String UpdateTime() {
        onUpdate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = simpleDateFormat.format(new Date());
        return date;
    }

    public String showLocation(Location location, boolean isWorkFiles) {
        if (location == null) return "";
        if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            Lat = location.getLatitude();
            Lon = location.getLongitude();
            text = "Lat = " + Lat + "\nLon = " + Lon + "\nTime = " + UpdateTime();
            if (isWorkFiles) {
                workWithFiles.WriteFile_InternalStorage(text, FILENAME);
                MyToast("getLLD");
            }
        }
        return text;
    }

    public void MyThread() {
        new Thread(new Runnable() {
            public void run() {
              String str = (getLocation.isWork + "\n" + getLocation.strCoordinates + "\n" + getLocation.strTime);
              try {
                  workWithFiles.WriteFile_InternalStorage(str, GetLocation.FILENAME);
                  Log.e("LOG", "FOR Файл записан * onClick");
              }catch (Exception e){
                  Log.e("LOG", "FOR Ошибка записи файла!!!");
              }
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopSelf();
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyToast("Служба запущена!");
        MyThread();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyToast("Служба остановлена!");
    }
}