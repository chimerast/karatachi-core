package org.karatachi.daemon.producer;

import org.karatachi.daemon.Daemon;

public abstract class WorkerDaemon extends Daemon {
    protected final ProducerWorkerDaemonGroup owner;

    public WorkerDaemon(ProducerWorkerDaemonGroup owner, String name) {
        super(name);

        this.owner = owner;
    }

    @Override
    protected final void work() throws Exception {
        WorkerTask task;
        while ((task = owner.poll()) != null) {
            try {
                if (work(task)) {
                    owner.commit(task);
                } else {
                    owner.rollback(task);
                }
            } catch (Exception e) {
                owner.rollback(task);
                throw e;
            }

            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    protected abstract boolean work(WorkerTask task) throws Exception;
}
