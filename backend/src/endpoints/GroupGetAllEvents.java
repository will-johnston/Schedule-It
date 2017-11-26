package endpoints;

import database.User;
import database.Event;
import database.Group;
import server.*;
import management.*;
import com.google.gson.*;
import java.util.ArrayList;

public class GroupGetAllEvents implements IAPIRoute{
    Tracker tracker;
    public GroupGetAllEvents(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        int[] args = parseArgs(request.getBody());  //returns cookie, month, year, groupid
        if (args == null) {
            String response = "{\"error\":\"Invalid Arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        int cookie = args[0];
        int groupid = args[1];
        if (cookie == 0) {
            String response = "{\"error\":\"Invalid Arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        if (!tracker.isLoggedIn(cookie)) {
            String response = "{\"error\":\"User not logged in\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        User user = tracker.getUser(cookie);
        if (user == null) {
            String response = "{\"error\":\"Couldn't get user\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        Group group = user.getGroupById(groupid, tracker);
        if (group == null) {
            String response = "{\"error\":\"Couldn't get group\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }

        Event[] events = group.getEvents();
        if (events == null) {
            //String response = "{\"error\":\"Couldn't get events\"}";
            //has no events
            Socketeer.send(HTTPMessage.makeResponse("{}", HTTPMessage.HTTPStatus.OK), sock);
            return;
        }

        String json = toJson(events);
        //JsonArray j = new JsonArray().addAll(Arrays.asList(events));
        //String json = j.toString();

        System.out.println(json);
        if (json == null) {
            String response = "{\"error\":\"Couldn't serialize calendar\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        else {
            Socketeer.send(HTTPMessage.makeResponse(json, HTTPMessage.HTTPStatus.OK), sock);
            return;
        }
    }
    public String toJson(Event[] events) {
        try {
            ArrayList<JsonObject> objs = new ArrayList<>(events.length);
            System.out.println("Adding events");
            for (Event event : events) {
                if (event == null) {
                    System.out.println("Tried to serialize null event");
                    continue;
                }
                try {
                    //id, name, time, description, address, type, is_open_ended
                    JsonObject item = new JsonObject();
                    item.addProperty("id", event.getEventID());
                    item.addProperty("name", event.getEvent_name());
                    item.addProperty("time", event.getTime().toString());
                    item.addProperty("description", event.getDescription());
                    item.addProperty("address", event.getAddress());
                    item.addProperty("type", event.getType());
                    item.addProperty("is_open_ended", event.getIs_open_ended());
                    objs.add(item);
                }
                catch (Exception e) {
                    System.out.println("Couldn't serialize event");
                    e.printStackTrace();
                }
            }
            System.out.println("Serializing to json");
            Gson gson = new Gson();
            return gson.toJson(objs, ArrayList.class);
        }
        catch (Exception e) {
            System.out.println("Couldn't serialie events");
            e.printStackTrace();
            return null;
        }
    }
    //returns cookie, month, year, groupid
    int[] parseArgs(String body) {
        try {
            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(body, JsonObject.class);
            if (!jobj.has("cookie")) {
                return null;
            }
            if (!jobj.has("groupid")) {
                return null;
            }
            return new int[] { jobj.get("cookie").getAsInt(),
                    jobj.get("groupid").getAsInt() };
        }
        catch (Exception e) {
            System.out.println("Failed to parse Args");
            return null;
        }
    }
}
