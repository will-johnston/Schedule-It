package management;

import com.google.gson.*;
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
            return null;
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
            return null;
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
    //Sends a friend invite from requester to toadd
    //Notification param is {requester id}
    public boolean sendFriendInvite(User requester, User toadd) {
        if (requester == null || toadd == null) {
            System.out.println("sendFriendInite(), requester or toadd is null");
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

}
