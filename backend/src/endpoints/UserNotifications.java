package endpoints;
import management.Tracker;
import database.Notification;
import database.User;
import server.*;
import com.google.gson.*;

import java.net.SocketAddress;
import java.sql.Timestamp;
import java.util.ArrayList;

public class UserNotifications implements IAPIRoute {
    Tracker tracker;
    public UserNotifications(Tracker tracker) {
        this.tracker = tracker;
    }
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        /*if (request.getMethod().equals("/user/notifications/get")) {
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
            ArrayList<Notification> notifications = user.getNotifications();
			if (notifications == null) {
                String response = "{}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.OK), sock);
                return;
            }
            GetNotifResponse[] responses = new GetNotifResponse[notifications.size()];
            int i = 0;
            for (Notification notification : notifications) {
                try {
                    responses[i] = new GetNotifResponse(notification);
                    i++;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Couldn't add notification to response array");
                }
            }
            String json = genericToJson(responses);
            if (json != null) {
                Socketeer.send(HTTPMessage.makeResponse(json, HTTPMessage.HTTPStatus.OK), sock);
                return;
            }
            else {
                //send response
                String response = "{\"error\":\"Couldn't get notifications\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
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

        /*String json = res.toJson();
        if (json == null) {
            String response = "{\"error\":\"Couldn't convert user settings\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        Socketeer.send(HTTPMessage.makeResponse(json, HTTPMessage.HTTPStatus.OK), sock);
        return;*/
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
    public String genericToJson(Object object) {
        try {
            return new Gson().toJson(object);
        }
        catch (Exception e) {
            return null;
        }
    }
    class GetNotifResponse {
        String type;
        int id;
        DataObj data;
        public GetNotifResponse(Notification notification) throws Exception {
            this.type = notification.getType().toString();
            this.id = notification.getNotifid();
            this.data = new DataObj(notification);
        }
        class DataObj {
            String fullName;
            String imageUrl = "";
            Timestamp sent;
            public DataObj(Notification notification) throws Exception {
                //params should be the user_id that sent the invite
                int requester = parseParams(notification.getParams());
                if (requester == -1) {
                    System.out.println("Invalid user_id format in params");
                    throw new Exception("Invalid user_id format in params");
                }
                User user = tracker.getUserById(requester);
                if (user == null) {
                    //try getting from database
                    user = User.fromDatabase(requester);
                    if (user == null) {
                        System.out.println("Invalid user_id in params");
                        throw new Exception("Invalid user_id in params");
                    }
                    else {
                        //add to tracker
                        tracker.addUser(user);
                    }
                }
                this.fullName = user.getName();
                this.imageUrl = user.getImageUrl();
                this.sent = notification.getCreated();
            }
            //return user_id of sender
            //returns -1 on failure
            int parseParams(String params) {
                if (params.contains(",")) {
                    return -1;
                }
                try {
                    return Integer.parseInt(params);
                }
                catch (Exception e) {
                    return -1;
                }
            }
        }
    }
}
