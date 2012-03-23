package org.karatachi.daemon;

import org.karatachi.thread.AcceptInterruptable;
import org.karatachi.thread.InterruptableOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Daemon {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private volatile Worker executor;

    private final String name;

    private volatile long lastStarted;
    private volatile long lastExecution;
    private volatile long nextRun;

    private volatile String detailText;

    public Daemon(String name) {
        this.name = name;
        this.lastStarted = 0L;
        this.nextRun = -1L;

        this.detailText = "Initialized.";
    }

    public final String getName() {
        return name;
    }

    public final long getLastStarted() {
        return lastStarted;
    }

    public final long getNextRun() {
        return nextRun;
    }

    public final long getLastExecutionTime() {
        return lastExecution;
    }

    public String getDetailText() {
        return detailText;
    }

    public final void setDetailText(String format, Object... args) {
        this.detailText = String.format(format, args);
    }

    public final synchronized void startup() {
        if (executor == null) {
            executor = new Worker(name);
            executor.setDaemon(true);
            executor.start();
            detailText = "Started.";
        }
    }

    public final synchronized void wakeup() {
        if (executor != null) {
            synchronized (executor) {
                nextRun = -1L;
                executor.notify();
            }
        }
    }

    public final synchronized void shutdown() {
        if (executor != null) {
            detailText = "Terminating.";
            executor.shutdownRequest();
        }
    }

    public final synchronized void join() throws InterruptedException {
        if (executor != null) {
            executor.join();
            executor = null;
            detailText = "Terminated.";
        }
    }

    public final synchronized Thread.State getStatus() {
        if (executor != null) {
            return executor.getState();
        } else {
            return Thread.State.TERMINATED;
        }
    }

    protected void updateNextRun() {
    }

    protected final void setNextRun(long nextRun) {
        this.nextRun = nextRun;
    }

    protected abstract void work() throws Exception;

    private class Worker extends Thread implements AcceptInterruptable {
        private volatile boolean shutdownRequested = false;
        private volatile InterruptableOperation interruptable = InterruptableOperation.NULL_OPERATION;

        public Worker(String name) {
            super(name);
        }

        @Override
        public void setInterruptable(InterruptableOperation interruptable)
                throws InterruptedException {
            this.interruptable = interruptable;
            if (Thread.interrupted())
                throw new InterruptedException();
        }

        @Override
        public void clearInterruptable() {
            interruptable = InterruptableOperation.NULL_OPERATION;
        }

        public void shutdownRequest() {
            shutdownRequested = true;
            interrupt();
            interruptable.interrupt();
        }

        @Override
        public void run() {
            logger.info("Daemon started.");
            while (!shutdownRequested) {
                try {
                    if (nextRun < 0L || nextRun < System.currentTimeMillis()) {
                        lastStarted = System.currentTimeMillis();
                        try {
                            work();
                        } finally {
                            lastExecution = System.currentTimeMillis()
                                    - lastStarted;
                            updateNextRun();
                        }
                    }

                    if (nextRun < 0L) {
                        synchronized (this) {
                            wait();
                        }
                    } else {
                        long sleep = nextRun - System.currentTimeMillis();
                        if (sleep > 0L) {
                            synchronized (this) {
                                wait(sleep);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (e instanceof InterruptedException) {
                        logger.info("Interrupted.");
                    } else {
                        logger.error("Execution error.", e);
                    }
                }
            }
            logger.info("Daemon stopped.");
        }
    }
}
