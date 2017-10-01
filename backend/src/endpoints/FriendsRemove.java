package endpoints;

import server.*;

import java.net.Socket;

//Remove friend from User
public class FriendsRemove implements IAPIRoute {

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
    }
}
