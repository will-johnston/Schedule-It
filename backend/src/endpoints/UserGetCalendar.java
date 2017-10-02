package endpoints;

import server.*;

import java.net.Socket;

//Gets the User's calendar
public class UserGetCalendar implements IAPIRoute {

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        Socketeer.send(HTTPMessage.makeNotImplemented(), sock);
    }
}
