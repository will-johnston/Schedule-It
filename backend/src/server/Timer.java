package server;

import java.util.Calendar;
import java.util.TimeZone;

public class Timer {
    long startTime;         //should be in seconds
    int seconds;

    //starts on Construction
    public Timer(int seconds) {
        this.seconds = Math.abs(seconds);
        this.startTime = getCurrentTime();
    }
    public boolean hasExpired() {
        if (startTime == 0) {
            return false;
        }
        long diff = getCurrentTime() - startTime;
        if (diff > seconds) {
            return true;
        }
        else {
            return false;
        }
    }
    public void restart() {
        startTime = getCurrentTime();
    }

    private long getCurrentTime() {
        return Calendar.getInstance(TimeZone.getTimeZone("EST")).getTimeInMillis() / 1000;
    }
}
