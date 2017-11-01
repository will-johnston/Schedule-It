package management;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
        HashMap<String, Boolean> uuids = new HashMap<>(pathCache.size());
        String[] keys = (String[])pathCache.keySet().toArray();
        for (int i = 0; i < uuids.size(); i++) {
            String[] slashSplit = keys[i].split("/");
            uuids.put(slashSplit[slashSplit.length - 1].split(".")[0], false);
        }
        return uuids;
    }
    public String makeUUID() {
        HashMap<String, Boolean> uuids = getUUIDs();
        String uuid = "";
        do {
            uuid = UUID.randomUUID().toString();
        }
        while (uuids.containsKey(uuid));
        uuids.put(uuid, false);
        return uuid;
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
            byte[] data = upload.getBlob();
            try {
                stream.write(data);
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
