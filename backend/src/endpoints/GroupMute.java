package endpoints;
import com.google.gson.*;
import database.Group;
import database.User;
import management.Tracker;
import server.HTTPMessage;
import server.SSocket;
import server.Socketeer;

public class GroupMute implements IAPIRoute {
    Tracker tracker;
    public GroupMute(Tracker tracker) {
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
        boolean mute = (boolean)args[2];
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
        if (mute) {
            //Soft search
            if (user.inGroup(groupid)) {
                if (user.muteGroup(groupid)) {
                    Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
                    return;
                }
                else {
                    String response = "{\"error\":\"Couldn't mute group\"}";
                    Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                    return;
                }
            }
            else {
                String response = "{\"error\":\"User not in group, can't mute\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
        }
        else {
            //Soft search
            if (user.inGroup(groupid)) {
                if (user.unmuteGroup(groupid)) {
                    Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
                    return;
                }
                else {
                    String response = "{\"error\":\"Couldn't unmute group\"}";
                    Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                    return;
                }
            }
            else {
                String response = "{\"error\":\"User not in group, can't unmute\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
        }
    }
    //cookie, groupId
    private Object[] parseArgs(String body) {
        try {
            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(body, JsonObject.class);
            if (!jobj.has("cookie")) {
                return null;
            }
            if (!jobj.has("groupid")) {
                return null;
            }
            if (!jobj.has("mute")) {
                return null;
            }
            return new Object[] {jobj.get("cookie").getAsInt(), jobj.get("groupid").getAsInt(), jobj.get("mute").getAsBoolean() };
        }
        catch (Exception e) {
            System.out.println("Failed to parse args");
            return null;
        }
    }
}