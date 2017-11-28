package management;

import com.google.gson.*;
import database.Group;
import database.Notification;
import database.User;
//
public class NotificationHandler {
    //singletons
    InvitationHandler invitationHandler;
    Gson gson;
    public NotificationHandler(Tracker tracker) {
        invitationHandler = new InvitationHandler(tracker);
        gson = new Gson();
    }
    //formats all notifications for delivery
    //returns message to be sent to the client
    public JsonObject format(Notification notification) throws Exception {
        if (notification == null) {
            System.out.println("Tried to handle null notification");
            return null;
        }
        if (notification.getType().isInvite()) {
            try {
                JsonObject response = invitationHandler.format(notification);
                if (response == null) {
                    System.out.println("Couldn't format invite notification. handler returned null");
                    return null;
                }
                return response;
            }
            catch (Exception e){
                System.out.println("Couldn't format invite notification");
                e.printStackTrace();
                return null;
            }
        }
        else {
            return null;
        }
    }
    //Handles responses from notifications
    public String handle(Notification notification, JsonObject response, User user) {
        if (notification == null || response == null || user == null) {
            return null;
        }
        if (notification.getType().isInvite()) {
            try {
                String handledResponse = invitationHandler.handle(user, notification, response);
                if (handledResponse == null) {
                    System.out.println("Couldn't handle invite notification, handler returned null");
                    return null;
                }
                return handledResponse;
            }
            catch (Exception e) {
                System.out.println("Couldn't handle invite notification");
                e.printStackTrace();
                return null;
            }
        }
        else {
            return null;
        }
    }
    public boolean sendFriendInvite(User requester, User toadd) {
        return invitationHandler.sendFriendInvite(requester, toadd);
    }
    public boolean sendGroupInvite(User requester, User invitee, Group group) {
        return invitationHandler.sendGroupInvite(requester, invitee, group);
    }
}
