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
    public String handle(User user, Notification notification) {
        return null;
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
        User user = tracker.getUserWithId(requester);
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

}
