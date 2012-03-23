package org.karatachi.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.karatachi.exception.IncompatibleConfigurationException;

public class DataSourceManager {
    private static String jndiBaseName = "jdbc";
    private static String replicatorMasterName = "master";

    private static DataSourceWrapper masterDataSource;
    private static Map<String, DataSourceWrapper> dataSources;

    private static DatabaseReplicator replicator;

    static {
        load();
    }

    public static void load() {
        masterDataSource = null;
        dataSources = getDataSources(jndiBaseName);
        replicator = new DatabaseReplicator(dataSources.values().toArray(
                new DataSourceWrapper[0]));
    }

    private static Map<String, DataSourceWrapper> getDataSources(String basename) {
        if (basename == null || basename.equals("")) {
            basename = "";
        } else if (!basename.endsWith("/")) {
            basename = basename + "/";
        }

        Map<String, DataSourceWrapper> ret = new HashMap<String, DataSourceWrapper>();
        try {
            InitialContext ic = new InitialContext();

            NamingEnumeration<Binding> bindings = ic
                    .listBindings("java:comp/env/" + basename);
            while (bindings.hasMore()) {
                Binding binding = bindings.next();
                if (!(binding.getObject() instanceof DataSource))
                    continue;

                String name = basename + binding.getName();
                DataSource dataSource = (DataSource) binding.getObject();
                boolean master = binding.getName().equals(replicatorMasterName);

                DataSourceWrapper wrapper = new DataSourceWrapper(name,
                        dataSource, master);
                ret.put(name, wrapper);
                if (master) {
                    masterDataSource = wrapper;
                }
            }

            return ret;
        } catch (NamingException e) {
            throw new IncompatibleConfigurationException(e);
        }
    }

    public static DatabaseReplicator getDatabaseReplicator() {
        return replicator;
    }

    public static ConnectionManager getMasterConnectionManager() {
        if (masterDataSource != null) {
            return new ConnectionManager(masterDataSource, 2);
        } else {
            throw new IncompatibleConfigurationException();
        }
    }

    public static ConnectionManager getConnectionManager(String basename,
            int stack) {
        return new ConnectionManager(dataSources.get(basename), stack + 2);
    }

    public static List<DataSourceWrapper> getDataSources() {
        ArrayList<DataSourceWrapper> ret = new ArrayList<DataSourceWrapper>(
                dataSources.values());
        Collections.sort(ret);
        return ret;
    }
}
