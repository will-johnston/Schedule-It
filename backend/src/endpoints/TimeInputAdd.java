package endpoints;

import server.*;
import management.*;
import database.AddTimeInput;
import java.net.Socket;
import com.google.gson.*;

//Adds a friend to a User
public class TimeInputAdd implements IAPIRoute {
    Tracker tracker;

    public TimeInputAdd(Tracker tracker, NotificationHandler handler) {
        this.tracker = tracker;
    }
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        //Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
        Object[] args = parseArgs(request.getBody());
        if (args == null) {
            String response = "{ \"error\" : \"Invalid arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        int groupID = (int)args[0];
        int eventID = (int)args[1];
        String time = (String)args[2];
        //User requester = tracker.getUser(cookie);
        if (AddTimeInput.addInput(groupID, eventID, time)) {
                Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
                return;
        } else {
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
            if (!jobj.has("eventID")) {
                return null;
            }
            if (!jobj.has("time")) {

                return null;
            }

            System.out.println("Has the right parameters");
            int groupID = jobj.get("groupID").getAsInt();
            int eventID = jobj.get("eventID").getAsInt();
            String time = jobj.get("time").getAsString();
            return new Object[] { groupID,eventID, time };

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("failed to parse args");
            return null;
        }
    }
}
