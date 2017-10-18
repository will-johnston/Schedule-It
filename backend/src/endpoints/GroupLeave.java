package endpoints;

import server.*;

import java.net.Socket;

//Leave the Group
public class GroupLeave implements IAPIRoute {

    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
    }
}
