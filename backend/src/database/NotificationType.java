package database;

//uses an int as a backend (not uint because that's also not a thing in java)
//Stores Invites for now
public class NotificationType {
    //invite = 0
    private int type;
    public NotificationType(String type) throws Exception {
		System.out.println("Requested convert of " + type);
        //resolve type
        String[] split = type.split("[.]");
		System.out.println("Split.length=" + split.length);
        if (split.length != 2) {
            throw new Exception("Invalid format");
        }
        if (split[0].toLowerCase().equals("invite")) {
            //invite method
            int method =  InviteFromString(split[1]);
            if (method == -1) {
                throw new Exception("Invalid method");
            }
            this.type = method;
        }
        else {
            throw new Exception("Unknown class");
        }
    }
    public static String InviteFriend() {
        return "invite.friend";
    }
    public static String InviteGroup() {
        return "invite.group";
    }
    public static String InviteEvent() {
        return "invite.event";
    }
    public String toString() {
        //returns invite.friends for the given type
        if (type >= 0 && type < 100) {
            //invite methods
            String method = InviteToString(type);
            if (method == null) return null;
            return String.format("invite.%s", method);
        }
        else {
            return null;
        }
    }
    public String InviteToString(int value) {
        if (value == 0) {
            return "friend";
        } else if (value == 1) {
            return "group";
        } else if (value == 2) {
            return "event";
        }
        else {
            return null;
        }
    }
    public int InviteFromString(String method) {
        if (method.toLowerCase().equals("friend")) {
            return 0;
        }
        else if (method.toLowerCase().equals("group")) {
            return 1;
        }
        else if (method.toLowerCase().equals("event")) {
            return 2;
        }
        else {
            return -1;
        }
    }
    public boolean isInvite() {
        if (type >= 0 && type < 100) {
            return true;
        }
        return false;
    }
}
