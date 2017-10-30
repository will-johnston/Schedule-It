package endpoints;

import server.*;
import management.*;
import database.AddTimeInput;
import java.net.Socket;
import com.google.gson.*;

//Adds a friend to a User
public class TimeInputAdd implements IAPIRoute {
    Tracker tracker;
    public TimeInputAdd(Tracker tracker) {
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
        int groupID = (int)args[0];
        int userID = (int)args[1];
        int eventID = (int)args[2];
        String time_preference = (String)args[3];
        /*if (!tracker.isLoggedIn(cookie)) {
            String response = "{ \"error\" : \"User not logged in\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }*/
        //User requester = tracker.getUser(cookie);
        if (AddTimeInput.addInput(groupID, userID, eventID, time_preference)) {
          //  if (tracker.updateUser(cookie, requester)) {
                Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
                return;
           /* }
            else {
                String response = "{ \"error\" : \"Failed to update user in API Server\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.MethodNotAllowed), sock);
                return;
            }*/
        }
        else {
            String response = "{ \"error\" : \"Failed to add time input\"}";
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

            if (!jobj.has("groupID")) {
                return null;
            }
            if (!jobj.has("userID")) {
                return null;
            }
            if (!jobj.has("eventID")) {
                return null;
            }
            if (!jobj.has("time_preference")) {
                return null;
            }

            System.out.println("Has the right parameters");
            int groupID = jobj.get("groupID").getAsInt();
            int userID = jobj.get("userID").getAsInt();
            int eventID = jobj.get("eventID").getAsInt();
            String time_preference = jobj.get("time_preference").getAsString();
            return new Object[] { groupID, userID, eventID, time_preference };
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("failed to parse args");
            return null;
        }
    }
}