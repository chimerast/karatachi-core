package org.karatachi.daemon.producer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.SortedSet;

import org.karatachi.daemon.Daemon;
import org.karatachi.daemon.DaemonGroup;

public abstract class ProducerWorkerDaemonGroup extends DaemonGroup {
    private final ProducerDaemon producer;

    public ProducerWorkerDaemonGroup(String name) {
        super(name);
        producer = newProducerInstance(groupName + "-0");
    }

    protected final WorkerTask poll() {
        return producer.poll();
    }

    protected final void commit(WorkerTask task) {
        producer.commit(task);
    }

    protected final void rollback(WorkerTask task) {
        producer.rollback(task);
    }

    public final SortedSet<WorkerTask> getTaskQueue() {
        return producer.getTaskQueue();
    }

    protected void addTask(Collection<? extends WorkerTask> tasks) {
        producer.addTask(tasks);
    }

    protected void clearTask() {
        producer.clearTask();
    }

    @Override
    public synchronized LinkedList<Daemon> getDaemons() {
        LinkedList<Daemon> ret = super.getDaemons();
        ret.addFirst(producer);
        return ret;
    }

    @Override
    protected synchronized void initialize() {
        producer.startup();
    }

    @Override
    protected synchronized void cleanup() throws InterruptedException {
        producer.shutdown();
        producer.join();
    }

    @Override
    protected synchronized void onChangeCount(int before, int after) {
        if (before == 0 && after > 0) {
            producer.wakeup();
        }
    }

    @Override
    protected final Daemon newInstance(String name) {
        return newWorkerInstance(name);
    }

    protected abstract WorkerDaemon newWorkerInstance(String name);

    protected abstract ProducerDaemon newProducerInstance(String name);
}
