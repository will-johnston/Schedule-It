package endpoints;

import management.Tracker;
import server.HTTPMessage;
import com.google.gson.*;
import server.Socketeer;
import database.*;

import java.net.Socket;

//gets the list of groups a user is a part of
public class GroupGet implements IAPIRoute {
    Tracker tracker;
    public GroupGet(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        int cookie = parseArgs(request.getBody());
        if (cookie == 0) {
            String response = "{\"error\":\"Invalid Arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        if (!tracker.isLoggedIn(cookie)) {
            String response = "{\"error\":\"User is not logged in\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        User user = tracker.getUser(cookie);
        if (user == null) {
            String response = "{\"error\":\"Couldn't get user\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        String[] groups = user.getGroups(tracker);
        if (groups == null) {
            String response = "{\"error\":\"Couldn't get groups\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        else {
            Response res = new Response(groups);
            String json = res.toJson();
            if (json == null) {
                String response = "{\"error\":\"Couldn't get groups\"}";
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            else {
                Socketeer.send(HTTPMessage.makeResponse(json, HTTPMessage.HTTPStatus.OK), sock);
                return;
            }
        }
        //Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
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
    class Response {
        String[] groups;
        public Response(String[] groups) {
            this.groups = groups;
            System.out.println("Groups length: " + groups.length);
        }
        public String toJson() {
            try {
                Gson gson = new Gson();
                return gson.toJson(this, Response.class);
            }
            catch (Exception e) {
                System.out.println("Couldn't convert to json");
                return null;
            }
        }
    }
}
