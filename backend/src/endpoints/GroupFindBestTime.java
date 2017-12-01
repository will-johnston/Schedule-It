package endpoints;

import database.*;
import server.*;
import management.*;
import com.google.gson.*;
import java.sql.Timestamp;
import java.text.*;
import java.util.*;
import server.*;
import management.*;
import com.google.gson.*;

public class GroupFindBestTime implements IAPIRoute {
    Tracker tracker;
    public GroupFindBestTime(Tracker tracker) {
        this.tracker = tracker;
    }
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        Object[] args = parseArgs(request.getBody());
        System.out.println("Parsed args");
        if (args == null) {
            String response = "{\"error\":\"Invalid Arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }

        int cookie = (int)args[0];
        int groupid = (int)args[1];
        int eventid = (int)args[2];
        String mode = "set";
        Timestamp time = TimeInputBestTime.findBestTime(groupid, eventid);
        Long timel = time.getTime();
        System.out.println("BEST TIME: " + timel);
        System.out.println("BEST TIMESTAMP: " + time);



        //from groupeditcalendar
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
        if (event.getIs_open_ended() == false) {
            Socketeer.send(HTTPMessage.makeResponse("{\"error\":\"Event is not open-ended\"}", HTTPMessage.HTTPStatus
                    .BadRequest),sock);
            return;
        }

        Timestamp date = time;
        System.out.println("DATE: " + date);
        if (date == null) {
            String response = "{\"error\":\"Invalid date\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        if (date != null) {
            event.setTime(date);
        }
        if (mode != null) {
            if (mode.equals("set")) {
                event.setIs_open_ended(false);
            } else {
                event.setIs_open_ended(true);
            }
        }
        if (group.editEvent(event)) {
            Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
            return;
        }
        else {
            String response = "{\"error\":\"Couldn't remove event locally\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        /*if (group.removeEvent(event.getEventID())) {
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
        }*/
    }


    //cookie, name, description, date, groupid
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



            return new Object[] {
                    jobj.get("cookie").getAsInt(),
                    jobj.get("groupid").getAsInt(),
                    jobj.get("eventid").getAsInt()
            };
        }
        catch (Exception e) {
            System.out.print("Invalid arguments");
            e.printStackTrace();
            return null;
        }
    }
}
