import org.junit.*;
import com.google.gson.*;
public class TestLogin {
    static String address = "http://scheduleit.duckdns.org/api/user/login";
    static Gson gson = new Gson();
    @Test
    public void testSuccessLogin() {
        CAssert.AssertDoesNotFail(() -> {
            Request request = new Request("test", "test");
            String requestmess = gson.toJson(request);
            CAssert.AssertNotNull(requestmess);
            System.out.println("Request " + requestmess + "length: " + requestmess.length());
            String firstres = HttpRequest.postRequest(address, requestmess);
            JsonObject jobj = gson.fromJson(firstres, JsonObject.class);
            Assert.assertTrue(jobj.has("cookie"));
            return 0;
        });
    }
    @Test
    public void testAlreadyLoggedIn() {
        CAssert.AssertDoesNotFail(() -> {
            int cookie = -1;
            for (int i = 0; i < 1; i++) {
                Request request = new Request("test", "test");
                String requestmess = gson.toJson(request);
                Assert.assertNotNull(requestmess);
                String firstres = HttpRequest.postRequest(address, requestmess);
                JsonObject jobj = gson.fromJson(firstres, JsonObject.class);
                Assert.assertTrue(jobj.has("cookie"));
                if (cookie != -1) {
                    Assert.assertEquals(cookie, jobj.get("cookie").getAsInt());
                }
                cookie = jobj.get("cookie").getAsInt();
            }
            return 0;
        });
    }
    @Test
    public void testNotAUser() {
        CAssert.AssertDoesFail(() -> {
            Request request = new Request("hgghfgd", "test");
            String requestmess = gson.toJson(request);
            CAssert.AssertNotNull(requestmess);
            String firstres = HttpRequest.postRequest(address, requestmess);
            return 0;
        });
    }
    @Test
    public void TestInvalidPassword() {
        CAssert.AssertDoesFail(() -> {
            Request request = new Request("test", "65412");
            String requestmess = gson.toJson(request);
            CAssert.AssertNotNull(requestmess);
            String firstres = HttpRequest.postRequest(address, requestmess);
            return 0;
        });
    }
    @Test
    public void TestInvalidUsername() {
        CAssert.AssertDoesFail(() -> {
            Request request = new Request("qwqwee", "test");
            String requestmess = gson.toJson(request);
            CAssert.AssertNotNull(requestmess);
            String firstres = HttpRequest.postRequest(address, requestmess);
            return 0;
        });
    }
    class Request {
        String name;
        String pass;
        public Request(String name, String pass) {
            this.name = name;
            this.pass = pass;
        }
    }
}
