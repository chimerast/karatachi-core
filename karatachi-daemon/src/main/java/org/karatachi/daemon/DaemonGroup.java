package org.karatachi.daemon;

import java.util.Iterator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DaemonGroup {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private volatile boolean running;

    protected final LinkedList<Daemon> daemons;
    protected final LinkedList<Daemon> removedDaemons;

    protected final String groupName;

    public DaemonGroup(String name) {
        this.running = false;

        this.daemons = new LinkedList<Daemon>();
        this.removedDaemons = new LinkedList<Daemon>();

        this.groupName = name;
    }

    public synchronized LinkedList<Daemon> getDaemons() {
        return new LinkedList<Daemon>(daemons);
    }

    public final String getGroupName() {
        return groupName;
    }

    public final boolean isRunning() {
        return running;
    }

    public final synchronized boolean upCount() {
        if (!running)
            return false;
        return setCount(getCount() + 1);
    }

    public final synchronized boolean downCount() {
        if (!running)
            return false;
        return setCount(getCount() - 1);
    }

    public final synchronized int getCount() {
        return daemons.size();
    }

    public final synchronized boolean setCount(int count) {
        if (!running)
            return false;
        if (count < 0)
            return false;

        int before = daemons.size();

        while (daemons.size() < count) {
            Daemon daemon = newInstance(groupName + "-" + (daemons.size() + 1));
            daemon.startup();
            daemons.addLast(daemon);
        }

        while (daemons.size() > count) {
            Daemon daemon = daemons.removeLast();
            daemon.shutdown();
            removedDaemons.addLast(daemon);
        }

        Iterator<Daemon> itr = removedDaemons.iterator();
        while (itr.hasNext()) {
            Daemon daemon = itr.next();
            if (daemon.getStatus() == Thread.State.TERMINATED) {
                try {
                    daemon.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                itr.remove();
            }
        }

        onChangeCount(before, count);
        logger.info("Thread count changed from {} to {}", before, count);
        return true;
    }

    public final synchronized void startup() {
        if (!running) {
            logger.info("Daemon group starting.");
            initialize();
            logger.info("Daemon group started.");
            running = true;
        }
    }

    public final synchronized void shutdown() throws InterruptedException {
        if (running) {
            logger.info("Deamon group stopping.");
            setCount(0);
            while (removedDaemons.size() > 0)
                removedDaemons.removeFirst().join();
            cleanup();
            logger.info("Daemon group stopped.");
            running = false;
        }
    }

    public final synchronized void wakeup() {
        if (running) {
            for (Daemon daemon : daemons) {
                daemon.wakeup();
            }
        }
    }

    protected void initialize() {
    }

    protected void cleanup() throws InterruptedException {
    }

    protected void onChangeCount(int before, int after) {
    }

    protected abstract Daemon newInstance(String name);
}
