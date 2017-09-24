import endpoints.*;
import server.*;
public class Main {

    //server.Router initialization should be done here
    public static Router initRouter() {
        try {
            Router router = new Router();
            router.add("/api/example/example", new ExampleEndpoint());
            router.add("/api/user/login", new UserLogin());
            router.add("/api/user/edit", new UserEdit());
            router.add("/api/user/groups/calendar/get", new GroupGetCalendar());
            router.add("/api/user/calendar/get", new UserGetCalendar());
            router.add("/api/user/groups/leave", new GroupLeave());
            router.add("/api/user/groups/join", new GroupJoin());
            router.add("/api/user/search", new UserSearch());
            router.add("/api/user/groups/add", new GroupAdd());
            router.add("/api/user/groups/edit", new GroupEdit());
            router.add("/api/user/groups/create", new GroupCreate());
            router.add("/api/user/friends/remove", new FriendsRemove());
            router.add("/api/user/create", new UserCreate());
            router.add("/api/user/friends/add", new FriendsAdd());
            return router;
        }
        catch (Exception e) {
            System.out.println("Init server.Router encountered an error!");
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
        while (server.getListening()) {
            //Todo listen for kill
            Thread.currentThread().sleep(1000);
        }
    }
}
