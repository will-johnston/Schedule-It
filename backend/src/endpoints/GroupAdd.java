package endpoints;

import server.*;

import java.net.Socket;

//Adds (invites) a user to the group
public class GroupAdd implements IAPIRoute {
    @Override
    public void setup() {

    }

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        Socketeer.sendText(HTTPMessage.makeNotImplemented(), sock);
    }
}
