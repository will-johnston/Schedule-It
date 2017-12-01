import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Stack;

public class Server {
/**
 * Created by Ryan on 9/20/2017.
 */

    private Boolean isSSL;
    Boolean isListening;
    private ServerSocket sock;
    private SSLServerSocket sslSock;
    private Thread listenThread;
    //private Thread securelistenThread;

    public Server(int port, int secureport) {
        try {
            //CertificateManager.load();
            sock = new ServerSocket(port);
            //SSLServerSocketFactory factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            //sslSock = (SSLServerSocket)factory.createServerSocket(secureport);
            isSSL = false;
            isListening = false;
            listenThread = null;
        } catch (IOException e) {
            e.printStackTrace();
            //System.out.println(String.format("server.Server cannot bind to port %d or port , already in use!", port));
        }
    }

    public void startListening() {
        try {
            isListening = true;
            listenThread = new Listener(sock);
            //securelistenThread = new Listener(sslSock);

            listenThread.start();
            //securelistenThread.start();
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
        //SSLServerSocket secureMainServer;
        boolean calledQuit;
        boolean isSsl;

        public Listener(ServerSocket server) {
            try {
                mainServer = server;
                calledQuit = false;
                this.isSsl = false;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*public Listener(SSLServerSocket server) {
            try {
                secureMainServer = server;
                calledQuit = false;
                this.isSsl = true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        public void run() {
            //Run until the program has requested stop/quit
            while (!calledQuit) {
                try {
                    if (isSsl) {
                        System.out.println("Recieved secure request");
                        //SSocket recievedSock = new SSocket(secureMainServer.accept(), isSsl);
                        //new Handler(recievedSock).run();
                    }
                    else {
                        System.out.println("Recieved a plain request");
                        SSocket recievedSock = new SSocket(mainServer.accept(), isSsl);
                        new Handler(recievedSock).run();
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
        SSocket sock;

        public Handler(SSocket sock) {
            this.sock = sock;
        }

        public void run() {
            try {
                InputStream in = sock.getInputStream();
                OutputStream out = sock.getOutputStream();
                try {
                    Socket proxySocket = proxyin(in, sock);
                    if (proxySocket == null) {
                        //timed out
                        System.out.println("Failed to parse HTTP Message, TIMED OUT");
                        String response = HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.GatewayTimeout);
                        out.write(response.getBytes(Charset.forName("UTF-8")));
                        out.flush();
                        sock.close();
                        return;
                    }
                    else {
                        proxySocket.shutdownOutput();
                    }
                    if (proxyout(proxySocket, sock)) {
                        System.out.println("Proxy out succeeded");
                        if (!proxySocket.isClosed()) {
                            proxySocket.close();
                        }
                        if (!sock.isClosed()) {
                            sock.close();
                        }
                    }
                    else {
                        System.out.println("Failed to proxy out");
                        if (!proxySocket.isClosed()) {
                            proxySocket.close();
                        }
                        if (!sock.isClosed()) {
                            sock.close();
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Failed to proxy");
                    System.out.println(String.format("Exception Message: %s", e.getMessage()));
                    e.printStackTrace();
                    String response = HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.Unknown);
                    out.write(response.getBytes(Charset.forName("UTF-8")));
                    out.flush();
                    sock.close();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("e.Message: " + e.getLocalizedMessage());
            }
        }
        public Socket proxyin(InputStream in, SSocket sock) throws Exception {
            byte[] buffer = new byte[20480];     //was 8KB, now 20KB
            Timer timer = new Timer(5);        //30 second timeout
            boolean inBody = false;
            Stack<Character> jsonStack = new Stack<>();
            boolean hasPushed = false;
            int lineCount = 0;
            boolean gottem = false;
            int type = -1;
            Socket proxySocket = null;
            PrintWriter writer = null;
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
                            writer.write(lines[i] + '\n');
                            writer.flush();
                            if (type == 0) {
                                //get request
                                return proxySocket;
                            }
                            //add body data
                            char[] chars = lines[i].toCharArray();
                            for (int j = 0; j < chars.length; j++) {
                                if (chars[j] == '{') {
                                    jsonStack.push('{');
                                    //System.out.println("Pushed to json stack");
                                    hasPushed = true;
                                } else if (chars[j] == '}') {
                                    jsonStack.pop();
                                }
                            }
                            if (jsonStack.empty() && hasPushed) {
                                //proxySocket.shutdownOutput();
                                writer.write(0);
                                writer.flush();
                                return proxySocket;
                            }
                            lineCount++;
                        }
                        else {
                            if (!gottem) {
                                //Should be GET or POST or other HTTP methods
                                proxySocket = startRequest(lines[i]);
                                //proxySocket = new Socket("http://scheduleitbd.duckdns.org", 8181);
                                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proxySocket.getOutputStream())));
                                writer.write(lines[i] + '\n');
                                writer.flush();
                                lineCount++;
                                type = getType(lines[i]);
                                gottem=true;
                            }
                            else if (lines[i].length() <= 1) {
                                //encountered the body
                                System.out.println("In the body");
                                inBody = true;
                                lineCount++;
                                writer.write(lines[i] + '\n');
                                writer.flush();
                                if (type == 0) {
                                    System.out.println("GET REQUEST, skipping");
                                    writer.write('\n');
                                    writer.flush();
                                    //*writer.write('\n');
                                    writer.flush();
                                    return proxySocket;
                                }
                            }
                            else {
                                //System.out.println("Length: " + lines[i].length());
                                //head.append(lines[i] + '\n');
                                writer.write(lines[i] + '\n');
                                writer.flush();
                                lineCount++;
                            }
                        }
                    }
                }
                Thread.sleep(50);
            }
            if (timer.hasExpired()) {
                return null;
            }
            return null;
        }
        public boolean proxyout(Socket proxySocket, SSocket sock) throws Exception {
            //copy all output data from proxySocket to output of sock
            if (proxySocket.isClosed() || sock.isClosed()){
                System.out.println("Called proxyout but one of the sockets is closed already");
                return false;
            }
            InputStream proxyin = proxySocket.getInputStream();
            OutputStream out = sock.getOutputStream();
            /*for (int i = 0; i < 5; i++) {
                if (proxyin.available() == 0) {
                    System.out.println("Available: 0");
                    Thread.sleep(10);
                }
                if (i == 4) {
                    System.out.println("Recieved nothing from host");
                    return false;
                }
            }
            while (proxyin.available() > 0) {
                if (proxySocket.isClosed() || proxySocket.isInputShutdown()) {
                    System.out.println("Proxy closed unexpectedly");
                    return false;
                }
                if (sock.isClosed() || sock.isOutputShutdown()) {
                    System.out.println("Client closed the connection");
                    return false;
                }
                char c = (char)proxyin.read();
                out.write(c);
                out.flush();
                System.out.print(c);
            }*/
            boolean kill = false;
            char lastchar = 0;
            while (!kill) {
                char c = (char) proxyin.read();
                if (c > 60000) {
                    kill = true;
                    break;
                }
                if (c != 0) {
                    out.write(c);
                    out.flush();
                    System.out.print(c);
                }
                /*if (c == '\n' && lastchar == '\n') {
                    kill = true;
                }*/
                if (c != '\r') {
                    lastchar = c;
                }
                if (c == '\n') {
                    int i = 0;
                }
                //lastchar = c;
            }
            return true;
        }
        public Socket startRequest(String line) throws Exception{
            String[] topSplit = line.split(" ");
            System.out.println("Started request with line:");
            System.out.println(line);
            //System.out.println(String.format("Line: '%s', split length: %d", line, topSplit.length));
            String method = topSplit[1];
            System.out.println("Method: " + method);
            if (method.equals("/user/groups/chat/bot")) {
                return new Socket("willjohnston.pythonanywhere.com", 80);
            }
            else {
                //normal proxy
                return new Socket("scheduleitdb.duckdns.org", 8181);
            }
        }
        //0 GET, 1 POST, -1 UNKNOWN
        public int getType(String line) {
            String[] topSplit = line.split(" ");
            System.out.println(String.format("Line: %s, split length: %d", line, topSplit.length));
            String type = topSplit[0];
            if (type.toLowerCase().equals("get")) {
                System.out.println("GET");
                return 0;
            }
            else if (type.toLowerCase().equals("post")) {
                System.out.println("POST");
                return 1;
            }
            else {
                System.out.println("UNKNOWN TYPE");
                return -1;
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

