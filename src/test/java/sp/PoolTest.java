package sp;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by andy on 5/8/16.
 */
public class PoolTest {
    @Test
    public void severalPoolsPrimes() {
        ThreadPool tp = new ThreadPool(2);
        LightFuture<?> t1 = tp.addTask(LightFutureTest.getAllPrimesLessThan(100));
        LightFuture<?> t2 = tp.addTask(LightFutureTest.getAllPrimesLessThan(100));
        try {
            List<Integer> l1 = (List<Integer>)t1.get();
            List<Integer> l2 = (List<Integer>)t2.get();
            assertEquals(l1, l2);

        } catch (LightFuture.LightExecutionException e) {
            fail();
        }
    }

    @Test
    public void severalPoolsPrimesManyTasks() {
        final int tasksNumber = 10000;
        final int poolSize = 2;
        ThreadPool tp = new ThreadPool(poolSize);

        ArrayList<LightFuture> tasks = new ArrayList<>(tasksNumber);
        for (int i = 0; i < tasksNumber; i++) {
            LightFuture<?> task = tp.addTask(LightFutureTest.getAllPrimesLessThan(1000000));
            tasks.add(task);
        }

        try {
            assertEquals(tasks.get(0).get(), tasks.get(tasksNumber - 1).get());
        } catch (LightFuture.LightExecutionException e) {
            fail();
        }
    }


}
