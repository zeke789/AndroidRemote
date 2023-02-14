package com.example.remoteapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.example.remoteapp.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static Activity activity1;
    private static Context context;

    public static TCPConnection mainThread = null;

    public boolean allowSMS;
    public boolean allowGEO;
    public static boolean permsAll = false;
    public static String passwd = "";

    private static final int FILE_PERMS_CODE = 1;
    private static final int SMS_PERMS_CODE = 2;
    private static final int GEO_PERMS_CODE = 3;

    private static final String[] FILE_PERMS_STRING = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE};
    private static final String[] SMS_PERMS_STRING = new String[]{Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS};
    private static final String[] GEO_PERMS_STRING = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


    private FusedLocationProviderClient fusedLocationClient;
    public String finalLoc = null;
    public static LocationManager locationManager;

    TextView pass;
    Button btnStart;

    public static Activity this2;

    public static Activity getThis() {
        return (Activity) activity1;
    }

    public static Activity getThis2() {
        return this2;
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        activity1 = (Activity) this;
        context = getApplicationContext();


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this2=this;

        locationManager= (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        pass = findViewById(R.id.editTextPassword); btnStart = findViewById(R.id.buttonStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( permsAll || requestAllPermissions() ){
                    if(!pass.getText().toString().equals("")){
                        start(pass.getText().toString());
                    }else{
                        Toast.makeText(MainActivity.this, "Debe establecer una contraseña antes de iniciar el servicio", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        permsAll = requestAllPermissions();
    }


    public static boolean checkPermissions(String[] perms)
    {
        for (int i = 0; i < perms.length; i++)
            if (getThis().checkCallingOrSelfPermission(perms[i]) != PackageManager.PERMISSION_GRANTED)
                return false;
        return true;
    }

    public void showNotPermissionToast()
    {
        Toast.makeText(this, "Debe aceptar todos los permisos para que la app funcione correctamente", Toast.LENGTH_LONG).show();
    }


    public boolean requestAllPermissions()
    {
        if (checkPermissions(FILE_PERMS_STRING)) {
            if (!checkPermissions(SMS_PERMS_STRING)) {
                requestSMSPermission();
                return false;
            }else{
                if (!checkPermissions(GEO_PERMS_STRING)) {
                    requestGeoPermission();
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            requestFilePermission();
            return false;
        }

    }

    private void start(String pass)
    {
        passwd = pass;
        Intent serviceIntent = new Intent(MainActivity.getThis(), SocketService.class);
        startService(serviceIntent);
    }

    @SuppressLint("MissingPermission")
    public String getLocation() {
        if(fusedLocationClient != null){
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        finalLoc = location.getLatitude()+","+location.getLongitude();
                    }else{
                        // send getgeo_error
                    }
                }
            });

            fusedLocationClient.getLastLocation().addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(" ## -->  error_geo_001  <-- ##");
                }
            });

        }else{
            System.out.println(" ## -->  fusedLocationCLient NULL  <-- ##");
        }
        return finalLoc;
    }

    public static void requestFilePermission()
    {
        ActivityCompat.requestPermissions(MainActivity.getThis(), FILE_PERMS_STRING, FILE_PERMS_CODE);
    }
    public static void requestSMSPermission()
    {
        ActivityCompat.requestPermissions(MainActivity.getThis(), SMS_PERMS_STRING, SMS_PERMS_CODE);
    }
    public static void requestGeoPermission()
    {
        ActivityCompat.requestPermissions(MainActivity.getThis(), GEO_PERMS_STRING, GEO_PERMS_CODE);
    }

    public boolean setPassword()
    {
        try{
            String passwd= pass.getText().toString();
            SharedPreferences settings = getSharedPreferences("userinfo", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("passwd",passwd);
            editor.apply();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FILE_PERMS_CODE:
                if( checkPermissions(FILE_PERMS_STRING) ){
                    requestSMSPermission();
                }else{
                    showNotPermissionToast();
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            case SMS_PERMS_CODE:
                if( checkPermissions(SMS_PERMS_STRING) ){
                    allowSMS=true;
                    requestGeoPermission();
                }else{
                    allowSMS=false;
                    showNotPermissionToast();
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            case GEO_PERMS_CODE:
                if( checkPermissions(GEO_PERMS_STRING) ){
                    allowGEO=true;
                }else{
                    allowGEO=false;
                    showNotPermissionToast();
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
}