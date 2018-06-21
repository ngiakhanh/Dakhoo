/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khang1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Khanh Nguyen
 */
public class ClientThead implements Runnable {
    private static Socket socketer;
    private BufferedReader reader;
    private PrintWriter client;
    private String usrname=null;
    public ClientThead(Socket socket, PrintWriter user) throws IOException {
        client = user;//gan doi tuong client vao writer da tao san o ServerStart
        socketer = socket;//gan socketer vao socket tao san o ServerStart
        InputStreamReader isReader = new InputStreamReader(socketer.getInputStream());
        reader = new BufferedReader(isReader);//tao buffer for reading
    }
    @Override
    public void run(){
        String message, connect = "Connect", disconnect = "Disconnect", chat = "Chat", priv = "Private", unpriv = "Unprivate", add = "Addfriend", unf = "Unfriend", acc = "Accept", ser = "Server", upt = "Update", make_dis = "Make disconnect", add_me = "Add me", dont_send = "Dont send", con = "Confirm", con_yes = "Confirm yes", con_no = "Confirm no", don_con = "Done confirm", con_end = "Confirm end", con_inv = "Confirm invite", con_inv_no = "Confirm invite-no", con_inv_yes = "Confirm invite-yes", don_con_inv = "Done confirm invite", end_con_inv = "End confirm invite";
        boolean same = false;
        String[] eachname; 
        String[] data;
            try 
            {
                while ((message = reader.readLine()) != null) 
                {
                    Server.Display.append("Received: " + message + "\n");
                    Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
                    data = message.split(":");
                    
                    if (usrname ==null)
                    {
                    usrname = data[0];
                    }
                    
                    if (data[2].equals(connect)) 
                    {
                        /*if (!Server.all_users.contains(usrname))
                        {
                            Server.all_users.add(usrname);
                        }
                        if (Server.off_users.contains(usrname))
                        {
                            int num = Server.all_users.indexOf(usrname);
                            tellPrivate(Server.waitingsender.get(num),Server.waitingresp.get(num),usrname,Server.waitingmem.get(num));
                        }*/
                        userAdd(usrname);
                        tellEveryone((usrname + ":" + data[1] + ":" + connect));
                        
                        //tellPrivate("Server","",usrname,"Make disconnect");
                    } 
                    else if (data[2].equals(disconnect)) 
                    {
                        Server.clientOutputStreams.remove(client);
                        userRemove(usrname);
                        if (same == false)
                        {
                        tellEveryone((usrname + ":" + data[1] + ":" + disconnect));
                        }
                        else
                        {
                        same = false;  
                        }
                    } 
                    else if (data[2].equals(dont_send))
                    {
                        same = true;
                    }
                    else if (data[2].equals(chat)) 
                    {  
                        tellEveryone(message);
                    }
                    else if (data[2].equals(priv))
                    {
      
                        eachname = data[3].split("-");
                        int i=0;
                        while (!"End".equals(eachname[i]))
                        {
                           tellPrivate(usrname,data[1],eachname[i++],data[4]);
                        }
                    }
                    else if (data[2].equals(con))
                    {
                        eachname = data[3].split("-");
                        int i=0;
                        while (!"End".equals(eachname[i]))
                        {
                           if (Server.priv_users.contains(eachname[i]))
                           {
                               tellPrivate(eachname[i],"",usrname,con_no);
                           }
                           else
                           {
                               tellPrivate(eachname[i],"",usrname,con_yes);
                               Server.priv_users.add(eachname[i]);
                           }
                           i++;
                        }
                        
                    }
                    else if (data[2].equals(con_end))
                    {
                        tellPrivate("Server","",usrname,don_con);
                    }
                    else if (data[2].equals(con_inv))
                    {
                        eachname = data[3].split("-");
                        int i=0;
                        while (!"End".equals(eachname[i]))
                        {
                           if (Server.priv_users.contains(eachname[i]))
                           {
                               tellPrivate(eachname[i],"",usrname,con_inv_no);
                           }
                           else
                           {
                               tellPrivate(eachname[i],"",usrname,con_inv_yes);
                               Server.priv_users.add(eachname[i]);
                           }
                           i++;
                        }
                    }
                    else if (data[2].equals(end_con_inv))
                    {
                        tellPrivate("Server","",usrname,don_con_inv);
                    }
                    else if (data[2].equals(unpriv))
                    {
                        eachname = data[3].split("-");
                        int i=0;
                        //System.out.println("Truoc khi out: "+Server.priv_users);
                        Server.priv_users.remove(usrname);
                        while (!"End".equals(eachname[i]))
                        {
                           tellUnprivate(usrname,eachname[i++]);
                        }
                        //System.out.println("Sau khi out: "+Server.priv_users);
                    }
                    else if (data[2].equals(add))
                    {
                        tellPrivate(usrname,"",data[1],add);
                    }
                    else if (data[2].equals(unf))
                    {
                        tellPrivate(usrname,"",data[1],unf);
                    }
                    else if (data[2].equals(acc))
                    {
                        tellPrivate(usrname,"",data[1],acc);
                    }
                    else if (data[2].equals(ser))
                    {
                        Server.Display.append(message + "\n");
                        Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
                    }
                    else if (data[2].equals(upt))
                    {
                        updateOnlList();
                    }
                    else if (data[2].equals(add_me))
                    {
                        Server.onl_users.add(usrname);
                        Server.off_users.remove(usrname);
                        updateOnlList();
                    }
                    else 
                    {
                        Server.Display.append(message+"-Unknown \n");
                        Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
                        tellPrivate("Server",message+"-Unknown",usrname,"");
                    }
                }
                   /*client.close();
                   reader.close();
                   socketer.close();*/
            } 
            catch (IOException ex) 
             {
                Server.Display.append("Lost " + usrname + " connection. \n");
                Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
                Logger.getLogger(ClientThead.class.getName()).log(Level.SEVERE, null, ex);
                Server.clientOutputStreams.remove(client);
                userRemove(usrname);
                tellEveryone((usrname+": :" + disconnect));
             }
    }
    
    public static void tellPrivate(String sender, String mess, String name, String mem)
    {
        String resp;
        int num;
        if (Server.onl_users.contains(name))
        {
           num = Server.onl_users.indexOf(name);
           resp = sender+":"+mess+":Private:"+mem+": ";
           try 
           {
            PrintWriter writer = (PrintWriter) Server.clientOutputStreams.get(num);//gan doi tuong writer cho clientOutputStream[num] (cho viet) trong ArrayList
            writer.println(resp);
            Server.Display.append("Sending: " + resp + "\n");
            writer.flush();
            Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
            } 
           catch (Exception ex) 
           {
            Server.Display.append("Error telling everyone. \n");
            Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
            Logger.getLogger(ClientThead.class.getName()).log(Level.SEVERE, null, ex);
           }
        }
        else if (Server.off_users.contains(name))
        {
            num = Server.onl_users.indexOf(sender);
            resp = name+":"+mess+":Private:"+mem+":Waiting";
            try 
           {
            PrintWriter writer = (PrintWriter) Server.clientOutputStreams.get(num);//gan doi tuong writer cho clientOutputStream[num] (cho viet) trong ArrayList
            writer.println(resp);
            Server.Display.append("Sending: " + resp + "\n");
            writer.flush();
            Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
            } 
           catch (Exception ex) 
           {
            Server.Display.append("Error telling privately. \n");
            Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
            Logger.getLogger(ClientThead.class.getName()).log(Level.SEVERE, null, ex);
           }
            
            /*num = Server.all_users.indexOf(name);
            Server.waitingresp.set(num, mess);
            Server.waitingsender.set(num, sender);
            
            /*num = Server.all_users.indexOf(name);
            Server.waitingresp.set(num, mess);
            Server.waitingsender.set(num, sender);
            Server.waitingmem.set(num, mem);*/
        }
        else
        {
            num = Server.onl_users.indexOf(sender);
            resp = "Server:"+"There is no member "+name+":Private:"+mem+": ";
            try 
           {
            PrintWriter writer = (PrintWriter) Server.clientOutputStreams.get(num);//gan doi tuong writer cho clientOutputStream[num] (cho viet) trong ArrayList
            writer.println(resp);
            Server.Display.append("Sending: " + resp + "\n");
            writer.flush();
            Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
            } 
           catch (Exception ex) 
           {
            Server.Display.append("Error telling privately. \n");
            Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
            Logger.getLogger(ClientThead.class.getName()).log(Level.SEVERE, null, ex);
           }
        }
    }
    
    public static void tellUnprivate(String sender, String name)
    {
        String resp;
        int num;
        if (Server.onl_users.contains(name))
        {
           num = Server.onl_users.indexOf(name);
           resp = sender+"::Unprivate:";
           try 
           {
            PrintWriter writer = (PrintWriter) Server.clientOutputStreams.get(num);//gan doi tuong writer cho clientOutputStream[num] (cho viet) trong ArrayList
            writer.println(resp);
            Server.Display.append("Sending: " + resp + "\n");
            writer.flush();
            Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
            } 
           catch (Exception ex) 
           {
            Server.Display.append("Error telling unprivately. \n");
            Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
            Logger.getLogger(ClientThead.class.getName()).log(Level.SEVERE, null, ex);
           }
        }
        else
        {
            num = Server.onl_users.indexOf(sender);
            resp = "Server:"+"Online list does not contain "+name+":Unprivate";
            try 
           {
            PrintWriter writer = (PrintWriter) Server.clientOutputStreams.get(num);//gan doi tuong writer cho clientOutputStream[num] (cho viet) trong ArrayList
            writer.println(resp);
            Server.Display.append("Sending: " + resp + "\n");
            writer.flush();
            Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
            } 
           catch (Exception ex) 
           {
            Server.Display.append("Error telling unprivately. \n");
            Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
            Logger.getLogger(ClientThead.class.getName()).log(Level.SEVERE, null, ex);
           }
        }
    }
    
    public static void tellEveryone(String mess)
    {
        ListIterator it = Server.clientOutputStreams.listIterator();
        while (it.hasNext()) 
        {
            try 
            {
                PrintWriter writer = (PrintWriter) it.next();//gan doi tuong writer cho moi clientOutputStream (cho viet) trong ArrayList
		writer.println(mess);
		Server.Display.append("Sending: " + mess + "\n");
                writer.flush();
                Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
            } 
            catch (Exception ex) 
            {
		Server.Display.append("Error telling everyone. \n");
                Server.Display.setCaretPosition(Server.Display.getDocument().getLength());
                Logger.getLogger(ClientThead.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }
    
    public void userAdd(String data)
    {
        String name = data;
        if (!Server.onl_users.contains(name))
        {
        Server.onl_users.add(name);
        //System.out.println("Onl users added: "+name);
        }
        if (Server.off_users.contains(name))
        {
        Server.off_users.remove(name);
        //System.out.println("Off users removed: "+name);
        }
        //Cap nhat lai danh sach online cho cac thanh vien moi/cu
        updateOnlList();
    }
    
    public void userRemove(String data)
    {
        String name = data;
        if (Server.onl_users.contains(name))
        {
        Server.onl_users.remove(name);
        //System.out.println("Onl users removed: "+name);
        }
        if (!Server.off_users.contains(name))
        {
        Server.off_users.add(name);
        //System.out.println("Off users added: "+name);
        }
        //Cap nhat lai danh sach online cho cac thanh vien
        updateOnlList();
    }
    
    
    public void updateOnlList()
    {
        String message, upt = ":Update", clear = "Server: :Clear";
        tellEveryone(clear);
        String[] tempList = new String[(Server.onl_users.size())];
        Server.onl_users.toArray(tempList);
        
        for (String token:tempList) 
        {
            message = "Server:" + token + upt;
            tellEveryone(message);
        }
        message = "Server::Done:";
        tellEveryone(message);
    }
}
