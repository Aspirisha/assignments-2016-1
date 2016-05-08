package sp;

import java.util.function.Function;

/**
 * Created by andy on 5/6/16.
 */
public interface LightFuture<R> {
   class LightExecutionException extends Exception { }

    public boolean isReady();
    R get() throws LightExecutionException;
    <T> LightFuture thenApply(Function<R, T> f);
}
