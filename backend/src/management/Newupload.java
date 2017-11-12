package management;

import server.HTTPMessage;
import management.Chunk;
import java.util.ArrayList;
import java.util.Comparator;

public class Newupload {
    public int chunkCount;         //length from client
    public int received;           //successful blocks recieved
    public int size;               //current size
    public HTTPMessage.MimeType type;
    public String path;
    public String uuid;
    public ArrayList<Chunk> chunks;
    public ArrayList<Chunk> failed;
    private Images images;
    public Newupload(int chunkCount, HTTPMessage.MimeType type, String uuid, Images images) throws Exception {
        if (chunkCount <= 0) {
            throw new Exception("Incorrect Chunk count");
        }
        if (uuid == null) {
            throw new Exception("Incorrect uuid");
        }
        if (type == HTTPMessage.MimeType.Unknown) {
            throw new Exception("Incorrect Mime Type");
        }
        received = 0;
        size = 0;
        this.images = images;
        this.chunkCount = chunkCount;
        this.type = type;
        this.uuid = uuid;
        this.path = images.makePath(uuid, type);
        chunks = new ArrayList<>(chunkCount);
        failed = new ArrayList<>();
    }
    //returns if the Chunk succeeded
    public boolean addNewChunk(byte[] data, int checksum, int chunkid) {
        //check if Chunk is already in either list
        Chunk chnk = new Chunk(data, checksum, chunkid);
        if (failedContains(chunkid)) {
            if (chnk.succeeded) {
                //add to succeeded list
                System.out.println("Added to succeeded");
                chunks.add(chnk);
                received = received + 1;
            }
        }
        else {
            if (chnk.succeeded) {
                System.out.println("added to succeeded");
                chunks.add(chnk);
                received++;
            }
            else {
                System.out.println("Added to failed");
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
            Chunk src = chunks.get(i);
            System.out.println("Copying from " + i + " to " + (src.size - 1));
            System.arraycopy(src.data, i, blob, src.size - 1, src.size);
            i = i + src.size;
            index = index + 1;
        }
        return blob;
    }
    public void sortChunks() {
        chunks.sort(new Comparator<Chunk>() {
                        @Override
                        public int compare(Chunk o1, Chunk o2) {
                            //return 0 if equal
                            //return -1 if o1 is < o2
                            //return 1 if 02 is > o1
                            return Integer.compare(o1.id, o2.id);
                        }
                    }
        );
    }
    public boolean isFinished() {
        if (received == chunkCount && failed.isEmpty()) {
            return true;
        }
        if (received == chunkCount) {
            System.out.println("Recieved equals chunkCount but failed isn't empty");
            return true;
        }
        System.out.println("ChunkCount: " + chunkCount + ", recieved: " + received);
        return false;
    }
    public String getWebPath() {
        try {
            return String.format("https://%s%s.%s", Images.webPath, uuid, HTTPMessage.getMimeExtension(type));
        }
        catch (Exception e) {
            return null;
        }
    }
}
