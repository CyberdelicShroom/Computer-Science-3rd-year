/**This class represents a mapping of a client connection to the NAT Box as an entry in the NAT Box's Table.
 * Each entry contains an externally valid address and an internal IP.
 * 
 * @author Keagan Selwyn Gill
 **/

package NAT;

class NATtableEntry {

    private String internalIP;
    private String externalIP;
    private long time;

    public NATtableEntry(String internalIP, String externalIP) {
        this.internalIP = internalIP;
        this.externalIP = externalIP;
    }

    public NATtableEntry(String internalIP, String externalIP, long time) {
        this.internalIP = internalIP;
        this.externalIP = externalIP;
        this.time = time;
    }

    public String getInternalIP() {
        return this.internalIP;
    }

    public String getExternalIP() {
        return this.externalIP;
    }

    public long getTime() {
        return this.time;
    }

    public void setExternalIP(String externalIP) {
        this.externalIP = externalIP;
    }

    // outputs the internal and external IP of the client.
    @Override
    public String toString() {
        return "(Internal IP = " + internalIP + ", External IP = " + externalIP + ")";
    }
}
