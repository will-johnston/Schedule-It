package endpoints;

import server.*;
import java.net.*;
import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by Ryan on 9/20/2017.
 */
public class ExampleEndpoint implements IAPIRoute {

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        try {
            OutputStream out = sock.getOutputStream();
            String message = "Recieved body: " + request.getBody();
            String response = HTTPMessage.makeBasicResponse(message);
            out.write(response.getBytes(Charset.forName("UTF-8")));
            out.flush();
            sock.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
