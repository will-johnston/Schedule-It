import org.junit.*;
import com.google.gson.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class RemoveEventsTest {
    String groupsGet = "http://scheduleitdb.duckdns.org:8181/user/groups/get";
    String calendarGet = "http://scheduleitdb.duckdns.org:8181/user/groups/calendar/all";
    String calendarEdit = "http://scheduleitdb.duckdns.org:8181/user/groups/calendar/edit";
    @Test
    public void test() {
        CAssert.AssertDoesNotFail(() -> {
            int cookie = Misc.login();
            int groupid = getMeGroup(cookie);
            int eventid = getFirstEvent(cookie, groupid);
            changeName(cookie, groupid, eventid);
            return 0;
        });
    }
    public int getMeGroup(int cookie) throws Exception {
        JsonObject request = new JsonObject();
        request.addProperty("cookie", cookie);
        String response = HttpRequest.postRequest(groupsGet, request.toString());
        //Gson gson = new GsonBuilder().registerTypeAdapter(Groups.class,
        //new GroupsDeserializer()).create();

        //Groups[] mpa = gson.fromJson(response, Groups[].class);
        //System.out.println(mpa.length);
        System.out.println("Response: " + response);
        JsonArray groups = new Gson().fromJson(response, JsonArray.class);
        for (JsonElement group : groups) {
            JsonObject obj = group.getAsJsonObject();
            System.out.println("Name: " + obj.get("name").getAsString());
            if (obj.get("name").getAsString().toLowerCase().equals("me")) {
                return obj.get("id").getAsInt();
            }
        }
        return 0;
    }
}
