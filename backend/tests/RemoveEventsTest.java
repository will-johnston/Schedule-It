import org.junit.*;
import com.google.gson.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class RemoveEventsTest {
    String groupsGet = "http://scheduleitdb.duckdns.org:8181/user/groups/get";
    String calendarGet = "http://scheduleitdb.duckdns.org:8181/user/groups/calendar/all";
    String calendarRemove = "http://scheduleitdb.duckdns.org:8181/user/groups/calendar/remove";
    @Test
    public void test() {
        CAssert.AssertDoesFail(() -> {
            int cookie = Misc.login();
            int groupid = getMeGroup(cookie);
            removeEvent(cookie, groupid);
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
    public void removeEvent(int cookie, int groupid) throws Exception {
        JsonObject request = new JsonObject();
        request.addProperty("cookie", cookie);
        request.addProperty("groupid", groupid);
        request.addProperty("eventid", 0);

        HttpRequest.postRequest(calendarRemove, request.toString());
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
