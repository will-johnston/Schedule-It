package endpoints;

/**
 * Created by williamjohnston on 11/20/17.
 */

import database.Group;
import database.ModifyGroup;
import database.User;

import database.Group;
import database.ModifyGroup;
import database.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import server.*;
import management.*;

import javax.naming.event.ObjectChangeListener;
import java.util.ArrayList;

/**
 * Created by williamjohnston on 11/20/17.
 */
public class GroupRemoveAdmin implements IAPIRoute {


    Tracker tracker;
    NotificationHandler handler;

    public GroupRemoveAdmin(Tracker tracker, NotificationHandler handler) {
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

        //only the group creator/owner can remove admins
        if (group.getOwner().getId() != user.getId()) {
            String response = "{\"error\":\"User removing admin is not creator\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }

        User groupmember = tracker.getUserByName((String) args[1]);
        int memberid = groupmember.getId();  //get id of group member

        if (groupmember == null) {
            String response = "{\"error\":\"Group member error. User to remove as admin doesn't exist\"}";
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
            String response = "{\"error\":\"User to remove is not in the group.\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }

        //make sure member is an admin
        /*ArrayList<String> admins = group.getAdmins();
        System.out.println("Member name: " + groupmember.getUsername());
        for (String admin : admins) {
            System.out.println("Admin: " + admin);

        }*/

        if (!group.isAdmin(groupmember.getUsername())) {
            String response = "{\"error\":\"Group member is not an admin\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }

        System.out.printf("group id: %d, group member id: %d\n", groupid, memberid);
        //remove admin priveleges from group member
        if (ModifyGroup.removeAdminPrivileges(groupid, memberid)) {
            group.updateAdmins();
            Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
            return;
        } else {
            String response = "{\"error\":\"Error removing admin privileges\"}";
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

