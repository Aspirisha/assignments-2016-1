package sp;

import java.util.function.Function;
import java.util.function.Supplier;

import static javafx.scene.input.KeyCode.T;

/**
 * Created by andy on 5/6/16.
 */
public class LightFutureImpl<R> implements LightFuture<R> {
    private ThreadPool pool;
    private Supplier<R> supplier;
    private R result = null;
    private volatile boolean ready = false;
    private boolean failed = false;

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

        pool.addTask(lf);
        return lf;
    }
}
