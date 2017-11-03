import org.junit.*;
import com.google.gson.*;
public class SeeGroupCalendar {
    @Test
    public void SeeCalendarTest() {
        CAssert.AssertDoesNotFail(() -> {
            String url = Misc.getUrl() + "user/groups/create";
            String calendarUrl = Misc.getUrl() + "user/groups/calendar/get";
            System.out.println("logging in");
            int cookie = Misc.login();
            if (cookie == -1) {
                Assert.fail();
            }
            JsonObject obj = new JsonObject();
            obj.addProperty("cookie", cookie);
            obj.addProperty("groupname", CAssert.generateString(4));
            System.out.println("Sending Group Create");
            String makeGroup = HttpRequest.postRequest(url, obj.toString());

            JsonObject seeObj = new JsonObject();
            seeObj.addProperty("cookie", cookie);
            seeObj.addProperty("month", 1);
            seeObj.addProperty("year", 2000);
            seeObj.addProperty("groupid", new Gson().fromJson(makeGroup, JsonObject.class).get("groupid").getAsInt());
            System.out.println("Sending get Calendar");
            HttpRequest.postRequest(calendarUrl, seeObj.toString());
            return 0;
        });
    }
}
