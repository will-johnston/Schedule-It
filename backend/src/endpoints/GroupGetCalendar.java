package endpoints;

import server.*;

import java.net.Socket;

//Gets the group calendar
public class GroupGetCalendar implements IAPIRoute {

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
    }
}
