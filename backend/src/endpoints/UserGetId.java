package endpoints;

import com.google.gson.*;
import server.*;
import management.Tracker;
import database.User;
import server.Socketeer;

import java.net.Socket;

//Gets user settings
public class UserGetId implements IAPIRoute {

    Tracker tracker;
    public UserGetId(Tracker tracker) {
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
        int id = user.getId();
	Gson gson = new Gson();
        String jsonid = gson.toJson(Integer.toString(id));
        Socketeer.send(HTTPMessage.makeResponse(jsonid, HTTPMessage.HTTPStatus.OK), sock);
        return;
    }
    //return 0 if fail, cookie if success
    private int parseArgs(String body) {
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
