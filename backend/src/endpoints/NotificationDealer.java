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
    private User getUser(int cookie, SSocket sock) {
        if (cookie == 0) {
            String response = "{\"error\":\"Invalid Arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return null;
        }
        if (!tracker.isLoggedIn(cookie)) {
            String response = "{\"error\":\"User not logged in\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return null;
        }
        User user = tracker.getUser(cookie);
        if (user == null) {
            String response = "{\"error\":\"Couldn't get user\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return null;
        }
        return user;
    }
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        if (request.getMethod().equals("/user/notifications/get")) {
            int cookie = parseGetArgs(request.getBody());
            User user = getUser(cookie, sock);
            if (user == null) {
                //already handled sending errors
                return;
            }
            //Get notifications, format them for return, send return
            try {
                Notification[] notifications = user.getNotifications();
                if (notifications == null) {
                    Socketeer.send(HTTPMessage.makeResponse("{}", HTTPMessage.HTTPStatus.OK), sock);
                    return;
                }
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
                Socketeer.send(HTTPMessage.makeResponse("{\"error\":\"some exception occurred\"}", HTTPMessage.HTTPStatus.MethodNotAllowed), sock);
                return;
            }
        }
        else if (request.getMethod().equals("/user/notifications/respond")) {
            /*Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
            return;*/
            Object[] args = parseHandleArgs(request.getBody()); //cookie, notif_id, response (JsonObject)
            if (args == null) {
                String response = "{\"error\":\"Invalid Arguments\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            User user = getUser((int)args[0], sock);
            if (user == null) {
                //already handled sending errors
                return;
            }
            Notification notification = user.getNotificationById((int)args[1]);
            if (notification == null) {
                String response = "{\"error\":\"Failed to get notification\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            String  handledResponse = handler.handle(notification, (JsonObject)args[2], user);
            if (handledResponse == null) {
                String response = "{\"error\":\"Failed to handle notification\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            else {
                Socketeer.send(HTTPMessage.makeResponse(handledResponse, HTTPMessage.HTTPStatus.OK), sock);
                return;
            }
        }
        else if (request.getMethod().equals("/user/notifications/dismiss")) {
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
    //cookie, notif_id, response (JsonObject)
    private Object[] parseHandleArgs(String body) {
        Gson gson = new Gson();
        JsonObject jobj = gson.fromJson(body, JsonObject.class);
        if (!jobj.has("cookie")) {
            return null;
        }
        if (!jobj.has("notification")) {
            return null;
        }
        if (!jobj.has("response")) {
            return null;
        }
        int cookie = jobj.get("cookie").getAsInt();
        JsonObject notification = jobj.getAsJsonObject("notification");
        if (!notification.has("id")) {
            return null;
        }
        JsonObject response = jobj.getAsJsonObject("response");
        return new Object[] {
                cookie, notification.get("id").getAsInt(), response
        };
    }
}
