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

    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        try {
            String[] args = parseArgs(request.getBody());
            if(args == null) {
                System.out.println("Args is null");
                Socketeer.send(HTTPMessage.makeResponse("{\"error\":\"No Arguments specified\"}\n", HTTPMessage.HTTPStatus.BadRequest, HTTPMessage.MimeType.appJson, true), sock);
                return;
            }
            else {
                //Have username, groupID, timestamp, and message
                Messages message = new Messages();
                boolean ret = message.setMessage(args);

                return;
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] parseArgs(String message) {
        try {
            Gson gson = new Gson();
            JsonObject bodyObj = gson.fromJson(message, JsonObject.class);
            if(!bodyObj.has("username") || !bodyObj.has("groupID")) {
                return null;
            }
            if(!bodyObj.has("time") || !bodyObj.has("line")) {
                return null;
            }
            String[] arr = new String[4];
            arr[0] = bodyObj.get("username").getAsString();
            arr[1] = bodyObj.get("groupID").getAsString();
            arr[2] = bodyObj.get("time").getAsString();
            arr[3] = bodyObj.get("line").getAsString();
            return arr;
        } 
        catch (Exception e) {
            System.out.println("Caught an exception");
            return null;
        }
    }
}