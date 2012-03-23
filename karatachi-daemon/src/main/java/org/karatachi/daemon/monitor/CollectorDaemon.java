package org.karatachi.daemon.monitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.karatachi.daemon.Daemon;

public abstract class CollectorDaemon extends Daemon {
    private static class Data {
        public long time;
        public double[] values;
    }

    private final long interval;
    private final long backlog;
    private final List<Data> datalog;

    public CollectorDaemon(String name) {
        this(name, 5, 2000);
    }

    public CollectorDaemon(String name, long interval, long backlog) {
        super(name);
        this.interval = interval;
        this.backlog = backlog;
        this.datalog = new ArrayList<Data>();
    }

    protected abstract double[] collectData();

    public final Double getValueLatest(int index) {
        synchronized (datalog) {
            if (datalog.size() == 0) {
                return null;
            }
            return datalog.get(datalog.size() - 1).values[index];
        }
    }

    public final Double getValueDiff(int index, long time) {
        synchronized (datalog) {
            if (datalog.size() == 0) {
                return null;
            }

            Data cur = datalog.get(datalog.size() - 1);

            ListIterator<Data> itr = datalog.listIterator(datalog.size());
            while (itr.hasPrevious()) {
                Data prev = itr.previous();
                if (prev.time < cur.time - time) {
                    long diff = cur.time - prev.time;
                    return (cur.values[index] - prev.values[index]) * time
                            / diff;
                }
            }
            return null;
        }
    }

    public final Double getValueDiff32Bit(int index, long time) {
        synchronized (datalog) {
            if (datalog.size() == 0) {
                return null;
            }

            Data cur = datalog.get(datalog.size() - 1);

            ListIterator<Data> itr = datalog.listIterator(datalog.size());
            while (itr.hasPrevious()) {
                Data prev = itr.previous();
                if (prev.time < cur.time - time) {
                    long diff = cur.time - prev.time;
                    double curvalue = cur.values[index];
                    double prevvalue = prev.values[index];

                    if (curvalue < prevvalue) {
                        return ((curvalue + (1L << 32)) - prevvalue) * time
                                / diff;
                    } else {
                        return (curvalue - prevvalue) * time / diff;
                    }
                }
            }
            return null;
        }
    }

    public final Double getValueAverage(int index, long time) {
        synchronized (datalog) {
            if (datalog.size() == 0) {
                return null;
            }

            double value = 0;
            int count = 0;

            Data cur = datalog.get(datalog.size() - 1);

            ListIterator<Data> itr = datalog.listIterator(datalog.size());
            while (itr.hasPrevious()) {
                Data prev = itr.previous();
                value += prev.values[index];
                ++count;
                if (prev.time < cur.time - time) {
                    break;
                }
            }

            if (count == 0) {
                return null;
            } else {
                return value / count;
            }
        }
    }

    @Override
    protected final void updateNextRun() {
        setNextRun(getLastStarted() + interval);
    }

    @Override
    protected void work() throws Exception {
        Data data = new Data();
        data.time = System.currentTimeMillis();
        data.values = collectData();

        synchronized (datalog) {
            datalog.add(data);

            Iterator<Data> itr = datalog.iterator();
            while (itr.hasNext()) {
                if (itr.next().time < data.time - backlog) {
                    itr.remove();
                } else {
                    break;
                }
            }
        }
    }
}
