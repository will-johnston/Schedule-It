package endpoints;

import server.*;

import java.net.Socket;

//Adds (invites) a user to the group
public class GroupAdd implements IAPIRoute {

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
    }
}
