package org.karatachi.daemon.producer.repeat;

import java.util.List;

import org.karatachi.daemon.Daemon;
import org.karatachi.daemon.producer.ProducerDaemon;
import org.karatachi.daemon.producer.ProducerWorkerDaemonGroup;
import org.karatachi.daemon.producer.WorkerDaemon;
import org.karatachi.daemon.producer.WorkerTask;

public abstract class RepeatDaemonGroup extends ProducerWorkerDaemonGroup {
    private final long producerInterval;
    private final long workerInterval;
    private final long taskInterval;

    public RepeatDaemonGroup(String name, long producerInterval,
            long workerInterval, long taskInterval) {
        super(name);
        this.producerInterval = producerInterval;
        this.workerInterval = workerInterval;
        this.taskInterval = taskInterval;
    }

    protected abstract List<? extends RepeatTask> replaceTask();

    protected abstract void work(Daemon worker, RepeatTask task)
            throws Exception;

    @Override
    protected final ProducerDaemon newProducerInstance(String name) {
        return new ProducerDaemon(this, name) {
            @Override
            protected void updateNextRun() {
                setNextRun(getLastStarted() + producerInterval);
            }

            @Override
            protected List<? extends WorkerTask> replaceTask() {
                return RepeatDaemonGroup.this.replaceTask();
            }

            @Override
            protected WorkerTask poll() {
                RepeatTask task = (RepeatTask) queue.pollFirst();
                if (task == null) {
                    return null;
                }

                if (System.currentTimeMillis() > task.getLastRunTime()
                        + taskInterval) {
                    task.updateLastRunTime();
                    running.add(task);
                    return task;
                } else {
                    queue.add(task);
                    return null;
                }
            }

            @Override
            protected void commit(WorkerTask task) {
                if (running.remove(task)) {
                    queue.add(task);
                }
            }

            @Override
            protected void rollback(WorkerTask task) {
                if (running.remove(task)) {
                    queue.add(task);
                }
            }
        };
    }

    @Override
    protected final WorkerDaemon newWorkerInstance(String name) {
        return new WorkerDaemon(this, name) {
            @Override
            protected void updateNextRun() {
                setNextRun(getLastStarted() + workerInterval);
            }

            @Override
            protected boolean work(WorkerTask task) throws Exception {
                RepeatDaemonGroup.this.work(this, (RepeatTask) task);
                return true;
            }
        };
    }
}
