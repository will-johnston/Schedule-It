package endpoints;

import database.Group;
import database.ModifyGroup;
import database.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import server.*;
import management.*;

import javax.naming.event.ObjectChangeListener;

/**
 * Created by williamjohnston on 11/20/17.
 */
public class GroupAddAdmin implements IAPIRoute {


    Tracker tracker;
    NotificationHandler handler;

    public GroupAddAdmin(Tracker tracker, NotificationHandler handler) {
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

        User groupmember = tracker.getUserByName((String) args[1]);
        int memberid = groupmember.getId();  //get id of group member

        if (groupmember == null) {
            String response = "{\"error\":\"Group member error. User to add as admin doesn't exist\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }

        Group[] groups = groupmember.getGroups(tracker);
        if (groups == null) {
            String response = "{\"error\":\"Couldn't get groups\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        System.out.println("group id: " + groupid);
        boolean isGroupMember = false;
        for (int i = 0; i < groups.length; i++) {
            System.out.println(groups[i].getId());
            if (group.getId() == groups[i].getId()) {
                isGroupMember = true;
            }
        }

        if (!isGroupMember) {
            String response = "{\"error\":\"User to promote is not in the group.\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        System.out.printf("group id: %d, group member id: %d\n", groupid, memberid);
        //add admin priveleges to group member
        if (ModifyGroup.addAdminPrivileges(groupid, memberid)) {
            Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
            return;
        } else {
            String response = "{\"error\":\"Error adding admin privileges\"}";
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

