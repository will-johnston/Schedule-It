import java.awt.*;

/**
 * Created by williamjohnston on 10/5/17.
 */
public class RestaurantEvent extends Event {
    String rest_name;

    public RestaurantEvent() {
        super();
    }
    public RestaurantEvent(EventType type, int id, String date, String time, String address, String rest_name) {
        super(type, id, date, time, address);
        this.rest_name = rest_name;
    }

    public String getRest_name() {
        return rest_name;
    }

    public void setRest_name(String rest_name) {
        this.rest_name = rest_name;
    }
}
