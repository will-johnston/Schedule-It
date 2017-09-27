package endpoints;

import server.*;

import java.net.Socket;

//Creates a new group
public class GroupCreate implements IAPIRoute {
    @Override
    public void setup() {

    }

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        Socketeer.sendText(HTTPMessage.makeNotImplemented(), sock);
    }
}
