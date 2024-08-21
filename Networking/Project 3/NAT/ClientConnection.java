/**A new instance of this class is created for every new client that
 * connects as this class conncurrently handles the receiving, constructing,
 * parsing, translating and routing of all packets that are sent through the NAT Box.
 * The Internet Control Message protocol (ICMP) is also implemented for error-reporting
 * to the source IP address when problems prevent the deilvery of packets.
 * 
 * @author Keagan Selwyn Gill
 **/

package NAT;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;

@SuppressWarnings("deprecation")
class ClientConnection extends Thread {

  private DataInputStream clientMessage = null;
  public DataOutputStream output = null;
  public PrintStream print = null;
  public NATtableEntry self = null;
  public Socket client = null;
  private final ClientConnection[] clientThreads;
  private LinkedList<NATtableEntry> NATtable = new LinkedList<NATtableEntry>();
  private LinkedList<NATtableEntry> pool = new LinkedList<NATtableEntry>();
  public  boolean Internal;
  public String message;

  public ClientConnection(Socket client, ClientConnection[] clientThreads, LinkedList<NATtableEntry> NATtable, NATtableEntry self, LinkedList<NATtableEntry> pool) {
    this.client = client;
    this.clientThreads = clientThreads;
    this.NATtable = NATtable;
    this.self = self;
    this.pool = pool;

    if (self.getInternalIP().equals(self.getExternalIP())) {
      Internal = false;
    } else {
      Internal = true;
    }
  }

  /**This method concurrently handles the incoming packets of all the clients 
   * and implements the Internet Control Message protocol for error-reporting
   * to the source IP address when problems prevent the deilvery of packets.
   * It also prints output to the user to inform the user about the status 
   * of the packet delivery. It also sends the payload packet to the desired
   * destination.
   */
  public void run() {
    ClientConnection[] clientThreads = this.clientThreads;
    try {
      clientMessage = new DataInputStream(client.getInputStream());
      output = new DataOutputStream(client.getOutputStream());
      print = new PrintStream(client.getOutputStream());
      byte[] packet = null;

      while (true) {

        message = clientMessage.readLine();
        if (message.equals("e")) {
          break;
        }

        packet = new byte[Integer.parseInt(message)];
        clientMessage.read(packet);
        String[] parsedPacket = NATUtilities.parsePacket(packet);
        String src = parsedPacket[0];
        String dest = parsedPacket[1];
        String payload = parsedPacket[2];
        boolean srcInt = false;
        boolean destInt = false;
        boolean intIP = false;

        for (ClientConnection c : clientThreads) {
          if (c != null && (c.self.getInternalIP().equals(src))){
            srcInt = c.Internal;
          }

          if (c != null && (c.self.getExternalIP().equals(dest) || c.self.getInternalIP().equals(dest))) {
            destInt = c.Internal;
            if (c.self.getInternalIP().equals(dest)) {
              intIP = true;
            }
          }
        }
        System.out.println("\nPacket received by NATBox:");
        System.out.println(Arrays.toString(parsedPacket));
        System.out.println("\nSource internal: "+srcInt);
        System.out.println("Destination internal: "+destInt + "\n");
        // NB: Internal packets can only send to internal clients through internal IP
        if (srcInt) {
          if (destInt) {
            // Source and Destination both internal
            // DO NOTHING TO PACKET IF VALID
            if (!intIP) {
              // Invalid
              System.out.println("Destination INVALID: " + dest);
              dest = "-1";
            } else {
              System.out.println("No packet alteration.");
            }
          } else {
            // Source internal and Destination external
            // TRANSLATE SOURCE
            for (ClientConnection c : clientThreads) {
              if (c != null && c.self.getInternalIP().equals(src)){
                src = c.self.getExternalIP();
                System.out.println("Source translated.");
              }
            }
          }
        } else {
          if (destInt) {
            // Source external and Destination internal
            // TRANSLATE DESTINATION
            for (ClientConnection c : clientThreads) {
              if (c != null && c.self.getExternalIP().equals(dest)){
                dest = c.self.getInternalIP();
                System.out.println("Destination translated.");
              }
            }
          } else {
            // Source and Dest both external
            System.out.println("**Packet Dropped**");
            for (ClientConnection c : clientThreads) {
              if (c!= null && (c.self.getInternalIP().equals(src) || c.self.getExternalIP().equals(src))) {
                packet = (src + "#" + dest + "#" + payload + "#Packet Could not be delivered").getBytes();
                c.print.println(packet.length);
                c.output.write(packet);
                System.out.println("\nERROR PACKET SENT:");
                System.out.println("["+src + ", "+dest+", "+payload+", Packet could not be delivered]");
                break;
              }
            }
            continue;
          }
        }
        packet = NATUtilities.constructPacket(src, dest, payload.getBytes());
        // Send packet
        boolean sent = false;
        for (ClientConnection c : clientThreads) {
          if (c!= null && c.self.getInternalIP().equals(dest)) {
            c.print.println(packet.length);
            c.output.write(packet);
            System.out.println("\nSent packet:");
            System.out.println("["+src + ", "+dest+", "+payload+"]");
            sent = true;
            break;
          }
        }
        // Return error packet
        if (!sent) {
          System.out.println("PACKET COULD NOT BE DELIVERED:");
          for (ClientConnection c : clientThreads) {
            if (c!= null && (c.self.getInternalIP().equals(src) || c.self.getExternalIP().equals(src))) {
              packet = (src + "#" + dest + "#" + payload + "#Packet Could not be delivered").getBytes();
              c.print.println(packet.length);
              c.output.write(packet);
              System.out.println("\nERROR PACKET SENT:");
              System.out.println("["+src + ", "+dest+", "+payload+", Packet Could not be delivered]");

              break;
            }
          }
        }   
      }
      // remove from NAT table
      NATtable.remove(self);
      //Release IP if we get that far
      for (NATtableEntry t : pool) {
        if (t.getExternalIP().equals(self.getExternalIP())) {
          t.setExternalIP("0");
        }
      }
      // Set client to null
      for (ClientConnection c : clientThreads) {
        if (c == this) {
          System.out.println(c.self.toString() + " *** HAS DISCONNECTED *** ");
          c = null;
        }
      }
      // print updated NAT Table and address pool
      System.out.println("NAT Table (after disconnection): ");
      NATUtilities.printTable(NATtable);
      System.out.println("Pool (after disconnection): ");
      NATUtilities.printTable(pool);
      clientMessage.close();
      output.close();
      client.close();
    } catch (IOException e) {
      for (ClientConnection c : clientThreads) {
        if (c == this) {
          System.out.println(c.self.toString() + " *** HAS TIMED OUT *** ");
          c = null;
        }
      }
    }

  }
}

