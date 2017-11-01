package endpoints;

import database.Group;
import server.*;
import com.google.gson.*;

import management.*;
import database.*;
import java.net.Socket;

//Creates a new group
public class GroupCreate implements IAPIRoute {

    Tracker tracker;
    public GroupCreate(Tracker tracker) {
        this.tracker = tracker;
    }
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        Object[] args = parseArgs(request.getBody()); //returns { cookie, groupname}
        if (args == null) {
            String message = "{\"error\":\"Invalid Arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(message, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        int cookie = (int)args[0];
        String groupname = (String)args[1];
        if (!tracker.isLoggedIn(cookie)) {
            String message = "{\"error\":\"User not logged in\"}";
            Socketeer.send(HTTPMessage.makeResponse(message, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        System.out.println("Checking if group exists");
        User owner = tracker.getUser(cookie);
        if (tracker.groupExists(groupname)) {
            String message = "{\"error\":\"Group already exists\"}";
            Socketeer.send(HTTPMessage.makeResponse(message, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        System.out.println("Creating group");
        int groupid = CreateGroup.createGroup(groupname, owner.getId());
        if (groupid > 0) {
            Group group = Group.fromDatabase(tracker, groupid);
            if (group == null) {
                String message = "{\"error\":\"Could not create Group\"}";
                Socketeer.send(HTTPMessage.makeResponse(message, HTTPMessage.HTTPStatus.MethodNotAllowed), sock);
                return;
            }
            else {
                owner.addToGroup(group);
                tracker.updateUser(cookie, owner);
                String message = String.format("{\"groupid\":\"%d\"}", groupid);
                Socketeer.send(HTTPMessage.makeResponse(message, HTTPMessage.HTTPStatus.OK), sock);
                return;
            }
        }
        else {
            String message = "{\"error\":\"Could not create group\"}";
            Socketeer.send(HTTPMessage.makeResponse(message, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
    }
    //{
    //"cookie" : 1232,
    //"groupname" : "307 group",
    //}
    //returns { cookie, groupname}
    private Object[] parseArgs(String body) {
        try {
            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(body, JsonObject.class);
            if (!jobj.has("cookie")) {
                return null;
            }
            if (!jobj.has("groupname")) {
                return null;
            }
            int cookie = jobj.get("cookie").getAsInt();
            String groupname = jobj.get("groupname").getAsString();
            return new Object[] { cookie, groupname };
        }
        catch (Exception e) {
            System.out.println("Failed to parse Args");
            return null;
        }
    }
}
