package endpoints;

import database.*;
import server.HTTPMessage;
import server.SSocket;
import server.Socketeer;
import management.*;
import com.google.gson.*;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.text.*;

import javax.print.attribute.standard.NumberUp;
import java.sql.*;
import java.text.DateFormat;

public class GroupAddCalendar implements IAPIRoute {
    Tracker tracker;
    public GroupAddCalendar(Tracker tracker) {
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
        int groupid = (int)args[4];  //cookie, name, description, date, groupid
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
        String name = (String)args[1], description = (String)args[2], rawDate = (String)args[3], type = (String)args[5];
        Timestamp date;
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
        Group group = user.getGroupById(groupid);
        //int eventID, String name, String type, String description, String image
        Event event = new Event(0,name, type, description, null);
        event.setTime(date);
        event.setGroupID(group.getId());
        if (group.addEvent(event)) {
            //send notifications
            try {
                //groupid, eventid
                String params = String.format("%d,%d",  group.getId(), event.getEventID());
                Notification notification = new Notification(-1,-1,"invite.event", params, event.getTime());
                group.notifyMembers(notification, tracker);
                Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
                return;
            }
            catch (Exception e) {
                String response = "{\"error\":\"Couldn't add notification\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
        }
        else {
            String response = "{\"error\":\"Couldn't add event\"}";
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
            if (!jobj.has("name")) {
                return null;
            }
            if (!jobj.has("description")) {
                return null;
            }
            if (!jobj.has("type")) {
                return null;
            }
            if (!jobj.has("date")) {
                return null;
            }
            if (!jobj.has("groupid")) {
                return null;
            }
            return new Object[] { jobj.get("cookie").getAsInt(),
                    jobj.get("name").getAsString(),
                    jobj.get("description").getAsString(),
                    jobj.get("date").getAsString(),
                    jobj.get("groupid").getAsInt(),
                    jobj.get("type").getAsString()};
        }
        catch (Exception e) {
            System.out.print("Invalid arguments");
            e.printStackTrace();
            return null;
        }
    }
}
