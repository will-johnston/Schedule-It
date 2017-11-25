package endpoints;

import database.*;
import server.*;
import management.*;
import com.google.gson.*;

import java.sql.Timestamp;
import java.text.*;
import java.util.*;

public class GroupRemoveCalendar implements IAPIRoute {
    Tracker tracker;
    public GroupRemoveCalendar(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        int[] args = parseArgs(request.getBody());  //returns cookie, month, year, groupid
        if (args == null) {
            String response = "{\"error\":\"Invalid Arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        int cookie = (int)args[0];
        int groupid = (int)args[1];
        int eventid = (int)args[2];
        if (cookie == 0) {
            String response = "{\"error\":\"Invalid Arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        if (!tracker.isLoggedIn(cookie)) {
            String response = "{\"error\":\"User not logged in\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        User user = tracker.getUser(cookie);
        if (user == null) {
            String response = "{\"error\":\"Couldn't get user\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        Group group = user.getGroupById(groupid, tracker);
        if (group == null) {
            String response = "{\"error\":\"Couldn't get group\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        if (!group.isAdmin(user.getUsername())) {
            String invAdmin = "{\"error\":\"User is not an admin in the group\"}";
            Socketeer.send(HTTPMessage.makeResponse(invAdmin, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        if (!group.eventExists(eventid)) {
            String response = "{\"error\":\"Event doesn't exist\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        if (group.removeEvent(eventid)) {
            Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
        }
        else {
            String response = "{\"error\":\"Couldn't remove event\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
    }
    //cookie, groupid, eventid
    int[] parseArgs(String body) {
        try {
            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(body, JsonObject.class);
            if (!jobj.has("cookie")) {
                return null;
            }
            if (!jobj.has("groupid")) {
                return null;
            }
            if (!jobj.has("eventid")) {
                return null;
            }
            return new int[] {
                    jobj.get("cookie").getAsInt(),
                    jobj.get("groupid").getAsInt(),
                    jobj.get("eventid").getAsInt()
            };
        }
        catch (Exception e) {
            System.out.println("Failed to parse Args");
            return null;
        }
    }
}
