import com.google.gson.*;
import org.junit.*;

import java.util.concurrent.Callable;

public class CreateGroupTest {
    @Test
    public void CreateGroupTest() {
        //fails on anything but 200
        CAssert.AssertDoesNotFail(() -> {
            String url = Misc.getUrl() + "user/groups/create";
            System.out.println("logging in");
            int cookie = Misc.login();
            if (cookie == -1) {
                Assert.fail();
            }
            JsonObject obj = new JsonObject();
            obj.addProperty("cookie", cookie);
            obj.addProperty("groupname", CAssert.generateString(4));
            System.out.println("Sending Group Create");
            HttpRequest.postRequest(url, obj.toString());
            return 0;
        });
    }
}
