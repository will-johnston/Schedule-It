package endpoints;

import server.*;

import java.net.Socket;

//Edit group info or set permissions
public class GroupEdit implements IAPIRoute {

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
    }
}
