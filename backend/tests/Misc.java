import com.google.gson.*;
public class Misc {
    public static int login() {
        String url = "http://scheduleit.duckdns.org/api/user/login";
        String username = "testingSprint2";
        String pass = "test";
        try {
            String message = "{\"name\":\"testingSprint2\",\"pass\":\"test\"}";
            String response = HttpRequest.postRequest(url,message);
            return new Gson().fromJson(response, JsonObject.class).get("cookie").getAsInt();
        }
        catch (Exception e) {
            return -1;
        }
    }
    public static String sendCookie(int cookie, String url) throws Exception {
        JsonObject obj = new JsonObject();
        obj.addProperty("cookie", cookie);
        return HttpRequest.postRequest(url, obj.toString());
    }
    public static String getUrl() {
        return "http://scheduleit.duckdns.org/api/";
    }
}
