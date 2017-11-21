import endpoints.*;
import server.*;
import management.*;
public class Main {

    //server.Router initialization should be done here
    public static Router initRouter() {
        try {
            Router router = new Router();
            Images images = new Images();
            Tracker tracker = new Tracker();
            NotificationHandler handler = new NotificationHandler(tracker);
            NotificationDealer dealer = new NotificationDealer(tracker, handler);
            upload up = new upload(images, tracker);           //For special upload case
            router.add("/upload", up);
            router.add("/upload/chunk", up);
            router.add("/upload/info", up);
            router.add("/example/example", new ExampleEndpoint());
            router.add("/user/login", new UserLogin(tracker));
            router.add("/user/edit", new UserEdit(tracker));
            router.add("/user/groups/leave", new GroupLeave(tracker));
            //router.add("/user/groups/join", new GroupJoin());
            //router.add("/user/search", new UserSearch());
            //router.add("/user/groups/add", new GroupAdd());
            router.add("/user/groups/edit", new GroupEdit());
            router.add("/user/groups/create", new GroupCreate(tracker));
            router.add("/user/groups/chat", new MessageAdd(tracker));
            router.add("/user/groups/getChat", new MessageGet(tracker));
            router.add("/user/friends/remove", new FriendsRemove(tracker));
            router.add("/user/create", new UserCreate(tracker));
            router.add("/user/friends/add", new FriendsAdd(tracker));
            router.add("/user/friends/invite", new FriendInvite(tracker, handler));
            router.add("/user/getsettings", new UserGetSettings(tracker));
            router.add("/user/friends/get", new FriendsGet(tracker));
            router.add("/user/notifications/get", dealer);
            router.add("/user/notifications/respond", dealer);
            //router.add("/user/notifications/dismiss", dealer);
            router.add("/user/groups/get", new GroupGet(tracker));
            router.add("/user/groups/invite", new GroupInvite(tracker, handler));
            router.add("/timeinput/add", new TimeInputAdd(tracker, handler));
	    router.add("/ugejunction/add", new UserGroupEventJunctionAdd(tracker));
	    router.add("/ugejunction/get", new UserGroupEventJunctionGet(tracker));
	    router.add("/user/groups/calendar/get", new GroupGetCalendar(tracker));
            router.add("/user/groups/calendar/add", new GroupAddCalendar(tracker));
            router.add("/user/groups/mute", new GroupMute(tracker));
            router.add("/test/user/delete", new UserDelete());
	    router.add("/user/getId", new UserGetId(tracker));
	    router.add("/user/groups/admin/add", new GroupAddAdmin(tracker, handler));
	    router.add("/user/groups/admin/remove", new GroupRemoveAdmin(tracker, handler));
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
        Server server = new Server(8181,8282);
        Router router = initRouter();
        Tracker tracker = new Tracker();
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
