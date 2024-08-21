/**
 * This class runs on a new thread that listens for and handles input from 
 * the server and clients. It handles file uploads and downloads, file searches,
 * and decryption of the message-key. It implements AES decryption for
 * decrypting an encrypted message-key that gets sent along with user and 
 * file data each time a file is transferred.
 * 
 * @author Keagan Selwyn Gill
 */

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.swing.*;

public class ClientListenerThread implements Runnable {

    private String username;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private JFrame frame;
    private JTextArea message_area;
    public static boolean isPaused;

    /**
     * Constructor for this class
     * 
     * @param socket      for connection to server
     * @param ois         for receiving messages
     * @param oos         for sending messages
     * @param message_area for displaying received messages
     */
    public ClientListenerThread(String username, Socket socket, ObjectInputStream ois, ObjectOutputStream oos,
            JTextArea message_area) {
        this.username = username;
        this.socket = socket;
        this.ois = ois;
        this.oos = oos;
        this.message_area = message_area;
    }

    /** 
     * This method searches for exact or substring matches of a given file name.
     * @param str the string that the client is searching for
     */
    public String search(String str) {
        ArrayList<File> files = ClientWindow.uploadedFiles;
        boolean[] included = new boolean[files.size()];
        for (int i = 0; i < included.length; i++) {
            included[i] = false;
        }
        String result = "/results ";

        // Add exact matches
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getName().toLowerCase().equals(str.toLowerCase())) {
                result += files.get(i) + "@";
                included[i] = true;
            }
        }
        // Add substring matches
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getName().toLowerCase().contains(str.toLowerCase()) && included[i] == false) {
                result += files.get(i).getName() + "@";
                included[i] = true;
            }
        }
        return result;
    }

    /**
     * Handles the output of received messages, file uploading, file download
     * requests, search requests, and message-key decryption.
     * 
     * @param message The Message object received from either the server or 
     * a client
     */
    public void handleMessage(Message message) {
        if (message.from().equals("SERVER")) {
            // User activity updates from server
            if (message.text().endsWith("has entered the chat!")) {
                message_area.append(message.text()+"\n");
            } else if (message.text().endsWith("has left the chat!")) {
                message_area.append(message.text()+"\n");
            } else if (message.text().startsWith("The following users ")) {
                message_area.append(message.text()+"\n");
            }
        } else if (message.text().startsWith("/search")) {
            // Handle search request
            if (message.from().equals(username)) {
                return;
            }
            String result = search(message.text().split("/search ", 2)[1]);
            if (result.equals("/results ")) {
                return;
            }
            
            try {
                oos.writeObject(new Message(result, username, message.from()));
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (message.text().startsWith("/results ")) {
            // Display results from file search
            String text = message.text().split("/results ")[1];
            String[] files = text.split("@");
            for (String file : files) {
                ClientWindow.searchFiles.add(file);
                ClientWindow.searchNames.add(message.from());
                ClientWindow.searchNum++;

                String line = ClientWindow.searchNum + " - " + file + "\n";
                message_area.append(line);
            }
        } else if (message.text().startsWith("/download ")) {
            // Handle download request
            String parts[] = message.text().split(" ", 5);
            String ciphertext = parts[1];
            String host = parts[2];
            int sendPort = Integer.parseInt(parts[3]);
            String filename = parts[4];
            
            // Security measure: Prompt the client whether they accept a
            // download request of one of their files

            int reply = JOptionPane.showConfirmDialog(null,
                    "Do you accept the download request of the file '" 
                            + filename + "' from " + message.from() + "?", "Download", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                // Send encrypted message-key back to originating client
                try {
                    oos.writeObject(new Message("@" + message.from() + " /key " + ciphertext, username));
                    oos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Thread thread = new Thread(new UploadData(host, filename, ClientWindow.progressBarUpload, sendPort));
                thread.start();
            } else {
                try {
                    oos.writeObject(new Message("@" + message.from() + " /denied ", username));
                    oos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (message.text().startsWith("/key ")) {
            // Get encrypted message-key, message-key, and aesKey.
            // Then decrypt the encrypted message-key and check if
            // it matches the original message-key. If so, then start
            // new download thread.
            String ciphertext = message.text().split("/key ", 2)[1];
            // System.out.println("Ciphertext in ClientListenerThread:");
            // System.out.println(ciphertext);
            // System.out.println("-------------------------------------");
            byte[] plaintext = null;
            String message_key_str = null;
            String plaintext_str = null;
            try {
                // System.out.println("AES Key in ClientListenerThread:");
                // System.out.println(Base64.getEncoder().encodeToString(ClientWindow.aesKey.getEncoded()));
                // System.out.println("-------------------------------------");
                // System.out.println("message_key from ClientListenerThread:");
                message_key_str = new String(ClientWindow.message_key, StandardCharsets.UTF_8);
                // System.out.println(message_key_str);
                // System.out.println("-------------------------------------");
                plaintext = AES_ECB_Decrypt(ClientWindow.aesKey, ciphertext.getBytes(StandardCharsets.UTF_8));
                // System.out.println("plaintext in ClientListenerThread:");
                plaintext_str = new String (plaintext, StandardCharsets.UTF_8);
                // System.out.println(plaintext_str);
                // System.out.println("-------------------------------------");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if(message_key_str.equals(plaintext_str)) {
                Thread thread = new Thread(new DownloadData(ClientWindow.progressBarDownload, message_area, ClientWindow.receivePort));
                thread.start();
            } else {
                message_area.append("SERVER: Keys do not match\n");
            }
                
        } else if (message.text().startsWith("/denied ")) {
            message_area.append(message.from()+ " denied your download request.\n");
        } else {
            // Display text message from given client
            String text = message.from() + ": " + message.text();
            message_area.append(text + "\n");
        }
        
        
    }

    // This method applys AES decryption on the given byte array using the
    // given key and the ECB & PKCS5PAdding options
    public static byte[] AES_ECB_Decrypt(SecretKey key, byte[] cipherText) throws Exception {
        // Init AES-ECB and then decrypt the ciphertext
        byte[] decoded = Base64.getDecoder().decode(cipherText);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(decoded);
    }

    /**
     * Closes socket and streams neatly and exits
     */
    public void shutdown() {
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
            e.printStackTrace();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * Thread execution that waits for messages while connected to server
     */
    @Override
    public void run() {
        if (socket.isConnected()) {
            try {
                ClientWindow.receivePort = ois.readInt();
            } catch (Exception e) {
                e.printStackTrace();
                shutdown();
            }
        }
        
        Message msg;
        try {
            while ((msg = (Message)ois.readObject()) != null) {
                handleMessage(msg);
            }
        } catch (Exception e) {
            shutdown();
        }
        
    }
}