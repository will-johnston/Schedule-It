package endpoints;

import server.*;

import java.net.Socket;

//Edit group info or set permissions
public class GroupEdit implements IAPIRoute {

    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
        /**
         TO IMPLEMENT ADMINS:
         User user = tracker.getUser(cookie);
         Group group = user.getGroupById(groupid, tracker);
         ArrayList<String> admins = group.getAdmins();

         if (!admins.contains(username)) {
            //if user is not an admin, create error message
            String adminErr = "{\"error\":\"User is not an admin in the group\"}";
            Socketeer.send(HTTPMessage.makeResponse(adminErr, HTTPMessage.HTTPStatus.BadRequest), sock);
         }
         **/


    }
}
