import java.time.*;
import java.util.concurrent.*;
public class Main {

    //Router initialization should be done here
    public static Router initRouter() {
        try {
            Router router = new Router();
            router.add("/api/example/example", new ExampleEndpoint());
            return router;
        }
        catch (Exception e) {
            System.out.println("Init Router encountered an error!");
            System.out.println(String.format("Error Message: %s", e.getMessage()));
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) throws Exception {
	// write your code here
        Server server = new Server(8181);
        Router router = initRouter();
        if (router == null) {
            //An error occured, can't start server
            System.out.println("Failed in initailze the router, shutting down the server");
            return;
        }
        server.startListening(router);
        while (server.isListening) {
            //Todo listen for kill
            Thread.currentThread().sleep(1000);
        }
    }
}
