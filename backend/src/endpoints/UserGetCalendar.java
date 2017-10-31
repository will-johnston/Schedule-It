package endpoints;

import database.User;
import database.Event;
import server.*;
import management.*;
import com.google.gson.*;

//Gets the User's calendar
public class UserGetCalendar implements IAPIRoute {
    Tracker tracker;
    public UserGetCalendar(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        int[] args = parseArgs(request.getBody());
        if (args == null) {
            String response = "{\"error\":\"Invalid Arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        int cookie = args[0];
        int month = args[1];
        int year = args[2];
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
        Event[] events = user.getEvents(year, month);
        if (events == null) {
            String response = "{\"error\":\"Couldn't get events\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        String json = toJson(events);
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
            for (Event event : events) {
                //id, name, time, description, address, type
                JsonObject item = new JsonObject();
                item.addProperty("id", event.getEventID());
                item.addProperty("name", event.getEvent_name());
                item.addProperty("time", event.getTime().toString());
                item.addProperty("description", event.getDescription());
                item.addProperty("address", event.getAddress());
                item.addProperty("type", event.getType());
                jobj.add("event", item);
            }
            Gson gson = new Gson();
            return gson.toJson(jobj, JsonObject.class);
        }
        catch (Exception e) {
            System.out.println("Couldn't serialie events");
            e.printStackTrace();
            return null;
        }
    }
    //returns cookie, month, year
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
            return new int[] { jobj.get("cookie").getAsInt(), jobj.get("month").getAsInt(), jobj.get("year").getAsInt() };
        }
        catch (Exception e) {
            System.out.println("Failed to parse Args");
            return null;
        }
    }
}
