package endpoints;

import server.*;
import com.google.gson.*;

import java.net.Socket;

//Logins into the system
//Should return a valid login cookie
public class UserLogin implements IAPIRoute {
    @Override
    public void execute(Socket sock, HTTPMessage request) {
        JsonObject bodyObj = gson.fromJson(request.getBody());
        JsonElement emailElement =  bodyObj.get("email");
        JsonElement passElement = bodyObj.get("pass");

        Socketeer.sendText(HTTPMessage.makeNotImplemented(), sock);
    }

    @Override
    public void setup() {

    }
}
