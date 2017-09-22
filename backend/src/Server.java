/**
 * Created by Ryan on 9/20/2017.
 */

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class Server {
    private Boolean isSSL;
    Boolean isListening;
    private ServerSocket sock;
    protected SSLServerSocket sslSock;
    private Thread listenThread;

    public Server(int port) {
        try {
            sock = new ServerSocket(port);
            isSSL = false;
            isListening = false;
            listenThread = null;
        } catch (IOException e) {
            System.out.println(String.format("Server cannot bind to port %d, already in use!", port));
        }
    }

    public Server(int port, String sslCertificate) {
        throw new NotImplementedException();
    }

    public void startListening(Router router) {
        isListening = true;
        listenThread = new Listener(sock, router);
        listenThread.start();
        System.out.println("Started listening");
    }

    public void stopListening() {

    }

    class Listener extends Thread {
        ServerSocket mainServer;
        Boolean calledQuit;
        Router router;

        public Listener(ServerSocket server, Router router) {
            mainServer = server;
            calledQuit = false;
            this.router = router;
        }

        public void run() {
            //Run until the program has requested stop/quit
            while (!calledQuit) {
                try {
                    Socket recievedSock = mainServer.accept();
                    new Handler(recievedSock, router).run();
                } catch (SocketException e) {

                } catch (IOException e) {

                }
            }
        }

        public void requestStop() {
            calledQuit = true;
        }
    }

    class Handler extends Thread {
        Router router;
        Socket sock;

        public Handler(Socket sock, Router router) {
            this.router = router;
            this.sock = sock;
        }

        public void run() {
            try {
                InputStream in = sock.getInputStream();
                OutputStream out = sock.getOutputStream();
                byte[] buffer = new byte[8192];
                int readResult = in.read(buffer);
                String header = new String(buffer, 0, readResult, StandardCharsets.UTF_8);
                //System.out.println("header: " + header);
                //System.out.println(String.format("Header length: %d, read: %d", header.length(), readResult));
                HTTPMessage mess;
                try {
                    mess = new HTTPMessage(header);
                    //mess.printDebugString();
                } catch (Exception e) {
                    System.out.println("Failed to parse HTTP Message");
                    System.out.println(String.format("Exception Message: %s", e.getMessage()));
                    e.printStackTrace();
                    String response = HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.Unknown);
                    out.write(response.getBytes(Charset.forName("UTF-8")));
                    out.flush();
                    sock.close();
                    return;
                }
                if (router.containsMethod(mess.method)) {
                    IAPIRoute endpoint = router.get(mess.method);
                    endpoint.execute(sock, mess);
                }
                else {
                    String response = HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.BadRequest);
                    out.write(response.getBytes(Charset.forName("UTF-8")));
                    out.flush();
                    sock.close();
                }
                //out.write(HTTPMessage.makeBasicResponse("{ \"message\": \"Hello World\" }").getBytes(Charset.forName("UTF-8")));
                /*String response = HTTPMessage.makeResponse("{ \"message\": \"Hello World\" }", HTTPMessage.HTTPStatus.OK);
                out.write(response.getBytes(Charset.forName("UTF-8")));
                out.flush();
                sock.close();*/
            } catch (Exception e) {
                System.out.println("e.Message: " + e.getLocalizedMessage());
            }
        }
    }
}