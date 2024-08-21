/**
 * This class runs on a new thread that connects to a peer-to-peer connection
 * for sending file data and handles the sending of file data in chunks.
 * 
 * @author Keagan Selwyn Gill
 */

import java.io.*;
import java.net.Socket;
import javax.swing.JProgressBar;

public class UploadData implements Runnable {
    private String host, fileName;
    private JProgressBar progressBar;
    private int port;

    /**
     * Constructor for initiating a new upload thread
     * 
     * @param host The recipient's IPv4 address
     * @param fileName The name of the file being sent
     * @param port the recipient's port number
     */
    public UploadData(String host, String fileName, JProgressBar progressBar, int port) {
        this.host = host;
        this.fileName = fileName;
        this.progressBar = progressBar;
        this.port = port;

    }

    @Override
    public void run() {

        Socket socket = null;
        ObjectOutputStream oos = null;
        // create new socket to initiate peer-to-peer connection
        // and create new output stream
        try {
            socket = new Socket(host, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // iterate through all uploaded files to find if the desired file
        // to send is there and then run the operations to send the file data
        for (File file : ClientWindow.uploadedFiles) {
            if (file.getName().equals(fileName)) {
                try {
                    FileInputStream fileIn = new FileInputStream(file);
                    int bytes = 0;
                    // buffer size 1% of file size
                    int bufferSize = (int) file.length() / 100;
                    if (bufferSize > 64 * 1024) {
                        bufferSize = 64 * 1024;
                    }
                    byte[] buffer = new byte[bufferSize];
                    // send file name and file size
                    oos.writeObject(file.getName());
                    oos.writeObject(file.length());
                    int fileSize = (int) file.length();
                    System.out.println("File upload started");
                    System.out.println("FILE SIZE = " + fileSize);
                    int sent = 0;
                    progressBar.setMaximum(fileSize);
                    // send the file data in chunks
                    while ((bytes = fileIn.read(buffer)) != -1) {
                        // calculate sending amount remaining and 
                        // update progress bar
                        sent += bytes;
                        System.out.println("Upload progress = " + sent + "/" + fileSize);
                        progressBar.setValue(sent);
                        oos.write(buffer, 0, bytes);
                    }
                    oos.flush();
                    fileIn.close();

                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressBar.setValue(0);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // close connection after upload has completed
        try {
            if (oos != null) {
                oos.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            
        }
    }

}
