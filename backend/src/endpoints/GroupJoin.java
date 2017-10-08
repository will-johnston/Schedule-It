package endpoints;

import server.*;

import java.net.Socket;

//Joins a given group
public class GroupJoin implements IAPIRoute {

    @Override
    public void execute(SSocket sock, HTTPMessage request) {
        Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
    }
}
