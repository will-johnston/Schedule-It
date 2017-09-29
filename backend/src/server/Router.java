package server;

import endpoints.IAPIRoute;

import java.util.*;

/**
 * Created by Ryan on 9/20/2017.
 */
//The idea is like a tri system where upper level keys ('user' of /api/user/login) are placed at the head
//and lower and lower keys or methods are the children of those nodes
//Keys are defined as a value with brackets on both sides. Eg: /user/
//Methods are defined as a value with brackets only on the left side. Eg: /login
//Didn't actually implement this, would be a chill perfomance gain though
public class Router {
    private HashMap<String, IAPIRoute> methods;
    public Router(int capacity) throws Exception {
        if (capacity < 0) {
            throw new Exception("Capacity can't be negative");
        }
        methods = new HashMap<String, IAPIRoute>(capacity);
    }
    public Router() throws Exception {
        this(20);
    }
    /*public void add(String method, endpoints.IAPIRoute obj) throws Exception {
        if (!method.contains("/")) {
            throw new Exception("Invalid method!");
        }
        String[] keys = getKeys(method);
        if (keys.length < 3) {
            throw new Exception("Invalid method, too few keys")
        }
        //Skip the first key because it should be empty
        for (int i = 2; i < keys.length - 1; i++) {

        }
    }*/
    public void add(String method, IAPIRoute obj) throws Exception {
        if (methods.containsKey(method)) {
            throw new Exception("Method already in the Table!");
        }
        methods.put(method, obj);
    }
    //Returns null if method isn't in table
    public IAPIRoute get(String method) {
        if (!methods.containsKey(method)) {
		System.out.println("Method doesn't exist");
            return null;
        }
        return methods.get(method);
    }
    public Boolean containsMethod(String method) {
        return methods.containsKey(method);
    }
    /*private String[] getKeys(String method) {
        return method.split("/");
    }*/
    //When the Node is a Key, the method field will be node but the next field will be filled
    //When the Node is a Method, the next field will be empty but the key field will not
    //The key field is used for an identifier (Eg: 'login' in /login) when the Node is a method
    /*class Node {
        String key;
        Node next;
        endpoints.IAPIRoute method;
        //Creating a Key
        public Node(String key, Node next) {

        }
        //Creating a Method
        public Node(String key, endpoints.IAPIRoute method) {

        }
    }*/
}
