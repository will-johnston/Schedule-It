import java.awt.*;

/**
 * Created by williamjohnston on 10/5/17.
 */
public class ConcertEvent extends Event {
    String conc_name;
    String artist;

    public ConcertEvent() {
        super();
    }
    public ConcertEvent(EventType type, int id, String date, String time, String address, String conc_name, String artist) {
        super(type, id, date, time, address);
        this.conc_name = conc_name;
        this.artist = artist;
    }

    public String getConc_name() {
        return conc_name;
    }

    public void setConc_name(String conc_name) {
        this.conc_name = conc_name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
