package endpoints;

import database.ModifyUserInDb;
import database.User;
import server.*;
import management.*;
import java.util.*;
import com.google.gson.*;

import java.net.Socket;

public class UserEdit implements IAPIRoute {
    Tracker tracker;
    public UserEdit(Tracker tracker) {
        this.tracker = tracker;
    }
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        System.out.println("Called UserEdit");
        Object[] args = parseArgs(request.getBody());
        if (args == null) {
            String response = "{\"error\":\"Invalid Arguments\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        String username = (String)args[0];
        int cookie = (int)args[1];
        //System.out.println(String.format("Username: %s, cookie: %d", args.get("username").toString(), Integer.parseInt(args.get("cookie").toString())));
        if (!tracker.isLoggedIn(cookie)) {
            String response = "{\"error\":\"User is not logged in\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        User user = tracker.getUser(cookie);
        if (args[2] != null) { user.setPassword((String)args[2]); }
        if (args[2] != null) { System.out.println("Set new password"); }
        if (args[3] != null) { user.setName((String)args[3]); }
        if (args[3] != null) { System.out.println("Set new name"); }
        if (args[4] != null) { user.setEmail((String)args[4]); }
        if (args[4] != null) { System.out.println("Set new email"); }
        if (args[5] != null) { user.setPhone((String)args[5]); }
        if (args[5] != null) { System.out.println("Set new phone"); }
        if (args[6] != null) { user.setImageUrl((String)args[6]); }
        if (args[6] != null) { System.out.println("Set new image"); }
        //Update Db
        try {
            ModifyUserInDb.modifyUser(makeMods(user), username);
        }
        catch (Exception e) {
            e.printStackTrace();
            String response = "{\"error\":\"Couldn't update database\"}";
            Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        tracker.updateUser(cookie, user);
        Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.OK), sock);
        return;
    }
    //username, cookie, password, fullname, email, phone number
    private Object[] parseArgs(String body) {
        //required fields; username, login cookie
        try {
            Gson gson = new Gson();
            JsonObject jobj = gson.fromJson(body, JsonObject.class);
            if (!jobj.has("username")) {
                return null;
            }
            if (!jobj.has("cookie")) {
                return null;
            }
            String username = jobj.get("username").getAsString(), password = null, fullname = null, email = null, phonenumber = null, image = null;
            int cookie = jobj.get("cookie").getAsInt();
            if (jobj.has("pass")) {
                password = jobj.get("pass").getAsString();
            }
            if (jobj.has("fullname")) {
                fullname = jobj.get("fullname").getAsString();
            }
            if (jobj.has("email")) {
                email = jobj.get("email").getAsString();
            }
            if (jobj.has("phone")) {
                phonenumber = jobj.get("phone").getAsString();
            }
            if (jobj.has("image")) {
                image = jobj.get("image").getAsString();
            }
            /*for (Map.Entry keyvalue : jobj.entrySet()) {
                if (keyvalue.getKey() == "pass") { password = keyvalue.getValue().toString(); }
                else if (keyvalue.getKey() == "fullname") { fullname = keyvalue.getValue().toString(); }
                else if (keyvalue.getKey() == "email") { email = keyvalue.getValue().toString(); }
                else if (keyvalue.getKey() == "phone") { phonenumber = keyvalue.getValue().toString(); }
            }*/
            return new Object[] { username, cookie, password, fullname, email, phonenumber, image };
        }
        catch (Exception e) {
            System.out.println("Failed to parse UserEdit args");
            return null;
        }
    }
    private String[] makeMods(User values) {
        //Format is {"email", "example@gmail.com", "fullname", "Clarence tarence", "password", "pss", "phone_number", "7"};
        String[] mods = new String[10];
        mods[0] = "email";
        mods[1] = values.getEmail();
        mods[2] = "fullname";
        mods[3] = values.getName();
        mods[4] = "password";
        mods[5] = values.getPassword();
        mods[6] = "phone_number";
        mods[7] = values.getPhone();
        mods[8] = "image";
        mods[9] = values.getImageUrl();
        for (int i = 0; i < mods.length; i++) {
            System.out.println(String.format("%d: %s", i, mods[i]));
        }
        return mods;
    }
}
