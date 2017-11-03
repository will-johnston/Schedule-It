import org.junit.*;
import com.google.gson.*;
public class LeaveGroup {
    @Test
    public void LeaveGroupTest() {
        //fails on anything but 200
        CAssert.AssertDoesNotFail(() -> {
            String url = Misc.getUrl() + "user/groups/create";
            String urlLeave = Misc.getUrl() + "user/groups/leave";
            System.out.println("logging in");
            int cookie = Misc.login();
            if (cookie == -1) {
                Assert.fail();
            }
            JsonObject obj = new JsonObject();
            obj.addProperty("cookie", Integer.toString(cookie));
            obj.addProperty("groupname", CAssert.generateString(4));
            System.out.println("Sending Group Create");
            String groupid = HttpRequest.postRequest(url, obj.toString());

            JsonObject leaveObj = new JsonObject();
            leaveObj.addProperty("cookie", cookie);
            leaveObj.addProperty("groupid", new Gson().fromJson(groupid, JsonObject.class).get("groupid").getAsInt());
            System.out.println("Leaving group");
            HttpRequest.postRequest(urlLeave, leaveObj.toString());
            return 0;
        });
    }
}
