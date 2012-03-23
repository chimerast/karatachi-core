package org.karatachi.daemon.producer.repeat;

import org.karatachi.daemon.producer.WorkerTask;

public abstract class RepeatTask implements WorkerTask {
    private final Comparable<Object> object;
    private volatile long lastRunTime;

    @SuppressWarnings("unchecked")
    public RepeatTask(Comparable<? extends Object> object, long lastRunTime) {
        this.lastRunTime = lastRunTime;
        this.object = (Comparable<Object>) object;
    }

    public final void updateLastRunTime() {
        this.lastRunTime = System.currentTimeMillis();
    }

    public final long getLastRunTime() {
        return lastRunTime;
    }

    @Override
    public final int compareTo(WorkerTask o) {
        if (this.lastRunTime < ((RepeatTask) o).lastRunTime) {
            return -1;
        } else if (this.lastRunTime > ((RepeatTask) o).lastRunTime) {
            return 1;
        } else {
            return object.compareTo(((RepeatTask) o).object);
        }
    }
}
