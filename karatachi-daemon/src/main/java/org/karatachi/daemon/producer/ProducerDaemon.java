package org.karatachi.daemon.producer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import org.karatachi.daemon.Daemon;

public abstract class ProducerDaemon extends Daemon {
    protected final ProducerWorkerDaemonGroup owner;

    protected volatile ConcurrentSkipListSet<WorkerTask> queue;
    protected final ConcurrentLinkedQueue<WorkerTask> running;
    protected final ConcurrentLinkedQueue<WorkerTask> finished;

    public ProducerDaemon(ProducerWorkerDaemonGroup owner, String name) {
        super(name);

        this.owner = owner;

        this.queue = new ConcurrentSkipListSet<WorkerTask>();
        this.running = new ConcurrentLinkedQueue<WorkerTask>();
        this.finished = new ConcurrentLinkedQueue<WorkerTask>();
    }

    protected WorkerTask poll() {
        WorkerTask task = queue.pollFirst();
        if (task != null) {
            running.add(task);
        }
        return task;
    }

    protected void commit(WorkerTask task) {
        if (running.remove(task)) {
            finished.add(task);
        }
    }

    protected void rollback(WorkerTask task) {
        if (running.remove(task)) {
            queue.add(task);
        }
    }

    public final SortedSet<WorkerTask> getTaskQueue() {
        return Collections.unmodifiableSortedSet(queue);
    }

    protected void addTask(Collection<? extends WorkerTask> tasks) {
        queue.addAll(tasks);
        owner.wakeup();
    }

    protected void clearTask() {
        queue.clear();
    }

    @Override
    protected void work() throws Exception {
        if (owner.getCount() == 0) {
            return;
        }

        List<? extends WorkerTask> replace = replaceTask();
        if (replace != null) {
            ConcurrentSkipListSet<WorkerTask> newTaskQueue =
                    new ConcurrentSkipListSet<WorkerTask>(replace);

            newTaskQueue.removeAll(running);

            WorkerTask removedTask;
            while ((removedTask = finished.poll()) != null) {
                newTaskQueue.remove(removedTask);
            }

            queue = newTaskQueue;

            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            // may be deadlock
            owner.wakeup();
        }

        setDetailText("Queue size: %s", queue.size());
    }

    protected abstract List<? extends WorkerTask> replaceTask();
}
