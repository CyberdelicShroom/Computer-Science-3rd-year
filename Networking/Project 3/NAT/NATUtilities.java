/**This class contains methods for necessary NAT routines.
 * The methods include, generating IP and MAC addresses, constructing 
 * a packet for transfer and parsing a packet so that is can be represented
 * in text and printed to the console. It also has a method to print the
 * current NAT Table to the console.
 * 
 * @author Keagan Selwyn Gill
 **/

package NAT;

import java.util.LinkedList;
import java.util.Random;

public class NATUtilities {

    
    /** This method generates a random MAC address
     * @return the MAC address string
     **/
    // Used source code for MAC address generator from stackoverflow (https://stackoverflow.com/a/24262057)
    static String generateRandomMACAddress() {
        Random rand = new Random();
        byte[] macAddr = new byte[6];
        rand.nextBytes(macAddr);
        macAddr[0] = (byte) (macAddr[0] & (byte) 254);
        StringBuilder sb = new StringBuilder(18);

        for (byte b : macAddr) {
            if (sb.length() > 0) {
                sb.append(":");
            }
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /** This method generates a random IP address
     * @return the IP address string
     **/
    static String generateRandomIPAddress() {
        return ((int)(Math.random()*256) + "." + (int)(Math.random()*256) + "." + (int)(Math.random()*256) + "." + (int)(Math.random()*256));
    }

    /** This method constructs a packet with the format of source IP, destination IP 
     *  and payload string, with a hashtag symbol as a delimiter.
     * @param source source IP address
     * @param destination destination IP address
     * @param payload the payload string in the form of a byte array
     * @return the packet to tranfer
     **/
    static byte[] constructPacket(String source, String destination, byte[] payload) {
        byte[] srcBytes = source.getBytes();
        byte[] destBytes = destination.getBytes();
        byte delimiter = '#';
        byte[] packet = new byte[srcBytes.length + destBytes.length + payload.length + 2];
        int j = 0;

        for (int i = 0; i < srcBytes.length; i++) {
            packet[i] = srcBytes[j];
            j++;
        }
        packet[srcBytes.length] = delimiter;
        j = 0;
        for (int i = srcBytes.length + 1; i < srcBytes.length + destBytes.length + 1; i++) {
            packet[i] = destBytes[j];
            j++;
        }
        packet[srcBytes.length + destBytes.length + 1] = delimiter;
        j = 0;
        for (int i = srcBytes.length + destBytes.length + 2; i < packet.length; i++) {
            packet[i] = payload[j];
            j++;
        }
        return packet;
    }

    /** This method splits packet into a string array with the use of 
     *  the hashtag delimiter.
     * @param packet the packet transferred
     * @return the string array with format: [Source IP, Destination IP, Payload string]
     **/
    static String[] parsePacket(byte[] packet) {
        return (new String(packet)).split("#");
    }

    /** This method prints the desired current NAT Table to the console.
     * @param table the NAT Table to be printed
     **/
    static void printTable(LinkedList<NATtableEntry> table) {
        for (int i = 0; i < table.size(); i++) {
            System.out.println(table.get(i).toString());
        }
        System.out.println();
    }
}
