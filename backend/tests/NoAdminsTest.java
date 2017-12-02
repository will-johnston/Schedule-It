import com.google.gson.reflect.TypeToken;
import database.Group;
import org.junit.*;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class NoAdminsTest {
    String groupsGet = "http://scheduleitdb.duckdns.org:8181/user/groups/get";
    String noAdmins = "http://scheduleitdb.duckdns.org:8181/user/groups/admin/no";
    @Test
    public void test() {
        CAssert.AssertDoesNotFail(() -> {
            int cookie = Misc.login();
            int groupid = getMeGroup(cookie);
            Assert.assertNotEquals(groupid, 0);
            callNoAdmins(groupid, cookie);
            return 1;
        });
    }
    public int getMeGroup(int cookie) throws Exception {
        JsonObject request = new JsonObject();
        request.addProperty("cookie", cookie);
        String response = HttpRequest.postRequest(groupsGet, request.toString());
        Type Groups = new TypeToken<List<Groups>>() {}.getType();
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
    public void callNoAdmins(int groupid, int cookie) throws Exception {
        /*
        *"cookie" : "int",
         "groupid" : "int",
         "noadmins" : "boolean"
        * */
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cookie", cookie);
        jsonObject.addProperty("groupid", groupid);
        jsonObject.addProperty("noadmins", true);
        HttpRequest.postRequest(noAdmins, jsonObject.toString());
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
    class GroupsDeserializer implements JsonDeserializer<Groups> {

        @Override
        public Groups deserialize(JsonElement json, Type typeOfT,
                                 JsonDeserializationContext context) throws JsonParseException {

            JsonArray array = json.getAsJsonArray();
            return new Groups(array.get(1).getAsInt(), array.get(0).getAsString());

        }
    }
}
