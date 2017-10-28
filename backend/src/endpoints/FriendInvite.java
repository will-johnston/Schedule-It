package endpoints;
import database.User;
import management.*;
import server.*;
import com.google.gson.*;

public class FriendInvite implements IAPIRoute {
    Tracker tracker;
    NotificationHandler handler;
    public FriendInvite(Tracker tracker, NotificationHandler handler) {
        this.tracker = tracker;
        this.handler = handler;
    }

    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        Object[] args = parseArgs(request.getBody());
        if (args == null) {
            String response = "{ \"error\" : \"Invalid arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        int cookie = (int)args[0];
        String username = (String)args[1];
        if (!tracker.isLoggedIn(cookie)) {
            String response = "{ \"error\" : \"User not logged in\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        User requester = tracker.getUser(cookie);
        User toadd = tracker.getUserByName(username);
        if (toadd == null) {
            //check and see if user exists in db
            toadd = User.fromDatabase(username);
            if (toadd != null) {
                //User to add is not in tracker, but in db, add to tracker
                if (!tracker.addUser(toadd)) {
                    String response = "{ \"error\" : \"Failed to add friend\"}";
                    Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                    return;
                }
            }
            else {
                String response = "{ \"error\" : \"Friend doesn't exist\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
        }
        if (handler.sendFriendInvite(requester, toadd)) {
            //success
            Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
            return;
        }
        else {
            //failed
            String response = "{ \"error\" : \"Couldn't invite friend\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
    }
    //return [cookie, username]
    private Object[] parseArgs(String body) {
        try {
            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(body, JsonObject.class);
            if (jobj == null) {
                return null;
            }
            if (!jobj.has("cookie")) {
                return null;
            }
            if (!jobj.has("username")) {
                return null;
            }
            System.out.println("Has the right parameters");
            int cookie = jobj.get("cookie").getAsInt();
            String username = jobj.get("username").getAsString();
            return new Object[] { cookie, username };
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("failed to parse args");
            return null;
        }
    }
}
