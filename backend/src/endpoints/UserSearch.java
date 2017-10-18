package endpoints;

import server.*;

import java.net.Socket;

//searches all the Users in the system
public class UserSearch implements IAPIRoute {
    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
    }
}
