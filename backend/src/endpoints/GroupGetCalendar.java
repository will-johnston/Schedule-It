package endpoints;

import server.*;

import java.net.Socket;

//Gets the group calendar
public class GroupGetCalendar implements IAPIRoute {
    @Override
    public void setup() {

    }

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        Socketeer.sendText(HTTPMessage.makeNotImplemented(), sock);
    }
}
