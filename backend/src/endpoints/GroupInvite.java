package endpoints;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import server.*;
import management.*;
import database.User;
import database.Group;

import javax.naming.event.ObjectChangeListener;

public class GroupInvite implements IAPIRoute {

    Tracker tracker;
    NotificationHandler handler;
    public GroupInvite(Tracker tracker, NotificationHandler handler) {
        this.tracker = tracker;
        this.handler = handler;
    }
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        Object[] args = parseArgs(request.getBody());
        if (args == null) {
            String response = "{\"error\":\"Invalid Arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        int cookie = (int)args[0];
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
        Group group = user.getGroupById((int)args[2], tracker);
        if (group == null) {
            String response = "{\"error\":\"Couldn't get group\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        User invitee = tracker.getUserByName((String)args[1]);
        if (invitee == null) {
            String response = "{\"error\":\"Couldn't invite user, user doesn't exist\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        if (handler.sendGroupInvite(user, invitee, group)) {
            Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
            return;
        }
        else {
            String response = "{\"error\":\"Couldn't invite user\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
    }
    Object[] parseArgs(String message) {
        try {
            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(message, JsonObject.class);
            if (!jobj.has("cookie")) {
                return null;
            }
            if (!jobj.has("invitee")) {
                return null;
            }
            if (!jobj.has("invitedto")) {
                return null;
            }
            return new Object[] { jobj.get("cookie").getAsInt(), jobj.get("invitee").getAsString(), jobj.get("invitedto").getAsInt()};
        }
        catch (Exception e) {
            System.out.println("Couldn't parse arguments");
            return null;
        }
    }
}
