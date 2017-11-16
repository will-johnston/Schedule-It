package database;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by williamjohnston on 11/11/17.
 */

public class Scheduler {
    private List<Long> times;

    public Scheduler(List<Long> times) {
        this.times = times;
        if (times != null) {
            //sort times
            Collections.sort(times);
        }
    }

    //Get Q1 and Q3 for interquartile range
    public Object[] calculateQ1Q3Indices() {

        double q1Index;
        double q3Index;

        double medianIndex = calculateMedianIndex(0, times.size() - 1);
        //edge case -> list of size 1
        if (medianIndex == 0) {
            q1Index = 0.0;
            q3Index = 0.0;
        }
        else if (medianIndex == Math.floor(medianIndex)) {
            //we have an integer
            q1Index = calculateMedianIndex(0, ((int )medianIndex) - 1);
            q3Index = calculateMedianIndex(((int) medianIndex) + 1, times.size() - 1);
        } else {
            //we have a rational number
            q1Index = calculateMedianIndex(0, (int) medianIndex);
            q3Index = calculateMedianIndex((int) Math.ceil(medianIndex), times.size() - 1);
        }
        //get the proper range
        q1Index = Math.ceil(q1Index);
        q3Index = Math.floor(q3Index);
        return new Object[] {q1Index, q3Index};
    }


    //find the median of an array list given the start and end index
    public double calculateMedianIndex(int start, int end) {
        //returns an "index" with added 0.5 if the list is even

        //check for valid indices
        if (start < 0 || end < 0 || start > end) {
            return -1;
        }
        if (end > this.times.size() - 1) {
            return -1;
        }

        //only 1 entry edge case
        if (end - start == 0) {
            return end;
        }

        double index;
        if ((end - start) % 2 != 0) {
            index = start + ((end - start) / 2) + 0.5;
        } else {
            index = start + (end - start) / 2;
        }

        return index;
    }


    public Long checkMostFrequent(double start, double end) {
        //find most frequent
        //if count of most frequent is 20% or larger than total times, use that time
        Map<Long, Integer> map = new HashMap<>();

        for (Long l : this.times) {
            Integer val = map.get(l);
            map.put(l, val == null ? 1 : val + 1);
        }

        Map.Entry<Long, Integer> max = null;
        for (Map.Entry<Long, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }
        long key = max.getKey();
        int value = max.getValue();
        if (value >= 0.20 * (times.size()) && value > 1) {
            //frequent enough to permit as event time and has more than 1 instance
            return key;
        } else {
            //not a frequent enough time, return null to trigger finding median
            return null;
        }
    }

    public Long findBestTime() {
        if (times == null) {
            return null;
        }

        Object[] range = calculateQ1Q3Indices();
        //get IQR (increment upper bound by 1 to handle exclusive domain)
        //apply 1.5 IQR rule
        long q1 = times.get(((Double) range[0]).intValue());
        long q3 = times.get(((Double) range[1]).intValue());
        long iqr = q3 - q1;
        long lowerThreshold = (long) (q1 - (iqr * 1.5));
        long upperThreshold = (long) (q3 + (iqr * 1.5));
        //TODO: use binary search tree to find indices for new range - outliers
        int lowerpost = Collections.binarySearch(times, lowerThreshold);  //find rank
        int upperpost = Collections.binarySearch(times, upperThreshold);  //find rank
        if (lowerpost < 0) {
            lowerpost = (lowerpost + 1) * -1;  //if threshold is not found
        }
        if (upperpost < 0) {
            upperpost = (upperpost + 1) * -1;  //if threshold is not found
        }
        if (lowerpost != upperpost) {
            //makes sure the size is not 1
            times = times.subList(lowerpost, upperpost);
        }

        //find most frequent index, if frequent enough, mf will be the best event time.
        Long mf = checkMostFrequent((double) range[0], (double) range[1]);
        if (mf == null) {
            //if not frequent enough, find median
            double medianInd = calculateMedianIndex(0, times.size() - 1);
            //cast to int, choose the time with lowest index if there if index is not
            long best = this.times.get((int) medianInd);
            return best;
        } else {
            //most frequent is best choice
            return mf;
        }

    }
}
