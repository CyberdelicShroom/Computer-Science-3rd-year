/**
 * This class handles setting up a new client by getting a unique
 * username from the user, reading in the client's port number, and
 * adding this instance of ClientHandler into the clientHandler arrayList.
 * It then listens for and handles incoming input from the associated user,  
 * whether it's a broadcast message to the other clients, a command, or a  
 * whisper message.
 * 
 * @author Keagan Selwyn Gill
 */

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JTextArea;
import java.sql.Timestamp;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private String username;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private JTextArea userActivityTAonServer;
    private int portCount;

    /**
     * Constructor for this handler
     * 
     * @param socket the corresponding client's socket
     * @param userActivityTAonServer JTextArea from server GUI
     * @param portCount the corresponding client's port
     */
    public ClientHandler(Socket socket, JTextArea userActivityTAonServer, int portCount) {
        try {
            this.socket = socket;
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());
            this.userActivityTAonServer = userActivityTAonServer;
            this.portCount = portCount;

        } catch (IOException e) {
            shutdown();
        }
    }

    /**
     * Handles incoming command from client, i.e. message starting with '/'
     * 
     * @param msg message containing the payload with command and who it is
     * being sent to and from
     */
    public void handleCommand(Message msg) {
        String[] parts = msg.text().split(" ", 2);
        String cmd = parts[0];
        if(cmd.equals("/results")) {
                for (ClientHandler handler : clientHandlers) {
                    if (handler.username.equals(msg.to())) {
                        userActivityTAonServer.append(msg.to() + " requested a file search from " + msg.from() + "\n");
                        try {
                            handler.oos.writeObject(msg);
                            handler.oos.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        } else {
            broadcast(msg);
        }
    }

    /**
     * Sends message from client to all other clients
     * 
     * @param msg message with payload and who it is from
     */
    public void broadcast(Message msg) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                clientHandler.oos.writeObject(msg);
                clientHandler.oos.flush();// manual clear before it fills
                if (msg.text().startsWith("/search")) {
                    Thread.sleep(200);
                }
            } catch (IOException e) {
                shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles directing messages to a given subset of clients
     * 
     * @param msg the message object to send to clients
     */
    public void directMessage(Message msg) {
        ArrayList<String> usernames = new ArrayList<String>();
        String text = msg.text();
        // Add all recipients into ArrayList and extract the text
        while (text.startsWith("@")) {
            String[] parts = text.split(" ", 2);
            usernames.add(parts[0].substring(1));
            text = parts[1];
        }
        // Check whether the text is a download request command or 
        // message-key send command
        if (text.startsWith("/download ") || text.startsWith("/key ") || text.startsWith("/denied ")) {
            for (ClientHandler handler : clientHandlers) {
                if (handler.username.equals(usernames.get(0))) {
                    try {
                        handler.oos.writeObject(new Message(text, msg.from()));
                        handler.oos.flush();
                    } catch (IOException e) {
                        shutdown();
                        return;
                    }
                    return;
                }
            }
            return;
        }

        // Create a new ClientHandler ArrayList containing the chosen
        // recipients
        String txt = "(Whisper)";
        String errTxt = "The following users do not exist: ";
        ArrayList<ClientHandler> handlers = new ArrayList<ClientHandler>();
        outer: for (String name : usernames) {
            for (ClientHandler handler : clientHandlers) {
                if (handler.username.equals(name)) {
                    handlers.add(handler);
                    continue outer;
                }
            }
            errTxt += " " + name;
        }
        txt += " " + text;
        // Check whether the recipients chosen exist in the session, if not
        // then send error message back to client
        if (usernames.size() > handlers.size()) {
            try {
                oos.writeObject(new Message(errTxt, "SERVER"));
                oos.flush();
            } catch (IOException e) {
                shutdown();
                return;
            }
        }
        
        // Direct the text to the given clients (whisper)
        if (!handlers.isEmpty()) {
            try {
                oos.writeObject(new Message(txt, msg.from()));
                oos.flush();
                for (ClientHandler handler : handlers) {
                    handler.oos.writeObject(new Message(txt, msg.from()));
                    handler.oos.flush();
                }
            } catch (IOException e) {
                shutdown();
                return;
            }
        }
    }

    /**
     * Checks whether the username that the client has entered is unique,
     * if not then the client will be informed that the username exists and
     * that they must enter a unique username
     */
    public void getUniqueUsername() {
        outer: while (socket.isConnected()) {
            try {
                String username = (String) ois.readObject();
                for (ClientHandler handler : clientHandlers) {
                    if (handler.username.equals(username)) {
                        oos.writeObject(new String("username exists"));
                        oos.flush();
                        continue outer;
                    }
                }
                this.username = username;
                // add to list of users
                oos.writeObject(new String("username unique"));
                oos.flush();
                break;
            } catch (Exception e) {
                shutdown();
                return;
            }
        }
    }

    /**
     * Remove client and send user disconnected messages to server and
     * other connected clients
     */
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcast(new Message(username + " has left the chat!", "SERVER"));
        userActivityTAonServer.append(username + " disconnected!\n");
    }

    /**
     * Neatly closes sockets and input output streams
     */
    public void shutdown() {
        removeClientHandler();
        try {
            if (ois != null) {
                ois.close();
            }
            if (oos != null) {
                oos.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {

        }
    }

    /**
     * The thread listens for messages from the client and handles it
     */
    @Override
    public void run() {
        InetAddress Inet = socket.getInetAddress();
        getUniqueUsername(); // check username uniqueness

        // Read in client's port number
        try {
            oos.writeInt(portCount);
            oos.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        // Update the server's user activity text area
        Timestamp Tstamp = new Timestamp(System.currentTimeMillis());
        userActivityTAonServer.append(username + " joined with host name: " + Inet.getHostName() + "\nTimestamp: " + Tstamp + "\n\n");

        // Add this instance of ClientHandler to the clientHandler arrayList
        clientHandlers.add(this);

        // Broadcast message the new client has joined
        broadcast(new Message(username + " has entered the chat!", "SERVER"));

        // Main loop that listens for incoming input from the associated user
        // and calls the necessary method for handling the input based on
        // what character the message payload starts with
        while (socket.isConnected()) {
            try {
                Message msg = (Message) ois.readObject();
                if (msg.text().startsWith("@")) {
                    directMessage(msg);
                } else if (msg.text().startsWith("/")) {
                    handleCommand(msg);
                } else {
                    broadcast(msg);
                }
            } catch (Exception e) {
                shutdown();
                return;
            }
        }
    }
}
