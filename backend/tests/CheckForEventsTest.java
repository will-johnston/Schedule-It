import org.junit.*;
import com.google.gson.*;
import java.util.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class CheckForEventsTest {

    String groupsGet = "http://scheduleitdb.duckdns.org:8181/user/groups/get";
    String calendarCheck = "http://scheduleitdb.duckdns.org:8181/user/groups/calendar/check";

    @Test
    public void test() {
        CAssert.AssertDoesNotFail(() -> {
            int cookie = Misc.login();
            int groupid = getMeGroup(cookie);
            check(groupid, cookie);
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
    public void check(int groupid, int cookie) throws Exception {
        /*
        *"cookie" : "int",
         "groupid" : "int",
        * */
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cookie", cookie);
        jsonObject.addProperty("groupid", groupid);
        HttpRequest.postRequest(calendarCheck, jsonObject.toString());
    }
    class Groups {
        private long id;
        private String imageUrl;
        private String muted;
        private String name;
        private boolean noadmins;
        public Groups(int id, String name) {
            this.id = id;
            this.name = name;
        }
        public long getId() { return id; }
        public void setId(long value) { this.id = value; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String value) { this.imageUrl = value; }

        public String getMuted() { return muted; }
        public void setMuted(String value) { this.muted = value; }

        public String getName() { return name; }
        public void setName(String value) { this.name = value; }

        public boolean getNoadmins() { return noadmins; }
        public void setNoadmins(boolean value) { this.noadmins = value; }
    }
}
