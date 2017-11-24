package endpoints;

import database.*;
import server.*;
import management.*;
import com.google.gson.*;

import java.sql.Timestamp;
import java.text.*;
import java.util.*;

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
        if (!group.isAdmin(user.getUsername())) {
            String invAdmin = "{\"error\":\"User is not an admin in the group\"}";
            Socketeer.send(HTTPMessage.makeResponse(invAdmin, HTTPMessage.HTTPStatus.BadRequest), sock);
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
        String rawDate = getDate(args);
        String description = getDescription(args);
        Timestamp date = null;
        if (rawDate != null) {
            try {
                //Tue, 31 Oct 2017 17:11:25 EST
                DateFormat utcFormat = new SimpleDateFormat("EEE, d MMM yyyy kk:mm:ss zzz");
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date newdate = utcFormat.parse(rawDate);

                date = new Timestamp(newdate.getTime());
                System.out.println("Timestamp: " + date.toString() + " || parsed time: " + newdate.toString());
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Couldn't convert to date object");
                date = null;
            }
            if (date == null) {
                String response = "{\"error\":\"Invalid date\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
        }
        if (name != null) {
            event.setName(name);
        }
        if (type != null) {
            event.setType(type);
        }
        if (date != null) {
            event.setTime(date);
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (group.removeEvent(event.getEventID())) {
            if (group.addEvent(event)) {
                Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
                return;
            }
            else {
                String response = "{\"error\":\"Couldn't add modified event\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
        }
        else {
            String response = "{\"error\":\"Couldn't remove event locally\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
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
