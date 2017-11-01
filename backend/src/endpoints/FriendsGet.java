package endpoints;
import com.google.gson.*;
import server.*;
import management.*;
import database.*;

import java.net.Socket;
import java.util.ArrayList;

public class FriendsGet implements IAPIRoute {
    Tracker tracker;
    public FriendsGet(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        int cookie = parseArgs(request.getBody());
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
        Response res = new Response(user.getFriends());
        String json = res.toJson();
        if (json == null) {
            String response = "{\"error\":\"Couldn't convert user settings\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        Socketeer.send(HTTPMessage.makeResponse(json, HTTPMessage.HTTPStatus.OK), sock);
        return;
    }
    //return 0 if fail, cookie if success
    private int parseArgs(String body) {
		System.out.println(body);
        try {
            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(body, JsonObject.class);
            if (!jobj.has("cookie")) {
                return 0;
            }
            return jobj.get("cookie").getAsInt();
        }
        catch (Exception e) {
			e.printStackTrace();
            System.out.println("Failed to parse Args");
            return 0;
        }
    }
    class Response {
        String[] friends;
        public Response(ArrayList<String> list) {
            friends = list.toArray(new String[list.size()]);
        }
        public String toJson() {
            try {
                Gson gson = new Gson();
                return gson.toJson(this, Response.class);
            }
            catch (Exception e) {
                return null;
            }
        }
    }
}
