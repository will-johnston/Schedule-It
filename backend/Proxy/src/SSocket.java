import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

//handles delegating communication over either a normal socket or an ssl socket
public class SSocket {
    private Socket sock;
    private SSLSocket sslsock;
    private boolean isSSL;
    public SSocket(Socket sock, boolean isSSL) {
        this.sock = sock;
        isSSL = false;
    }
    public SSocket(SSLSocket sslsock, boolean isSSL) {
        this.sslsock = sslsock;
        this.isSSL = isSSL;
    }
    public void close() throws IOException {
        if (isSSL) {
            sslsock.close();
        }
        else {
            sock.close();
        }
    }
    public void connect(SocketAddress endpoint) throws IOException {
        if (isSSL) {
            sslsock.connect(endpoint);
        }
        else {
            sock.connect(endpoint);
        }
    }
    public boolean isClosed() {
        if (isSSL) {
            return sslsock.isClosed();
        }
        else {
            return sock.isClosed();
        }
    }
    public OutputStream getOutputStream() throws IOException {
        if (isSSL) {
            return sslsock.getOutputStream();
        }
        else {
            return sock.getOutputStream();
        }
    }
    public InputStream getInputStream() throws IOException {
        if (isSSL) {
            return sslsock.getInputStream();
        }
        else {
            return sock.getInputStream();
        }
    }
    public boolean isOutputShutdown() {
        if (isSSL) {
            return sslsock.isOutputShutdown();
        }
        else {
            return sock.isOutputShutdown();
        }
    }
    public SocketAddress getRemoteSocketAddress() {
        if (isSSL) {
            return sslsock.getRemoteSocketAddress();
        }
        else {
            return sock.getRemoteSocketAddress();
        }
    }
    public boolean isConnected() {
        if (isSSL) {
            return sslsock.isConnected();
        }
        else {
            return sock.isConnected();
        }
    }
}
