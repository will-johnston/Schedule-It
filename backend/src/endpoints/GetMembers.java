package endpoints;

import database.Group;
import database.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import server.*;
import java.util.ArrayList;
import management.*;

public class GetMembers implements IAPIRoute {
    Tracker tracker;
    public GetMembers(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        int[] args = parseArgs(request.getBody());
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
        int groupid = (int) args[1];
        Group group = user.getGroupById(groupid, tracker);
        if (group == null) {
            String response = "{\"error\":\"Couldn't get group\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        String[] usernames = group.getMembers();
        if (usernames == null) {
            String response = "{\"error\":\"Couldn't get group members\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        ArrayList<JsonObject> memberList = new ArrayList<>(usernames.length);
        for (int i = 0; i < usernames.length; i++) {
            System.out.println(String.format("i: %d length: %d", i, usernames.length));
            User member = tracker.getUserByName(usernames[i]);
            if (member == null) {
                System.out.println("Couldn't add Member with name: " + usernames[i]);
            }
            else {
                //add user to response
                JsonObject memberObj = new JsonObject();
                memberObj.addProperty("username", member.getUsername());
                memberObj.addProperty("name", member.getName());
                memberObj.addProperty("image", member.getImageUrl());
                memberList.add(memberObj);
            }
        }
        Gson gson = new Gson();
        Socketeer.send(HTTPMessage.makeResponse(gson.toJson(memberList), HTTPMessage.HTTPStatus.OK), sock);
    }
    //returns cookie, groupid
    int[] parseArgs(String message) {
        try {
            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(message, JsonObject.class);
            if (!jobj.has("cookie")) {
                return null;
            }
            if (!jobj.has("groupid")) {
                return null;
            }
            return new int[] {
              jobj.get("cookie").getAsInt(),
              jobj.get("groupid").getAsInt()
            };
        } catch (Exception e) {
            System.out.println("Couldn't parse arguments");
            return null;
        }
    }
}
