package endpoints;

import database.User;
import database.Event;
import database.Group;
import server.*;
import management.*;
import com.google.gson.*;

import java.util.ArrayList;

public class GroupEditCalendar implements IAPIRoute {
    Tracker tracker;
    public GroupEditCalendar(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        Object[] args = parseArgs(request.getBody());  //returns cookie, month, year, groupid
        if (args == null) {
            String response = "{\"error\":\"Invalid Arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        int cookie = (int)args[0];
        int groupid = (int)args[1];
        int eventid = (int)args[2];
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

        Event event = group.getEvent(eventid);
        if (event == null) {
            //String response = "{\"error\":\"Couldn't get events\"}";
            //has no events
            Socketeer.send(HTTPMessage.makeResponse("{\"error\":\"Event doesn't exist\"}", HTTPMessage.HTTPStatus
                            .BadRequest),sock);
            return;
        }
        String name = getName(args);
        String type = getType(args);
        String date = getDate(args);
        String description = getDescription(args);
        //remove
    }
    //returns cookie, groupid, eventid, name, description, type, date
    Object[] parseArgs(String body) {
        try {
            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(body, JsonObject.class);
            if (!jobj.has("cookie")) {
                return null;
            }
            if (!jobj.has("groupid")) {
                return null;
            }
            if (!jobj.has("eventid")) {
                return null;
            }
            ArrayList<Object> objects = new ArrayList<>(3);
            objects.add(jobj.get("cookie").getAsInt());
            objects.add(jobj.get("groupid").getAsInt());
            objects.add(jobj.get("eventid").getAsInt());
            if (jobj.has("name")) {
                objects.add(new Tuple("name", jobj.get("name").getAsString()));
            }
            if (jobj.has("description")) {
                objects.add(new Tuple("description", jobj.get("description").getAsString()));
            }
            if (jobj.has("date")) {
                objects.add(new Tuple("date", jobj.get("date").getAsString()));
            }
            if (jobj.has("type")) {
                objects.add(new Tuple("type", jobj.get("type").getAsString()));
            }
            Object[] arr = new Object[objects.size()];
            objects.toArray(arr);
            return arr;
        }
        catch (Exception e) {
            System.out.println("Failed to parse Args");
            return null;
        }
    }
    public String getName(Object[] arr) {
        if (arr == null) {
            return null;
        }
        for (int i = 3; i < arr.length; i++) {
            Tuple tup = (Tuple)arr[i];
            if (tup.Item1.equals("name")) {
                return (String)tup.Item2;
            }
        }
        return null;
    }
    public String getDescription(Object[] arr) {
        if (arr == null) {
            return null;
        }
        for (int i = 3; i < arr.length; i++) {
            Tuple tup = (Tuple)arr[i];
            if (tup.Item1.equals("description")) {
                return (String)tup.Item2;
            }
        }
        return null;
    }
    public String getDate(Object[] arr) {
        if (arr == null) {
            return null;
        }
        for (int i = 3; i < arr.length; i++) {
            Tuple tup = (Tuple)arr[i];
            if (tup.Item1.equals("date")) {
                return (String)tup.Item2;
            }
        }
        return null;
    }
    public String getType(Object[] arr) {
        if (arr == null) {
            return null;
        }
        for (int i = 3; i < arr.length; i++) {
            Tuple tup = (Tuple)arr[i];
            if (tup.Item1.equals("type")) {
                return (String)tup.Item2;
            }
        }
        return null;
    }
}
