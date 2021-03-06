package management;
import server.HTTPMessage;

import java.io.*;
import java.util.*;

public class Images {
    public static String imagesPath = "/home/scheduleit/app/images/";
    public static String webPath = "scheduleit.duckdns.org/images/";
    HashMap<String, Boolean> pathCache;
    public Images() {
        pathCache = new HashMap<>(10);
    }
    //returns true if the file already exists
    //False if the file is not there
    public synchronized boolean doesExist(String path) {
        if (pathCache.containsKey(path)) {
            return pathCache.get(path);
        }
        else {
            try {
                File tmpFile = new File(path);
                tmpFile.createNewFile();
                if (tmpFile.isDirectory()) {
                    return false;
                }
                if (tmpFile.exists()) {
                    pathCache.put(path, true);
                    return true;
                }
                else {
                    pathCache.put(path, false);
                    return false;
                }
            }
            catch (Exception e) {
                return false;
            }
        }
    }
    private HashMap<String, Boolean> getUUIDs() {
        if (pathCache.size() == 0 || pathCache.isEmpty()) {
            return new HashMap<>(1);
        }
        HashMap<String, Boolean> uuids = new HashMap<>(pathCache.size());
        String[] keys = (String[])pathCache.keySet().toArray();
        for (int i = 0; i < uuids.size(); i++) {
            String[] slashSplit = keys[i].split("/");
            uuids.put(slashSplit[slashSplit.length - 1].split(".")[0], false);
        }
        return uuids;
    }
    public String makeUUID(HTTPMessage.MimeType type) {
        HashMap<String, Boolean> uuids = getUUIDs();
        String uuid = "";
        while(true) {
            uuid = UUID.randomUUID().toString();
            if (uuids.containsKey(uuid)) {
                continue;
            }
            File f = new File(makePath(uuid,type));
            if (f.exists()) {
                uuids.put(uuid, true);
            }
            else {
                uuids.put(uuid, false);
                return uuid;
            }
        }
    }
    //should format as images/{UUID}.{type}
    public String makePath(String uuid, HTTPMessage.MimeType type) {
        if (type == HTTPMessage.MimeType.Unknown) {
            return null;
        }
        String extension = "";
        if (type == HTTPMessage.MimeType.imageJpeg) {
            extension = "jpg";
        }
        else if (type == HTTPMessage.MimeType.imagePng) {
            extension = "png";
        }
        else {
            System.out.println("Mime type " + type + " is not supported");
            return null;
        }
        return String.format("%s%s.%s", imagesPath, uuid, extension);
    }
    public synchronized boolean addPath(String path) {
        if (doesExist(path)) {
            return false;
        }
        else {
            pathCache.put(path, true);
            return true;
        }
    }
    public boolean writeOut(Newupload upload) {
        if (upload ==  null) {
            System.out.println("Tried to write out null");
            return false;
        }
        try {
            FileOutputStream stream = new FileOutputStream(upload.path);
            System.out.println("writing to " + upload.path);
            //byte[] data = upload.getBlob();
            try {
                //int remaining = data.length;
                int location = 0;
                /*while (remaining > 0) {
                    if (remaining < 1000)  {
                        stream.write(data, location, remaining);
                        location += remaining;
                        remaining = remaining - remaining;
                    }
                    else {
                        stream.write(data, location, 1000);
                        remaining = remaining - 1000;
                        location += 1000;
                    }
                    stream.flush();
                }*/
                //System.out.println("Data length: " + data.length);
                upload.sortChunks();
                for (Chunk blob  : upload.chunks) {
                    System.out.println("Blob size: " + blob.size + ", length: "  + blob.data.length + ", id: " + blob.id);
                    /*for (int i = 0; i < blob.data.length; i++) {
                        //System.out.println((int)blob.data[i]);
                        stream.write(blob.data[i]);
                    }*/
                    stream.write(blob.data,0,blob.data.length);
                }
                /*for (int i = 0; i < data.length; i++) {
                    System.out.println((int)data[i]);
                    stream.write(data[i]);
                }*/
                stream.flush();
                stream.close();
                return true;
            }
            catch (Exception next) {
                next.printStackTrace();
                stream.close();
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to writeout");
            return false;
        }
    }
}
