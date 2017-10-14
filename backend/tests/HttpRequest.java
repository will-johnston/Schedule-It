import jdk.internal.util.xml.impl.Input;
import server.HTTPMessage;
import java.io.*;
import sun.net.www.http.HttpClient;

import java.net.*;

public class HttpRequest {
    public static synchronized String sendRequest(String address, HTTPMessage.HTTPMethod type, String message) throws Exception {
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
        String res = new String(buffer, 0, readresult);
        System.out.println(res);
        conn.disconnect();
        return res;
    }
    public static String getRequest(String address) throws Exception {
        return sendRequest(address, HTTPMessage.HTTPMethod.GET, "");
    }
    public static String postRequest(String address, String message) throws Exception {
        return sendRequest(address, HTTPMessage.HTTPMethod.POST, message);
    }
    private static String makeRequest(String address, HTTPMessage.HTTPMethod type, String message) {
        /*
        POST /api/user/login HTTP/1.1
        Host: scheduleit.duckdns.org
        Content-Type: application/json
        Cache-Control: no-cache
        Postman-Token: 33672a9f-461e-9cbd-dc2b-bbde358eda89

        {
          "name" : "string",
          "pass" : "string"
        }
         */
        //String host from address
        String[] addresses = breakAddress(address);
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s %s HTTP/1.1\n", HTTPMessage.getHTTPMethodName(type), addresses[1]));
        builder.append(String.format("Host: %s\n", addresses[0]));
        //builder.append("Content-Type: application/json\n");
        //builder.append("Source: ScheduleIt Tester\n");
        //builder.append("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0\n");
        //builder.append("Accept: */*;q=0.1\n");
        //builder.append(String.format("Content-Length: %d\n\n", message.length()));
        //builder.append("\n");
        //builder.append(message);
        return builder.toString();
    }
    //Return [host, field]
    private static String[] breakAddress(String address) {
        int first = address.indexOf('/');
        if (first == -1) {
            //Requested / on the host
            return new String[] { address, "/" };
        }
        else {
            return new String[] { address.substring(0,first), address.substring(first)};
        }
    }
}
