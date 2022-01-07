package client;

import network.TCPConnection;
import network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;

public class ClientWindow extends JFrame  implements ActionListener, TCPConnectionListener {
    private  static  final  String IP_ADDR = "localhost";
    private  static  final  int PORT = 8189;
    private  static  final  int WIDTH = 600;
    private  static  final  int HEIGHT = 600;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickName = new JTextField("Anonymus");
    private final JTextField fieldInput = new JTextField();


    private  TCPConnection connection;



    private ClientWindow () {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // установим выход на крестик
        setSize(WIDTH,HEIGHT); // установим размер окна
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setVisible(true);
        log.setEditable(false);
        log.setLineWrap(true);
        fieldInput.addActionListener(this);
        add(log, BorderLayout.CENTER);
        add(fieldInput,BorderLayout.SOUTH);
        add(fieldNickName,BorderLayout.NORTH);
        try {
            connection = new TCPConnection(this, IP_ADDR,PORT);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if(msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickName.getText()+ " : " + msg );
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready...");
    }

    @Override
    public void onReseiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection exception: " + e );
    }

    private synchronized void printMessage (String msg){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    log.append(LocalDateTime.now().toString() + " : " + msg + "\n");
                    log.setCaretPosition(log.getDocument().getLength());
                }
            });
    }
}
