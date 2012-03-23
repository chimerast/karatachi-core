package org.karatachi.daemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.karatachi.exception.IncompatibleConfigurationException;

public class DaemonManager {
    private static final List<Daemon> uniqueDaemons =
            Collections.synchronizedList(new LinkedList<Daemon>());
    private static final Map<Class<? extends DaemonGroup>, DaemonGroup> daemonGroups =
            Collections.synchronizedMap(new HashMap<Class<? extends DaemonGroup>, DaemonGroup>());

    static {
        addDaemonGroup(new UniqueDaemonGroup());
    }

    public static void addUniqueDaemon(Daemon daemon) {
        uniqueDaemons.add(daemon);
    }

    public static void addDaemonGroup(DaemonGroup daemonGroup) {
        synchronized (daemonGroups) {
            if (!daemonGroups.containsKey(daemonGroup.getClass())) {
                daemonGroups.put(daemonGroup.getClass(), daemonGroup);
            } else {
                throw new IncompatibleConfigurationException();
            }
        }
    }

    public static void shutdownAll() {
        synchronized (daemonGroups) {
            for (DaemonGroup daemonGroup : daemonGroups.values()) {
                try {
                    daemonGroup.shutdown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends DaemonGroup> T getDaemonGroup(Class<T> daemonGroup) {
        return (T) daemonGroups.get(daemonGroup);
    }

    public static List<DaemonGroup> getDaemonGroups() {
        return new ArrayList<DaemonGroup>(daemonGroups.values());
    }

    public static class UniqueDaemonGroup extends DaemonGroup {
        private UniqueDaemonGroup() {
            super("Unique Daemons");
        }

        @Override
        protected void initialize() {
            synchronized (uniqueDaemons) {
                for (Daemon daemon : uniqueDaemons) {
                    daemon.startup();
                    daemons.addLast(daemon);
                }
            }
        }

        @Override
        protected final Daemon newInstance(String name) {
            throw new UnsupportedOperationException();
        }
    }
}
