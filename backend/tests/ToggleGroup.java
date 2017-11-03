import com.google.gson.*;
import org.junit.*;
public class ToggleGroup {
    @Test
    public void ToggleGroupTest() {
        CAssert.AssertDoesNotFail(() -> {
            String url = Misc.getUrl() + "user/groups/create";
            String toggleUrl = Misc.getUrl() + "user/groups/mute";
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

            JsonObject muteObjTrue = new JsonObject();
            muteObjTrue.addProperty("cookie", cookie);
            muteObjTrue.addProperty("groupid", groupid);
            muteObjTrue.addProperty("mute", true);
            System.out.println("Sending mute");
            HttpRequest.postRequest(toggleUrl, muteObjTrue.toString());

            JsonObject muteObjFalse = new JsonObject();
            muteObjFalse.addProperty("cookie", cookie);
            muteObjFalse.addProperty("groupid", groupid);
            muteObjFalse.addProperty("mute", false);

            System.out.println("Sending unmute");
            HttpRequest.postRequest(toggleUrl, muteObjFalse.toString());
            return 0;
        });
    }
}
