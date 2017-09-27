package server;

import java.io.OutputStream;
import java.net.*;
import java.nio.charset.Charset;

//Handles misc Socket tasks
public class Socketeer {
    //Return true if send was successful, false if could not send
    public static Boolean sendText(String message, Socket sock) {
        if (sock.isClosed()) {
            return false;
        }
        if (!sock.isConnected()) {
            //try to reconnect
            try {
                sock.connect(sock.getRemoteSocketAddress());
            }
            catch (Exception e) {
                return false;
            }
        }
        if (sock.isOutputShutdown()) {
            return false;
        }
        try {
            OutputStream out = sock.getOutputStream();
            out.write(message.getBytes(Charset.forName("UTF-8")));
            out.flush();
            sock.close();
        }
        catch (Exception e) {
            return false;
        }
        return true;
        /*String response = HTTPMessage.makeBasicResponse(message);
            out.write(response.getBytes(Charset.forName("UTF-8")));
            out.flush();
            sock.close();*/
    }
    //Return true if send was successful, false if could not send
    public static Boolean sendJSON(String json, Socket sock) {
        //Not implemented
        return false;
    }
}
