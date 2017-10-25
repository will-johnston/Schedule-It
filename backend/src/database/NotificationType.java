package database;

//uses an int as a backend
public class NotificationType {
    //invite = 0
    private int type;
    public NotificationType(String type) throws Exception {
        //resolve type
        String[] split = type.split(".");
        if (split.length != 2) {
            throw new Exception("Invalid format");
        }
        if (split[0].toLowerCase().equals("invite")) {
            //invite method
            InviteMethod invite = InviteMethod(split[1]);
            type = invite.getLevelCode();
        }
        else {
            throw new Exception("Unknown class");
        }
    }
    public static String getType(String Class, String Method) {
        //not sure if this is necessary for now
        return null;
    }
    public String ToString() {
        //returns invite.friends for the given type
        if (type >= 0 && type < 100) {
            //invite methods
            String method = inviteMethods(type).ToString();
            if (method == null) return null;
            return String.format("invite.%s", method);
        }
        else {
            return null;
        }
    }
    private enum inviteMethods {
        friend(0),
        group(1),
        event(2)
        ;
        private final int levelCode;
        inviteMethods(int levelCode) {
            this.levelCode = levelCode;
        }
        inviteMethods(String method) {
            if (method.toLowerCase().equals("friend")) {
                this.levelCode = 0;
            }
            else if (method.toLowerCase().equals("group")) {
                this.levelCode = 1;
            }
            else if (method.toLowerCase().equals("event")) {
                this.levelCode = 2;
            }
            else {
                throw new Exception("invalid method");
            }
        }
        public int getLevelCode() {
            return this.levelCode;
        }
        public String ToString() {
            switch (levelCode) {
                case 0:
                    return "friend";
                case 1:
                    return "group";
                case 2:
                    return "event";
                default:
                    return null;
            }
        }
    }
}
