/**
 * This class runs on a new thread that creates a peer-to-peer connection
 * for receiving file data and handles the receiving of file data in chunks.
 * 
 * @author Keagan Selwyn Gill
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;

public class DownloadData implements Runnable {
    private JProgressBar progressBar;
    private boolean isPaused;
    private JTextArea message_area;
    private int port;

    /**
     * Constructor for initiating a new download thread
     * 
     * @param progressBar The recipient's GUI progress bar for downloading
     * @param message_area The recipient's JTextArea for displaying messages
     * @param port the recipient's port number
     */
    public DownloadData(JProgressBar progressBar, JTextArea message_area, int port) {
        this.progressBar = progressBar;
        this.message_area = message_area;
        this.port = port;
    }

    @Override
    public void run() {

        ServerSocket serverSocket = null;
        Socket socket = null;
        ObjectInputStream ois = null;
        String fileName = null;
        try {
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            ois = new ObjectInputStream(socket.getInputStream());

            // receive file name and file size
            fileName = (String) ois.readObject();
            long size = (long) ois.readObject();
            int fileSize = (int) size;
            System.out.println("File download started");
            System.out.println("FILE SIZE = " + fileSize);

            // calculate buffer size to be 1% of file size
            int bufferSize = fileSize / 100;
            if (bufferSize > 64 * 1024) {
                bufferSize = 64 * 1024;
            }
            byte[] buffer = new byte[bufferSize];
            int read = 0;
            int totalRead = 0;
            FileOutputStream fos = new FileOutputStream(fileName);
            progressBar.setMaximum(fileSize);
            // receive file in chunks
            while ((read = ois.read(buffer)) != -1) {
                totalRead += read;
                
                // if paused, wait
                isPaused = ClientWindow.isPaused;
                while (isPaused) {
                    isPaused = ClientWindow.isPaused;
                    System.out.println("isPaused in loop = " + isPaused);
                }
                // write the file data to a new local file
                // and update the progress bar
                fos.write(buffer, 0, read);
                progressBar.setValue(totalRead);
                System.out.println("Download progress = " + totalRead + "/" + fileSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            // close connection after download has completed
            if (ois != null) {
                ois.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
            message_area.append("File '" + fileName + "' has been received successfully!\n");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progressBar.setValue(0);
        } catch (IOException e) {

        }

    }

}
