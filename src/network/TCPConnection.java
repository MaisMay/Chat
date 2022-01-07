package network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {
    private final Socket socket;
    private final Thread rxThread; // будет слушать входящее соединение
    private TCPConnectionListener eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    // конструктор соединения (создаст сокет по параметрам)

    public TCPConnection(TCPConnectionListener eventListener,String ipAdrr, int port)  throws  IOException{
        this(eventListener, new Socket(ipAdrr,port)); // перегруз конструктора
    }

    // конструктор соединения (кто-то снаружи создаст сокет)

    public TCPConnection (TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));


        // поток слушает все входящее
        rxThread = new Thread(new Runnable() { // интерфейс ранбл
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()) { // пока поток не прерван
                        // получить строчку и передать ивентлистнеру
                        eventListener.onReseiveString(TCPConnection.this,in.readLine());
                    }


                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();


    }
    // метод отправки сообщения
    public synchronized void  sendString(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this,e);
            disconnect();
        }
    }
    // конец метода отправки сообщения
    // метод разрыва соединения
    public synchronized void  disconnect() {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
        // конец метода разрыва
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
