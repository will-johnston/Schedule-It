/**
 * Created by williamjohnston on 10/5/17.
 */
public class MovieEvent extends Event {
    String mov_name;
    String theator_name;



    public MovieEvent() {
        super();
    }
    public MovieEvent(EventType type, int id, String date, String time, String address, String mov_name, String theator_name) {
        super(type, id, date, time, address);
        this.mov_name = mov_name;
        this.theator_name = theator_name;
    }

    public String getMov_name() {
        return mov_name;
    }

    public void setMov_name(String mov_name) {
        this.mov_name = mov_name;
    }

    public String getTheator_name() {
        return theator_name;
    }

    public void setTheator_name(String theator_name) {
        this.theator_name = theator_name;
    }

}

