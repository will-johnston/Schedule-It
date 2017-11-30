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

        String rawExpDate = (String)args[6];  //expiration date if event is open-ended
        boolean is_open_ended = false;  //determines if event is open-ended

        /**
          if rawDate == "null"
            then must be open ended
            rawExpDate != "null"
            find timestamp for rawExpDate
            create event with time = expiration_date, is_open_ended = true
         else if rawExpDate == "null"
            then must be set
            is_open_ended = false;
            find timestamp for rawDate
         **/


        Timestamp date;
        try {
            //Tue, 31 Oct 2017 17:11:25 EST
            DateFormat utcFormat = new SimpleDateFormat("EEE, d MMM yyyy kk:mm:ss zzz");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date newdate;

            System.out.println("rd: " + rawDate);
            System.out.println("red: " + rawExpDate);
            //Expiration time will be the event time if the event is open-ended.
            if (!rawDate.equals("None")) {
                //then the event is NOT open-ended
                System.out.println("raw Date");
                is_open_ended = false;
                newdate = utcFormat.parse(rawDate);
            } else if (!rawExpDate.equals("None")) {
                //then the event is open-ended, event_time is expiration date in order to show on calendar
                System.out.println("raw expiration date");
                is_open_ended = true;
                newdate = utcFormat.parse(rawExpDate);
            } else {
                //both are "None", throw error
                String response = "{\"error\":\"No date or expiration_date\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            date = new Timestamp(newdate.getTime());
            System.out.println("Timestamp: " + date.toString() + " || parsed time: " + newdate.toString());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldn't convert to date object");
            date = null;
        }

        if (date == null) {
            String response = "{\"error\":\"Invalid date\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        try {
            //to remove tracker bug
            Group[] groups = user.getGroups(tracker);
            if (groups == null) {
                String response = "{\"error\":\"Couldn't get groups\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            
            //now get group
            Group group = user.getGroupById(groupid);

            //make sure user is admin in group
            //ArrayList<String> admins = group.getAdmins();
            //System.out.println("admins: " + admins);
            //String username = user.getUsername();
            if (!group.isAdmin(user.getUsername())) {
                //if user is not an admin, create error message
                String invAdmin = "{\"error\":\"User is not an admin in the group\"}";
                Socketeer.send(HTTPMessage.makeResponse(invAdmin, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            } else {
                System.out.println("User is an admin");
                System.out.printf("%s, %s, %s\n", name, type, description);

                //int eventID, String name, String type, String description, String image, is_open_ended
                Event event = new Event(0, name, type, description, null, is_open_ended);
                event.setTime(date);
                event.setGroupID(group.getId());
                if (group.eventExists(event)) {
                    //send error NO DUPS
                    String response = "{\"error\":\"Event already exists at this time\"}";
                    Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                    return;
                }
                if (group.addEvent(event)) {
                    //send notifications
                    if (group.isMeGroup()) {
                        //don't store any notifications for me group
                        Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
                        return;
                    }
                    try {
                        //groupid, eventid
                        String params = String.format("%d,%d", group.getId(), event.getEventID());
                        Notification notification = new Notification(-1, -1, "invite.event", params, event.getTime());
                        group.notifyMembers(notification, tracker);
                        Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
                        return;
                    } catch (Exception e) {
                        String response = "{\"error\":\"Couldn't add notification\"}";
                        Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                        return;
                    }
                } else {
                    String response = "{\"error\":\"Couldn't add event\"}";
                    Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                    return;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
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
            if (!jobj.has("expiration_time")) {
                return null;
            }



            return new Object[] { jobj.get("cookie").getAsInt(),
                    jobj.get("name").getAsString(),
                    jobj.get("description").getAsString(),
                    jobj.get("date").getAsString(),
                    jobj.get("groupid").getAsInt(),
                    jobj.get("type").getAsString(),
                    jobj.get("expiration_time").getAsString()};
        }
        catch (Exception e) {
            System.out.print("Invalid arguments");
            e.printStackTrace();
            return null;
        }
    }
}
