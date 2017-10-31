package endpoints;

import database.Event;
import database.EventPutter;
import database.User;
import server.HTTPMessage;
import server.SSocket;
import server.Socketeer;
import management.*;
import com.google.gson.*;

import javax.print.attribute.standard.NumberUp;
import java.sql.*;

public class UserAddCalendar implements IAPIRoute {
    Tracker tracker;
    public UserAddCalendar(Tracker tracker) {
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
        String name = (String)args[1], description = (String)args[2], rawDate = (String)args[3];
        Timestamp date;
        try {
            date = Timestamp.valueOf(rawDate);
        }
        catch (Exception e) {
            System.out.println("Couldn't convert to date object");
            date = null;
        }
        if (date == null) {
            String response = "{\"error\":\"Invalid date\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        //int eventID, String name, String type, String description, String image
        Event event = new Event(0,name, null, description, null);
        event.setTime(date);
        if (user.addEvent(event)) {
            Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
            return;
        }
        else {
            String response = "{\"error\":\"Couldn't add event\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
    }
    //cookie, name, description, date
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
            if (!jobj.has("date")) {
                return null;
            }
            return new Object[] { jobj.get("cookie").getAsInt(), jobj.get("name").getAsString(),
                    jobj.get("decription").getAsString(), jobj.get("date").getAsString() };
        }
        catch (Exception e) {
            System.out.print("Invalid arguments");
            e.printStackTrace();
            return null;
        }
    }
}
