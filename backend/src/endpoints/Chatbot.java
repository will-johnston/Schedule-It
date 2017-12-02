package endpoints;

import server.HTTPMessage;
import server.SSocket;
import server.Socketeer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Chatbot implements IAPIRoute {
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        try {
            String response = sendRequest("http://willjohnston.pythonanywhere.com/api/chatterbot/",
                    HTTPMessage.HTTPMethod.POST, request.getBody());
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.OK), sock);
            return;
        }
        catch (Exception e) {
            Socketeer.send(HTTPMessage.makeResponse("{\"error\":\"chatbot returned not 200\")", HTTPMessage.HTTPStatus.Unknown), sock);
            return;
        }
    }

    public synchronized String sendRequest(String address, HTTPMessage.HTTPMethod type, String message) throws Exception {
        //make request
        /*String request = makeRequest(address, type, message);
        String[] addresses = breakAddress(address);
        System.out.println(request);
        //send request
        Socket sock = new Socket(addresses[0], 80);
        InputStream in = sock.getInputStream();
        OutputStream out = sock.getOutputStream();
        out.write(request.getBytes());
        out.flush();
        out.close();
        byte[] buffer = new byte[8192];     //8KB buffer
        int readlength = in.read(buffer);
        in.close();
        String response = new String(buffer, 0 , readlength);
        System.out.println(response);
        return new HTTPMessage(response);*/
        URL obj = new URL(address);
        HttpURLConnection conn = (HttpURLConnection)obj.openConnection();
        conn.setRequestMethod(HTTPMessage.getHTTPMethodName(type));
        if (type == HTTPMessage.HTTPMethod.POST) {
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            out.write(message.getBytes());
            out.flush();
        }
        else {
            conn.setDoInput(true);
        }
        InputStream in = conn.getInputStream();
        byte[] buffer = new byte[8192];
        int readresult = in.read(buffer);
        if (readresult == -1) {
            return null;
        }
        String res = new String(buffer, 0, readresult);
        System.out.println(res);
        conn.disconnect();
        return res;
    }
}
