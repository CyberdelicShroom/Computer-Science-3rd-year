/**
 * This class provides a custom object to use for sending messages between 
 * the server and clients as well as among clients by specifying the payload, 
 * who it is sent to, and who it sent from.
 * 
 * @author Keagan Selwyn Gill
 */

import java.io.Serializable;

public class Message implements Serializable {
    private String text, from, to;

    /**
     * Constructor which specifies the payload and who it is sent from
     * 
     * @param text The actual text to send
     * @param from Where it is sent from
     */
    public Message(String text, String from) {
        this.text = text;
        this.from = from;
        this.to = "";
    }

    /**
     * Constructor which specifies the payload, who it is sent from, and
     * who it is sent to
     * 
     * @param text The payload data, i.e., the text
     * @param from the sender's username
     */
    public Message(String text, String from, String to) {
        this.text = text;
        this.from = from;
        this.to = to;
    }

    /**
     * @return returns the payload data, i.e., the text
     */
    public String text() {
        return text;
    }

    /**
     * @return returns the sender's username
     */
    public String from() {
        return from;
    }

    /**
     * @return returns the recipient's username
     */
    public String to() {
        return to;
    }

}

