public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Started Proxy Server");
        Server server = new Server(8181, 8282);
        server.startListening();
        while (server.getListening()) {
            //Todo listen for kill
            Thread.currentThread().sleep(1000);
        }
    }
}
