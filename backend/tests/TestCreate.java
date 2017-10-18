import org.junit.*;
import com.google.gson.*;
public class TestCreate {
    static String address = "http://scheduleit.duckdns.org/api/user/create";
    static Gson gson = new Gson();
    @Test
    public void TestBasicCreate() {
        //use delete from users where username like "_____TEST"; to cleanup
        CAssert.AssertDoesNotFail(() -> {
            String values = CAssert.generateString(5) + "TEST";
            Request request = new Request(values, values, values, values, values);
            String requestmess = gson.toJson(request);
            Assert.assertNotNull(requestmess);
            String firstres = HttpRequest.postRequest(address, requestmess);
            JsonObject jobj = gson.fromJson(firstres, JsonObject.class);
            Assert.assertTrue(jobj.has("cookie"));
            return 0;
        });
    }
    @Test
    public void TestUserAlreadyExists() {
        CAssert.AssertDoesFail(() -> {
            Request request = new Request("test", "test", "test", "test", "test");
            String requestmess = gson.toJson(request);
            Assert.assertNotNull(requestmess);
            String firstres = HttpRequest.postRequest(address, requestmess);
            JsonObject jobj = gson.fromJson(firstres, JsonObject.class);
            Assert.assertTrue(jobj.has("cookie"));
            return 0;
        });
    }
    @Test
    public void TestInvalidPasswordCreate() {
        CAssert.AssertDoesFail(() -> {
            Request request = new Request("test", null, "test", "test", "test");
            String requestmess = gson.toJson(request);
            Assert.assertNotNull(requestmess);
            String firstres = HttpRequest.postRequest(address, requestmess);
            JsonObject jobj = gson.fromJson(firstres, JsonObject.class);
            Assert.assertTrue(jobj.has("cookie"));
            return 0;
        });
    }
    @Test
    public void TestInvalidUsernameCreate() {
        CAssert.AssertDoesFail(() -> {
            Request request = new Request("test", "test", "test", "test", null);
            String requestmess = gson.toJson(request);
            Assert.assertNotNull(requestmess);
            String firstres = HttpRequest.postRequest(address, requestmess);
            JsonObject jobj = gson.fromJson(firstres, JsonObject.class);
            Assert.assertTrue(jobj.has("cookie"));
            return 0;
        });
    }
    class Request {
        String name;
        String pass;
        String email;
        String phone;
        String username;
        public Request(String name, String pass, String email, String phone, String username) {
            this.name = name;
            this.pass = pass;
            this.email = email;
            this.phone = phone;
            this.username = username;
        }
    }
}
