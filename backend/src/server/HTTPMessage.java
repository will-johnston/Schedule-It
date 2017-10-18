package server;

import java.util.*;
import java.text.*;

/**
 * Created by Ryan on 9/20/2017.
 */
public class HTTPMessage {
    HashMap<String, Header> headers = new HashMap<>();
    String body;
    HTTPMethod methodType;
    String method;
    float HTTPversion;
    public HTTPMessage(String rawHTTP) throws Exception {
	//System.out.println(rawHTTP);
        String[] lines = rawHTTP.split("[\n]");
        Boolean headerBoundary = false;
        StringBuilder bodyBuilder = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.length() <= 1 && !headerBoundary) {
                //We've encountered the end of the header, it's all body from here
                headerBoundary = true;
				System.out.println("Encountered boundary: " + line);
                continue;
            }
            if (i == 0) {
                //Should be GET or POST or other HTTP methods
                String[] topSplit = line.split(" ");
                if (topSplit.length < 3) {
                    throw new Exception("Invalid Request");
                }
                switch (topSplit[0].toUpperCase()) {
                    case "POST":
                        methodType = HTTPMethod.POST;
                        break;
                    case "GET":
                        methodType = HTTPMethod.GET;
                        break;
                    case "DELETE":
                        methodType = HTTPMethod.DELETE;
                        break;
                    default:
                        methodType = HTTPMethod.UNKNOWN;
                        break;
                }
                method = topSplit[1];
                String[] versionSplit = topSplit[2].split("/");
                try {
                    HTTPversion = Float.parseFloat(versionSplit[1]);
                }
                catch (Exception e) {
                    //I don't care about resolving this Exception
                    HTTPversion = -1f;
                }
                continue;
            }
            if (headerBoundary) {
				System.out.println("Adding" + line);
                bodyBuilder.append(line);
                bodyBuilder.append('\n');
            }
            else {
                Header header = new Header(line);
                if (header.Key == null || header.Value == null) {
                    System.out.println(line + " did not parse as a header");
                }
                else {
                    headers.put(header.Key, header);
                }
            }
        }
        body = bodyBuilder.toString();
    }
    // This method creates a generic response
    public static String makeBasicResponse(String message) {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 200 OK\n");
        response.append("Date: ");
        // Date format: Thu, 21 Sep 2017 00:16:05 GMT
        response.append(new SimpleDateFormat("EEE, d MMM YYYY 00:kk:ss zzz").format(new Date()));
        response.append('\n');
        response.append("Connection: close\n");
        response.append("server.Server: ScheduleIt API server.Server\n");
        response.append("Accept-Ranges: bytes\n");
        response.append("Content-Type: application/json\n");
        response.append(String.format("Content-Length: %d\n", message.length()));
        //response.append("Access-Control-Allow-Origin: https://scheduleit.duckdns.org, http://scheduleit.duckdns.org\n\n");
		response.append("Access-Control-Allow-Origin: *\n\n");
        response.append(message);
        response.append('\n');
        return response.toString();
    }
    public static String makeResponse(String message, HTTPStatus status, MimeType type, Boolean closeConnection) {
        StringBuilder response = new StringBuilder();
        response.append(String.format("HTTP/1.1 %d %s\n", getHTTPStatusCode(status), getHTTPStatusName(status)));
		if (closeConnection) {
			response.append("Connection: close\n");
		}
        response.append("Accept-Ranges: bytes\n");
        response.append(String.format("Content-Type: %s\n", getMimeTypeName(type)));
        response.append(String.format("Content-Length: %d\n", message.length()));
        //response.append("Access-Control-Allow-Origin: https://scheduleit.duckdns.org, http://scheduleit.duckdns.org\n\n");
        response.append("Access-Control-Allow-Origin: *\n\n");
        response.append(message);
        response.append('\n');
        return response.toString();
    }
	public static String makeResponse(String message, HTTPStatus status) {
		return makeResponse(message, status, MimeType.appJson, true);
	}
	public static String makeRedirect(String location) {
		StringBuilder response = new StringBuilder();
		response.append(String.format("HTTP/1.1 %d %s\n", getHTTPStatusCode(HTTPStatus.RedirectFound), getHTTPStatusName(HTTPStatus.RedirectFound)));
		response.append("Connection: close\n");
		response.append(String.format("Location: %s\n", location));
		response.append("Accept-Ranges: bytes\n");
		response.append("Access-Control-Allow-Origin: *\n\n");
		//response.append("Access-Control-Allow-Origin: https://scheduleit.duckdns.org, http://scheduleit.duckdns.org\n\n");
		return response.toString();
	}
	public static String makeNotImplemented(){
        return makeResponse("NotImplemented", HTTPStatus.MethodNotAllowed, MimeType.textPlain, true);
    }
    public void printDebugString() {
        // Method, MethodType, HTTP Version
        System.out.println(String.format("Method: %s, Type: %s, HTTP Version: %f", method, methodType, HTTPversion));
        // Print Headers
        System.out.println(String.format("server.Header Count: %d", headers.size()));
        for (Header header : headers.values()) {
            System.out.println(String.format("Key: %s, Value: %s", header.Key, header.Value));
        }
        System.out.println(String.format("Body Length: %d", body.length()));
        System.out.println(body);
    }
    public enum HTTPMethod {
        GET,
        POST,
        DELETE,
        UNKNOWN
    }
    public enum HTTPStatus {
        OK,
        BadRequest,
        Unauthorized,
        MethodNotAllowed,
        RedirectFound,
        NotFound,
        Unknown
    }
	public enum MimeType {
		appJson,
		textPlain,
		imageJpeg,
		imagePng,
		Unknown
	}
    public static int getHTTPStatusCode(HTTPStatus status) {
        switch (status) {
            case OK:
                return 200;
            case BadRequest:
                return 400;
            case NotFound:
                return 404;
            case Unauthorized:
                return 401;
            case MethodNotAllowed:
                return 405;
            case RedirectFound:
                return 302;
            case Unknown:
            default:
                //Return Internal server.Server Error
                return 500;
        }
    }
    public static String getHTTPStatusName(HTTPStatus status) {
        switch (status) {
            case OK:
                return "OK";
            case BadRequest:
                return "Bad Request";
            case NotFound:
                return "Not Found";
            case Unauthorized:
                return "Unauthorized";
            case MethodNotAllowed:
                return "Method Not Allowed";
            case RedirectFound:
                return "Redirect Found";
            case Unknown:
            default:
                //Return Internal server.Server Error
                return "Internal server.Server Error";
        }
    }
    public static String getHTTPMethodName(HTTPMethod method) {
	    switch (method){
            case GET:
                return "GET";
            case POST:
                return "POST";
            case DELETE:
                return "DELETE";
            case UNKNOWN:
            default:
                return "UNKNOWN";
        }
    }
	public static String getMimeTypeName(MimeType type) {
		switch (type) {
			case appJson:
				return "application/json";
			case imageJpeg:
				return "image/jpeg";
            case textPlain:
                return "text/plain";
            case imagePng:
				return "image/png";
			case Unknown:
			default:
				//Return 
				return "application/unknown";
		}
	}
    public Header getHeader(String key) {
        if (headers.containsKey(key)) {
            return headers.get(key);
        }
        else {
            return null;
        }
    }
    public static MimeType getMimeTypeFromString(String mime) {
	    if (mime.equals("application/json")) {
	        return MimeType.appJson;
        }
        if (mime.equals("image/jped")) {
	        return MimeType.imageJpeg;
        }
        if (mime.equals("text/plain")) {
	        return MimeType.textPlain;
        }
        if (mime.equals("image/png")) {
	        return MimeType.imagePng;
        }
        return MimeType.Unknown;
    }

    //Getters and Setters because Java is dumb

    public HashMap<String, Header> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public HTTPMethod getMethodType() {
        return methodType;
    }
    public float getHTTPversion() {
        return HTTPversion;
    }

    public String getMethod() {
        return method;
    }
}
