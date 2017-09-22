/**
 * Created by Ryan on 9/20/2017.
 */
public class Header {
    String Key;
    String Value;
    public Header(String rawValue) {
        if (rawValue == null) {
            System.out.println("Tried to parse a null header!");
            return;
        }
        try {
            String[] headerSplit = rawValue.split(":");
            Key = headerSplit[0].trim();
            Value = headerSplit[1].trim();
        }
        catch (Exception e) {
            System.out.println("RawValue: " + rawValue);
            System.out.println(e.getMessage());
        }
    }
}
