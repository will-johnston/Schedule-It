package endpoints;

import database.ModifyUserInDb;
import database.User;
import database.Messages;
import server.*;
import management.*;
import java.util.*;
import com.google.gson.*;
import java.net.Socket;

public class MessageAdd implements IAPIRoute {

    Tracker tracker;
    public MessageAdd(Tracker tracker) {
        this.tracker = tracker;
    }

    /*Proposed changes
    * Get rid of username field
    * Get rid of time field
    * Use Tracker to get username
    * Use Tracker to check whether or not the user is actually signed in
    * Use SQL to handle current time (it's so easy)
    * Also updated Tracker
    * */
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        try {
            String[] args = parseArgs(request.getBody());
            if(args == null) {
                System.out.println("Args is null");
                Socketeer.send(HTTPMessage.makeResponse("{\"error\":\"No Arguments specified\"}\n", HTTPMessage.HTTPStatus.BadRequest, HTTPMessage.MimeType.appJson, true), sock);
                return;
            }
            int cookie = Integer.parseInt(args[0]);
            if (cookie == 0) {
                //invalid cookie sent error
                Socketeer.send(HTTPMessage.makeResponse("{\"error\":\"No Arguments specified\"}\n", HTTPMessage.HTTPStatus.BadRequest, HTTPMessage.MimeType.appJson, true), sock);
                return;
            }
            if (!tracker.isLoggedIn(cookie)) {
                //disallow adding chats when not logged in because that would be weird
                String response = "{\"error\":\"User is not logged in\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            User user = tracker.getUser(cookie);
            if (user == null) {
                //For some reason, we couldn't get the User from the tracker and bad things could stem from this
                //If this happens, Ryan should be let known
                String response = "{\"error\":\"Couldn't get user\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            String username = user.getUsername();
            if (username == null) {
                //This shouldn't happen BUT IF IT DOES, we're f-ed
                String response = "{\"error\":\"Couldn't get username\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            //Have username, groupID, line
            Messages message = new Messages();
            args[0] = username;
            boolean ret = message.setMessage(args);
            Socketeer.send(HTTPMessage.makeResponse("{\"Success\":\"Message sent to database\"}\n", HTTPMessage.HTTPStatus.OK, HTTPMessage.MimeType.appJson, true), sock);

            return;
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //returns cookie, groupID, line
    private String[] parseArgs(String message) {
        try {
            Gson gson = new Gson();
            JsonObject bodyObj = gson.fromJson(message, JsonObject.class);
            if (!bodyObj.has("cookie") || !bodyObj.has("line") || !bodyObj.has("groupID")) {
                return null;
            }
            String[] arr = new String[3];
            arr[0] = bodyObj.get("cookie").getAsString();
            arr[1] = bodyObj.get("groupID").getAsString();
            arr[2] = bodyObj.get("line").getAsString();
            return arr;
        } 
        catch (Exception e) {
            System.out.println("Caught an exception");
            return null;
        }
    }
}