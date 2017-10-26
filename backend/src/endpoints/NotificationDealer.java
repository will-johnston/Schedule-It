package endpoints;
import management.NotificationHandler;
import management.Tracker;
import database.Notification;
import database.User;
import server.*;
import com.google.gson.*;

import java.net.SocketAddress;
import java.sql.Timestamp;
import java.util.ArrayList;

public class NotificationDealer implements IAPIRoute {
    Tracker tracker;
    NotificationHandler handler;
    public NotificationDealer(Tracker tracker, NotificationHandler handler) {
        this.tracker = tracker;
        this.handler = handler;
    }
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        int cookie = parseGetArgs(request.getBody());
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
        if (request.getMethod().equals("/user/notifications/get")) {
            //Get notifications, format them for return, send return
            try {
                Notification[] notifications = user.getNotifications();
                JsonObject[] objects = new JsonObject[notifications.length];
                int j = 0;
                for (Notification notification : notifications) {
                    JsonObject formatted = handler.format(notification);
                    if (formatted == null) {
                        System.out.println("Failed to format notification " + notification.getNotifid());
                        continue;
                    }
                    objects[j] = formatted;
                    j++;
                }
                Gson gson = new Gson();
                String message = gson.toJson(objects);
                if (message == null) {
                    Socketeer.send(HTTPMessage.makeResponse("{\"error\":\"couldn't serialize notifications\"}", HTTPMessage.HTTPStatus.BadRequest), sock);
                    return;
                }
                else {
                    Socketeer.send(HTTPMessage.makeResponse(message, HTTPMessage.HTTPStatus.OK), sock);
                    return;
                }
            }
            catch (Exception e) {
                System.out.println("Encountered an error returning notifications");
                e.printStackTrace();
            }
        }
        else if (request.getMethod().equals("/user/notifications/respond")) {
            Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
            return;
        }
        else {
            Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
    }
    private int parseGetArgs(String body) {
        try {
            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(body, JsonObject.class);
            if (!jobj.has("cookie")) {
                return 0;
            }
            return jobj.get("cookie").getAsInt();
        }
        catch (Exception e) {
            System.out.println("Failed to parse Args");
            return 0;
        }
    }
}
