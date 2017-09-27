package endpoints;

import server.*;

import java.net.Socket;

//Logins into the system
//Should return a valid login cookie
public class UserLogin implements IAPIRoute {
    @Override
    public void execute(Socket sock, HTTPMessage request) {
        Socketeer.sendText(HTTPMessage.makeNotImplemented(), sock);
    }

    @Override
    public void setup() {

    }
}
