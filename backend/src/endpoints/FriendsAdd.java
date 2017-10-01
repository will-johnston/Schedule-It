package endpoints;

import server.*;

import java.net.Socket;

//Adds a friend to a User
public class FriendsAdd implements IAPIRoute {

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        Socketeer.send(HTTPMessage.makeNotImplemented(), sock);

    }
}
