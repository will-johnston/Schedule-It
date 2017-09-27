package endpoints;

import server.*;

import java.net.Socket;

//Leave the Group
public class GroupLeave implements IAPIRoute {
    @Override
    public void setup() {

    }

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        Socketeer.sendText(HTTPMessage.makeNotImplemented(), sock);
    }
}
