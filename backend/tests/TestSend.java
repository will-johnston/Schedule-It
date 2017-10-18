import org.junit.*;
import server.HTTPMessage;

import java.util.concurrent.Callable;

public class TestSend {
    @Test
    public void testGetIndex() {
        CAssert.AssertDoesNotFail(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                String message = HttpRequest.sendRequest("http://scheduleit.duckdns.org/", HTTPMessage.HTTPMethod.GET, "");
                Assert.assertNotNull(message);
                return 1;
            }
        });
    }
    @Test
    //Expect to fail
    public void testGetAPIRoute() {
        CAssert.AssertDoesFail(() -> {
            HttpRequest.getRequest("http://scheduleit.duckdns.org/api/");
            return 1;
        });
    }

}
