/**This class handles all client connections and communications, including all of the
 * algorithms and implementations required. The NAT-Box is constantly listening
 * for attempted connections from internal and external clients and will execute
 * the correct protocols for the connections of these clients until a predetermined
 * limit has been reached. Every client is added to the NAT-Table, with External 
 * clients being mapped to their own IP address and internal clients having their 
 * internal IP address (obtained via DHCP) mapped to their externally valid IP address.
 * For every client that connects, a ClientConnection object is created. This object 
 * handles the receiving, parsing, translating, and routing of all packets that are sent
 * through the NAT-Box.
 * 
 * @author Keagan Selwyn Gill
 **/

package NAT;

import java.io.PrintStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class NATBox {

    private static ServerSocket server = null;
    private static Socket client = null;
    private static final int clientLimit = 10;
    private static long timeLimit = 300000; // 5min
    private static final ClientConnection[] clientThreads = new ClientConnection[clientLimit];
    private static PrintStream output = null;
    private static LinkedList<NATtableEntry> NATtable = new LinkedList<NATtableEntry>();
    private static LinkedList<NATtableEntry> pool = new LinkedList<NATtableEntry>();
    private static final String MAC = NATUtilities.generateRandomMACAddress();
    private static final String IP = NATUtilities.generateRandomIPAddress();
    private static int counter = 0;
    private static boolean canTime = true;

    /**
     * @param args the command line arguments 
     * args[0] : time limit for each NAT mapping in milliseconds
     */
    public static void main(String[] args) {
        int port = 6969;
        timeLimit = Long.parseLong(args[0]);
        Timer timer = new Timer();

        System.out.println("IP: " + IP);
        System.out.println("MAC: " + MAC);

        TimerTask timerTask1 = new TimerTask() {
            @Override
            public void run() {
                //clear NAT and pool
                NATtable.remove();
                pool.get(counter).setExternalIP("0");
                try {
                    clientThreads[counter].client.close();
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                counter++;
                System.out.println("NAT Table (after timer): ");
                NATUtilities.printTable(NATtable);
                System.out.println("Pool (after timer): ");
                NATUtilities.printTable(pool);
                canTime = true;
            }
        };

        TimerTask timerTask2 = new TimerTask() {
            @Override
            public void run() {
                //clear NAT and pool
                NATtable.remove();
                pool.get(counter).setExternalIP("0");
                try {
                    clientThreads[counter].client.close();
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                counter++;
                System.out.println("NAT Table (after timer): ");
                NATUtilities.printTable(NATtable);
                System.out.println("Pool (after timer): ");
                NATUtilities.printTable(pool);
                canTime = false;
            }
        };

        constructPool(clientLimit);

        // open ServerSocket
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create new socket for each new client that attempts to connect
        while (true) {
            int i;

            ServerDHCP(port);
            if (timeLimit < 30000) {
                if (canTime) {
                    timer.schedule(timerTask1, timeLimit);
                    canTime = false;
                } else {
                    timer.schedule(timerTask2, timeLimit);
                    canTime = true;
                }
            }
            System.out.println("NAT Table: ");
            NATUtilities.printTable(NATtable);
            try {
                client = server.accept();
                for (i = 0; i < clientLimit; i++) {
                    if (clientThreads[i] == null) {
                        clientThreads[i] = new ClientConnection(client, clientThreads,
                                            NATtable, NATtable.get(NATtable.size() - 1),
                                            pool);
                        clientThreads[i].start();
                        break;
                    }
                }
                // Message if too many clients have connected
                if (i == clientLimit) {
                    output = new PrintStream(client.getOutputStream());
                    output.println("Client limit reached.");
                    output.close();
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** This method implements the Dynamic Host Configuration Protocol 
     *  on the NAT Box side, sends the offer for a new client connection,
     *  and sends an acknowledgent once the client has connected and has 
     *  it's IP assigned.
     * @param port the port number of the network
     **/
    private static void ServerDHCP(int port) {
        DatagramSocket socket = null;
        String clientIP = "";
        int count = 0;
        int poolPos = 0;

        try {
            socket = new DatagramSocket(port);
            byte[] payload = new byte[100];
            DatagramPacket payloadPacket = new DatagramPacket(payload, payload.length);
            boolean listening = true;

            while (listening) {
                socket.receive(payloadPacket);
                byte[] buffer = payloadPacket.getData();
                int clientPort = payloadPacket.getPort();
                InetAddress address = payloadPacket.getAddress();

                if (buffer[0] == 'e') {
                    System.out.println("External client");
                    //add to NAT table
                    for (int i = 1; i < buffer.length; i++) {
                        count++;
                        if (buffer[i] == '%') {
                            break;
                        }
                    }
                    clientIP = new String(buffer, 1, count - 1);
                    NATtableEntry newClient = new NATtableEntry(clientIP, clientIP);
                    NATtable.add(newClient);
                    listening = false;
                    socket.close();
                } else {
                    System.out.println("Internal client");
                }
                if (buffer[0] == 'd') {
                    //send ip address to offer
                    //use a '%' to mark the end of the payload
                    //get the IP of the client
                    count = 0;

                    for (int i = 1; i < buffer.length; i++) {
                        count++;
                        if (buffer[i] == '%') {
                            break;
                        }
                    }
                    clientIP = new String(buffer, 1, count - 1);
                    System.out.println("clientIP = " + clientIP);
                    byte[] intIP;
                    payload = new byte[100];
                    payload[0] = 'o';

                    //get from pool
                    count = 0;
                    for (int i = 0; i < pool.size(); i++) {
                        if (pool.get(i).getExternalIP().equals("0")) {
                            intIP = pool.get(i).getInternalIP().getBytes();
                            poolPos = i;
                            for (int j = 0; j < intIP.length; j++) {
                                payload[j + 1] = intIP[j];
                                count++;
                            }
                            payload[count + 1] = '%';
                            break;
                        }
                        System.out.println("Client limit reached!");
                    }
                    payloadPacket = new DatagramPacket(payload, payload.length, address, clientPort);
                    socket.send(payloadPacket);
                    System.out.println("Sent offer");
                }

                if (buffer[0] == 'r') {
                    //send ack 
                    //assign ip to client
                    String newIP = "";
                    count = 0;

                    for (int i = 1; i < buffer.length; i++) {
                        count++;
                        if (buffer[i] == '%') {
                            break;
                        }
                    }

                    newIP = new String(buffer, 1, count - 1);
                    NATtableEntry newClient = new NATtableEntry(newIP, clientIP, timeLimit);
                    NATtable.add(newClient);
                    pool.get(poolPos).setExternalIP(clientIP);
                    payload = new byte[100];
                    payload[0] = 'a';
                    payload[1] = '%';
                    payloadPacket = new DatagramPacket(payload, payload.length, address, clientPort);
                    
                    socket.send(payloadPacket);
                    System.out.println("Sent Ack");
                    listening = false;
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** This method creates a pool of non-unique IP addresses for internal clients.
     * @param clientLimit the maximum number of clients that can connect to the NATBox
     **/
    private static void constructPool(int clientLimit) {
        String prefix = "192.168.0.";

        for (int i = 0; i < clientLimit; i++) {
            String poolIP = prefix + i;
            NATtableEntry toPool = new NATtableEntry(poolIP, "0");
            pool.add(toPool);
        }
    }
}
