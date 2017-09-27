package endpoints;

import server.*;

import java.net.Socket;

//Joins a given group
public class GroupJoin implements IAPIRoute {
    @Override
    public void setup() {

    }

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        Socketeer.sendText(HTTPMessage.makeNotImplemented(), sock);
    }
}
