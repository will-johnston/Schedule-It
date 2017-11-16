/**
 * Created by williamjohnston on 11/11/17.
 */
import com.sun.tools.javac.util.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;


public class SchedulerTest {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(SchedulerTest.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println(result.wasSuccessful());
    }

    List<Long> l1, l2, l3, l4, l5, l6, l7, l8, l9, l10, l11;
    @Before
    public void setUp(){
        long[] la1 = {1,2,3};
        l1 = Arrays.stream(la1).boxed().collect(Collectors.toList());
        long[] la2 = {1,2,3,4};
        l2 = Arrays.stream(la2).boxed().collect(Collectors.toList());
        long[] la3 = {1};
        l3 = Arrays.stream(la3).boxed().collect(Collectors.toList());
        long[] la4 = {1,2,3,4,5,6,7};
        l4 = Arrays.stream(la4).boxed().collect(Collectors.toList());
        long[] la5 = {0,1,2,3,4};
        l5 = Arrays.stream(la5).boxed().collect(Collectors.toList());
        long[] la6 = {1,1,1,1,1,2};
        l6 = Arrays.stream(la6).boxed().collect(Collectors.toList());
        long[] la7 = {1,1,1,2,2};
        l7 = Arrays.stream(la7).boxed().collect(Collectors.toList());
        long[] la8 = {1,1,3,4,5,6};
        l8 = Arrays.stream(la8).boxed().collect(Collectors.toList());
        long[] la9 = {1,2,3,3,4,5,6,7,8,9};
        l9 = Arrays.stream(la9).boxed().collect(Collectors.toList());
        long[] la10 = {1,2,3,4,5};
        l10 = Arrays.stream(la10).boxed().collect(Collectors.toList());
        long[] la11 = {1, 333,350, 470, 471, 485,11120};
        l11 = Arrays.stream(la11).boxed().collect(Collectors.toList());


    }
    @Test
    public void medianTest1() {
        //odd number of entries
        Scheduler s = new Scheduler(l1);
        double m1 = s.calculateMedianIndex(0, 2);
        assertEquals(m1, 1.0, 0.01);
    }
    @Test
    public void medianTest2() {
        //event number of entries
        Scheduler s = new Scheduler(l2);
        double m1 = s.calculateMedianIndex(0, 3);
        assertEquals(m1, 1.5, 0.01);
    }
    @Test
    public void medianTest3() {
        //one entry
        Scheduler s = new Scheduler(l3);
        double m1 = s.calculateMedianIndex(0, 0);
        assertEquals(m1, 0.0, 0.01);
    }
    @Test
    public void medianTest4() {
        //invalid indices
        Scheduler s = new Scheduler(l3);
        double m1 = s.calculateMedianIndex(-1, 7);
        assertEquals(m1, -1, 0.01);
    }
    @Test
    public void medianTest5() {
        //invalid indices
        Scheduler s = new Scheduler(l3);
        double m1 = s.calculateMedianIndex(8, 7);
        assertEquals(m1, -1, 0.01);
    }
    @Test
    public void quartileTest1() {
        //list 1
        Scheduler s = new Scheduler(l1);
        Object[] q = s.calculateQ1Q3Indices();
        assertEquals(0.0 , q[0]);
        assertEquals(2.0 , q[1]);
    }
    @Test
    public void quartileTest2() {
        //list 1
        Scheduler s = new Scheduler(l2);
        Object[] q = s.calculateQ1Q3Indices();
        assertEquals(1.0 , q[0]);
        assertEquals(2.0 , q[1]);
    }
    @Test
    public void quartileTest3() {
        //list 1
        Scheduler s = new Scheduler(l3);
        Object[] q = s.calculateQ1Q3Indices();
        assertEquals(0.0 , q[0]);
        assertEquals(0.0 , q[1]);
    }
    @Test
    public void quartileTest4() {
        //list 1
        Scheduler s = new Scheduler(l4);
        Object[] q = s.calculateQ1Q3Indices();
        assertEquals(1.0 , q[0]);
        assertEquals(5.0 , q[1]);
    }
    @Test
    public void quartileTest5() {
        //list 1
        Scheduler s = new Scheduler(l5);
        Object[] q = s.calculateQ1Q3Indices();
        assertEquals(1.0 , q[0]);
        assertEquals(3.0 , q[1]);
    }
    @Test
    public void mostFreqTest1() {
        //list 6
        Scheduler s = new Scheduler(l6);
        long mf = s.checkMostFrequent(0, l6.size() - 1);
        assertEquals(1 , mf);
    }
    @Test
    public void mostFreqTest2() {
        //list 7
        Scheduler s = new Scheduler(l7);
        long mf = s.checkMostFrequent(0, l7.size() - 1);
        assertEquals(1 , mf);
    }
    @Test
    public void mostFreqTest3() {
        //list 8
        Scheduler s = new Scheduler(l8);
        long mf = s.checkMostFrequent(0, l8.size() - 1);
        assertEquals(1 , mf);
    }
    @Test
    public void mostFreqTest4() {
        //list 9
        Scheduler s = new Scheduler(l9);
        long mf = s.checkMostFrequent(0, l9.size() - 1);
        assertEquals(3 , mf);
    }
    @Test
    public void mostFreqTest5() {
        //list 10
        Scheduler s = new Scheduler(l10);
        Long mf = s.checkMostFrequent(0, l10.size() - 1);
        assertEquals(null , mf);
    }
    @Test
    public void findBestTimeTest1() {
        //list 1
        Scheduler s = new Scheduler(l1);
        long ret = s.findBestTime();
        assertEquals(2 , ret);
    }
    @Test
    public void findBestTimeTest2() {
        //list 2
        Scheduler s = new Scheduler(l2);
        long ret = s.findBestTime();
        assertEquals(2 , ret);
    }
    @Test
    public void findBestTimeTest3() {
        //list 3
        Scheduler s = new Scheduler(l3);
        long ret = s.findBestTime();
        assertEquals(1 , ret);
    }
    @Test
    public void findBestTimeTest4() {
        //list 4
        Scheduler s = new Scheduler(l4);
        long ret = s.findBestTime();
        assertEquals(4 , ret);
    }
    @Test
    public void findBestTimeTest5() {
        //list 5
        Scheduler s = new Scheduler(l5);
        long ret = s.findBestTime();
        assertEquals(2 , ret);
    }
    @Test
    public void findBestTimeTest6() {
        //list 6
        Scheduler s = new Scheduler(l6);
        long ret = s.findBestTime();
        assertEquals(1 , ret);
    }
    @Test
    public void findBestTimeTest7() {
        //list 7
        Scheduler s = new Scheduler(l7);
        long ret = s.findBestTime();
        assertEquals(1 , ret);
    }
    @Test
    public void findBestTimeTest8() {
        //list 8
        Scheduler s = new Scheduler(l8);
        long ret = s.findBestTime();
        assertEquals(1 , ret);
    }
    @Test
    public void findBestTimeTest9() {
        //list 9
        Scheduler s = new Scheduler(l9);
        long ret = s.findBestTime();
        assertEquals(3 , ret);
    }
    @Test
    public void findBestTimeTest10() {
        //list 10
        Scheduler s = new Scheduler(l10);
        long ret = s.findBestTime();
        assertEquals(3 , ret);
    }
    @Test
    public void findBestTimeTest11() {
        //null list
        Scheduler s = new Scheduler(null);
        Long ret = s.findBestTime();
        assertEquals(null , ret);
    }
    @Test
    public void findBestTimeTest12() {
        //list 11
        Scheduler s = new Scheduler(l11);
        long ret = s.findBestTime();
        assertEquals(470 , ret);
    }
}
