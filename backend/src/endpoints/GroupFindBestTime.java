package endpoints;

import database.*;
import server.HTTPMessage;
import server.SSocket;
import server.Socketeer;
import management.*;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.text.*;

import javax.print.attribute.standard.NumberUp;
import java.sql.*;
import java.text.DateFormat;

public class GroupFindBestTime implements IAPIRoute {
    Tracker tracker;
    public GroupFindBestTime(Tracker tracker) {
        this.tracker = tracker;
    }
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        Object[] args = parseArgs(request.getBody());
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
        //get event
        Event event = group.getEvent(eventid);
        //make sure event is open-ended
        boolean is_open_ended = event.getIs_open_ended();
        if (!is_open_ended) {
            String response = "{\"error\":\"Event is not open ended\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        //call TimeInputBestTime.findBestTime(int groupID, int eventID)
        Timestamp time = TimeInputBestTime.findBestTime(groupid, eventid);
        if (time == null) {
            String response = "{\"error\":\"Couldn't find best time\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
       //change event properties: date, is_open_ended
        event.setTime(time);
        event.setIs_open_ended(false);

        //remove event locally, readd.
        if (group.removeEvent(event.getEventID())) {
            if (group.addEvent(event)) {
                Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
                return;
            }
            else {
                String response = "{\"error\":\"Couldn't add modified event when updating tracker\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
        }
        else {
            String response = "{\"error\":\"Couldn't remove event locally when updating tracker\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
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
                    jobj.get("groupid").getAsString(),
                    jobj.get("eventid").getAsString()
            };
        }
        catch (Exception e) {
            System.out.print("Invalid arguments");
            e.printStackTrace();
            return null;
        }
    }
}
