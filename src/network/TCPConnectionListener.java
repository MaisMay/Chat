package network;

public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection tcpConnection);
    void onReseiveString(TCPConnection tcpConnection,String value);
    void onDisconnect(TCPConnection tcpConnection);
    void onException(TCPConnection tcpConnection,Exception e);

}
