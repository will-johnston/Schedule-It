package endpoints;

import server.*;

import java.net.Socket;

//Creates a new group
public class GroupCreate implements IAPIRoute {

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
    }
}
