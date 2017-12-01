package endpoints;

import com.google.gson.*;
import server.*;
import management.Tracker;
import database.User;
import server.Socketeer;

import java.net.Socket;

//Gets user settings
public class UserSettings implements IAPIRoute {

    Tracker tracker;
    public UserSettings(Tracker tracker) {
        this.tracker = tracker;
    }
    @Override
    /*
    Returns
    {
    "username" : "",
    "fullname" : "",
    "email" : "",
    "phone" : "",
    "imageURL" : ""
    }
     */
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
        Response res = new Response(user.getUsername(), user.getName(), user.getEmail(), user.getPhone(),
                user.getImageUrl());
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
        String username;
        String fullname;
        String email;
        String phone;
        String image;
        public Response(String username, String fullname, String email, String phone, String imageURL) {
            this.username = username;
            this.fullname = fullname;
            this.email = email;
            this.phone = phone;
            if (imageURL == null) {
                this.image = "";
            }
            else {
                this.image = imageURL;
            }
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
