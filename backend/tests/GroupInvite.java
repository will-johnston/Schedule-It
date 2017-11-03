import com.google.gson.*;
import org.junit.*;
public class GroupInvite {
    @Test
    public void GroupInvite() {
        CAssert.AssertDoesNotFail(() -> {
            String url = Misc.getUrl() + "user/groups/create";
            String inviteUrl = Misc.getUrl() + "user/groups/invite";
            System.out.println("logging in");
            int cookie = Misc.login();
            if (cookie == -1) {
                Assert.fail();
            }
            JsonObject obj = new JsonObject();
            obj.addProperty("cookie", cookie);
            obj.addProperty("groupname", CAssert.generateString(4));
            System.out.println("Sending Group Create");
            String createdGroup = HttpRequest.postRequest(url, obj.toString());
            int groupid = new Gson().fromJson(createdGroup, JsonObject.class).get("groupid").getAsInt();

            String invitee = "";

            return 0;
        });

    }
}
