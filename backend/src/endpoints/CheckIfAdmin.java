package endpoints;

import database.Group;
import database.ModifyGroup;
import database.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import server.*;
import java.util.ArrayList;
import management.*;

public class CheckIfAdmin implements IAPIRoute {
    Tracker tracker;
    public CheckIfAdmin(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        Object[] args = parseArgs(request.getBody());
        if (args == null) {
            String response = "{\"error\":\"Invalid Arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        int cookie = (int) args[0];
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
        int groupid = (int) args[2];
        Group group = user.getGroupById(groupid, tracker);
        if (group == null) {
            String response = "{\"error\":\"Couldn't get group\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }

        ArrayList<String> current_admins = group.getAdmins();
        if (!current_admins.contains(user.getUsername())) {
            String response = "{\"value\":\"false\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.OK), sock);
            return;
        }
        else {
            String response = "{\"value\":\"true\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.OK), sock);
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
            if (!jobj.has("groupmember")) {  //username of group member to receive admin privileges

                return null;
            }
            if (!jobj.has("groupid")) {
                return null;
            }
            return new Object[]{jobj.get("cookie").getAsInt(), jobj.get("groupmember").getAsString(), jobj.get("groupid").getAsInt()};
        } catch (Exception e) {
            System.out.println("Couldn't parse arguments");
            return null;
        }
    }
}
