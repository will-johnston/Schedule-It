package endpoints;

import database.Group;
import management.Tracker;
import server.HTTPMessage;
import com.google.gson.*;
import server.Socketeer;

import java.net.Socket;

//gets the list of groups a user is a part of
public class GroupGet implements IAPIRoute {
    Tracker tracker;
    public GroupGet(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
    }
    //returns the user cookie
    //Return 0 on failure
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
