package endpoints;

import server.HTTPMessage;

import java.math.BigInteger;
import java.net.Socket;
import java.util.*;
import management.*;
import com.google.gson.*;
import server.Socketeer;

//A special type of endpoint where multiple uploads can happen at the same time
//
public class upload implements IAPIRoute {
    Images images;
    Tracker tracker;
    HashMap<Integer, newupload> uploads;        //Uploadid -> newupload
    public upload(Images images, Tracker tracker) {
        this.images = images;
        this.tracker = tracker;
        uploads = new HashMap<>(10);
    }
    private int getNewUploadId() {
        int result = 0;
        do {
            result = new BigInteger(128, new Random()).intValue();
        }
        while (uploads.containsKey(result));
        return result;
    }

    @Override
    public void execute(Socket sock, HTTPMessage request) {
        if (request.getMethod().equals("/upload")) {
            //starting a new upload
            //returns mimeType, length, cookie, size, uploadType
            System.out.println("Recieved new upload request");
            Object[] args = parseNewUploadRequest(request.getBody());
            if (args == null) {
                Socketeer.send(HTTPMessage.makeResponse("{ \"error\" : \"Failed to parse arguments\" }",
                        HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            if (!tracker.isLoggedIn((int)args[2])) {
                Socketeer.send(HTTPMessage.makeResponse("{ \"error\" : \"User not logged in\" }",
                        HTTPMessage.HTTPStatus.MethodNotAllowed), sock);
                return;
            }
            System.out.println("Handling request");
            //create new uuid and uploadid
            String uuid = images.makeUUID();
            int uploadid = getNewUploadId();
            try {
                newupload up = new newupload((int)args[1], (HTTPMessage.MimeType) args[0] , uuid);
                uploads.put(uploadid, up);
                String response = String.format("{\"uploadid\":\"%d\"}\n", uploadid);
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.OK,
                        HTTPMessage.MimeType.appJson, false), sock);
                return;
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not add newupload");
                Socketeer.send(HTTPMessage.makeResponse("{ \"error\" : \"Could not handle request\" }",
                        HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }

        }
        else if (request.getMethod().equals("/upload/chunk")) {
            //adding a chunk
            //{"checksum" : 69548,
            // "cookie" : 794681312,
            // "length" : 8080,
            // "uploadid" : 684216,
            // "chunkid" : 25           //25th (starting from 0) chunk
            //}
            //Upload data
            System.out.println("Recieved new chunk request");
            Object[] args = parseChunkUploadRequest(request.getBody());
            if (args == null) {
                Socketeer.send(HTTPMessage.makeResponse("{ \"error\" : \"Failed to parse arguments\" }",
                        HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            if (!tracker.isLoggedIn((int)args[1])) {
                Socketeer.send(HTTPMessage.makeResponse("{ \"error\" : \"User not logged in\" }",
                        HTTPMessage.HTTPStatus.MethodNotAllowed), sock);
                return;
            }
            if (!uploads.containsKey((int)args[2])) {
                Socketeer.send(HTTPMessage.makeResponse("{ \"error\" : \"Uploadid is incorrect\" }",
                        HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            try {
                int uploadid = (int)args[2];
                newupload up = uploads.get(uploadid);
                boolean success = up.addNewChunk(getChunkData((String)args[5]), (int)args[0], (int)args[3]);
                String response;
                if (success) {
                    response = "{\"success\":\"true\"}";
                }
                else {
                    response = "{\"success\":\"false\"}";
                }
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.OK,
                        HTTPMessage.MimeType.appJson, false), sock);
                return;
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to handle new chunk request");
                Socketeer.send(HTTPMessage.makeResponse("{ \"error\" : \"Could not handle chunk request\" }",
                        HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
        }
        else {
            // /upload/info
            // {"cookie" : 736548,
            // "uploadid" : 67984 }
            //Returns info about the upload
            System.out.println("Recieved chunk info request");
            int[] args = parseUploadInfoRequest(request.getBody());
            if (args == null) {
                Socketeer.send(HTTPMessage.makeResponse("{ \"error\" : \"Failed to parse arguments\" }",
                        HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            if (!tracker.isLoggedIn(args[0])) {
                Socketeer.send(HTTPMessage.makeResponse("{ \"error\" : \"User not logged in\" }",
                        HTTPMessage.HTTPStatus.MethodNotAllowed), sock);
                return;
            }
            if (!uploads.containsKey(args[1])) {
                Socketeer.send(HTTPMessage.makeResponse("{ \"error\" : \"Uploadid is incorrect\" }",
                        HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            newupload up = uploads.get(args[1]);

        }
    }
    //returns mimeType, length, cookie, size, uploadType
    private Object[] parseNewUploadRequest(String body) {
        try {
            System.out.println("Trying to parse upload args");
            Gson gson = new Gson();
            JsonObject bodyObj = gson.fromJson(body, JsonObject.class);
            if (bodyObj == null) {
                System.out.println("Failed to parse JSON");
                return null;
            }
            if (!bodyObj.has("mimeType")) {
                return null;
            }
            if (!bodyObj.has("size")) {
                return null;
            }
            if (!bodyObj.has("length")) {
                return null;
            }
            if (!bodyObj.has("cookie")) {
                return null;
            }
            if (!bodyObj.has("uploadType")) {
                return null;
            }
            System.out.println("Upload Request has correct params");
            //ArrayList<Object> arr = new ArrayList<>(4);
            Object[] arr =  new Object[5];
            arr[0] = HTTPMessage.getMimeTypeFromString(bodyObj.get("mimeType").getAsString());
            arr[1] = bodyObj.get("length").getAsInt();
            arr[2] = bodyObj.get("cookie").getAsInt();
            arr[3] = bodyObj.get("size").getAsInt();
            arr[4] = bodyObj.get("uploadType").getAsInt();
            return arr;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to parse Args");
            return null;
        }
    }
    //return checksum, cookie, uploadid, chunkid, and data (String)
    private Object[] parseChunkUploadRequest(String body) {
        //get json from body
        int last = body.indexOf('}');
        String json = body.substring(0, last + 1);
        System.out.println(json);
        try {
            Gson gson = new Gson();
            JsonObject bodyObj = gson.fromJson(json, JsonObject.class);
            if (!bodyObj.has("checksum")) {
                return null;
            }
            if (!bodyObj.has("cookie")) {
                return null;
            }
            if (!bodyObj.has("uploadid")) {
                return null;
            }
            if (!bodyObj.has("length")) {
                return null;
            }
            if (!bodyObj.has("chunkid")) {
                return null;
            }
            Object[] args = new Object[6];
            args[0] = bodyObj.get("checksum").getAsInt();
            args[1] = bodyObj.get("cookie").getAsInt();
            args[2] = bodyObj.get("uploadid").getAsInt();
            args[3] = bodyObj.get("chunkid").getAsInt();
            args[4] = bodyObj.get("length").getAsInt();
            args[5] = body.substring(last + 3).trim();
            return args;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to parse args");
            return null;
        }
    }
    private int[] parseUploadInfoRequest(String body) {
        try {
            Gson gson = new Gson();
            JsonObject bodyObj = gson.fromJson(body, JsonObject.class);
            if (!bodyObj.has("cookie")) {
                return null;
            }
            if (!bodyObj.has("uploadid")) {
                return null;
            }
            int[] arr = new int[2];
            arr[0] = bodyObj.get("cookie").getAsInt();
            arr[1] = bodyObj.get("uploadid").getAsInt();
            return arr;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to parse args");
            return null;
        }
    }
    private byte[] getChunkData(String data) {
        char[] chars = data.toCharArray();
        byte[] blob = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            blob[i] = (byte)chars[i];
        }
        return blob;
    }
    private String infoToJson(newupload up) {
        try {
            JsonObject jobj = new JsonObject();
            //chunksRemaining, uuid, failed
            jobj.addProperty("chunksRemaining", up.chunkCount - up.received);
            jobj.addProperty("uuid", up.uuid);
            if (up.failed == null || up.failed.size() == 0) {
                jobj.addProperty("failed", "");
            }
            else {
                int[] arr = new int[];
                for (chunk chnk : up.failed) {

                }
            }
        }
        catch (Exception e) {
            System.out.println("Couldn't convert info to json");
            return null;
        }
    }
    class newupload {
        int chunkCount;         //length from client
        int received;           //successful blocks recieved
        int size;               //current size
        HTTPMessage.MimeType type;
        String path;
        String uuid;
        ArrayList<chunk> chunks;
        ArrayList<chunk> failed;
        public newupload(int chunkCount, HTTPMessage.MimeType type, String uuid) throws Exception {
            if (chunkCount <= 0) {
                throw new Exception("Incorrect chunk count");
            }
            if (uuid == null) {
                throw new Exception("Incorrect uuid");
            }
            if (type == HTTPMessage.MimeType.Unknown) {
                throw new Exception("Incorrect Mime Type");
            }
            received = 0;
            size = 0;
            this.chunkCount = chunkCount;
            this.type = type;
            this.uuid = uuid;
            this.path = images.makePath(uuid, type);
            chunks = new ArrayList<>(chunkCount);
            failed = new ArrayList<>();
        }
        //returns if the chunk succeeded
        public boolean addNewChunk(byte[] data, int checksum, int chunkid) {
            //check if chunk is already in either list
            chunk chnk = new chunk(data, chunkid, checksum);
            if (failedContains(chunkid)) {
                if (chnk.succeeded) {
                    //add to succeeded list
                    chunks.add(chnk);
                }
                received = received + 1;
            }
            else {
                if (chnk.succeeded) {
                    chunks.add(chnk);
                }
                else {
                    failed.add(chnk);
                }
            }
            return chnk.succeeded;
        }
        private boolean failedContains(int chunkid) {
            for (int i = 0; i < failed.size(); i++) {
                if (failed.get(i).getId() == chunkid) {
                    return true;
                }
            }
            return false;
        }
        //returns all the chunks in one array
        public byte[] getBlob() {
            byte[] blob = new byte[size];
            int i = 0, index = 0;
            while (i < size) {
                chunk src = chunks.get(i);
                System.out.println("Copying from " + i + " to " + (src.size - 1));
                System.arraycopy(src.data, i, blob, src.size - 1, src.size);
                i = i + src.size;
                index = index + 1;
            }
            return blob;
        }
    }
    class chunk {
        int size;
        byte[] data;
        boolean succeeded;
        int recievedChecksum;
        int actualChecksum;
        int id;
        public chunk(byte[] data, int checksum, int chunkid) {
            this.data = data;
            this.size = data.length;
            this.recievedChecksum = checksum;
            actualChecksum = checksum();
            id = chunkid;
            if (actualChecksum == recievedChecksum) {
                succeeded = true;
            }
            else {
                succeeded = false;
            }
        }
        private int checksum() {
            int MODULUS = 65535;
            int sum = 0;
            for (int i = 0; i < size; i++) {
                sum = data[i] % MODULUS;
            }
            return sum;
        }
        public int getId() {
            return id;
        }
        public int getSize() {
            return size;
        }
    }
}
