import java.net.*;

/**
 * Created by Ryan on 9/20/2017.
 */

//All API endpoints should be registered as an IAPIRoute implementing object
//TODO fill out routing functions
public interface IAPIRoute {
    void setup();       //Used during initialization, if needed
    void execute(Socket sock, HTTPMessage request);
}
