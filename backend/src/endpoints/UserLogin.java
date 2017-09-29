package endpoints;

import database.*;
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
                Socketeer.send(HTTPMessage.makeResponse("\"Error\":\"No Arguments specified\"", HTTPMessage.HTTPStatus.BadRequest, HTTPMessage.MimeType.appJson, true), sock);
            }
            else {
                System.out.println(String.format("name: %s, pass: %s", args[0], args[1]));
                User user = User.fromDatabase(args[0]);
                if (user == null) {
                    Socketeer.send(HTTPMessage.makeResponse("\"Error\":\"User doesn't exist\"", HTTPMessage.HTTPStatus.BadRequest, HTTPMessage.MimeType.appJson, true), sock);
                }
                else if (args[1] == user.getPassword()) {
                    Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
                }
                else {
                    Socketeer.send(HTTPMessage.makeResponse("\"Error\" : \"Invalid Password\"", HTTPMessage.HTTPStatus.BadRequest, HTTPMessage.MimeType.appJson, true), sock);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Returns an array of [email, password]
    private String[] parseArgs(String message) {
        try {
            Gson gson = new Gson();
            JsonObject bodyObj = gson.fromJson(message, JsonObject.class);
            if (!bodyObj.has("name")) {
                return null;
            }
            if (!bodyObj.has("pass")) {
                return null;
            }
            String[] arr = new String[2];
            arr[0] = bodyObj.get("name").getAsString();
            arr[1] = bodyObj.get("pass").getAsString();
            return arr;
        }
        catch (Exception e) {
            System.out.println("Caught an exception");
            return null;
        }
    }
}
