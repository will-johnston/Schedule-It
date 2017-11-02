package endpoints;

import server.*;
import management.*;
import database.*;
import java.net.Socket;
import com.google.gson.*;

//Adds a friend to a User
public class UserGroupEventJunctionGet implements IAPIRoute {
    Tracker tracker;
    public UserGroupEventJunctionGet(Tracker tracker) {
        this.tracker = tracker;
    }
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        Object[] args = parseArgs(request.getBody());
        String response;
        if (args == null) {
            response = "{ \"error\" : \"Invalid arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        int userID = (int)args[0];
        int groupID = (int)args[1];

        int eventID = GetUserGroupEvent.getuge(groupID, userID);
        response = "{ \"eventID\" : \"" + eventID + "\"}";
        Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.OK), sock);
        return;
    }
    //return [cookie, username]
    private Object[] parseArgs(String body) {
        try {
            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(body, JsonObject.class);
            if (jobj == null) {
                return null;
            }
            if (!jobj.has("userID")) {
                return null;
            }
            if (!jobj.has("groupID")) {
                return null;
            }

            System.out.println("Has the right parameters");
            int userID = jobj.get("userID").getAsInt();
            int groupID = jobj.get("groupID").getAsInt();

            return new Object[] { userID, groupID};
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("failed to parse args");
            return null;
        }
    }
}