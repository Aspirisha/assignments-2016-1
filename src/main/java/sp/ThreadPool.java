package sp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by andy on 5/7/16.
 */
public class ThreadPool {
    private LinkedList<LightFuture<?>> taskQueue = new LinkedList<>();
    private List<Thread> threads = new ArrayList<>();
    private volatile int idle;

    public ThreadPool(int n) {

        for (int i = 0; i < n; i++) {
            Thread t = new Thread(() -> {
                while (true) {
                    LightFuture<?> task = null;
                    synchronized (ThreadPool.this) {
                        idle++;
                        try {
                            ThreadPool.this.wait();
                        } catch (InterruptedException e) {
                            return;
                        }

                        if (ThreadPool.this.taskQueue.isEmpty()) {
                            continue;
                        }

                        task = taskQueue.pop();
                        idle--;

                    }

                    try {
                        task.get();
                    } catch (LightFuture.LightExecutionException e) { e.printStackTrace();}
                }

            });
            threads.add(t);
            t.start();
        }
    }

    public <R> LightFuture<?> addTask(Supplier<R> s) {
        LightFuture<?> task = new LightFutureImpl<>(s, this);
        addTask(task);
        return task;
    }

    public synchronized <R> void addTask(LightFuture<R> task) {
        taskQueue.push(task);
        notify();
    }

    public synchronized int idleNumber() {
        return idle;
    }

    public void shutdown() {
        for (Thread t : threads) {
            t.interrupt();
        }
    }
}
