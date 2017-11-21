package endpoints;

import database.User;
import database.Event;
import database.Group;
import server.*;
import management.*;
import com.google.gson.*;

import java.util.Arrays;

//Gets the User's calendar
public class GroupGetCalendar implements IAPIRoute {
    Tracker tracker;
    public GroupGetCalendar(Tracker tracker) {
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
        int month = args[1];
        int year = args[2];
        int groupid = args[3];
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

        Event[] events = group.getEvents(year, month);
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
            JsonObject jobj = new JsonObject();
            System.out.println("Adding events");
            int counter = 0;
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
                    jobj.add("event" + counter, item);
                }
                catch (Exception e) {
                    System.out.println("Couldn't serialize event");
                    e.printStackTrace();
                }
                counter++;

            }
            System.out.println("Serializing to json");
            Gson gson = new Gson();
            return gson.toJson(jobj, JsonObject.class);
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
            if (!jobj.has("month")) {
                return null;
            }
            if (!jobj.has("year")) {
                return null;
            }
            if (!jobj.has("groupid")) {
                return null;
            }
            return new int[] { jobj.get("cookie").getAsInt(),
                    jobj.get("month").getAsInt(),
                    jobj.get("year").getAsInt(),
                    jobj.get("groupid").getAsInt() };
        }
        catch (Exception e) {
            System.out.println("Failed to parse Args");
            return null;
        }
    }
}
