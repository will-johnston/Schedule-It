package endpoints;

import database.ModifyUserInDb;
import database.User;
import database.Messages;
import server.*;
import management.*;
import java.util.*;
import com.google.gson.*;
import java.net.Socket;

public class MessageGet implements IAPIRoute {

    Tracker tracker;
    public MessageGet(Tracker tracker) {
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

            if (args == null) {
                String response = "{ \"error\" : \"Invalid arguments\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }

            int groupID = Integer.parseInt(args[0]);
            int cookie = Integer.parseInt(args[1]);

            if (cookie == 0) {
                String response = "{\"error\":\"Invalid Arguments\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }

            if (!tracker.isLoggedIn(cookie)) {
                String response = "{\"error\":\"User is not logged in\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            User user = tracker.getUser(cookie);

            if (user == null) {
                String response = "{\"error\":\"Couldn't get user\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }


            Messages message = new Messages();
            ArrayList<String> chat = message.getMessage(groupID);

            if (chat == null) {
                String response = "{\"error\":\"Couldn't get chat from database\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            String json_chat = new Gson().toJson(chat);
            Socketeer.send(HTTPMessage.makeResponse(json_chat, HTTPMessage.HTTPStatus.OK, HTTPMessage.MimeType.appJson, true), sock);
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
            if (!bodyObj.has("groupID")) {
                return null;
            }
            if (!bodyObj.has("cookie")) {
                return null;
            }
            String[] arr = new String[2];
            arr[0] = bodyObj.get("groupID").getAsString();
            arr[1] = bodyObj.get("cookie").getAsString();
            return arr;
        } 
        catch (Exception e) {
            System.out.println("Caught an exception");
            return null;
        }
    }
}
