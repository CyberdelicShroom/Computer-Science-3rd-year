/**This class creates and handles new internal and external clients.
 * It listens for input by the user to send a payload to another client through the NAT Box
 * or to disconnect from the NAT Box, listens for incoming packets coming from other clients
 * commucated through the NAT Box, sends acknoledgements back to the source, and implements 
 * the Dynamic Host Configuration Protocol.
 * 
 * @author Keagan Selwyn Gill
 **/


package NAT;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

@SuppressWarnings("deprecation")
public class Client implements Runnable {

    private static Socket socket = null;
    private static DataInputStream in = null;
    private static DataInputStream receiver = null;
    private static DataOutputStream out = null;
    private static PrintStream print = null;
    private static String IP;
    private static String newIP;
    private static String MAC;
    private static String host;
    private static int port;

    /**
     * @param args the command line arguments 
     * args[0] : host 
     * args[1] : port
     * args[2] : internal/external (int/ext)
     */
    public static void main(String[] args) {
        host = args[0];
        port = Integer.parseInt(args[1]);
        String internalORexternal = args[2];
        internalORexternal = internalORexternal.toLowerCase();

        MAC = NATUtilities.generateRandomMACAddress();
        IP = NATUtilities.generateRandomIPAddress();
        if (internalORexternal.equals("int")) {
            // request IP (i.e. Execute DHCP)
            ClientDHCP();
            System.out.println("IP = " + IP + "; Internal IP = " + newIP);
        } else if (internalORexternal.equals("ext")) {
            //send something to make ServerDHCP add to NAT
            DatagramSocket socket = null;
            int count = 0;
            IP = NATUtilities.generateRandomIPAddress();
            newIP = IP;
            try {
                socket = new DatagramSocket();
                byte[] payload = new byte[100];
                payload[0] = 'e';
                byte[] byteIP = IP.getBytes();

                for (int i = 0; i < byteIP.length; i++) {
                    payload[i + 1] = byteIP[i];
                    count++;
                }
                payload[count + 1] = '%';
                DatagramPacket p = new DatagramPacket(payload, payload.length, InetAddress.getByName(host), port);
                socket.send(p);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("IP: " + IP);
        }
        System.out.println("MAC: " + MAC);
        // Connect to NAT-Box
        try {
            socket = new Socket(host, port);
            receiver = new DataInputStream(socket.getInputStream());
            in = new DataInputStream(new BufferedInputStream(System.in));
            out = new DataOutputStream(socket.getOutputStream());
            print = new PrintStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (socket != null && receiver != null && out != null) {
            try {
                // Listen for input by the user to send a payload to another client through the NAT Box
                // or to disconnect from the NAT Box.
                new Thread(new Client()).start();
                while (true) {
                    String message = in.readLine().trim();
                    if (message.startsWith("send")) {
                        String[] details = message.split(" ");
                        byte[] p = NATUtilities.constructPacket(newIP, details[1], "payload".getBytes());
                        print.println(p.length);
                        out.write(p);
                    }
                    if (message.startsWith("EXIT")) {
                        print.println("e");
                        break;
                    }
                }
                out.close();
                in.close();
                receiver.close();
                socket.close();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** This method implements the Dynamic Host Configuration Protocol 
     *  for a new internal client. It also exchanges connection configuration
     *  packets and updates the user via output on the console.
     **/
    private static void ClientDHCP() {
        DatagramSocket socket = null;
        String tempIP = "";
        int pos = 0;
        int count = 0;
        try {
            socket = new DatagramSocket();
            byte[] payload = new byte[100];
            payload[0] = 'd';
            //send IP as well
            for (int i = 0; i < IP.length(); i++) {
                payload[i + 1] = (byte) IP.charAt(i);
                pos = i + 1;
            }
            payload[pos + 1] = '%';
            for (int i = 1; i < payload.length; i++) {
                count++;
                if (payload[i] == '%') {
                    break;
                }
            }
            DatagramPacket payloadPacket = new DatagramPacket(payload, payload.length, InetAddress.getByName(host), port);
            socket.send(payloadPacket);
            System.out.println("Sent discover");
            boolean listening = true;

            while (listening) {
                socket.receive(payloadPacket);
                byte[] buff = payloadPacket.getData();
                if (buff[0] == 'o') {
                    //send ip address you want
                    payload = new byte[100];
                    payload[0] = 'r';
                    count = 0;
                    for (int i = 1; i < buff.length; i++) {
                        payload[i] = buff[i];
                        count++;
                        if (buff[i] == '%') {
                            payload[i] = '%';
                            break;
                        }
                    }
                    tempIP = new String(buff, 1, count - 1);
                    payloadPacket = new DatagramPacket(payload, payload.length, InetAddress.getByName(host), port);
                    socket.send(payloadPacket);
                    System.out.println("Sent request");
                }
                if (buff[0] == 'a') {
                    newIP = tempIP;
                    listening = false;
                    socket.close();
                    System.out.println("Ack received");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        messageListener();
    }

    /**This method listens for incoming packets coming from other clients
     * commucated through the NAT Box and sends an acknoledgement back to the source.
     **/
    public void messageListener() {
        byte[] packet = null;
        String message;

        try {
            while (true) {
                if ((message = receiver.readLine()) != null) {
                    // Receive packet
                    packet = new byte[Integer.parseInt(message)];
                    receiver.read(packet);
                    String[] p = NATUtilities.parsePacket(packet);
                    System.out.println(Arrays.toString(p));
                    if (p[2].equals("payload") && p.length <= 3) {
                        // Send Acknowledgement packet
                        String ack = newIP + "#" + p[0] + "#received";
                        packet = ack.getBytes();
                        print.println(packet.length);
                        out.write(packet);
                    }
                } else {
                    System.out.println("Lost connection with NATBox");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            System.out.println("DISCONNECTED");
        }
    }
}
