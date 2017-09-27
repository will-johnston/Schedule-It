package endpoints;

import server.*;
import com.google.gson.*;

import java.net.Socket;

//Logins into the system
//Should return a valid login cookie
// /api/user/login
public class UserLogin implements IAPIRoute {

    @Override
    public void setup() {

    }

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        Gson gson = new Gson();
        try {
            String[] args = parseArgs(request.getBody());
            if (args == null) {
                System.out.println("Args is null");
            }
            else {
                System.out.println(String.format("Email: %s, pass: %s", args[0], args[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Socketeer.sendText(HTTPMessage.makeNotImplemented(), sock);
    }
    //Returns an array of [email, password]
    private String[] parseArgs(String message) {
        try {
            Gson gson = new Gson();
            JsonObject bodyObj = gson.fromJson(message, JsonObject.class);
            if (!bodyObj.has("email")) {
                return null;
            }
            if (!bodyObj.has("pass")) {
                return null;
            }
            String[] arr = new String[2];
            arr[0] = bodyObj.get("email").getAsString();
            arr[1] = bodyObj.get("pass").getAsString();
            return arr;
        }
        catch (Exception e) {
            System.out.println("Caught an exception");
            return null;
        }
    }
}
