package endpoints;

import database.*;
import server.*;
import com.google.gson.*;
import management.*;

import java.net.Socket;

//Creates a new User in the system
public class UserCreate implements IAPIRoute {
    Tracker tracker;
    public UserCreate(Tracker tracker) {
        this.tracker = tracker;
    }
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
	System.out.println("Called Create User");
        String[] args = parseArgs(request.getBody());
        //args[0] - email
        //args[1] - pass
        //args[2] - name
        //args[3] - phone
        //args[4] - user
        if (args == null) {
            System.out.println("Invalid Arguments");
            Socketeer.send(HTTPMessage.makeResponse("", HTTPMessage.HTTPStatus.BadRequest), sock);
            return;
        }
        //try {
	if (AddUserToDb.addUser(args[4], args[2], args[1], args[0], args[3])) {
        User user = new User(args[2], args[0],args[1],args[3], -1, args[4], null, null);
        int cookie = tracker.login(user);
        System.out.println("Successfully added user, returning cookie");
        String response = String.format("{\"cookie\": \"%d\"}\n", cookie);
        //create the Me group
        System.out.println("Creating group Me with creatorID: " + user.getId());
        String[] results = GetFromDb.getUserFromName(args[4]);
        if (results != null) {
            try {
                System.out.println("Setting id");
                user.setId(Integer.parseInt(results[0]));
                int groupid = CreateGroup.createGroup("Me", user.getId());
                if (groupid > 0) {
                    //add to tracker and all that jazz
                    Group group = Group.fromDatabase(tracker, groupid);
                    if (group != null) {
                        user.addToGroup(group);
                        tracker.updateUser(cookie, user);
                        //Add clarence to group
                        User clarence = tracker.getClarence();
                        group.addUser(clarence);
                        clarence.addToGroup(group);
                    }
                }
            }
            catch (Exception e) {
                System.out.println("Failed to get id");
                e.printStackTrace();
            }

        }
        else {
            System.out.println("Couldn't get user from db after putting");
        }
		Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.OK), sock);
	}
	else {
		System.out.println("Failed to add, sending error");
        Socketeer.send(HTTPMessage.makeResponse("Failed to add user", HTTPMessage.HTTPStatus.Unknown), sock);
	}
        //}
        /*catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to add, sending error");
            Socketeer.send(HTTPMessage.makeResponse("Failed to add user", HTTPMessage.HTTPStatus.Unknown), sock);
	    return;
        }*/
    }

    //email, pass, name, phone, username
    //TODO change arr[4] to accept username
    public String[] parseArgs(String message) {
        //id, username, fullname, password, email, phoneNumber
        try {
		System.out.println("Trying to parse args");
            Gson gson = new Gson();
            JsonObject bodyObj = gson.fromJson(message, JsonObject.class);
            if (bodyObj == null) {
                System.out.println("Failed to parse JSON");
                return null;
            }
            if (!bodyObj.has("email")) {
                return null;
            }
            if (!bodyObj.has("pass")) {
                return null;
            }
            if (!bodyObj.has("name")) {
                return null;
            }
            if (!bodyObj.has("phone")) {
                return null;
            }
            if (!bodyObj.has("username")) {
                return null;
            }
		System.out.println("UserCreate has correct params");
            String[] arr = new String[5];
            arr[0] = bodyObj.get("email").getAsString();
            arr[1] = bodyObj.get("pass").getAsString();
            arr[2] = bodyObj.get("name").getAsString();
            arr[3] = bodyObj.get("phone").getAsString();
            arr[4] = bodyObj.get("username").getAsString();
            return arr;
        }
        catch (Exception e) {
            System.out.println("Caught an exception");
            e.printStackTrace();
            return null; 
        }
    }
}
