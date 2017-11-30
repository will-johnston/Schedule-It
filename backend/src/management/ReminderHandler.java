package management;

import database.*;
import com.google.gson.*;

public class ReminderHandler implements IHandler {
    Tracker tracker;
    public ReminderHandler(Tracker tracker) {
        this.tracker = tracker;
    }
    @Override
    public JsonObject format(Notification notification) throws Exception {
        if (notification == null) {
            return null;
        }
        Event event = getEvent(notification);
        if (event == null) {
            return null;
        }
        JsonObject object = new JsonObject();
        object.addProperty("type", "remind.event");
        object.addProperty("name", event.getEvent_name());
        object.addProperty("date", event.getTime().toString());
        object.addProperty("groupid", event.getGroupID());
        object.addProperty("eventid", event.getEventID());
        object.addProperty("id", notification.getNotifid());
        return object;
    }
    @Override
    public String handle(User user, Notification notification, JsonObject response) {
        return "";
    }
    //groupid, eventid
    public Event getEvent(Notification notification) throws Exception {
        if (notification == null) {
            return null;
        }
        int eventid = -1;
        int groupid = -1;
        try {
            String[] split = notification.getParams().split("[,]");
            groupid = Integer.parseInt(split[0]);
            eventid = Integer.parseInt(split[1]);
        }
        catch (Exception e) {
            eventid = -1;
            groupid = -1;
        }
        if (eventid == -1 || groupid == -1) {
            System.out.println("Invalid event_id format in params");
            throw new Exception("Invalid event_id format in params");
        }
        Group group = tracker.getGroupById(groupid);
        if (group == null) {
            System.out.println("Null group");
            return null;
        }
        Event event = group.getEvent(eventid);
        /*if (event == null) {
            event = GetFromDb.getEvent(eventid);
        }*/
        return event;
    }
}
