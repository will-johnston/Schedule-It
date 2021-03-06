package server;
/**
 * Created by Ryan on 9/20/2017.
 */

import endpoints.IAPIRoute;
import management.CertificateManager;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Stack;

public class Server {
    private Boolean isSSL;
    Boolean isListening;
    private ServerSocket sock;
    private SSLServerSocket sslSock;
    private Thread listenThread;
    private Thread securelistenThread;

    public Server(int port, int secureport) {
        try {
            CertificateManager.load();
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
			System.out.println("Recieved secure request");
                        SSocket recievedSock = new SSocket(secureMainServer.accept(), isSsl);
                        new Handler(recievedSock, router).run();
                    }
                    else {
			System.out.println("Recieved a plain request");
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
				/*Thread.sleep(5);			//Sleep 5 whole seconds
                byte[] buffer = new byte[9216];
                int readResult = in.read(buffer);
				System.out.println("In available: " + in.available());
                String header = new String(buffer, 0, readResult, StandardCharsets.UTF_8);
                System.out.println("header: " + header);
                System.out.println(String.format("server.Header length: %d, read: %d", header.length(), readResult));
                HTTPMessage mess;*/
                HTTPMessage mess;
                try {
                    mess = getRequest(in, sock);
                    if (mess == null) {
                        //timed out
                        System.out.println("Failed to parse HTTP Message, TIMED OUT");
                        String response = HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.GatewayTimeout);
                        out.write(response.getBytes(Charset.forName("UTF-8")));
                        out.flush();
                        sock.close();
                        return;
                    }
                    //System.out.println("Server recieved " + mess.method);
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
                    if (!sock.isClosed()) {
                        //endpoint failed, send error
                        Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.Unknown), sock);
                    }
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
        public HTTPMessage getRequest(InputStream in, SSocket sock) throws Exception {
            byte[] buffer = new byte[20480];     //was 8KB, now 20KB
            StringBuilder head = new StringBuilder();
            StringBuilder body = new StringBuilder();
            Timer timer = new Timer(30);        //30 second timeout
            boolean inBody = false;
            int length = -1;
            Stack<Character> jsonStack = new Stack<>();
            boolean hasPushed = false;
            while (!timer.hasExpired()) {
                if (sock.isClosed()) {
                    //client canceled
                    System.out.println("Client cancelled the request");
                    return null;
                }
                //read what there is, find content-length and verify that the data has been recieved
                if (in.available() > 0) {
                    int read = in.read(buffer);
                    String readstr = new String(buffer, 0, read);
                    String[] lines = readstr.split("\n");
                    for (int i = 0; i < lines.length; i++) {
                        System.out.println(lines[i]);
                        if (inBody) {
                            if (length == 0) {
                                //GET requests and other requests without bodies
                                return new HTTPMessage(head.toString(), body.toString());
                            }
                            //add body data
                            char[] chars = lines[i].toCharArray();
                            for (int j = 0; j < chars.length; j++) {
                                if (chars[j] == '{') {
                                    jsonStack.push('{');
				    //System.out.println("Pushed to json stack");
                                    hasPushed = true;
                                }
                                else if (chars[j] == '}') {
                                    jsonStack.pop();
                                }
                            }
				//System.out.println("Adding to body: " + lines[i]);
                            body.append(lines[i] + '\n');
                            /*if ((body.length() - 1) == length || body.length() == length) {
                                return new HTTPMessage(head.toString(), body.toString());
                            }
                            else {
                                System.out.println(String.format("Builder length is %d, -1 is %d, content length is %d", body.length(), body.length() - 1, length));
                            }*/
                            if (jsonStack.empty() && hasPushed) {
                                return new HTTPMessage(head.toString(), body.toString());
                            }
                        }
                        else {
                            //add request headers
                            if (lines[i].contains("Content-Length")) {
                                //add to builder then parse line
                                head.append(lines[i] + '\n');
                                String[] keyvalue = lines[i].split(":");
                                if (keyvalue.length < 2) {
                                    System.out.println("Content-Length is improperly formatted");
                                    //System.out.println("Was: " + lines[i]);
                                    return null;
                                }
                                try {
                                    length = Integer.parseInt(keyvalue[1].trim());
                                }
                                catch (Exception e) {
                                    System.out.println("Couldn't parse Content-Length value");
                                    //System.out.println("Was: " + lines[i]);
                                    return null;
                                }
                            }
                            else if (lines[i].length() <= 1) {
                                //encountered the body
				//System.out.println("In the body");
                                inBody = true;
                                /*if (length == -1) {
				    System.out.println("No Content-Length specified");
                                    //No Content-Length specified
                                    return new HTTPMessage(head.toString(), body.toString());
                                }*/
                                continue;
                            }
                            else {
				//System.out.println("Length: " + lines[i].length());
                                head.append(lines[i] + '\n');
                            }
                        }
                    }
                }
                Thread.sleep(50);
            }
            if (timer.hasExpired()) {
                return  null;
            }
            return null;
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
