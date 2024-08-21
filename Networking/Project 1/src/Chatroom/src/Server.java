/** The Server class keeps track of all clients connected. 
 * It is responsible for establishing new connections, and 
 * then creating and passing these on to the connection/client 
 * handler class (called ConnectionHandler). The server has a 
 * broadcast function, which allows it (and other clients) to
 * send messages to all currently connected clients. It also 
 * stores administration information about users connected etc. 
 * and passes this on to all clients.
 * 
 * This program also contains the ConnectionHandler class.
 * A new ConnectionHandler is constructed for every client
 * that connects to the server. The ConnectionHandler makes
 * use of the server's broadcast function to send messages
 * to all currently connected clients. Whispers are handled
 * by the ConnectionHandler by sending only to the target of
 * the whisper, instead of broadcasting to everyone by using
 * the ‘/whisper’ command. The ConnectionHandler also handles
 * all the other commands that the clients can send to the
 * server.
 *
 * @author Keagan Selwyn Gill
 **/


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import static java.lang.System.out;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private ArrayList<ConnectionHandler> connections = new ArrayList<>();
    private ArrayList<String> clients = new ArrayList<>();
    private boolean done = false;
    private ExecutorService threadPool;
    private ServerSocket server;

    @Override
    public void run() {
        try {
            server = new ServerSocket(4000);
            System.out.println("Server is running on port 4000");
            threadPool = Executors.newCachedThreadPool();
            while (!done) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                threadPool.execute(handler);
            }
        } catch (Exception e) {
            shutdown();
        }

    }

    /** This is the broadcast method. Allows the server 
     * (and other clients) to send messages to all currently
     * connected clients.
     * @param message string to broadcast to all clients.
     **/
    public void bCast(String message) {
        for (ConnectionHandler ch : connections) {
            if (ch != null) {
                ch.sendMessage(message);
            }
        }
    }

    /** This is the whisper method. It sends a private message
     *  to a specified user.
     * @param whisper message string to send.
     * @param recipient the recipient user of the whisper message.
     * @param sender the user who sent the whisper message.
     **/
    public void whisper(String whisper, String recipient, String sender) {
        for (ConnectionHandler ch : connections) {
            if (ch.username.equals(recipient)) {
                ch.whisperMessage(whisper, sender);
            }
            if (ch.username.equals(sender)) {
                ch.whisperMessage(whisper, sender);
            }
        }
    }

    /** This method terminates all threads, 
     *  closes the server socket as well as 
     *  all client sockets that are connected
     *  to the server.
     **/
    public void shutdown() {
        done = true;
        threadPool.shutdown();
        if (!server.isClosed()) {
            try {
                server.close();
            } catch (IOException e) {
            }
        }
        for (ConnectionHandler ch : connections) {
            ch.shutdown();
        }
    }

    class ConnectionHandler implements Runnable {

        private Socket client;
        private DataInputStream in;
        private DataOutputStream out;
        private String username;

        public ConnectionHandler(Socket client) {
            this.client = client;
        }
        
        /** This method requests a unique username
         *  from the client until the client sends a 
         *  unique username.
         * @param username username that the client chose.
         * @param in the data input stream of the client.
         * @return the unique username.
         **/
        public String requestUniqueName(String username, DataInputStream in) {
            try {
                while (clients.contains(username)) {
                    out.writeUTF("That username is already in use, try entering another by typing it in the message box and sending it: ");
                    username = in.readUTF();
                }
            } catch (IOException e) {
                
            }
            return username;
        }

        /** This method sends out strings of all the currently
         *  connected clients to the client that called the method
         *  and prints them out to the terminal of the server.
         **/
        public void printConnectedUsers() {
            System.out.println("\nConnected users:");
            try {
                out.writeUTF("\nConnected users:");
                for (String username : clients) {
                    System.out.println(username);
                    out.writeUTF(username);
                }
                System.out.println();

                out.writeUTF("");
            } catch (IOException e) {
                
            }
        }

        @Override
        public void run() {
            try {
                out = new DataOutputStream(client.getOutputStream());
                in = new DataInputStream(client.getInputStream());
                username = in.readUTF();
                
                if (username != null) {
                    if (clients.contains(username)) {
                        username = requestUniqueName(username, in);
                        clients.add(username);
                    } else {
                        clients.add(username);
                    }
                    System.out.println(username + " has connected.");
                    bCast(username + " has entered the chat.");
                    String message;
                    
                    while ((message = in.readUTF()) != null) {
                        if (message.startsWith("/name ")) {
                            String[] messageSplit = message.split(" ", 2);
                            String originalName = username;
                            
                            if (!messageSplit[1].equals("") && !messageSplit[1].equals(" ")) {
                                if (clients.contains(messageSplit[1])) {
                                    username = requestUniqueName(messageSplit[1], in);
                                    clients.add(username);
                                    int index = clients.indexOf(originalName);
                                    clients.remove(index);
                                } else {
                                    username = messageSplit[1];
                                    clients.add(username);
                                    int index = clients.indexOf(originalName);
                                    clients.remove(index);
                                }
                                bCast(originalName + " renamed themself to " + username);
                                System.out.println(originalName + " renamed themself to " + username);
                            } else {
                                out.writeUTF("No username provided. Try the command again with a unique username.");
                            }

                        } else if (message.startsWith("/quit")) {
                            int index = clients.indexOf(username);
                            clients.remove(index);
                            System.out.println(username + " has disconnected.");
                            bCast(username + " has left the chat.");
                            shutdown();
                        } else if (message.startsWith("/users")) {
                            printConnectedUsers();
                        } else if (message.startsWith("/whisper ")) {
                            
                            String[] messageSplit = message.split(" ", 3);
                            String whisper = messageSplit[2];
                            String recipient = messageSplit[1];
                            if (clients.contains(recipient)) {
                                whisper(whisper, recipient, username);
                            } else {
                                out.writeUTF("'"+recipient+"'" + " does not exist in this chat room.");
                                printConnectedUsers();
                            }
                        } else {
                            bCast(username + ": " + message);
                        }

                    }

                }

            } catch (IOException e) {
                shutdown();
            }
        }

        /** This method writes out the message string
         *  to the clients data output stream.
         * @param message the string to be sent to clients.
         **/
        public void sendMessage(String message) {
            try {
                out.writeUTF(message);
            } catch (IOException e) {
                
            }
            
        }
        
        /** This method functions the same as the sendMessage
         *  method. The only difference is that a string gets
         *  appended to the output string which indicates
         *  to the recipient client that the message was 
         *  send privately.
         *  @param whisper the string to be sent to the specified client.
         *  @param sender the user who sent the whisper message.
         **/
        public void whisperMessage(String whisper, String sender) {
            try {
                out.writeUTF(sender + " (whisper): " + whisper);
            } catch (IOException e) {
                
            }
            
        }

        /** This method closes all data streams of the client 
         * and closes the clients socket.
         **/
        public void shutdown() {
            try {
                in.close();
                out.close();
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (IOException e) {
                
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}

