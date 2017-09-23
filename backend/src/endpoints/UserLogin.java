package endpoints;

import server.HTTPMessage;

import java.net.Socket;

//Logins into the system
//Should return a valid login cookie
public class UserLogin implements IAPIRoute {
    @Override
    public void execute(Socket sock, HTTPMessage request) {

    }

    @Override
    public void setup() {

    }
}
