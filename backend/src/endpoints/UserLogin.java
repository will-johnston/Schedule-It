package endpoints;

import database.*;
import server.*;
import com.google.gson.*;
import management.*;
import java.net.Socket;

//Logins into the system
//Should return a valid login cookie
// /api/user/login
public class UserLogin implements IAPIRoute {

    Tracker tracker;
    public UserLogin(Tracker tracker) {
        this.tracker = tracker;
    }
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        Gson gson = new Gson();
        try {
            String[] args = parseArgs(request.getBody());
            if (args == null) {
                System.out.println("Args is null");
                Socketeer.send(HTTPMessage.makeResponse("{\"error\":\"No Arguments specified\"}\n", HTTPMessage.HTTPStatus.BadRequest, HTTPMessage.MimeType.appJson, true), sock);
                return;
            }
            else {
                System.out.println(String.format("name: %s, pass: %s", args[0], args[1]));
                User user = User.fromDatabase(args[0]);
                if (user == null) {
                    String response = "{\"error\":\"User doesn't exist\"}";
                    Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest,
                            HTTPMessage.MimeType.appJson, true), sock);
                    return;
                }
                if (args[1].equals(user.getPassword())) {
                    int cookie = tracker.login(user);   //if user is already logged in, handle silently
                    String response = String.format("{\"cookie\":\"%d\"}", cookie);
                    Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.OK), sock);
                    return;
                }
                else {
                    String response = "{\"error\":\"Invalid Password\"}";
                    Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest,
                            HTTPMessage.MimeType.appJson, true), sock);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Returns an array of [email, password]
    private String[] parseArgs(String message) {
        System.out.println(message);
	try {
            Gson gson = new Gson();
            if (gson == null) { System.out.println("Gson is null"); }
            JsonObject bodyObj = gson.fromJson(message, JsonObject.class);
            if (bodyObj == null) { System.out.println("bodyObj is null"); }
            if (!bodyObj.has("name")) {
                System.out.println("Name is null");
                return null;
            }
            if (!bodyObj.has("pass")) {
                System.out.println("pass is null");
                return null;
            }
            String[] arr = new String[2];
            arr[0] = bodyObj.get("name").getAsString();
            arr[1] = bodyObj.get("pass").getAsString();
            return arr;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Caught an exception");
            return null;
        }
    }
}
