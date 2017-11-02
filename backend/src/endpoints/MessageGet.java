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
            //Have username, groupID, line
            Messages message = new Messages();
            String groupID = args[0];
            boolean ret = message.getMessage(args);
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
            if (!bodyObj.has("groupID")) {
                return null;
            }
            String[] arr = new String[3];
            arr[0] = bodyObj.get("groupID").getAsString();
            return arr;
        } 
        catch (Exception e) {
            System.out.println("Caught an exception");
            return null;
        }
    }
}