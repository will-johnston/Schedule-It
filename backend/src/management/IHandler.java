package management;
import com.google.gson.JsonObject;
import database.Notification;
import database.User;

public interface IHandler {
    public String handle(User user, Notification notification, JsonObject response) throws Exception;
    public JsonObject format(Notification notification) throws Exception;
}