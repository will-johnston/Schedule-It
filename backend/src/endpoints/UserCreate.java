package endpoints;

import database.AddUserToDb;
import server.*;
import com.google.gson.*;

import java.net.Socket;

//Creates a new User in the system
public class UserCreate implements IAPIRoute {
    @Override
    public void setup() {

    }

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        String[] args = parseArgs(request.getBody());
        AddUserToDb.addUser("0", args[4], args[2], args[1], args[0], args[3]);
        Socketeer.sendJSON("\"cookie\": 1234\"", sock);
        //Socketeer.sendText(HTTPMessage.makeNotImplemented(), sock);
    }

    //email, pass, name, phone, username
    public String[] parseArgs(String message) {
        //id, username, fullname, password, email, phoneNumber
        try {
            Gson gson = new Gson();
            JsonObject bodyObj = gson.fromJson(message, JsonObject.class);
            if (!bodyObj.has("email")) {
                return null;
            }
            if (!bodyObj.has("pass")) {
                return null;
            }
            String[] arr = new String[5];
            arr[0] = bodyObj.get("email").getAsString();
            arr[1] = bodyObj.get("pass").getAsString();
            arr[2] = bodyObj.get("name").getAsString();
            arr[3] = bodyObj.get("phone").getAsString();
            arr[4] = arr[0].split("@")[0];
            return arr;
        }
        catch (Exception e) {
            System.out.println("Caught an exception");
            return null;
        }
    }
}
