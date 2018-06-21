
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khang1;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;
import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.xml.sax.SAXException;

/**
 *
 * @author USER
 */

public class Client extends javax.swing.JFrame {
static Socket socket=null;
static DataInputStream din;
static DataOutputStream dout;
BufferedReader reader;
PrintWriter writer;

static String IP="localhost";
//static String IP="10.70.174.248";
static int PortNum=10000;
//static int PortNum=59636;
static String msgin=""; 
static InetAddress currip=null;
static boolean same_name = false;

ArrayList<String> onl_users = new ArrayList();
ArrayList<String> off_users = new ArrayList();
ArrayList<String> friends = new ArrayList();
ArrayList<String> black_list = new ArrayList();

static boolean stt=false;

static String Name;
static String tempName;
String adder;
String unfriender;

DefaultListModel source = new DefaultListModel(); 
DefaultListModel source2 = new DefaultListModel(); 
DefaultListModel drain = new DefaultListModel();
DefaultListModel friends_box = new DefaultListModel();
DefaultListModel block_box = new DefaultListModel();
DefaultListModel invite_box = new DefaultListModel();

List<String> chosen_ones = new ArrayList();

static ArrayList<String> room1 = new ArrayList(); String[] temproom1;
static ArrayList<String> busy_users = new ArrayList();
static ArrayList<String> free_users = new ArrayList();
/*
ArrayList<String> room2 = new ArrayList(); String[] temproom2;
ArrayList<String> room3 = new ArrayList(); String[] temproom3; */

static ArrayList<String> waitingmess = new ArrayList();
static ArrayList<String> waitingreceiver = new ArrayList();
static ArrayList<String> waitingmemoraction = new ArrayList();

static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    /**
     * Creates new form Client
     */

    public Client() {
        initComponents();
    }
    public void ListenThread() 
    {
         Thread IncomingReader = new Thread(new IncomingReader());
         IncomingReader.start();
    }
     public void userAdd(String data) 
    {
         Display1.append(data + " is now online.\n");
         Display1.setCaretPosition(Display1.getDocument().getLength());
         source.clear();
         source2.clear();
         drain.clear();
         onl_users.forEach((current_user) -> {
            if (!current_user.equals(Name))
            {
            source.addElement(current_user);
            }
         });
         off_users.forEach((current_user) -> {
            if (!current_user.equals(Name))
            {
            source2.addElement(current_user);
            }
         });
         Onl_list.setModel(source);
         Off_list.setModel(source2);
    }
    public void userRemove(String data) 
    {
        Display1.append(data + " is now offline.\n");
        Display1.setCaretPosition(Display1.getDocument().getLength());
        source.clear();
        source2.clear();
        drain.clear();
        onl_users.forEach((current_user) -> {
            if (!current_user.equals(Name))
            {
            source.addElement(current_user);
            }
        });
        off_users.forEach((current_user) -> {
            if (!current_user.equals(Name))
            {
            source2.addElement(current_user);
            }
        });
        Onl_list.setModel(source);
        Off_list.setModel(source2);
    }
    public void sendDisconnect() 
    {
        String bye = (Name + ": :Disconnect");
        try
        {
            writer.println(bye); 
            writer.flush(); 
        } 
        catch (Exception e) 
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
            Display1.append("Could not send Disconnect Message!\n");
            Display1.setCaretPosition(Display1.getDocument().getLength());
        }
    }
    public void Disconnect() 
    {
        if (stt==true)
        {
        try 
        {
            Display1.append("Disconnected.\n");
            Display1.setCaretPosition(Display1.getDocument().getLength());
            if (socket!=null)
            {
            socket.close(); socket=null;
            }
            stt=false;
            onl_users.clear();
            off_users.clear();
        
        } catch(IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            Display1.append("Failed to disconnect. \n");
            Display1.setCaretPosition(Display1.getDocument().getLength());
        }
        }
    }
    public class IncomingReader implements Runnable
    {
        @Override
        public void run() 
        {
            String[] data; String [] subdata;
            String stream, clear = "Clear", close = "Close", connect = "Connect", disconnect = "Disconnect", chat = "Chat", upt = "Update", priv = "Private", unpriv = "Unprivate", add = "Addfriend", unf = "Unfriend", acc = "Accept", wai = "Waiting", don = "Done", make_dis = "Make disconnect", con_no = "Confirm no",  don_con = "Done confirm", con_inv_no = "Confirm invite-no", con_inv_yes = "Confirm invite-yes", don_con_inv = "Done confirm invite";
            int count =0;
            try 
            {
                while ((stream = reader.readLine()) != null) 
                {
                     data = stream.split(":");

                     if (data[2].equals(chat)) 
                     {
                         if (!data[1].equals("")&&!black_list.contains(data[0]))
                         {
                        Display1.append(data[0] + ": " + data[1] + "\n");
                        Display1.setCaretPosition(Display1.getDocument().getLength());
                         }
                     } 
                     else if (data[2].equals(connect))
                     {
                         if ((friends.contains(data[0])||Name.equals(data[0]))&&!black_list.contains(data[0]))
                         {
                             if (Name.equals(data[0]))
                             {
                                if (count!=1)
                                {
                                 count++; 
                                }
                                else
                                {
                                   try 
                                   {
                                Dialog.showMessageDialog(null,"Your account has been logged in another device. Your current session will be expired!","Warning",JOptionPane.WARNING_MESSAGE);       
                                writer.println(Name + "::Dont send");
                                writer.flush();
                                sendDisconnect();
                                writer.println(Name + "::Add me");
                                writer.flush();
                                Thread.sleep(100);
                                Disconnect();
                                stt=false;
                                Online_list.setEnabled(false);
                                Friends_list.setEnabled(false);
                                Black_list.setEnabled(false);
                                onl_users.clear();
                                off_users.clear();
                                   } 
                                   catch (InterruptedException ex) 
                                   {
                                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                   }
                                   finally
                                   {
                                System.exit(0);
                                   }
                                }
                             }
                             userAdd(data[0]);
                         }  
                            
                         if (waitingreceiver.contains(data[0]))
                         {
                            int num;
                            while (waitingreceiver.contains(data[0])) 
                            {
                             num = waitingreceiver.indexOf(data[0]);
                             if (!waitingmemoraction.get(num).equals(add)&&!waitingmemoraction.get(num).equals(unf)&&!waitingmemoraction.get(num).equals(acc))
                             {
                             writer.println(Name + ":" + waitingmess.get(num) + ":Private:" + waitingreceiver.get(num)+"-End" + ":"+ waitingmemoraction.get(num));
                             }
                             else
                             {
                             writer.println(Name + ":" + waitingreceiver.get(num) + ":" + waitingmemoraction.get(num));    
                             }
                             writer.flush();
                             waitingreceiver.remove(num);
                             waitingmess.remove(num);
                             waitingmemoraction.remove(num);
                             //System.out.println("done");
                            }
                         }

                     } 
                     else if (data[2].equals(disconnect)) 
                     {
                         if ((friends.contains(data[0]))&&!black_list.contains(data[0]))
                         {
                         userRemove(data[0]);
                         }
                     }
                     else if (data[2].equals(upt))
                     {
                         /*System.out.println("Before:");
                         System.out.println("Onl:"+onl_users);
                         System.out.println("Off:"+off_users);
                         System.out.println("Friends:"+friends);
                         Display1.append("Received upt\n");*/
                         if (friends.contains(data[1])||Name.equals(data[1]))
                         {
                         onl_users.add(data[1]);
                         //System.out.println("Add in onl:"+data[1]);
                         }
    
                     }
                     else if (data[2].equals(don))
                     {
                         friends.stream().filter((user) -> (!onl_users.contains(user))).forEachOrdered((user) -> {
                             off_users.add(user);
                             //System.out.println("Add in off:"+user);
                         }); /*System.out.println("After:");
                         System.out.println("Onl:"+onl_users);
                         System.out.println("Off:"+off_users);
                         System.out.println("Friends:"+friends);
                         System.out.println("");*/
                     }
                     else if (data[2].equals(clear)) 
                     {
                        //Display1.append("Received clear\n");
                        onl_users.clear();
                        off_users.clear();
                     }
                     else if (data[2].equals(close))
                     {
                        Display1.append(data[1]+"\n");
                        Display1.setCaretPosition(Display1.getDocument().getLength());
                        if (!room1.isEmpty())
                        {
                           Display_room1.append(data[1]+"\n");
                           Display_room1.setCaretPosition(Display_room1.getDocument().getLength()); 
                        }
                        Disconnect();
                     }
                     else if (data[2].equals(priv))
                     {
                        
                        if (!data[4].equals(wai))
                      {
                        if (data[3].equals(con_no))
                        {
                            //System.out.println("Before:"+room1);
                            busy_users.add(data[0]);
                            room1.remove(data[0]);
                            //System.out.println("After:"+room1);
                        }
                        else if (data[3].equals(don_con))
                        {
                            //System.out.println("After:"+room1);
                            Display_room1.setText("");
                            Members.setText("");
                            if (room1.size()<2)
                            {
                                room1.clear();
                                busy_users.clear();
                                Display_room1.setText("");
                                Members.setText("");
                                Dialog.showMessageDialog(null, "Everyone is all busy right now, sorry..","Warning",JOptionPane.WARNING_MESSAGE);
                                writer.println(Name + "::Unprivate:End-");
                                writer.flush();
                            }
                            else
                            {         
                                  ListIterator it = room1.listIterator();
                                  while(it.hasNext())
                                  {
                                      Members.append(it.next()+", "); 
                                  }
              
                                  it = room1.listIterator(); 
                                  while (it.hasNext()) 
                                  {  
                                  Object temp = it.next();
                                  //System.out.println(temp);
                                  writer.println(Name + ":Welcome to my private chat room!:Private:" + temp+"-End" + ":"+Members.getText());
                                  writer.flush();
                                  }
                                  
                                  it = busy_users.listIterator();
                                  while (it.hasNext()) 
                                  {  
                                  Object temp = it.next();
                                  ListIterator it2 = room1.listIterator();
                                  while (it2.hasNext())
                                  {
                                  writer.println(Name + ":"+temp+" cannot accept my invite to this room due to being busy right now."+":Private:" + it2.next()+"-End" + ":"+Members.getText());
                                  writer.flush();
                                  }
            
                                  }
                              busy_users.clear();
                              JROOM1.setVisible(true);
                              JROOM1.setLocation(dim.width/2-JROOM1.getSize().width/2, dim.height/2-JROOM1.getSize().height/2);
                            }    
                        }
                        else if (data[3].equals(con_inv_no))
                        {
                            busy_users.add(data[0]);
                        }
                        else if (data[3].equals(con_inv_yes))
                        {
                            free_users.add(data[0]);
                            room1.add(data[0]);
                        }

                        else if (data[3].equals((don_con_inv)))
                        {
                            Members.setText("");
                            ListIterator it = room1.listIterator();
                            while(it.hasNext())
                            {
                                Members.append(it.next()+", "); 
                            }
                            
                            it = free_users.listIterator(); 
                            while (it.hasNext()) 
                            {  
                                Object temp = it.next();
                                ListIterator it2 = room1.listIterator();
                                while(it2.hasNext())
                                {
                                    writer.println(Name + ":"+temp+" has joined in this room."+":Private:" + it2.next()+"-End" + ":"+Members.getText());
                                    writer.flush();
                                }
                            }
                            
                            it = busy_users.listIterator();
                            while (it.hasNext()) 
                            {  
                               Object temp = it.next();
                               ListIterator it2 = room1.listIterator();
                               while (it2.hasNext())
                               {
                                 writer.println(Name + ":"+temp+" cannot accept my invite to this room due to being busy right now."+":Private:" + it2.next()+"-End" + ":"+Members.getText());
                                 writer.flush();
                                }
                            }
                              busy_users.clear();
                              free_users.clear();
                              JROOM1.setVisible(true);
                              JROOM1.setLocation(dim.width/2-JROOM1.getSize().width/2, dim.height/2-JROOM1.getSize().height/2);
                        }
                        else if (data[3].equals(make_dis))
                        {
                          try 
                          {
                                sendDisconnect();
                                Thread.sleep(100);
                                Disconnect();
                                stt=false;
                                onl_users.clear();
                                off_users.clear();
                                Online_list.setEnabled(false);
                                Friends_list.setEnabled(false);
                                Black_list.setEnabled(false);
                          } 
                          catch (InterruptedException ex) 
                          {
                                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                          }
                        }
                        else if (data[3].equals(add))
                       {
                          if (!data[0].equals("Server")&&!black_list.contains(data[0]))
                              {
                          adder = data[0];
                          int k = ConfirmFriendRequest();
                          if (k==0)                      
                          {
                              if (!friends.contains(adder))
                              {
                              friends.add(adder);
                              writer.println(Name + ":" + adder + ":" + acc);
                              writer.flush();
                              friends_box.clear();
                              friends.forEach((current_friend) -> {
                              friends_box.addElement(current_friend);
                              });
                              }
                          }
                              }
                          else if (data[0].equals("Server"))
                          {
                              Display1.append(data[0] + ": " + data[1] + "\n");
                              Display1.setCaretPosition(Display1.getDocument().getLength()); 
                          }
                       }
                        else if (data[3].equals(unf))
                       {
                           unfriender = data[0];
                           if (friends.contains(unfriender))
                           {
                           friends.remove(unfriender);
                           Dialog.showMessageDialog(null, unfriender + " has deleted you from their friends list :(","Warning",JOptionPane.WARNING_MESSAGE);
                           friends_box.clear();
                           friends.forEach((current_friend) -> {
                           friends_box.addElement(current_friend);
                           });
                           }
                           writer.println(Name + "::" + upt);
                           writer.flush();
                       }
                        else if (data[3].equals(acc))
                       {
                         if (!friends.contains(data[0])&&!black_list.contains(data[0]))
                         {
                         friends.add(data[0]);
                         Dialog.showMessageDialog(null, data[0] + " has accepted your request :3","Information",JOptionPane.INFORMATION_MESSAGE);
                         friends_box.clear();
                         friends.forEach((current_friend) -> {
                         friends_box.addElement(current_friend);
                         });
                         }
                         writer.println(Name + "::" + upt);
                         writer.flush();                     
                       }
                        else if (!black_list.contains(data[0]))
                       {
                            if (!data[1].equals(""))
                            {
                        Display_room1.append(data[0] + ": " + data[1] + "\n");
                        Display_room1.setCaretPosition(Display_room1.getDocument().getLength());
                        if (!Members.getText().equals(data[3]))
                        {
                        Members.setText(data[3]);
                        subdata = data[3].split(", ");
                        
                        room1.clear(); 
                        busy_users.clear();
                        room1.addAll(Arrays.asList(subdata));
                        
                        }
                        if (!JROOM1.isEnabled()||!JROOM1.isVisible()||!JROOM1.isActive())
                        {
                            JROOM1.setEnabled(true);
                            JROOM1.setVisible(true);
                            JROOM1.setTitle(Name);
                            JROOM1.setAutoRequestFocus(true);
                            JROOM1.setLocation(dim.width/2-JROOM1.getSize().width/2, dim.height/2-JROOM1.getSize().height/2);
                        }
                            }

                      }
                    }
                    else if (data[4].equals(wai))
                    {
                        waitingreceiver.add(data[0]);
                        waitingmess.add(data[1]);
                        waitingmemoraction.add(data[3]);
                    } 
                     }
                     else if (data[2].equals(unpriv))
                     {
                        Display_room1.append(data[0] + " has left the room.\n");
                        Display_room1.setCaretPosition(Display_room1.getDocument().getLength());

                        //Members.setText(data[3]);
                        //subdata = data[3].split(", ");
                        
                        if (room1.contains(data[0]))
                        {
                            room1.remove(data[0]);
                        }
                        
                        Members.setText("");
                        ListIterator it = room1.listIterator();
                       while (it.hasNext()) 
                        {
                        Members.append((String) it.next()+", ");
                        }
                        
                        if (!JROOM1.isEnabled()||!JROOM1.isVisible()||!JROOM1.isActive())
                        {
                            JROOM1.setEnabled(true);
                            JROOM1.setVisible(true);
                            JROOM1.setAutoRequestFocus(true);
                            JROOM1.setLocation(dim.width/2-JROOM1.getSize().width/2, dim.height/2-JROOM1.getSize().height/2);
                        }
                     }
                     else
                     {
                         Display1.append(Arrays.toString(data) + "-Unknown\n");
                         Display1.setCaretPosition(Display1.getDocument().getLength());
                         writer.println(Name + ":" + Arrays.toString(data) + "-Unknown" + ":" + "Server");
                         writer.flush();
                     }
                }
           }
           catch(IOException ex) 
           {
               
               Disconnect();
               Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex); 
           } 
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        Dialog = new javax.swing.JOptionPane();
        IP_AND_PORT = new javax.swing.JFrame();
        Port1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        IP1 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        LOGIN = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
        Authen1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        NEWS = new javax.swing.JFrame();
        Web = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        ASK_ME = new javax.swing.JFrame();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Ask = new javax.swing.JTextArea();
        jButton4 = new javax.swing.JButton();
        CHAT_ROOM = new javax.swing.JFrame();
        jScrollPane3 = new javax.swing.JScrollPane();
        Onl_list = new javax.swing.JList<>();
        jScrollPane5 = new javax.swing.JScrollPane();
        Private_list = new javax.swing.JList<>();
        Add = new javax.swing.JButton();
        Remove = new javax.swing.JButton();
        Current = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        Off_list = new javax.swing.JList<>();
        Refresh = new javax.swing.JButton();
        JROOM1 = new javax.swing.JFrame();
        Send_room1 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        Members = new javax.swing.JTextArea();
        Message_room1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        Display_room1 = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        FRIENDS_LIST = new javax.swing.JFrame();
        jScrollPane7 = new javax.swing.JScrollPane();
        Friends_list = new javax.swing.JList<>();
        Addfriend = new javax.swing.JButton();
        Unfriend = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        ADD_FRIEND = new javax.swing.JFrame();
        IDName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        BLACK_LIST = new javax.swing.JFrame();
        jScrollPane8 = new javax.swing.JScrollPane();
        Black_box = new javax.swing.JList<>();
        Addfriend1 = new javax.swing.JButton();
        Unfriend1 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        BLOCK = new javax.swing.JFrame();
        IDName1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        GUESTS_LIST = new javax.swing.JFrame();
        jScrollPane10 = new javax.swing.JScrollPane();
        Invite_list = new javax.swing.JList<>();
        Unfriend2 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        Authen = new javax.swing.JButton();
        Connect = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        Display1 = new javax.swing.JTextArea();
        Disconnect = new javax.swing.JButton();
        Clear = new javax.swing.JButton();
        JMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        Exit = new javax.swing.JMenuItem();
        Features = new javax.swing.JMenu();
        News = new javax.swing.JMenuItem();
        Virtual_Assistant = new javax.swing.JMenuItem();
        Friend_list = new javax.swing.JMenuItem();
        Online_list = new javax.swing.JMenuItem();
        Black_list = new javax.swing.JMenuItem();
        Setting = new javax.swing.JMenu();
        Change_IP_and_Port = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        Help = new javax.swing.JMenu();
        About = new javax.swing.JMenuItem();

        IP_AND_PORT.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        IP_AND_PORT.setTitle("IP and Port");
        IP_AND_PORT.setAlwaysOnTop(true);
        IP_AND_PORT.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        IP_AND_PORT.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        IP_AND_PORT.setLocation(new java.awt.Point(0, 0));
        IP_AND_PORT.setLocationByPlatform(true);
        IP_AND_PORT.setMinimumSize(new java.awt.Dimension(420, 123));
        IP_AND_PORT.setResizable(false);
        IP_AND_PORT.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                IP_AND_PORTWindowClosing(evt);
            }
        });

        Port1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Port1.setToolTipText("");
        Port1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Port1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel3.setText("IP");

        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel4.setText("Port");

        IP1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        IP1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IP1ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jButton6.setText("Cancel");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout IP_AND_PORTLayout = new javax.swing.GroupLayout(IP_AND_PORT.getContentPane());
        IP_AND_PORT.getContentPane().setLayout(IP_AND_PORTLayout);
        IP_AND_PORTLayout.setHorizontalGroup(
            IP_AND_PORTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(IP_AND_PORTLayout.createSequentialGroup()
                .addGroup(IP_AND_PORTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(IP_AND_PORTLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(IP1, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, IP_AND_PORTLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)))
                .addGroup(IP_AND_PORTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, IP_AND_PORTLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Port1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, IP_AND_PORTLayout.createSequentialGroup()
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67))))
        );
        IP_AND_PORTLayout.setVerticalGroup(
            IP_AND_PORTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, IP_AND_PORTLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(IP_AND_PORTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IP1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(Port1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addGroup(IP_AND_PORTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        LOGIN.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        LOGIN.setTitle("Dakhoo (Free edition)");
        LOGIN.setAlwaysOnTop(true);
        LOGIN.setLocationByPlatform(true);
        LOGIN.setMinimumSize(new java.awt.Dimension(530, 550));
        LOGIN.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);

        Authen1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        Authen1.setText("Login");
        Authen1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Authen1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(188, Short.MAX_VALUE)
                .addComponent(Authen1, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(181, 181, 181))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(331, Short.MAX_VALUE)
                .addComponent(Authen1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(129, 129, 129))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        LOGIN.getContentPane().add(jPanel1, gridBagConstraints);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/khang1/newpackage/18554920_1154996947940185_131312426_n.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        LOGIN.getContentPane().add(jLabel8, gridBagConstraints);

        NEWS.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        NEWS.setTitle("News");
        NEWS.setAlwaysOnTop(true);
        NEWS.setLocationByPlatform(true);
        NEWS.setMinimumSize(new java.awt.Dimension(400, 210));
        NEWS.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                NEWSWindowClosing(evt);
            }
        });

        Web.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Web.setText("http://www.tuoitre.vn");
        Web.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WebActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel1.setText("Enter your URL:");

        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jButton2.setText("OK");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jButton5.setText("Cancel");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout NEWSLayout = new javax.swing.GroupLayout(NEWS.getContentPane());
        NEWS.getContentPane().setLayout(NEWSLayout);
        NEWSLayout.setHorizontalGroup(
            NEWSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NEWSLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Web)
                .addContainerGap())
            .addGroup(NEWSLayout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(71, Short.MAX_VALUE))
        );
        NEWSLayout.setVerticalGroup(
            NEWSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NEWSLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(NEWSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(Web, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(NEWSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ASK_ME.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        ASK_ME.setTitle("Virtual Assistant");
        ASK_ME.setAlwaysOnTop(true);
        ASK_ME.setLocationByPlatform(true);
        ASK_ME.setMinimumSize(new java.awt.Dimension(450, 285));
        ASK_ME.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                ASK_MEWindowClosing(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel2.setText("Question:");

        jButton3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jButton3.setText("OK");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        Ask.setColumns(20);
        Ask.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Ask.setRows(5);
        jScrollPane1.setViewportView(Ask);

        jButton4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jButton4.setText("Cancel");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ASK_MELayout = new javax.swing.GroupLayout(ASK_ME.getContentPane());
        ASK_ME.getContentPane().setLayout(ASK_MELayout);
        ASK_MELayout.setHorizontalGroup(
            ASK_MELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ASK_MELayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addGap(17, 17, 17))
            .addGroup(ASK_MELayout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
        );
        ASK_MELayout.setVerticalGroup(
            ASK_MELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ASK_MELayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(ASK_MELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ASK_MELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton4)
                    .addComponent(jButton3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        CHAT_ROOM.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        CHAT_ROOM.setTitle("Chat room");
        CHAT_ROOM.setAlwaysOnTop(true);
        CHAT_ROOM.setLocationByPlatform(true);
        CHAT_ROOM.setMinimumSize(new java.awt.Dimension(445, 445));
        CHAT_ROOM.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                CHAT_ROOMWindowClosing(evt);
            }
        });

        Onl_list.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.MatteBorder(null), "Online user(s)", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 3, 15))); // NOI18N
        Onl_list.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jScrollPane3.setViewportView(Onl_list);

        Private_list.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.MatteBorder(null), "Room mem", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 3, 15))); // NOI18N
        Private_list.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jScrollPane5.setViewportView(Private_list);

        Add.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Add.setText("Add");
        Add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddActionPerformed(evt);
            }
        });

        Remove.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Remove.setText("Remove");
        Remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveActionPerformed(evt);
            }
        });

        Current.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Current.setText("Current room");
        Current.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CurrentActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jButton8.setText("Cancel");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jButton9.setText("OK");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        Off_list.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.MatteBorder(null), "Offline user(s)", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 3, 15))); // NOI18N
        Off_list.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jScrollPane9.setViewportView(Off_list);

        Refresh.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Refresh.setText("Refresh");
        Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout CHAT_ROOMLayout = new javax.swing.GroupLayout(CHAT_ROOM.getContentPane());
        CHAT_ROOM.getContentPane().setLayout(CHAT_ROOMLayout);
        CHAT_ROOMLayout.setHorizontalGroup(
            CHAT_ROOMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CHAT_ROOMLayout.createSequentialGroup()
                .addGroup(CHAT_ROOMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CHAT_ROOMLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                        .addComponent(Current)
                        .addGap(2, 2, 2))
                    .addGroup(CHAT_ROOMLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(CHAT_ROOMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(CHAT_ROOMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Add, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Remove, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(Refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)))
                .addGroup(CHAT_ROOMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(CHAT_ROOMLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30))
        );
        CHAT_ROOMLayout.setVerticalGroup(
            CHAT_ROOMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CHAT_ROOMLayout.createSequentialGroup()
                .addContainerGap(39, Short.MAX_VALUE)
                .addGroup(CHAT_ROOMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CHAT_ROOMLayout.createSequentialGroup()
                        .addGroup(CHAT_ROOMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(CHAT_ROOMLayout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CHAT_ROOMLayout.createSequentialGroup()
                        .addComponent(Add, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(Refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55)
                        .addComponent(Remove, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)))
                .addGroup(CHAT_ROOMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Current, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        JROOM1.setTitle("Private chat room 1");
        JROOM1.setAlwaysOnTop(true);
        JROOM1.setLocationByPlatform(true);
        JROOM1.setMinimumSize(new java.awt.Dimension(570, 430));
        JROOM1.setResizable(false);
        JROOM1.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                JROOM1formWindowClosing(evt);
            }
        });

        Send_room1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Send_room1.setText("Send");
        Send_room1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Send_room1ActionPerformed(evt);
            }
        });

        Members.setEditable(false);
        Members.setColumns(20);
        Members.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Members.setRows(5);
        Members.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Members.setDropMode(javax.swing.DropMode.INSERT);
        jScrollPane4.setViewportView(Members);

        Message_room1.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Message_room1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Message_room1ActionPerformed(evt);
            }
        });
        Message_room1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Message_room1KeyPressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel5.setText("Members");

        Display_room1.setEditable(false);
        Display_room1.setColumns(20);
        Display_room1.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Display_room1.setRows(5);
        Display_room1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        Display_room1.setDropMode(javax.swing.DropMode.INSERT);
        jScrollPane6.setViewportView(Display_room1);

        jMenu2.setText("File");
        jMenu2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItem3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jMenuItem3.setText("Invite");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItem2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jMenuItem2.setText("Left room");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        JROOM1.setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout JROOM1Layout = new javax.swing.GroupLayout(JROOM1.getContentPane());
        JROOM1.getContentPane().setLayout(JROOM1Layout);
        JROOM1Layout.setHorizontalGroup(
            JROOM1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JROOM1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(JROOM1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JROOM1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(13, 13, 13)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JROOM1Layout.createSequentialGroup()
                        .addComponent(Message_room1)
                        .addGap(18, 18, 18)
                        .addComponent(Send_room1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        JROOM1Layout.setVerticalGroup(
            JROOM1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JROOM1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(JROOM1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(JROOM1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(JROOM1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Send_room1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Message_room1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        FRIENDS_LIST.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        FRIENDS_LIST.setTitle("Friends list");
        FRIENDS_LIST.setAlwaysOnTop(true);
        FRIENDS_LIST.setLocationByPlatform(true);
        FRIENDS_LIST.setMinimumSize(new java.awt.Dimension(385, 430));
        FRIENDS_LIST.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                FRIENDS_LISTWindowClosing(evt);
            }
        });

        Friends_list.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.MatteBorder(null), "Friends list", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 3, 15))); // NOI18N
        Friends_list.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jScrollPane7.setViewportView(Friends_list);

        Addfriend.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Addfriend.setText("Add");
        Addfriend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddfriendActionPerformed(evt);
            }
        });

        Unfriend.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Unfriend.setText("Remove");
        Unfriend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UnfriendActionPerformed(evt);
            }
        });

        jButton10.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jButton10.setText("OK");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout FRIENDS_LISTLayout = new javax.swing.GroupLayout(FRIENDS_LIST.getContentPane());
        FRIENDS_LIST.getContentPane().setLayout(FRIENDS_LISTLayout);
        FRIENDS_LISTLayout.setHorizontalGroup(
            FRIENDS_LISTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FRIENDS_LISTLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Addfriend, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Unfriend, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FRIENDS_LISTLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(132, 132, 132))
        );
        FRIENDS_LISTLayout.setVerticalGroup(
            FRIENDS_LISTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FRIENDS_LISTLayout.createSequentialGroup()
                .addGroup(FRIENDS_LISTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FRIENDS_LISTLayout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(Addfriend, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(FRIENDS_LISTLayout.createSequentialGroup()
                        .addGap(158, 158, 158)
                        .addComponent(Unfriend, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(FRIENDS_LISTLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        ADD_FRIEND.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        ADD_FRIEND.setTitle("Add friend");
        ADD_FRIEND.setAlwaysOnTop(true);
        ADD_FRIEND.setLocationByPlatform(true);
        ADD_FRIEND.setMinimumSize(new java.awt.Dimension(400, 210));
        ADD_FRIEND.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                ADD_FRIENDWindowClosing(evt);
            }
        });

        IDName.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        IDName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IDNameActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel6.setText("Your friend name:");

        jButton7.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jButton7.setText("OK");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton12.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jButton12.setText("Cancel");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ADD_FRIENDLayout = new javax.swing.GroupLayout(ADD_FRIEND.getContentPane());
        ADD_FRIEND.getContentPane().setLayout(ADD_FRIENDLayout);
        ADD_FRIENDLayout.setHorizontalGroup(
            ADD_FRIENDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ADD_FRIENDLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(IDName)
                .addContainerGap())
            .addGroup(ADD_FRIENDLayout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(89, Short.MAX_VALUE))
        );
        ADD_FRIENDLayout.setVerticalGroup(
            ADD_FRIENDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ADD_FRIENDLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(ADD_FRIENDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(IDName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(ADD_FRIENDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7)
                    .addComponent(jButton12))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        BLACK_LIST.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        BLACK_LIST.setTitle("Black list");
        BLACK_LIST.setAlwaysOnTop(true);
        BLACK_LIST.setLocationByPlatform(true);
        BLACK_LIST.setMinimumSize(new java.awt.Dimension(385, 430));
        BLACK_LIST.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                BLACK_LISTWindowClosing(evt);
            }
        });

        Black_box.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.MatteBorder(null), "Black list", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 3, 15))); // NOI18N
        Black_box.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jScrollPane8.setViewportView(Black_box);

        Addfriend1.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Addfriend1.setText("Add");
        Addfriend1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Addfriend1ActionPerformed(evt);
            }
        });

        Unfriend1.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Unfriend1.setText("Remove");
        Unfriend1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Unfriend1ActionPerformed(evt);
            }
        });

        jButton11.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jButton11.setText("OK");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout BLACK_LISTLayout = new javax.swing.GroupLayout(BLACK_LIST.getContentPane());
        BLACK_LIST.getContentPane().setLayout(BLACK_LISTLayout);
        BLACK_LISTLayout.setHorizontalGroup(
            BLACK_LISTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BLACK_LISTLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Addfriend1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(BLACK_LISTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BLACK_LISTLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(BLACK_LISTLayout.createSequentialGroup()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Unfriend1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        BLACK_LISTLayout.setVerticalGroup(
            BLACK_LISTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BLACK_LISTLayout.createSequentialGroup()
                .addGroup(BLACK_LISTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BLACK_LISTLayout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(Addfriend1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(BLACK_LISTLayout.createSequentialGroup()
                        .addGap(158, 158, 158)
                        .addComponent(Unfriend1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(BLACK_LISTLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        BLOCK.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        BLOCK.setTitle("Block");
        BLOCK.setAlwaysOnTop(true);
        BLOCK.setLocationByPlatform(true);
        BLOCK.setMinimumSize(new java.awt.Dimension(400, 210));
        BLOCK.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                BLOCKWindowClosing(evt);
            }
        });

        IDName1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        IDName1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IDName1ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel7.setText("Block:");

        jButton13.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jButton13.setText("OK");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jButton14.setText("Cancel");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout BLOCKLayout = new javax.swing.GroupLayout(BLOCK.getContentPane());
        BLOCK.getContentPane().setLayout(BLOCKLayout);
        BLOCKLayout.setHorizontalGroup(
            BLOCKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BLOCKLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(IDName1)
                .addContainerGap())
            .addGroup(BLOCKLayout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(89, Short.MAX_VALUE))
        );
        BLOCKLayout.setVerticalGroup(
            BLOCKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BLOCKLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(BLOCKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(IDName1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(BLOCKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton13)
                    .addComponent(jButton14))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GUESTS_LIST.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        GUESTS_LIST.setTitle("Friends list");
        GUESTS_LIST.setAlwaysOnTop(true);
        GUESTS_LIST.setLocationByPlatform(true);
        GUESTS_LIST.setMinimumSize(new java.awt.Dimension(385, 430));
        GUESTS_LIST.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                GUESTS_LISTWindowClosing(evt);
            }
        });

        Invite_list.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.MatteBorder(null), "Guests list", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 3, 15))); // NOI18N
        Invite_list.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jScrollPane10.setViewportView(Invite_list);

        Unfriend2.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Unfriend2.setText("Cancel");
        Unfriend2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Unfriend2ActionPerformed(evt);
            }
        });

        jButton15.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jButton15.setText("OK");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout GUESTS_LISTLayout = new javax.swing.GroupLayout(GUESTS_LIST.getContentPane());
        GUESTS_LIST.getContentPane().setLayout(GUESTS_LISTLayout);
        GUESTS_LISTLayout.setHorizontalGroup(
            GUESTS_LISTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(GUESTS_LISTLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(GUESTS_LISTLayout.createSequentialGroup()
                .addContainerGap(61, Short.MAX_VALUE)
                .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(Unfriend2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65))
        );
        GUESTS_LISTLayout.setVerticalGroup(
            GUESTS_LISTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(GUESTS_LISTLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(GUESTS_LISTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Unfriend2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        Authen.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Authen.setText("Login");
        Authen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AuthenActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setAlwaysOnTop(true);
        setLocation(new java.awt.Point(500, 500));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        Connect.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Connect.setText("Connect");
        Connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConnectActionPerformed(evt);
            }
        });

        Display1.setEditable(false);
        Display1.setColumns(20);
        Display1.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        Display1.setRows(5);
        Display1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        Display1.setDropMode(javax.swing.DropMode.INSERT);
        jScrollPane2.setViewportView(Display1);

        Disconnect.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Disconnect.setText("Disconnect");
        Disconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisconnectActionPerformed(evt);
            }
        });

        Clear.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Clear.setText("Clear");
        Clear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ClearMouseEntered(evt);
            }
        });
        Clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearActionPerformed(evt);
            }
        });

        jMenu1.setText("File");
        jMenu1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItem1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jMenuItem1.setText("My IP Address");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItem4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jMenuItem4.setText("Sign out");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        Exit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.SHIFT_MASK));
        Exit.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Exit.setText("Exit");
        Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitActionPerformed(evt);
            }
        });
        jMenu1.add(Exit);

        JMenuBar1.add(jMenu1);

        Features.setText("Features");
        Features.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        News.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.SHIFT_MASK));
        News.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        News.setText("News");
        News.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewsActionPerformed(evt);
            }
        });
        Features.add(News);

        Virtual_Assistant.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.SHIFT_MASK));
        Virtual_Assistant.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Virtual_Assistant.setText("Virtual Assistant");
        Virtual_Assistant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Virtual_AssistantActionPerformed(evt);
            }
        });
        Features.add(Virtual_Assistant);

        Friend_list.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK));
        Friend_list.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Friend_list.setText("Friends List");
        Friend_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Friend_listActionPerformed(evt);
            }
        });
        Features.add(Friend_list);

        Online_list.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_MASK));
        Online_list.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Online_list.setText("Chat Room (Limited)");
        Online_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Online_listActionPerformed(evt);
            }
        });
        Features.add(Online_list);

        Black_list.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK));
        Black_list.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Black_list.setText("Black List");
        Black_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Black_listActionPerformed(evt);
            }
        });
        Features.add(Black_list);

        JMenuBar1.add(Features);

        Setting.setText("Setting");
        Setting.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        Change_IP_and_Port.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_MASK));
        Change_IP_and_Port.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Change_IP_and_Port.setText("Change IP and Port");
        Change_IP_and_Port.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Change_IP_and_PortActionPerformed(evt);
            }
        });
        Setting.add(Change_IP_and_Port);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItem5.setText("Your Access Token (Premium only)");
        jMenuItem5.setEnabled(false);
        Setting.add(jMenuItem5);

        JMenuBar1.add(Setting);

        Help.setText("Help");
        Help.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        About.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK));
        About.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        About.setText("About");
        Help.add(About);

        JMenuBar1.add(Help);

        setJMenuBar(JMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(72, 72, 72)
                        .addComponent(Connect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                        .addComponent(Clear)
                        .addGap(62, 62, 62)
                        .addComponent(Disconnect)
                        .addGap(64, 64, 64)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Connect)
                    .addComponent(Clear)
                    .addComponent(Disconnect))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConnectActionPerformed
    if (stt==false)
    {
        try 
        {
        socket = new Socket(IP,PortNum);
        InputStreamReader streamreader = new InputStreamReader(socket.getInputStream());
        reader = new BufferedReader(streamreader);
        writer = new PrintWriter(socket.getOutputStream());
            
            {    
            String anon="TheK";
            Random generator = new Random(); 
            int i = generator.nextInt(999) + 1;
            String is=String.valueOf(i);
            anon=anon.concat(is);
            Name=anon;
            //Name="TheK792";
            }
            
        writer.println(Name + ": :Connect");
        writer.flush();
        if (reader.readLine()!=null)
        {
        stt=true;
        Dialog.showMessageDialog(this, "Connect sucessfully.","Information",JOptionPane.INFORMATION_MESSAGE);
        if (!Name.equals(tempName))
        {
            friends_box.clear();
            friends.clear();
            block_box.clear();
            black_list.clear();
        }
        Online_list.setEnabled(true);
        Friend_list.setEnabled(true);
        Black_list.setEnabled(true);
   /**/ this.setTitle(Name+" - Free edition");
        ListenThread();
        }
        else
        {
        Dialog.showMessageDialog(this, "Failed to make connection.","Information",JOptionPane.INFORMATION_MESSAGE);    
        }
        }     
        catch (UnknownHostException ex1)
        {
        Dialog.showMessageDialog(this, "Unknown IP.","Warning",JOptionPane.WARNING_MESSAGE);
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1);
        }
        catch (IOException ex) 
        {
        try 
        {
            Dialog.showMessageDialog(this, "There are no available servers at this site right now.","Information",JOptionPane.INFORMATION_MESSAGE);
            if (stt==true)
            {
                if (socket!=null)
                {
            socket.close();
            socket=null;
                }
            stt=false;
            onl_users.clear();
            off_users.clear();
            }
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex1) 
        {
            Dialog.showMessageDialog(this, "Unknown error, please try again later!","Error",JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }
    }
    else
    {
        Display1.append("You are already connected. \n");
        Display1.setCaretPosition(Display1.getDocument().getLength());
    }
    }//GEN-LAST:event_ConnectActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    try {
        currip = InetAddress.getLocalHost();
        Dialog.showMessageDialog(this, "Current IP address: "+currip.getHostAddress(),"Information",JOptionPane.INFORMATION_MESSAGE);// TODO add your handling code here:
    } catch (UnknownHostException ex) {
        Dialog.showMessageDialog(this, "Unknown error. Please try again later!","Error",JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
    }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void DisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisconnectActionPerformed
        if (stt==true)
        {
            int k = Confirm();
            if (k==0)
            {
                try {
                    sendDisconnect();
                    Thread.sleep(100);
                    Disconnect();
                    stt=false;
                    onl_users.clear();
                    off_users.clear();
                    Online_list.setEnabled(false);
                    Friend_list.setEnabled(false);
                    Black_list.setEnabled(false);
                    tempName = Name;
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                Dialog.showMessageDialog(this, "Be careful in the next time, sir!","Warning",JOptionPane.WARNING_MESSAGE);
            }
        }
        else
        {
            Dialog.showMessageDialog(this, "There is already no connection to be closed.","Warning",JOptionPane.WARNING_MESSAGE);    
        }
    }//GEN-LAST:event_DisconnectActionPerformed

    private void ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitActionPerformed
        int k = Confirm();
        if (k==0)
        {
            if (stt==true)
            {
            try {
                sendDisconnect();
                Thread.sleep(100);
                Disconnect();
                stt=false;
                onl_users.clear();  
                off_users.clear();
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
            System.exit(0);
        }
          else
        {
        Dialog.showMessageDialog(this, "Be careful in the next time, sir!","Warning",JOptionPane.WARNING_MESSAGE);      
        }// TODO add your handling code here:
    }//GEN-LAST:event_ExitActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String PortStr =Port1.getText().trim();
        try
        {
            if (stt==false)
            {
                if (!"".equals(IP1.getText().trim())&&!"".equals(PortStr))
                {
                IP = IP1.getText().trim();
                PortNum = Integer.parseInt(PortStr);
                IP_AND_PORT.dispose();
                this.setEnabled(true);
                }
                else
                {
                Dialog.showMessageDialog(this, "You missed one or more required inputs.","Warning",JOptionPane.WARNING_MESSAGE);
                }
            }
            else
            {
                if (!"".equals(IP1.getText().trim())&&!"".equals(PortStr))
                {
                    if (!IP.equals(IP1.getText().trim())||PortNum!=Integer.parseInt(PortStr))
                    {
                    Dialog.showMessageDialog(this, "Please disconnect at first, sir!","Warning",JOptionPane.WARNING_MESSAGE);
                    }   
                IP_AND_PORT.dispose();
                this.setEnabled(true);
                }
                else
                {
                Dialog.showMessageDialog(this, "You missed one or more required inputs.","Warning",JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        catch (NumberFormatException ex1)
        {
        Dialog.showMessageDialog(this, "Wrong port number format, madam!","Warning",JOptionPane.WARNING_MESSAGE);
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void Change_IP_and_PortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Change_IP_and_PortActionPerformed
        IP_AND_PORT.setVisible(true);
        IP_AND_PORT.toFront();
        this.setEnabled(false);
        IP_AND_PORT.setLocation(dim.width/2-IP_AND_PORT.getSize().width/2, dim.height/2-IP_AND_PORT.getSize().height/2);
        
        IP1.setText(IP);
        Port1.setText(Integer.toString(PortNum));
                // TODO add your handling code here:
    }//GEN-LAST:event_Change_IP_and_PortActionPerformed

    private void AuthenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AuthenActionPerformed
        try{
        String domain = "https://goo.gl/HbmiOF";
        String appId = "1953292198245796";
        String authUrl = "https://graph.facebook.com/oauth/authorize?type=user_agent&client_id="+appId+"&redirect_uri="+domain+"&scope=user_about_me,"
                + "user_actions.books,user_actions.fitness,user_actions.music,user_actions.news,user_actions.video,user_birthday,user_education_history,"
                + "user_events,user_photos,user_friends,user_games_activity,user_hometown,user_likes,user_location,user_photos,user_relationship_details,"
                + "user_relationships,user_religion_politics,user_status,user_tagged_places,user_videos,user_website,user_work_history,"
                + "manage_pages,publish_actions,read_insights,read_page_mailboxes,rsvp_event";
        // user_activities, user_groups, user_interests, manage_notifications, read_mailbox, read_stream
        System.setProperty("webdirver.chrome.driver", "chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get(authUrl);
        String accessToken;
        while(true){
            if(driver.getCurrentUrl().contains("https://www.facebook.com/KhanhTrang1996"))
            {
            String url = driver.getCurrentUrl();
            accessToken = url.replaceAll(".*#access_token=(.+)&.*", "$1");
            driver.quit();
            FacebookClient fbClient = new DefaultFacebookClient(accessToken);
            User user = fbClient.fetchObject("me",User.class);
            Name=user.getName();
            LOGIN.dispose();
            this.setTitle(Name+" - Free edition");
            this.setVisible(true);
            this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
            break;
            }
        }
    }catch (WebDriverException e){
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e); 
        }
        catch(Exception ex1){
            Dialog.showMessageDialog(this, "Unknown error, please try again later!","Error",JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1); 
        }
    }//GEN-LAST:event_AuthenActionPerformed

    private void NewsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewsActionPerformed
        NEWS.setVisible(true);
        NEWS.toFront();
        this.setEnabled(false);
        NEWS.setLocation(dim.width/2-NEWS.getSize().width/2, dim.height/2-NEWS.getSize().height/2);
    }//GEN-LAST:event_NewsActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        if (!"".equals(Web.getText().trim()))
        {
        try {
        final HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(Web.getText().trim()));
        final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
        String content = CommonExtractors.CANOLA_EXTRACTOR.getText(doc);//ARTICLE,DEFAULT,KEEPEVERYTHING,LARGESTCONTENT,CANOLA
        Display1.append(content+"\n");
        this.setEnabled(true);
        NEWS.dispose();
    } catch (MalformedURLException ex) {
        Dialog.showMessageDialog(this, "Malformed URL!","Error",JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException | SAXException | BoilerpipeProcessingException ex) {
        Dialog.showMessageDialog(this, "Unknown or processing error.","Error",JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
    }finally{
        this.setEnabled(true);
        NEWS.dispose();
        Display1.setCaretPosition(Display1.getDocument().getLength());
        }
        }
        else{
         Dialog.showMessageDialog(this, "No input ?!","Warning",JOptionPane.WARNING_MESSAGE);   
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void Virtual_AssistantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Virtual_AssistantActionPerformed

       ASK_ME.toFront();
       ASK_ME.setVisible(true);
       this.setEnabled(false);
       ASK_ME.setLocation(dim.width/2-ASK_ME.getSize().width/2, dim.height/2-ASK_ME.getSize().height/2);
    }//GEN-LAST:event_Virtual_AssistantActionPerformed

    private void Port1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Port1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Port1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        /*if (args.length > 0)
            input = args[0];*/
        // The WAEngine is a factory for creating WAQuery objects,
        // and it also used to perform those queries. You can set properties of
        // the WAEngine (such as the desired API output format types) that will
        // be inherited by all WAQuery objects created from it. Most applications
        // will only need to crete one WAEngine object, which is used throughout
        // the life of the application.
        WAEngine engine = new WAEngine();
        // These properties will be set in all the WAQuery objects created from this WAEngine.
        engine.setAppID("HKJ625-7WW2LHG5QA");
        engine.addFormat("plaintext");
        // Create the query.
        WAQuery query = engine.createQuery();
        // Set properties of the query.
        if (!"".equals(Ask.getText().trim())){
        query.setInput(Ask.getText().trim());
        try {
            // For educational purposes, print out the URL we are about to send:
            /*Display1.append("Query URL:");
            Display1.append(engine.toURL(query)+"\n");
            Display1.append("\n");Display1.setCaretPosition(Display1.getDocument().getLength());*/
            // This sends the URL to the Wolfram|Alpha server, gets the XML result
            // and parses it into an object hierarchy held by the WAQueryResult object.
            WAQueryResult queryResult = engine.performQuery(query);
            if (queryResult.isError()) {
                Dialog.showMessageDialog(this,"Query error: code: "+queryResult.getErrorCode()+"\nMessage: " + queryResult.getErrorMessage()+"\n","Error",JOptionPane.ERROR_MESSAGE);
            /*  Display1.append("Error: ");
                Display1.append("Code: " + queryResult.getErrorCode()+"\n");
                Display1.append("Message: " + queryResult.getErrorMessage()+"\n");Display1.setCaretPosition(Display1.getDocument().getLength());*/
            } else if (!queryResult.isSuccess()) {
                Display1.append("I'm sorry! Your question was not understood or no results available."+"\n");
            } else {
                // Got a result.
                //Display1.append("Successful query! Pods follow:\n");Display1.setCaretPosition(Display1.getDocument().getLength());
                int i=0;
                for (WAPod pod : queryResult.getPods()) 
                {
                    if (i==0)
                    {
                        i++;
                        continue;                       
                    }
                    if (!pod.isError()) 
                    {
                        Display1.append("--------------------------------------------------------------"+"\n");
                        Display1.append("ANSWER:\n");
                        for (WASubpod subpod : pod.getSubpods()) 
                        {
                            for (Object element : subpod.getContents()) 
                            {
                                if (element instanceof WAPlainText) 
                                {
                                    Display1.append(((WAPlainText) element).getText()+"\n");
                                }
                            }
                        }
                        Display1.append("--------------------------------------------------------------"+"\n");
                    }
                }

                }
                Display1.setCaretPosition(Display1.getDocument().getLength());
                // Ignored many other types of Wolfram|Alpha output, such as warnings, assumptions, etc.
                // These can be obtained by methods of WAQueryResult or objects deeper in the hierarchy.
        } catch (WAException e) {
            Dialog.showMessageDialog(this, "Unknown error, please try again later!","Error",JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
        }finally{
        this.setEnabled(true);
        ASK_ME.dispose();
        Display1.setCaretPosition(Display1.getDocument().getLength());
        }
        }
        else
        {
            Dialog.showMessageDialog(this, "No input ?!","Warning",JOptionPane.WARNING_MESSAGE);
        }
// TODO add your handling code here:// TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        this.setEnabled(true);
        ASK_ME.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        this.setEnabled(true);
        NEWS.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        this.setEnabled(true);
        IP_AND_PORT.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed
 
    private void WebActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WebActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_WebActionPerformed
    
    private void Online_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Online_listActionPerformed
        
        writer.println(Name+"::Update");
        writer.flush();
        try 
        {
        Thread.sleep(100);
        } 
        catch (InterruptedException ex) 
        {
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        CHAT_ROOM.setVisible(true);
        CHAT_ROOM.toFront();
        this.setEnabled(false);
        CHAT_ROOM.setLocation(dim.width/2-CHAT_ROOM.getSize().width/2, dim.height/2-CHAT_ROOM.getSize().height/2);
        
        if (room1.isEmpty())
        {
            Current.setEnabled(false);
        }
        else
        {
            Current.setEnabled(true);
        }
        
        source.clear();
        source2.clear();
        drain.clear();
        onl_users.forEach((current_user) -> {
            if (!current_user.equals(Name))
            {
            source.addElement(current_user);
            }
        });
        off_users.forEach((current_user) -> {
            if (!current_user.equals(Name))
            {
            source2.addElement(current_user);
            }
        });
        Onl_list.setModel(source);
        Off_list.setModel(source2);
    }//GEN-LAST:event_Online_listActionPerformed

    private void IP1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IP1ActionPerformed
    // TODO add your handling code here:
    }//GEN-LAST:event_IP1ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int k = Confirm();
        if (k==0)
        {
            if (stt==true)
            {
            try 
            {
                sendDisconnect();
                Thread.sleep(100);
                Disconnect();
                stt=false;
                onl_users.clear();
                off_users.clear();
            } catch (InterruptedException ex) 
            {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
            System.exit(0);
        }
        else
        {
        Dialog.showMessageDialog(this, "Be careful in the next time, sir!","Warning",JOptionPane.WARNING_MESSAGE);      
        }// TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosing

    private void IP_AND_PORTWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_IP_AND_PORTWindowClosing
        this.setEnabled(true);
        IP_AND_PORT.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_IP_AND_PORTWindowClosing

    private void NEWSWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_NEWSWindowClosing
        this.setEnabled(true);
        NEWS.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_NEWSWindowClosing

    private void ASK_MEWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_ASK_MEWindowClosing
        this.setEnabled(true);
        ASK_ME.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_ASK_MEWindowClosing

    private void AddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddActionPerformed

        chosen_ones = Onl_list.getSelectedValuesList();
        ListIterator it = chosen_ones.listIterator();
        while (it.hasNext())
        {
            Object temp = it.next();
            source.removeElement(temp);
            drain.addElement(temp);
        }
        chosen_ones = Off_list.getSelectedValuesList();
        it = chosen_ones.listIterator();
        while (it.hasNext())
        {
            Object temp = it.next();
            source2.removeElement(temp);
            drain.addElement(temp);
        }
        Private_list.setModel(drain);
    }//GEN-LAST:event_AddActionPerformed

    private void Friend_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Friend_listActionPerformed
        FRIENDS_LIST.setVisible(true);
        FRIENDS_LIST.toFront();
        this.setEnabled(false);
        FRIENDS_LIST.setLocation(dim.width/2-FRIENDS_LIST.getSize().width/2, dim.height/2-FRIENDS_LIST.getSize().height/2);
        
        friends_box.clear();
        friends.forEach((current_friend) -> {
            friends_box.addElement(current_friend);
    });
        Friends_list.setModel(friends_box);
    }//GEN-LAST:event_Friend_listActionPerformed

    private void CHAT_ROOMWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_CHAT_ROOMWindowClosing
        this.setEnabled(true);
        CHAT_ROOM.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_CHAT_ROOMWindowClosing

    private void RemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveActionPerformed
        chosen_ones = Private_list.getSelectedValuesList();
        ListIterator it = chosen_ones.listIterator();
        while (it.hasNext())
        {
            Object temp = it.next();
            drain.removeElement(temp);
            if (onl_users.contains(temp))
            {
            source.addElement(temp);
            }
            else if (off_users.contains(temp))
            {
            source2.addElement(temp);    
            }
        }
        
    }//GEN-LAST:event_RemoveActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed

        if (Private_list.getModel().getSize()>0)
        {
            if (room1.isEmpty())
            {
                int size = Private_list.getModel().getSize();
                //System.out.println(size);
                Members.append(Name+", ");
                    for (int i=0; i<size;i++)
                    {
                if (!Private_list.getModel().getElementAt(i).equals(Name))
                {
                    Members.append(Private_list.getModel().getElementAt(i)+", ");
                }
                room1.add(Private_list.getModel().getElementAt(i));
                    }
                if (!room1.contains(Name))
                {
                    room1.add(Name);
                }
                if (room1.size()<2)
                {
                    room1.clear();
                    busy_users.clear();
                    Members.setText("");
                    Display_room1.setText("");
                    JROOM1.setVisible(false);
                }
                else
                {
                    //JROOM1.setVisible(true);
                    JROOM1.setTitle(Name);
                    ListIterator it = room1.listIterator();
                    Display_room1.setText("");
                    while (it.hasNext()) 
                    {
                 writer.println(Name + "::Confirm:" + it.next().toString()+"-End:"+Members.getText());       
                 //writer.println(Name + ":Welcome to my private chat room!:Private:" + it.next().toString()+"-End" + ":"+Members.getText());
                 writer.flush();
                    }
                 writer.println(Name + "::Confirm end:"+Members.getText());
                 writer.flush();
                 //System.out.println("Before:"+room1);
                }
            }
            else
            {
            Dialog.showMessageDialog(this, "You have no private chat room left in free edition.","Warning",JOptionPane.WARNING_MESSAGE);
            }
        }
        CHAT_ROOM.dispose();
        this.setEnabled(true);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        this.setEnabled(true);
        CHAT_ROOM.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void Send_room1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Send_room1ActionPerformed
        if (stt==true)
    {
         if (!"".equals(Message_room1.getText().trim()))
         {
             try
             {
                 ListIterator it = room1.listIterator();
                 while (it.hasNext()) 
                 {
                 writer.println(Name + ":" + Message_room1.getText().trim() + ":Private:" + it.next().toString()+"-End" + ":"+Members.getText());
                 writer.flush();
                 }
                 Message_room1.setText("");
             }
             catch (Exception e)
             {
                 Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
             }
         } 
         else
         {
             Message_room1.setText("");
             Message_room1.requestFocus();
         }
    }
    else
    {
       Dialog.showMessageDialog(this, "There is no connection right now.","Warning",JOptionPane.WARNING_MESSAGE); 
    }// TODO add your handling code here:
    }//GEN-LAST:event_Send_room1ActionPerformed

    private void JROOM1formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_JROOM1formWindowClosing
        if (room1.size()<2)
        {
            Dialog.showMessageDialog(this, "This room will be now close to free the memory.","Warning",JOptionPane.WARNING_MESSAGE);
            writer.println(Name + "::Unprivate:End-");
            writer.flush();
            room1.clear();
            busy_users.clear();
            Members.setText("");
            Display_room1.setText("");
        }
    }//GEN-LAST:event_JROOM1formWindowClosing

    private void Message_room1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Message_room1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Message_room1ActionPerformed

    private void Message_room1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Message_room1KeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if (stt==true)
            {
                if (!"".equals(Message_room1.getText().trim()))
              {
                try
                { 
                 ListIterator it = room1.listIterator();
                 while (it.hasNext()) 
                 {
                 writer.println(Name + ":" + Message_room1.getText().trim() + ":Private:" + it.next().toString()+"-End" + ":"+Members.getText());
                 writer.flush();
                 }
                 Message_room1.setText("");
                }
               catch (Exception e)
                {
                 Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
                }
              } 
            else
            {
             Message_room1.setText("");
             Message_room1.requestFocus();
            }
    }
    else
    {
       Dialog.showMessageDialog(this, "There is no connection right now.","Warning",JOptionPane.WARNING_MESSAGE); 
    }  
        }// TODO add your handling code here:
    }//GEN-LAST:event_Message_room1KeyPressed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        int n = Confirm();
        if (n==0)
        {
            if (room1.size()<2)
            {
            Dialog.showMessageDialog(this, "Because you are the only one in this room, the room will be now close to free the memory.","Warning",JOptionPane.WARNING_MESSAGE);
            writer.println(Name + "::Unprivate:End-");
            writer.flush();
            }
            else
            {
            room1.remove(Name);
            ListIterator it = room1.listIterator();
                 while (it.hasNext()) 
                 {
                 writer.println(Name + "::Unprivate:" + it.next().toString()+"-End");
                 writer.flush();
                 }
            }
            JROOM1.setVisible(false);
            room1.clear();
            busy_users.clear();
            Members.setText("");
            Display_room1.setText("");
        }
        else
        {
        Dialog.showMessageDialog(this, "Be careful in the next time, sir!","Warning",JOptionPane.WARNING_MESSAGE);    
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void CurrentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CurrentActionPerformed
        CHAT_ROOM.dispose();
        JROOM1.setVisible(true);
        JROOM1.setEnabled(true);
        this.setEnabled(true);
        
        JROOM1.setLocation(dim.width/2-JROOM1.getSize().width/2, dim.height/2-JROOM1.getSize().height/2);
    }//GEN-LAST:event_CurrentActionPerformed

    private void AddfriendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddfriendActionPerformed
        FRIENDS_LIST.setEnabled(false);
        ADD_FRIEND.setVisible(true);
        ADD_FRIEND.toFront();
        ADD_FRIEND.setLocation(dim.width/2-ADD_FRIEND.getSize().width/2, dim.height/2-ADD_FRIEND.getSize().height/2);

    }//GEN-LAST:event_AddfriendActionPerformed

    private void UnfriendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UnfriendActionPerformed
        chosen_ones = Friends_list.getSelectedValuesList();
        ListIterator it = chosen_ones.listIterator();
        int k = Confirm();
        if (k==0)
        {
            while (it.hasNext())
            {
            Object temp = it.next();
            friends.remove(temp);
            writer.println(Name + ":" + temp + ":" + "Unfriend");
            writer.flush();
            }
            friends_box.clear();
            friends.forEach((current_friend) -> {
            friends_box.addElement(current_friend);
    });
        }
        else
        {
            Dialog.showMessageDialog(this, "Be careful in the next time, sir!","Warning",JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_UnfriendActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        FRIENDS_LIST.dispose();
        this.setEnabled(true);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void FRIENDS_LISTWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_FRIENDS_LISTWindowClosing
        this.setEnabled(true);
        FRIENDS_LIST.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_FRIENDS_LISTWindowClosing

    private void IDNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IDNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IDNameActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        if (!"".equals(IDName.getText().trim())&&!Name.equals(IDName.getText().trim())&&!black_list.contains(IDName.getText().trim()))
         {
             try
             {
                 if (!friends.contains(IDName.getText().trim()))
                 {
                 writer.println(Name + ":" + IDName.getText().trim() + ":" + "Addfriend");
                 writer.flush();
                 IDName.setText("");
                 ADD_FRIEND.dispose();
                 FRIENDS_LIST.setEnabled(true);
                 }
             }
             catch (Exception e)
             {
                 Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
             }
         } 
         
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        ADD_FRIEND.dispose();
        FRIENDS_LIST.setEnabled(true);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void ADD_FRIENDWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_ADD_FRIENDWindowClosing
        FRIENDS_LIST.setEnabled(true);
        ADD_FRIEND.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_ADD_FRIENDWindowClosing

    private void Addfriend1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Addfriend1ActionPerformed
        BLACK_LIST.setEnabled(false);
        BLOCK.setVisible(true);
        BLOCK.toFront();
        BLOCK.setLocation(dim.width/2-BLOCK.getSize().width/2, dim.height/2-BLOCK.getSize().height/2);
    }//GEN-LAST:event_Addfriend1ActionPerformed

    private void Unfriend1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Unfriend1ActionPerformed
        chosen_ones = Black_box.getSelectedValuesList();
        ListIterator it = chosen_ones.listIterator();
        int k = Confirm();
        if (k==0)
        {
            while (it.hasNext())
            {
            Object temp = it.next();
            black_list.remove(temp);
            }
            block_box.clear();
            black_list.forEach((current_friend) -> {
            block_box.addElement(current_friend);
    });
        }
        else
        {
            Dialog.showMessageDialog(this, "Be careful in the next time, sir!","Warning",JOptionPane.WARNING_MESSAGE);
        }// TODO add your handling code here:
    }//GEN-LAST:event_Unfriend1ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        this.setEnabled(true);
        BLACK_LIST.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_jButton11ActionPerformed

    private void BLACK_LISTWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_BLACK_LISTWindowClosing
        this.setEnabled(true);
        BLACK_LIST.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_BLACK_LISTWindowClosing

    private void Black_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Black_listActionPerformed
        BLACK_LIST.setVisible(true);
        BLACK_LIST.toFront();
        this.setEnabled(false);
        BLACK_LIST.setLocation(dim.width/2-BLACK_LIST.getSize().width/2, dim.height/2-BLACK_LIST.getSize().height/2);
        
        block_box.clear();
        black_list.forEach((current_block) -> {
            block_box.addElement(current_block);
    });
        Black_box.setModel(block_box);
    }//GEN-LAST:event_Black_listActionPerformed

    private void IDName1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IDName1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IDName1ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        if (!IDName1.getText().trim().equals("")&&!IDName1.getText().trim().equals(Name))
        {
            String tempName = IDName1.getText().trim();
            if (!black_list.contains(tempName))
            {
                black_list.add(tempName);
                if (friends.contains(tempName))
                {
                friends.remove(tempName);
                writer.println(Name + ":" + tempName + ":" + "Unfriend");
                writer.flush();
                }
                
                block_box.clear();
                black_list.forEach((current_friend) -> {
                block_box.addElement(current_friend);
                
                writer.println(Name + "::" + "Update");
                writer.flush();
            });
                Black_box.setModel(block_box);
                IDName1.setText("");
                BLACK_LIST.setEnabled(true);
                BLOCK.dispose();
            }
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void BLOCKWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_BLOCKWindowClosing
        BLACK_LIST.setEnabled(true);
        BLOCK.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_BLOCKWindowClosing

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        BLACK_LIST.setEnabled(true);
        BLOCK.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_jButton14ActionPerformed

    private void RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshActionPerformed
        writer.println(Name+"::Update");
        writer.flush();
        try 
        {
        Thread.sleep(100);
        } 
        catch (InterruptedException ex) 
        {
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        source.clear();
        source2.clear();
        drain.clear();
        onl_users.forEach((current_user) -> {
            if (!current_user.equals(Name))
            {
            source.addElement(current_user);
            }
        });
        off_users.forEach((current_user) -> {
            if (!current_user.equals(Name))
            {
            source2.addElement(current_user);
            }
        });
        Onl_list.setModel(source);
        Off_list.setModel(source2);
    }//GEN-LAST:event_RefreshActionPerformed

    private void ClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearActionPerformed
        Display1.setText("");
    }//GEN-LAST:event_ClearActionPerformed

    private void ClearMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ClearMouseEntered
        setCursor(new Cursor(Cursor.HAND_CURSOR));// TODO add your handling code here:
    }//GEN-LAST:event_ClearMouseEntered

    private void Unfriend2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Unfriend2ActionPerformed
        GUESTS_LIST.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_Unfriend2ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        chosen_ones = Invite_list.getSelectedValuesList();
        ListIterator it = chosen_ones.listIterator();
        while (it.hasNext())
        {
            writer.println(Name + "::Confirm invite:" + it.next() +"-End:"+Members.getText());       
            writer.flush();
        }
        writer.println(Name + "::End confirm invite: ");
        writer.flush();
        GUESTS_LIST.dispose();
    }//GEN-LAST:event_jButton15ActionPerformed

    private void GUESTS_LISTWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_GUESTS_LISTWindowClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_GUESTS_LISTWindowClosing

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        GUESTS_LIST.setVisible(true);
        GUESTS_LIST.toFront();
        GUESTS_LIST.setLocation(dim.width/2-GUESTS_LIST.getSize().width/2, dim.height/2-GUESTS_LIST.getSize().height/2);
        
        invite_box.clear();
        friends.forEach((current_friend) -> {
            if (!room1.contains(current_friend))
            {
            invite_box.addElement(current_friend);
            }
    });
        Invite_list.setModel(invite_box);// TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        try {
                if (stt==true)
                {
                    sendDisconnect();
                    Thread.sleep(100);
                    Disconnect();
                    stt=false;
                }
                    onl_users.clear();
                    off_users.clear();
                    Online_list.setEnabled(false);
                    Friend_list.setEnabled(false);
                    Black_list.setEnabled(false);
                    tempName = Name;
                    //JFrame main = new Client7_test();
                    //main.setVisible(true);
                    this.setVisible(false);
                    LOGIN.setVisible(true);
                    LOGIN.toFront();
                    LOGIN.setLocation(dim.width/2-LOGIN.getSize().width/2, dim.height/2-LOGIN.getSize().height/2);
                    Display1.setText("");
                    DateFormat format1 = new SimpleDateFormat("EEEE, MMMM d, yyyy  HH:mm:ss\n");
                    Display1.append(format1.format(Calendar.getInstance().getTime()));
                    Display1.setCaretPosition(Display1.getDocument().getLength());
            } 
        catch (InterruptedException ex) 
        {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void Authen1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Authen1ActionPerformed
try{
        String domain = "https://goo.gl/HbmiOF";
        String appId = "1953292198245796";
        String authUrl = "https://graph.facebook.com/oauth/authorize?type=user_agent&client_id="+appId+"&redirect_uri="+domain+"&scope=user_about_me,"
                + "user_actions.books,user_actions.fitness,user_actions.music,user_actions.news,user_actions.video,user_birthday,user_education_history,"
                + "user_events,user_photos,user_friends,user_games_activity,user_hometown,user_likes,user_location,user_photos,user_relationship_details,"
                + "user_relationships,user_religion_politics,user_status,user_tagged_places,user_videos,user_website,user_work_history,"
                + "manage_pages,publish_actions,read_insights,read_page_mailboxes,rsvp_event";
        // user_activities, user_groups, user_interests, manage_notifications, read_mailbox, read_stream
        System.setProperty("webdirver.chrome.driver", "chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get(authUrl);
        String accessToken;
        while(true){
            if(driver.getCurrentUrl().contains("https://www.facebook.com/KhanhTrang1996"))
            {
            String url = driver.getCurrentUrl();
            accessToken = url.replaceAll(".*#access_token=(.+)&.*", "$1");
            driver.quit();
            FacebookClient fbClient = new DefaultFacebookClient(accessToken);
            User user = fbClient.fetchObject("me",User.class);
            Name=user.getName();
            LOGIN.dispose();
            this.setTitle(Name+" - Free edition");
            this.setVisible(true);
            this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
            break;
            }
        }
    }catch (WebDriverException e){
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e); 
        }
        catch(Exception ex1){
            Dialog.showMessageDialog(this, "Unknown error, please try again later!","Error",JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1); 
        }

        // TODO add your handling code here:

        // TODO add your handling code here:
    }//GEN-LAST:event_Authen1ActionPerformed
    private int Confirm(){
       Object[] options = {"Absolutely yes!","Opps! It's a mistake!",};
       int n = JOptionPane.showOptionDialog(this,"Are you sure ?","Confirmation",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]); 
       return n;
    }
    private int ConfirmFriendRequest(){
       Object[] options = {"HolyYes!","Keep him/her away from me!",};
       int n = JOptionPane.showOptionDialog(this,adder + " want to add you in his/her friend list and know your online status. Do you agree ?","Confirmation",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[1]); 
       return n;
    }
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String args[]) throws IOException {
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            /*!!*/     new Client().setVisible(false);
                       LOGIN.setVisible(true);
                       Authen1.setBorderPainted(true);
            LOGIN.setLocation(dim.width/2-LOGIN.getSize().width/2, dim.height/2-LOGIN.getSize().height/2);
            Online_list.setEnabled(false);
            Friend_list.setEnabled(false);
            Black_list.setEnabled(false);
            DateFormat format1 = new SimpleDateFormat("EEEE, MMMM d, yyyy  HH:mm:ss\n");
            Display1.append(format1.format(Calendar.getInstance().getTime()));
            Display1.setCaretPosition(Display1.getDocument().getLength());
        });
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFrame ADD_FRIEND;
    private javax.swing.JFrame ASK_ME;
    private javax.swing.JMenuItem About;
    private javax.swing.JButton Add;
    private javax.swing.JButton Addfriend;
    private javax.swing.JButton Addfriend1;
    private javax.swing.JTextArea Ask;
    private javax.swing.JButton Authen;
    private static javax.swing.JButton Authen1;
    private javax.swing.JFrame BLACK_LIST;
    private javax.swing.JFrame BLOCK;
    private javax.swing.JList<String> Black_box;
    private static javax.swing.JMenuItem Black_list;
    private javax.swing.JFrame CHAT_ROOM;
    private javax.swing.JMenuItem Change_IP_and_Port;
    private static javax.swing.JButton Clear;
    private static javax.swing.JButton Connect;
    private javax.swing.JButton Current;
    private static javax.swing.JOptionPane Dialog;
    private static javax.swing.JButton Disconnect;
    private static javax.swing.JTextArea Display1;
    private static javax.swing.JTextArea Display_room1;
    private javax.swing.JMenuItem Exit;
    private javax.swing.JFrame FRIENDS_LIST;
    private javax.swing.JMenu Features;
    private static javax.swing.JMenuItem Friend_list;
    private javax.swing.JList<String> Friends_list;
    private javax.swing.JFrame GUESTS_LIST;
    private javax.swing.JMenu Help;
    private javax.swing.JTextField IDName;
    private javax.swing.JTextField IDName1;
    private javax.swing.JTextField IP1;
    private javax.swing.JFrame IP_AND_PORT;
    private javax.swing.JList<String> Invite_list;
    private javax.swing.JMenuBar JMenuBar1;
    private javax.swing.JFrame JROOM1;
    private static javax.swing.JFrame LOGIN;
    private static javax.swing.JTextArea Members;
    private javax.swing.JTextField Message_room1;
    private javax.swing.JFrame NEWS;
    private javax.swing.JMenuItem News;
    private javax.swing.JList<String> Off_list;
    private javax.swing.JList<String> Onl_list;
    private static javax.swing.JMenuItem Online_list;
    private javax.swing.JTextField Port1;
    private javax.swing.JList<String> Private_list;
    private javax.swing.JButton Refresh;
    private javax.swing.JButton Remove;
    private javax.swing.JButton Send_room1;
    private javax.swing.JMenu Setting;
    private javax.swing.JButton Unfriend;
    private javax.swing.JButton Unfriend1;
    private javax.swing.JButton Unfriend2;
    private javax.swing.JMenuItem Virtual_Assistant;
    private javax.swing.JTextField Web;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    // End of variables declaration//GEN-END:variables
}
