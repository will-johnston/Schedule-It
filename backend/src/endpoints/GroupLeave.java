package endpoints;

import server.*;
import management.Tracker;
import database.User;
import database.Group;
import com.google.gson.*;
import java.net.Socket;

//Leave the Group
public class GroupLeave implements IAPIRoute {
    Tracker tracker;
    public GroupLeave(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        //Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
        int[] args = parseArgs(request.getBody()); //returns [cookie, groupid]
        if (args == null) {
            String response = "{ \"error\" : \"Invalid arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        if (!tracker.isLoggedIn(args[0])) {
            String response = "{ \"error\" : \"User not logged in\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        //remove from group
        User user = tracker.getUser(args[0]);
        //Should already check if in group
        if (user.removeFromGroup(args[1], tracker)) {
            //send success
            Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
            return;
        }
        else {
            //send false
            //check if user is creator of group
            Group group = user.getGroupById(args[1]);
            /*This is a temporary fix for deleting a group*/
            if (group != null) {
                if (group.removeUser(user)) {
                    tracker.removeGroup(group.getId());
                    Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
                    return;
                }
                else {
                    String response = "{\"error\":\"Failed to remove from group\"}\n";
                    Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                    return;
                }
            }
            else {
                String response = "{\"error\":\"User not a part of group\"}\n";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
        }
    }
    /*
    Accepts a request like
    {
    "cookie" : 324354,
    "groupid" : 6783
    }
    */
    //returns [cookie, groupid]
    private int[] parseArgs(String body) {
        try {
            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(body, JsonObject.class);
            if (!jobj.has("cookie")) {
                return null;
            }
            if (!jobj.has("groupid")) {
                return null;
            }
            return new int[] { jobj.get("cookie").getAsInt(), jobj.get("groupid").getAsInt() };
        }
        catch (Exception e) {
            System.out.println("Failed to parse Args");
            return null;
        }
    }
}
