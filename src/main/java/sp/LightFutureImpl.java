package sp;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by andy on 5/6/16.
 */
public class LightFutureImpl<R> implements LightFuture<R> {
    private ThreadPool pool;
    private Supplier<R> supplier;
    private R result = null;
    private volatile boolean ready = false;
    private boolean failed = false;
    private ArrayList<LightFuture<?>> childrenTasks = new ArrayList<>();

    LightFutureImpl(Supplier<R> s, ThreadPool p) {
        supplier = s;
        pool = p;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public R get() throws LightExecutionException {
        if (!ready) {
            synchronized (LightFutureImpl.this) {
                if (!ready) {
                    try {
                        result = supplier.get();
                    } catch (Exception e) {
                        failed = true;
                    }
                    ready = true;
                }
            }

            for (LightFuture task : childrenTasks) {
                pool.addTask(task);
            }
            childrenTasks.clear();
        }

        if (failed)
            throw new LightExecutionException();
        return result;
    }

    @Override
    public <T> LightFuture thenApply(Function<R, T> f) {
        LightFuture<T> lf = new LightFutureImpl<>(() -> {
            try {
                return f.apply(get());
            } catch (LightExecutionException e) {
                return null;
            }
        }, pool);

        if (ready)
            pool.addTask(lf);
        else
            childrenTasks.add(lf);
        return lf;
    }
}
