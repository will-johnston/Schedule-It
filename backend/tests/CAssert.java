import org.junit.Assert;
import java.util.*;
import java.util.concurrent.Callable;

public class CAssert {
    public static void AssertDoesNotFail(Callable<Integer> function) {
        try {
            function.call();
            pass();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    public static void AssertDoesFail(Callable<Integer> function) {
        try {
            function.call();
            fail();
        }
        catch (Exception e) {
            e.printStackTrace();
            pass();
        }
    }
    public static void AssertNotNull(Object obj) {
        if (obj == null || "null".equals((String)obj)) {
            fail();
        }
    }
    static void fail() {
        System.out.println("CAssert FAIL");
        Assert.fail();
    }
    static void pass() {
        System.out.println("CAssert PASS");
    }
    public static String generateString(int length)
    {
        Random rng = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }
}
