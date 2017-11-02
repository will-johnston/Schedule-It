package endpoints;

import server.HTTPMessage;

import java.math.BigInteger;
import java.util.*;
import management.*;
import com.google.gson.*;
import server.*;
import server.Socketeer;

//A special type of endpoint where multiple uploads can happen at the same time
//
public class upload implements IAPIRoute {
    Images images;
    Tracker tracker;
    HashMap<Integer, Newupload> uploads;        //Uploadid -> Newupload
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
                Newupload up = new Newupload((int)args[1], (HTTPMessage.MimeType) args[0] , uuid, images);
                uploads.put(uploadid, up);
                String response = String.format("{\"uploadid\":\"%d\"}\n", uploadid);
                Socketeer.send(HTTPMessage.makeResponse(response, HTTPMessage.HTTPStatus.OK,
                        HTTPMessage.MimeType.appJson, false), sock);
                return;
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not add Newupload");
                Socketeer.send(HTTPMessage.makeResponse("{ \"error\" : \"Could not handle request\" }",
                        HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }

        }
        else if (request.getMethod().equals("/upload/chunk")) {
            //adding a Chunk
            //{"checksum" : 69548,
            // "cookie" : 794681312,
            // "length" : 8080,
            // "uploadid" : 684216,
            // "chunkid" : 25           //25th (starting from 0) Chunk
            //}
            //Upload data
            System.out.println("Recieved new Chunk request");
            Object[] args = parseChunkUploadRequest(request.getBody());
            System.out.println("Checking for null");
            if (args == null) {
                Socketeer.send(HTTPMessage.makeResponse("{ \"error\" : \"Failed to parse arguments\" }",
                        HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            System.out.println("Checking if logged in");
            if (!tracker.isLoggedIn((int)args[1])) {
                Socketeer.send(HTTPMessage.makeResponse("{ \"error\" : \"User not logged in\" }",
                        HTTPMessage.HTTPStatus.MethodNotAllowed), sock);
                return;
            }
            System.out.println("Checking if contains key");
            if (!uploads.containsKey((int)args[2])) {
                Socketeer.send(HTTPMessage.makeResponse("{ \"error\" : \"Uploadid is incorrect\" }",
                        HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
            try {
                int uploadid = (int)args[2];
                System.out.println("Getting upload");
                Newupload up = uploads.get(uploadid);
                System.out.println("Adding chunk");
                boolean success = up.addNewChunk(getChunkData((String)args[5]), (int)args[0], (int)args[3]);
                String response;

                if (success) {
                    response = "{\"success\":\"true\"}";
                    if (up.isFinished()) {
                        //send path
                        System.out.println("Finished upload!");
                        if (images.writeOut(up)) {
                            System.out.println("Accessable at " + up.getWebPath());
                        }
                        else {
                            System.out.println("Failed to write out data");
                        }
                    }
                    else {
                        System.out.println("Isn't finished");
                    }
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
                System.out.println("Failed to handle new Chunk request");
                Socketeer.send(HTTPMessage.makeResponse("{ \"error\" : \"Could not handle Chunk request\" }",
                        HTTPMessage.HTTPStatus.BadRequest), sock);
                return;
            }
        }
        else {
            // /upload/info
            // {"cookie" : 736548,
            // "uploadid" : 67984 }
            //Returns info about the upload
            System.out.println("Recieved Chunk info request");
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
            Newupload up = uploads.get(args[1]);

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
            arr[4] = bodyObj.get("uploadType").getAsString();
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
        try {
            Gson gson = new Gson();
            JsonObject bodyObj = gson.fromJson(body, JsonObject.class);
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
            if (!bodyObj.has("data")) {
                return null;
            }
            Object[] args = new Object[6];
            args[0] = bodyObj.get("checksum").getAsInt();
            args[1] = bodyObj.get("cookie").getAsInt();
            args[2] = bodyObj.get("uploadid").getAsInt();
            args[3] = bodyObj.get("chunkid").getAsInt();
            args[4] = bodyObj.get("length").getAsInt();
            args[5] = bodyObj.get("data").getAsString();
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
    //Data is base64 encoded
    private byte[] getChunkData(String data) {
        //char[] chars = data.toCharArray();
        byte[] blob = Base64.getDecoder().decode(data);
        System.out.println("" + blob[0] + " " + blob[1] + " " + blob[2] + " " + blob[3]);
        return blob;
    }
    private byte[] resolveUint(byte[] blob) {
        for (int i = 0; i < blob.length; i++) {
            blob[i] = makeUint(blob[i]);
        }
        return blob;
    }
    private byte makeUint(byte num) {
        if (num < 0) {
            return (byte)(256 + (int)num);
        }
        else {
            return num;
        }
    }
    private String infoToJson(Newupload up) {
        try {
            JsonObject jobj = new JsonObject();
            //chunksRemaining, uuid, failed
            jobj.addProperty("chunksRemaining", up.chunkCount - up.received);
            jobj.addProperty("uuid", up.uuid);
            if (up.failed == null || up.failed.size() == 0) {
                jobj.addProperty("failed", "");
            }
            else {
                Gson gson = new Gson();
                JsonElement element = gson.toJsonTree(up.failed);
                jobj.add("failed", element);
            }
            return jobj.toString();
        }
        catch (Exception e) {
            System.out.println("Couldn't convert info to json");
            return null;
        }
    }
}
