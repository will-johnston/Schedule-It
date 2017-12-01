import org.junit.*;
import com.google.gson.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/*
* Only passes everynow and then, seems like an issue with HttpRequest
* */
public class EditEventsTest {
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
    public int getFirstEvent(int cookie, int groupid) throws Exception {
        JsonObject request = new JsonObject();
        request.addProperty("cookie", cookie);
        request.addProperty("groupid", groupid);
        String response = HttpRequest.postRequest(calendarGet, request.toString());
        System.out.println("Response: " + response);
        JsonArray events = new Gson().fromJson(response, JsonArray.class);

        JsonObject event = events.get(0).getAsJsonObject();
        return event.get("id").getAsInt();
    }
    public void changeName(int cookie, int groupid, int eventid) throws Exception {
        JsonObject request = new JsonObject();
        request.addProperty("cookie", cookie);
        request.addProperty("groupid", groupid);
        request.addProperty("eventid", eventid);
        request.addProperty("name", "testomundo");
        HttpRequest.postRequest(calendarEdit, request.toString());
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
    public class Calendar {
        private String description;
        private long id;
        private boolean isOpenEnded;
        private String name;
        private Response response;
        private String time;
        private String type;

        public String getDescription() { return description; }
        public void setDescription(String value) { this.description = value; }

        public long getId() { return id; }
        public void setId(long value) { this.id = value; }

        public boolean getIsOpenEnded() { return isOpenEnded; }
        public void setIsOpenEnded(boolean value) { this.isOpenEnded = value; }

        public String getName() { return name; }
        public void setName(String value) { this.name = value; }

        public Response getResponse() { return response; }
        public void setResponse(Response value) { this.response = value; }

        public String getTime() { return time; }
        public void setTime(String value) { this.time = value; }

        public String getType() { return type; }
        public void setType(String value) { this.type = value; }
    }
    public class Response {
        private long count;

        public long getCount() { return count; }
        public void setCount(long value) { this.count = value; }
    }
}
