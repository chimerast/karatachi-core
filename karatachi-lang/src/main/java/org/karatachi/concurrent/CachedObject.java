package org.karatachi.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public abstract class CachedObject<V> {
    private volatile Future<V> cached;

    public V get() throws InterruptedException {
        while (true) {
            Future<V> future = cached;
            if (future == null) {
                Callable<V> eval = new Callable<V>() {
                    public V call() throws InterruptedException {
                        return open();
                    }
                };
                FutureTask<V> futureTask = new FutureTask<V>(eval);
                synchronized (this) {
                    if (cached == null) {
                        cached = futureTask;
                    } else {
                        future = cached;
                    }
                }
                if (future == null) {
                    future = futureTask;
                    futureTask.run();
                }
            }
            try {
                return future.get();
            } catch (CancellationException e) {
                clear();
            } catch (ExecutionException e) {
                throw launderThrowable(e.getCause());
            }
        }
    }

    public void clear() {
        cached = null;
    }

    protected abstract V open();

    private static RuntimeException launderThrowable(Throwable t) {
        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        } else if (t instanceof Error) {
            throw (Error) t;
        } else {
            throw new IllegalStateException("Not unchecked", t);
        }
    }
}
