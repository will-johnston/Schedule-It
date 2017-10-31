package endpoints;

import server.*;

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
    public void execute(SSocket sock, HTTPMessage request) {
        if (request.getMethod().equals("/upload")) {
            //starting a new upload
            // {{"type": "image/jpg",
            //   "size": 65535,
            //   "length": 78,
            //   "cookie": 1297658432568
            //}
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
                    response = "\"success\":\"true\"\n";
                }
                else {
                    response = "\"success\":\"false\"\n";
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
        }
    }
    private Object[] parseNewUploadRequest(String body) {
        try {
            System.out.println("Trying to parse upload args");
            Gson gson = new Gson();
            JsonObject bodyObj = gson.fromJson(body, JsonObject.class);
            if (bodyObj == null) {
                System.out.println("Failed to parse JSON");
                return null;
            }
            if (!bodyObj.has("type")) {
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
            System.out.println("Upload Request has correct params");
            //ArrayList<Object> arr = new ArrayList<>(4);
            Object[] arr =  new Object[3];
            arr[0] = (HTTPMessage.getMimeTypeFromString(bodyObj.get("type").getAsString()));
            arr[1] = (bodyObj.get("length").getAsInt());
            arr[2] = (bodyObj.get("cookie").getAsInt());
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
            args[5] = body.substring(last + 3);
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
    class newupload {
        int chunkCount;         //length from client
        int received;
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
                }
            }
            else {
                if (chnk.succeeded) {
                    chunks.add(chnk);
                }
                else {
                    failed.add(chnk);
                }
                received = received + 1;
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
