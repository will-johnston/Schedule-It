package server; /**
 * Created by Ryan on 9/20/2017.
 */

import endpoints.IAPIRoute;
import server.HTTPMessage;
import server.Router;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Server {
    private Boolean isSSL;
    Boolean isListening;
    private ServerSocket sock;
    private SSLServerSocket sslSock;
    private Thread listenThread;
    private Thread securelistenThread;

    public Server(int port, int secureport) {
        try {
            System.setProperty("javax.net.ssl.keyStore", "certificate.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "scheduleit");
            java.lang.System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");

            sock = new ServerSocket(port);
            SSLServerSocketFactory factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            sslSock = (SSLServerSocket)factory.createServerSocket(secureport);
            isSSL = false;
            isListening = false;
            listenThread = null;
        } catch (IOException e) {
            e.printStackTrace();
            //System.out.println(String.format("server.Server cannot bind to port %d or port , already in use!", port));
        }
    }

    public void startListening(Router router) {
        try {
            isListening = true;
            listenThread = new Listener(sock, router);
            securelistenThread = new Listener(sslSock, router);

            listenThread.start();
            securelistenThread.start();
            System.out.println("Started listening");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopListening() {

    }

    class Listener extends Thread {
        ServerSocket mainServer;
        SSLServerSocket secureMainServer;
        boolean calledQuit;
        Router router;
        boolean isSsl;

        public Listener(ServerSocket server, Router router) {
            try {
                mainServer = server;
                calledQuit = false;
                this.router = router;
                this.isSsl = false;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        public Listener(SSLServerSocket server, Router router) {
            try {
                secureMainServer = server;
                calledQuit = false;
                this.router = router;
                this.isSsl = true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            //Run until the program has requested stop/quit
            while (!calledQuit) {
                try {
                    if (isSsl) {
                        SSocket recievedSock = new SSocket(secureMainServer.accept(), isSsl);
                        new Handler(recievedSock, router).run();
                    }
                    else {
                        SSocket recievedSock = new SSocket(mainServer.accept(), isSsl);
                        new Handler(recievedSock, router).run();
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void requestStop() {
            calledQuit = true;
        }
    }

    class Handler extends Thread {
        Router router;
        SSocket sock;

        public Handler(SSocket sock, Router router) {
            this.router = router;
            this.sock = sock;
        }

        public void run() {
            try {
                InputStream in = sock.getInputStream();
                OutputStream out = sock.getOutputStream();
                //Max recieve size is 8 KB, allow for overhead
                byte[] buffer = new byte[9216];
                int readResult = in.read(buffer);
                String header = new String(buffer, 0, readResult, StandardCharsets.UTF_8);
                //System.out.println("header: " + header);
                //System.out.println(String.format("server.Header length: %d, read: %d", header.length(), readResult));
                HTTPMessage mess;
                try {
                    mess = new HTTPMessage(header);
			System.out.println("Server recieved " + mess.method);
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
                //out.write(server.HTTPMessage.makeBasicResponse("{ \"message\": \"Hello World\" }").getBytes(Charset.forName("UTF-8")));
                /*String response = server.HTTPMessage.makeResponse("{ \"message\": \"Hello World\" }", server.HTTPMessage.HTTPStatus.OK);
                out.write(response.getBytes(Charset.forName("UTF-8")));
                out.flush();
                sock.close();*/
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("e.Message: " + e.getLocalizedMessage());
            }
        }
    }

    //Getters and Setters

    public Boolean getListening() {
        return isListening;
    }

    public Boolean getSSL() {
        return isSSL;
    }

}
