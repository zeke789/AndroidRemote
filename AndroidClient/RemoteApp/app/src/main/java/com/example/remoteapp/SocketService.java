package com.example.remoteapp;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


public class SocketService extends Service {

    Thread thread = null;
    static TCPConnection tcpConn = null;
    public static final String CHANNELID = "FSC";
    public Context thiss;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {return null;}

    public SocketService()
    {
        thiss = this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public  void stopService()
    {
        this.stopSelf();
    }

    private void beginForegroundServiceWithNotif()
    {
        try{
            String input = "RemoteApp Activa";
            createNotificationChannel();
            Intent i3 = new Intent(this,Main2Activity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0, i3, 0);
            Notification notification = new NotificationCompat.Builder(this, CHANNELID)
                    .setContentTitle("Android_Seguridad")
                    .setContentText(input)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(Notification.PRIORITY_MIN)
                    .setContentIntent(pendingIntent).build();

            if(thread == null){
                this.startForeground(1, notification);
                tcpConn = new TCPConnection();
                thread = new Thread(tcpConn);
                thread.start();
                tcpConn.setPassword(MainActivity.passwd);

                MainActivity.mainThread = tcpConn;
                Actions acts = new Actions();

            }else{
                Log.i("_otro_","INFO_012897" );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            beginForegroundServiceWithNotif();
        }else{
            try{
                tcpConn = new TCPConnection();
                thread = new Thread(tcpConn);
                thread.start();
                MainActivity.mainThread = tcpConn;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            beginForegroundServiceWithNotif();
        }else{
            Thread t = new Thread(new TCPConnection());t.start();
        }
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        super.onTaskRemoved(rootIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNELID, "Notificaciones", NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }



}
