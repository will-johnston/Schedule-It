import java.net.Socket;
import java.nio.charset.Charset;
import java.time.*;
import java.util.concurrent.*;
import java.io.OutputStream;
import server.*;
import endpoints.*;
public class RedirectTest {

    public static void main(String[] args) throws Exception {
        // write your code here
        Server server = new Server(8181);
        Router router = new Router();
        router.add("/api/test/redirect", new IAPIRoute() {

            @Override
            public void execute(Socket sock, HTTPMessage request) {
                try {
                    OutputStream out = sock.getOutputStream();
                    String response = HTTPMessage.makeRedirect("http://www.google.com");
                    System.out.println("User Agent: " + request.getHeader("User-Agent").getValue());
                    System.out.println("Response: " + response);
                    out.write(response.getBytes(Charset.forName("UTF-8")));
                    out.flush();
                    out.close();
                }
                catch (Exception e) {
                    System.out.println("Test crashed");
                }
            }
        });
        server.startListening(router);
        while (server.getListening()) {
            Thread.currentThread().sleep(1000);
        }
    }
}
