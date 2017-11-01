package endpoints;

import server.*;
import management.*;
import database.*;
import java.net.Socket;
import com.google.gson.*;

//Adds a friend to a User
public class UserGroupEventJunctionAdd implements IAPIRoute {
    Tracker tracker;
    public UserGroupEventJunctionAdd(Tracker tracker) {
        this.tracker = tracker;
    }
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        //Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
        // { "cookie" : 3434,
        // "username" : "friend to add"}
        Object[] args = parseArgs(request.getBody());
        if (args == null) {
            String response = "{ \"error\" : \"Invalid arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        int userID = (int)args[0];
        int groupID = (int)args[1];
        int eventID = (int)args[2];
        /*
        if (!tracker.isLoggedIn(cookie)) {
            String response = "{ \"error\" : \"User not logged in\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }*/
        //User requester = tracker.getUser(cookie);
        if (SetUserGroupEvent.setuge(groupID, userID, eventID)) {
            //if (tracker.updateUser(cookie, requester)) {
                Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
                return;
          //  }
         //   else {
                String response = "{ \"error\" : \"Failed to update user in API Server\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.MethodNotAllowed), sock);
                return;
        //    }
        }
        else {
            String response = "{ \"error\" : \"Failed to add user, group, event junction\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
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
            if (!jobj.has("eventID")) {
                return null;
            }
            System.out.println("Has the right parameters");
            int userID = jobj.get("userID").getAsInt();
            int groupID = jobj.get("groupID").getAsInt();
            int eventID = jobj.get("eventID").getAsInt();

            return new Object[] { userID, groupID, eventID };
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("failed to parse args");
            return null;
        }
    }
}