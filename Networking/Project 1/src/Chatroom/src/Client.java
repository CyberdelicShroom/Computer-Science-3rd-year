/** The Client class is responsible for connecting
 * to the server and maintaining all input and output
 * from the server. This class therefore holds the 
 * input and output streams and assigns these to the 
 * relevant threads on startup. The code generated by 
 * the Netbeans IDE which handles the GUI is also present 
 * in this class. This class also contains the code, that 
 * had to be manually implemented, which handles the 
 * operations that occur upon clicking 
 * the buttons on the GUI.
 * 
 * @author Keagan Selwyn Gill
 **/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client extends javax.swing.JFrame {
    
    static Socket client;
    static DataInputStream in;
    static DataOutputStream out;
    static String hostAddress;
    static int Port;
    static String username;
    /**
     * Creates new form Client
     */
    public Client() {
        initComponents();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        hostAddressField = new javax.swing.JTextField();
        portField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        message_area = new javax.swing.JTextArea();
        message_text = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        connectButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        hostAddressField.setText("Host Address");

        portField.setText("Port");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Connect to server");

        usernameField.setText("Username");

        message_area.setColumns(20);
        message_area.setRows(5);
        jScrollPane1.setViewportView(message_area);

        message_text.setText("Message");

        sendButton.setText("Send");
        sendButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sendButtonMouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Chatroom");

        connectButton.setText("Connect");
        connectButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                connectButtonMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(148, 148, 148)
                                        .addComponent(jLabel1))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(hostAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(connectButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(message_text, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sendButton))
                            .addComponent(jScrollPane1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(182, 182, 182)
                        .addComponent(jLabel2)))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hostAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(connectButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(message_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendButton))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /** This method executes when the user clicks the send button.
     *  It stores the input from the user given in the message box,
     *  writes message out to the data output stream of the user,
     *  sets the message box text to an empty string, and then
     *  checks if the user entered the quit command and terminates
     *  the program if so.
     **/
    private void sendButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sendButtonMouseClicked
        try {
            String msg = message_text.getText();
            out.writeUTF(msg);
            message_text.setText("");
            if(msg.equals("/quit")){
                System.exit(0);
            }
        } catch(IOException e) {
        
        }
    }//GEN-LAST:event_sendButtonMouseClicked

    /** This method executes when the user clicks the connect button.
     *  It stores the input from the user given in the host address field,
     *  port number field and username field. It then creates a new socket
     *  with the stored host address and port number. It then writes out
     *  the username string to the data output stream of the user, reads
     *  in a string from the server via the data input stream which contains
     *  a string indicating that a new user has connected, then that string
     *  gets appended to the message area field. Finally, a new InputHandler
     *  instance is created as well as a Thread which then runs that 
     *  InputHandler instance.
     **/
    private void connectButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_connectButtonMouseClicked
        hostAddress = hostAddressField.getText();
        Port = Integer.parseInt(portField.getText());
        username = usernameField.getText();
        try {
            client = new Socket(hostAddress,Port);
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());

            out.writeUTF(username);
            String connected_msg = in.readUTF();
            message_area.setText(connected_msg);
            
            InputHandler inHandler = new InputHandler();
            Thread t = new Thread(inHandler);
            t.start();
        } catch (IOException ex) {

        }
    }//GEN-LAST:event_connectButtonMouseClicked
    
    /** This method closes all data streams of the client 
     * and closes the clients socket.
     **/
    public void shutdown() {
        try {
            in.close();
            out.close();
            if(!client.isClosed()){
                client.close();
            }
        } catch (IOException e) {

        }
    }

    class InputHandler implements Runnable {
        // This class handles all the input coming from the server as well as
        // other clients. It listens for input, stores the input into a variable
        // called 'msgin' and then appends this input string into the message
        // area field. If there is an issue running this, then the shutdown
        // method is called.
        @Override
        public void run() {
            try {
                String msgin = "";
                while ((msgin = in.readUTF()) != null) {
                    message_area.setText(message_area.getText() + "\n" + msgin);
                }
            } catch (Exception e) {
                shutdown();
            }
        }

    }

    public static void main(String args[]) {
        
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client().setVisible(true);
                
            }
        });
        
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton connectButton;
    private javax.swing.JTextField hostAddressField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea message_area;
    private javax.swing.JTextField message_text;
    private javax.swing.JTextField portField;
    private javax.swing.JButton sendButton;
    private javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
