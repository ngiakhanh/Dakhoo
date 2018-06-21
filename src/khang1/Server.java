/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khang1;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


/**
 *
 * @author USER
 */
public class Server extends javax.swing.JFrame {
static ServerSocket server=null;
static Socket socket=null;
static DataInputStream din;
static DataOutputStream dout;

//static String temp="";
static String msgin="";

static boolean stt=false;
//static boolean changeport=false;

static InetAddress currip=null;
static int PortNum=10000;
//static int PortNum=59636;

static String username;

//List<Thread> appThreads = new ArrayList<>();
static ArrayList clientOutputStreams;
//static ArrayList <String> all_users;
static ArrayList <String> onl_users;
static ArrayList <String> off_users;
static ArrayList <String> priv_users;
/*static ArrayList <String> waitingresp;
static ArrayList <String> waitingsender;
static ArrayList <String> waitingmem;
static ArrayList <String> blacklist;*/

Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * Creates new form Server
     */
    public Server() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Dialog = new javax.swing.JOptionPane();
        PORT = new javax.swing.JFrame();
        PortLab = new javax.swing.JLabel();
        Port = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Display = new javax.swing.JTextArea();
        Message = new javax.swing.JTextField();
        Send = new javax.swing.JButton();
        Create = new javax.swing.JButton();
        Close = new javax.swing.JButton();
        Clear = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        File = new javax.swing.JMenu();
        My_IP_Address = new javax.swing.JMenuItem();
        Exit = new javax.swing.JMenuItem();
        Features = new javax.swing.JMenu();
        Online_list = new javax.swing.JMenuItem();
        Setting = new javax.swing.JMenu();
        Change_port = new javax.swing.JMenuItem();
        Help = new javax.swing.JMenu();
        About = new javax.swing.JMenuItem();

        Dialog.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        PORT.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        PORT.setAlwaysOnTop(true);
        PORT.setMinimumSize(new java.awt.Dimension(309, 220));
        PORT.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                PORTWindowClosing(evt);
            }
        });

        PortLab.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        PortLab.setText("Port");

        Port.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Port.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PortActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PORTLayout = new javax.swing.GroupLayout(PORT.getContentPane());
        PORT.getContentPane().setLayout(PORTLayout);
        PORTLayout.setHorizontalGroup(
            PORTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PORTLayout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addGroup(PORTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PORTLayout.createSequentialGroup()
                        .addComponent(PortLab)
                        .addGap(8, 8, 8)))
                .addGroup(PORTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PORTLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PORTLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(Port, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(32, 32, 32))
        );
        PORTLayout.setVerticalGroup(
            PORTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PORTLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(PORTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PortLab)
                    .addComponent(Port, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(62, 62, 62)
                .addGroup(PORTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Server");
        setAlwaysOnTop(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        Display.setEditable(false);
        Display.setColumns(20);
        Display.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Display.setRows(5);
        Display.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        Display.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Display.setDropMode(javax.swing.DropMode.INSERT);
        Display.setNextFocusableComponent(Message);
        jScrollPane1.setViewportView(Display);

        Message.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Message.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MessageActionPerformed(evt);
            }
        });
        Message.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MessageKeyPressed(evt);
            }
        });

        Send.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Send.setText("Send");
        Send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendActionPerformed(evt);
            }
        });

        Create.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Create.setText("Create");
        Create.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                CreateMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                CreateMouseExited(evt);
            }
        });
        Create.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateActionPerformed(evt);
            }
        });

        Close.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Close.setText("Close");
        Close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseActionPerformed(evt);
            }
        });

        Clear.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Clear.setText("Clear");
        Clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearActionPerformed(evt);
            }
        });

        File.setText("File");
        File.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        My_IP_Address.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.SHIFT_MASK));
        My_IP_Address.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        My_IP_Address.setText("My IP Address");
        My_IP_Address.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                My_IP_AddressActionPerformed(evt);
            }
        });
        File.add(My_IP_Address);

        Exit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.SHIFT_MASK));
        Exit.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Exit.setText("Exit");
        Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitActionPerformed(evt);
            }
        });
        File.add(Exit);

        jMenuBar1.add(File);

        Features.setText("Features");
        Features.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        Online_list.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_MASK));
        Online_list.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Online_list.setText("Online list");
        Online_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Online_listActionPerformed(evt);
            }
        });
        Features.add(Online_list);

        jMenuBar1.add(Features);

        Setting.setText("Setting");
        Setting.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        Change_port.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK));
        Change_port.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Change_port.setText("Change port");
        Change_port.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Change_portActionPerformed(evt);
            }
        });
        Setting.add(Change_port);

        jMenuBar1.add(Setting);

        Help.setText("Help");
        Help.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        About.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK));
        About.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        About.setText("About");
        Help.add(About);

        jMenuBar1.add(Help);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Message, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Send)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(Create)
                        .addGap(83, 83, 83)
                        .addComponent(Clear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Close)
                        .addGap(73, 73, 73)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Create)
                    .addComponent(Close)
                    .addComponent(Clear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Message, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                    .addComponent(Send, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendActionPerformed
     if (stt==true)
     {
        if (!"".equals(Message.getText().trim()))
       {
    ClientThead.tellEveryone("Server:"+Message.getText().trim()+":Chat");
    Display.append("Server: "+Message.getText().trim()+"\n");
    Display.setCaretPosition(Display.getDocument().getLength());
    Message.setText("");
       }
     }
     else
     {
    Dialog.showMessageDialog(this, "There is no connection right now.","Warning",JOptionPane.WARNING_MESSAGE); 
     }
    }//GEN-LAST:event_SendActionPerformed

    private void MessageKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MessageKeyPressed

  if(evt.getKeyCode() == KeyEvent.VK_ENTER){
    if (stt==true)
    {
    if (!"".equals(Message.getText().trim()))
     {
    ClientThead.tellEveryone("Server:"+Message.getText().trim()+":Chat");
    Display.append("Server: "+Message.getText().trim()+"\n");
    Display.setCaretPosition(Display.getDocument().getLength());
    Message.setText("");
     }
    }
    else
     {
    Dialog.showMessageDialog(this, "There is no connection right now.","Warning",JOptionPane.WARNING_MESSAGE);     
     }
    }//GEN-LAST:event_MessageKeyPressed
}
    private void CreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateActionPerformed
        if (stt==false)
        {
        Thread starter = new Thread(new ServerStart());
        starter.start();
        Display.append("Server is ready.\n");
        Display.setCaretPosition(Display.getDocument().getLength());
        if (Online_list.isEnabled()==false)
        {
            Online_list.setEnabled(true);
        }
        stt = true;
        }
        else
        {
        Dialog.showMessageDialog(this, "Server is already ready.","Warning",JOptionPane.WARNING_MESSAGE);      
        }
    }//GEN-LAST:event_CreateActionPerformed

    private void My_IP_AddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_My_IP_AddressActionPerformed
    try 
    {
        currip = InetAddress.getLocalHost();
        Dialog.showMessageDialog(this, "Current IP address: "+currip.getHostAddress(),"Information",JOptionPane.INFORMATION_MESSAGE);       
    } catch (UnknownHostException ex) 
    {
        Dialog.showMessageDialog(this, "Unknown error, please try again later!","Error",JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
    }
    }//GEN-LAST:event_My_IP_AddressActionPerformed

    private void CloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseActionPerformed
        if (stt==true)
        {
        int n=Confirm();
            if (n==0)
            {
            ClientThead.tellEveryone("Server:Server is stopping and all users will be disconnected.:Close");
            Display.append("Server is stopping... \n");
            Display.setCaretPosition(Server.Display.getDocument().getLength());
            try
            { 
                
                Thread.sleep(1000);
                stt=false;
                if (server!=null)
                {
                server.close(); server=null;
                }
                if (socket!=null)
                {
                socket.close(); socket=null;
                }
                onl_users.clear();
                clientOutputStreams.clear();
                Display.append("Stopped successfully! \n");
                Display.setCaretPosition(Display.getDocument().getLength());
             }
            catch (SocketException ex)
                {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            catch(InterruptedException ex) 
                {
                    Thread.currentThread().interrupt();
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    Dialog.showMessageDialog(this, "Server cannot stop.","Error",JOptionPane.ERROR_MESSAGE);
                }   
            catch (IOException ex) 
                {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    Dialog.showMessageDialog(this, "Server cannot stop.","Error",JOptionPane.ERROR_MESSAGE);
                }
                }
        }
        else
        {
            Dialog.showMessageDialog(this, "There is already no connection to be closed.","Warning",JOptionPane.WARNING_MESSAGE);
        }
        
    }//GEN-LAST:event_CloseActionPerformed

    private void ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitActionPerformed

        int n=Confirm();
            if (n==0)
            {
                if (stt==true)
                {
            ClientThead.tellEveryone("Server:Server is stopping and all users will be disconnected.:Close");
            Display.append("Server is stopping... \n");
            Display.setCaretPosition(Server.Display.getDocument().getLength());
            try
            { 
                
                Thread.sleep(1000);
                stt=false;
                if (server!=null)
                {
                server.close(); server=null;
                }
                if (socket!=null)
                {
                socket.close(); socket=null;
                }
                onl_users.clear();
                clientOutputStreams.clear();
                Display.append("Stopped successfully! \n");
                Display.setCaretPosition(Display.getDocument().getLength());
             }
            catch (SocketException ex)
                {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            catch(InterruptedException ex) 
                {
                    Thread.currentThread().interrupt();
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    Dialog.showMessageDialog(this, "Server cannot stop.","Error",JOptionPane.ERROR_MESSAGE);
                }   
            catch (IOException ex) 
                {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    Dialog.showMessageDialog(this, "Server cannot stop.","Error",JOptionPane.ERROR_MESSAGE);
                }
                }
                System.exit(0);
            }
        else
        {
            Dialog.showMessageDialog(this, "Be careful in the next time, sir!","Warning",JOptionPane.WARNING_MESSAGE);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_ExitActionPerformed

    private void Change_portActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Change_portActionPerformed
        PORT.setVisible(true);
        PORT.toFront();
        this.setEnabled(false);
        Port.setText(Integer.toString(PortNum));
        PORT.setLocation(dim.width/2-PORT.getSize().width/2, dim.height/2-PORT.getSize().height/2);
    }//GEN-LAST:event_Change_portActionPerformed

    private void PortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PortActionPerformed
        // TODO dd your handling code here:
    }//GEN-LAST:event_PortActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    String PortStr = Port.getText().trim();
    try
    {
      if (stt==false)
        {
             if (!"".equals(PortStr))
            {
            PortNum = Integer.parseInt(PortStr);
            }  
        }
      else
        {
             if (!"".equals(PortStr)&&PortNum!=Integer.parseInt(PortStr))
            {
                Dialog.showMessageDialog(this, "Close the current server first, sir!","Warning",JOptionPane.WARNING_MESSAGE);
            }  
        }
        PORT.dispose();
        this.setEnabled(true);
    }
    catch (NumberFormatException e)
    {  
        Dialog.showMessageDialog(this, "Wrong port number format, sir!","Warning",JOptionPane.WARNING_MESSAGE);
        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
    }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void Online_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Online_listActionPerformed
        Display.append("--------------------------------------------------------------"+"\n");
        Display.append("Online user(s) : \n");
        onl_users.forEach((current_user) -> {
            Display.append(current_user+"\n");
        });


        Display.append("Offline user(s) : \n");
        off_users.forEach((current_user) -> {
            Display.append(current_user+"\n");
        });
        Display.append("--------------------------------------------------------------"+"\n");
        
        Display.setCaretPosition(Display.getDocument().getLength());
    }//GEN-LAST:event_Online_listActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        PORT.setVisible(false);
        this.setEnabled(true);// TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void MessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MessageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MessageActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int n=Confirm();
            if (n==0)
            {
                if (stt==true)
                {
            ClientThead.tellEveryone("Server:Server is stopping and all users will be disconnected.:Close");
            Display.append("Server is stopping... \n");
            Display.setCaretPosition(Server.Display.getDocument().getLength());
            try
            { 
                
                Thread.sleep(2000);
                stt=false;
                if (server!=null)
                {
                server.close(); server=null;
                }
                if (socket!=null)
                {
                socket.close(); socket=null;
                }
                onl_users.clear();
                clientOutputStreams.clear();
                Display.append("Stopped successfully! \n");
                Display.setCaretPosition(Display.getDocument().getLength());
             }
            catch (SocketException ex)
                {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            catch(InterruptedException ex) 
                {
                    Thread.currentThread().interrupt();
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    Dialog.showMessageDialog(this, "Server cannot stop.","Error",JOptionPane.ERROR_MESSAGE);
                }   
            catch (IOException ex) 
                {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    Dialog.showMessageDialog(this, "Server cannot stop.","Error",JOptionPane.ERROR_MESSAGE);
                }
                }
                System.exit(0);
            }
        else
        {
                Dialog.showMessageDialog(this, "Be careful in the next time, sir!","Warning",JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_formWindowClosing

    private void PORTWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_PORTWindowClosing
        PORT.setVisible(false);
        this.setEnabled(true);// TODO add your handling code here:
    }//GEN-LAST:event_PORTWindowClosing

    private void ClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearActionPerformed
        Display.setText("");
    }//GEN-LAST:event_ClearActionPerformed

    private void CreateMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CreateMouseEntered
  setCursor(new Cursor(Cursor.HAND_CURSOR));      // TODO add your handling code here:
    }//GEN-LAST:event_CreateMouseEntered

    private void CreateMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CreateMouseExited
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));    // TODO add your handling code here:
    }//GEN-LAST:event_CreateMouseExited
    public int Confirm()
    {
        Object[] options = {"Fucking yes!","Opps! it's a mistake!",};
        int n = JOptionPane.showOptionDialog(this,"Are you fucking sure ?","Confirmation",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
        return n; 
    }
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String args[]) throws IOException, InterruptedException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
                new Server().setVisible(true);
                Online_list.setEnabled(false);
                DateFormat format1 = new SimpleDateFormat("EEEE, MMMM d, yyyy  HH:mm:ss\n");
                Display.append(format1.format(Calendar.getInstance().getTime()));
                Display.setCaretPosition(Display.getDocument().getLength());
        }); 
    }

    public class ServerStart implements Runnable 
    {
        @Override
        public void run() 
        {
            clientOutputStreams = new ArrayList();
            onl_users = new ArrayList();
            off_users = new ArrayList();
            priv_users = new ArrayList();
            try 
            {
                server = new ServerSocket(PortNum);
                while (true) 
                {
				socket = server.accept();
				PrintWriter writer = new PrintWriter(socket.getOutputStream());//boc doi tuong writer (cho viet) vao outputstream
				clientOutputStreams.add(writer);//them writer vao danh sach clientOutputStream
                                // ArrayList -> nhieu writer -) nhieu outputstream tu nhieu socket thread
				Thread listener = new Thread(new ClientThead(socket, writer));//quang moi thread vao ClientThead
                                listener.start();//start ClientThead
				Display.append("Got a connection. \n");
                                Display.setCaretPosition(Server.Display.getDocument().getLength());
                }
            }
            catch (BindException ex)
            {
                Display.append("Another version is in used. \n");
                Display.setCaretPosition(Display.getDocument().getLength());
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (SocketException ex)
            {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (IOException ex)
            {
                Display.append("Error making a connection. \n");
                Display.setCaretPosition(Display.getDocument().getLength());
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally
            {
                try 
                {
                    if (stt==true)
                    {
                      if (server!=null)
                      {
                    server.close(); server=null;
                      }
                      if (socket!=null)
                      {
                    socket.close(); socket = null;
                      }
                    stt=false;
                    onl_users.clear();
                    clientOutputStreams.clear();
                    }
                } 
                catch (IOException ex) 
                {
                    Display.append("Unknown error while trying closing socket. \n");
                    Display.setCaretPosition(Display.getDocument().getLength());
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem About;
    private javax.swing.JMenuItem Change_port;
    private javax.swing.JButton Clear;
    private javax.swing.JButton Close;
    private javax.swing.JButton Create;
    private static javax.swing.JOptionPane Dialog;
    public static javax.swing.JTextArea Display;
    private javax.swing.JMenuItem Exit;
    private javax.swing.JMenu Features;
    private javax.swing.JMenu File;
    private javax.swing.JMenu Help;
    private javax.swing.JTextField Message;
    private javax.swing.JMenuItem My_IP_Address;
    private static javax.swing.JMenuItem Online_list;
    private javax.swing.JFrame PORT;
    private javax.swing.JTextField Port;
    private javax.swing.JLabel PortLab;
    private javax.swing.JButton Send;
    private javax.swing.JMenu Setting;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
