import database.*;
import management.*;
import java.util.concurrent.*;
public class LoggedInTest {
    public static void main(String[] args) throws Exception {
        Tracker tracker = new Tracker();
        User user = new User("bob", "bob@aol.com", "asdf", "1234567890", 0, null, null);
        int cookie = tracker.login(user);
        System.out.println("Logged in? " + tracker.isLoggedIn(cookie));
        Thread.sleep(60 * 1000);
        System.out.println("Logged in? " + tracker.isLoggedIn(cookie));
        //Test logged out
        tracker.setTimeout(5);          //5 seconds
        Thread.sleep(5999);
        System.out.println("Logged in? " + tracker.isLoggedIn(cookie));
    }
}
