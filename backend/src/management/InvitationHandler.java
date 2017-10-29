package management;

import com.google.gson.*;
import com.sun.org.apache.xpath.internal.operations.Mod;
import database.*;

import java.sql.Timestamp;

//handles
//invite.friend
//invite.group
//invite.
public class InvitationHandler implements IHandler {
    Tracker tracker;
    public InvitationHandler(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public String handle(User user, Notification notification, JsonObject response) {
        String type = notification.getType().toString();
        if (type.equals("invite.friend")) {
            //if user accepts invite, make friends
            try {
                if (!response.has("accept")) {
                    return null;
                }
                if (response.get("accept").getAsBoolean()) {
                    //make friends and then get rid of notification
                    user.clearNotification(notification);
                    User newfriend = tracker.getUserById(Integer.parseInt(notification.getParams()));
                    user.addFriend(newfriend.getUsername());
                    newfriend.addFriend(user.getUsername());
                    return "";
                }
                else {
                    //get rid of notification
                    user.clearNotification(notification);
                    return "";
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else if (type.equals("invite.group")) {
            try {
                if (!response.has("accept")) {
                    return null;
                }
                if (response.get("accept").getAsBoolean()) {
                    //add to group and then get rid of notification
                    user.clearNotification(notification);
                    Group group = tracker.getGroupById(Integer.parseInt(notification.getParams()));
                    if (ModifyGroup.addUserToGroup(group.getId(), user.getId())) {
                        group.addUser(user);
                        return "";
                    }
                    else {
                        return null;
                    }
                }
                else {
                    //get rid of notification
                    user.clearNotification(notification);
                    return "";
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            return null;
        }
    }

    @Override
    public JsonObject format(Notification notification) throws Exception {
        String type = notification.getType().toString();
        if (type.equals("invite.friend")) {
            User user = getRequestingUser(notification);
            if (user == null) {
                return null;
            }
            return makeFriendInvite(notification, user);
        }
        else if (type.equals("invite.group")) {
            try {
                //Get User that invited
                //Get Group that being invited to
                User user = getRequestingUser(notification);
                if (user == null) {
                    return null;
                }
                Group group = tracker.getGroupById(Integer.parseInt(notification.getParams()));
                if (group == null) {
                    System.out.println("Couldn't get group with id " + notification.getParams());
                    return null;
                }
                return makeGroupInvite(notification, group);
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            return null;
        }
    }
    //Found in params of a friend invite
    public User getRequestingUser(Notification notification) throws Exception {
        int requester = -1;
        if (!notification.getParams().contains(",")) {
            try {
                requester =  Integer.parseInt(notification.getParams());
            }
            catch (Exception e) {
            }
        }

        if (requester == -1) {
            System.out.println("Invalid user_id format in params");
            throw new Exception("Invalid user_id format in params");
        }
        User user = tracker.getUserById(requester);
        if (user == null) {
            //try getting from database
            user = User.fromDatabase(requester);
            if (user == null) {
                System.out.println("Invalid user_id in params");
                throw new Exception("Invalid user_id in params");
            }
            else {
                //add to tracker
                tracker.addUser(user);
            }
        }
        return user;
    }
    public JsonObject makeFriendInvite(Notification notification, User user) {
        JsonObject jobj = new JsonObject();
        jobj.addProperty("type", "invite.friend");
        jobj.addProperty("id", notification.getNotifid());
        JsonObject data = new JsonObject();
        data.addProperty("fullName", user.getName());
        data.addProperty("imageUrl", user.getImageUrl());
        data.addProperty("sent", notification.getCreated().toString());
        jobj.add("data", data);
        return jobj;
    }
    public JsonObject makeGroupInvite(Notification notification, Group group) {
        if (notification == null) { System.out.println("notification null"); }
        if (group == null) { System.out.println("group is null"); }
        JsonObject jobj = new JsonObject();
        jobj.addProperty("type", "invite.group");
        jobj.addProperty("id", notification.getNotifid());
        JsonObject data = new JsonObject();
        if (group.getName() == null) { System.out.println("name est nullum");}
        data.addProperty("groupname", group.getName());
        if (group.getImagePath() == null) { System.out.println("image path is null");}
        data.addProperty("imageUrl", group.getImagePath());
        if (notification.getCreated() == null) { System.out.println("sent is null");}
        data.addProperty("sent", notification.getCreated().toString());
        jobj.add("data", data);
        return jobj;
    }
    //Sends a friend invite from requester to toadd
    //Notification param is {requester id}
    public boolean sendFriendInvite(User requester, User toadd) {
        if (requester == null || toadd == null) {
            System.out.println("sendFriendInvite(), requester or toadd is null");
            return false;
        }
        //Create the Notification
        Notification notification;
        try {
            //make the params
            String params = Integer.toString(requester.getId());
            //set user_id, params, and type
            notification = new Notification(-1, toadd.getId(), NotificationType.InviteFriend(), params, null);
            notification = NotificationInDb.add(notification);
            if (notification == null) {
                return false;
            }
            else {
                if (toadd.addNotificatin(notification)) {
                    return true;
                }
                else {
                    System.out.println("Couldn't add notification to user");
                    return false;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("sendFriendInvite() Couldn't create Notification");
            return false;
        }
    }
    public boolean sendGroupInvite(User requester, User invitee, Group group) {
        if (requester == null || invitee == null || group == null) {
            System.out.println("sendGroupInvite(), requester or invitee or group is null");
            return false;
        }
        //Create the notification
        Notification notification;
        try {
            String params = Integer.toString(group.getId());
            notification = new Notification(-1, invitee.getId(), NotificationType.InviteGroup(), params, null);
            notification = NotificationInDb.add(notification);
            if (notification == null) {
                return false;
            }
            else {
                if (invitee.addNotificatin(notification)) {
                    return true;
                }
                else {
                    System.out.println("Couldn't add notification to user");
                    return false;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("sendgroupInvite() couldn't create Notification");
            return false;
        }
    }
}
