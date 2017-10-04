package server;

import java.io.OutputStream;
import java.net.*;
import java.nio.charset.Charset;

//Handles misc Socket tasks
public class Socketeer {
    //Return true if send was successful, false if could not send
    public static Boolean send(String message, Socket sock) {
        if (sock.isClosed()) {
            System.out.println("Socket is Closed");
            return false;
        }
        if (!sock.isConnected()) {
            //try to reconnect
            try {
                sock.connect(sock.getRemoteSocketAddress());
            }
            catch (Exception e) {
                System.out.println("Socket is not connected");
                return false;
            }
        }
        if (sock.isOutputShutdown()) {
            System.out.println("Socket output is shutdown");
            return false;
        }
        try {
            //System.out.println("Try to send " + message);
            OutputStream out = sock.getOutputStream();
            out.write(message.getBytes(Charset.forName("UTF-8")));
            out.flush();
            sock.close();
            System.out.println("Sent!");
        }
        catch (Exception e) {
            System.out.println("Failed to write out stream");
            return false;
        }
        return true;
        /*String response = HTTPMessage.makeBasicResponse(message);
            out.write(response.getBytes(Charset.forName("UTF-8")));
            out.flush();
            sock.close();*/
    }
}
