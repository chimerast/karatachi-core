package org.karatachi.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseReplicator {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final DataSourceWrapper[] dataSources;

    private final DataSourceWrapper master;
    private final DataSourceWrapper[] slaves;

    private final String keyColumn;
    private final String updateColumn;

    private final int fetchSize = 1000;

    DatabaseReplicator(DataSourceWrapper[] dataSources) {
        this(dataSources, "id", "updated_date");
    }

    DatabaseReplicator(DataSourceWrapper[] dataSources, String idColumn,
            String updateColumn) {
        this.dataSources = dataSources;

        DataSourceWrapper master = null;
        List<DataSourceWrapper> slaves = new ArrayList<DataSourceWrapper>();
        for (DataSourceWrapper dataSource : dataSources) {
            if (dataSource.isMaster()) {
                master = dataSource;
            } else {
                slaves.add(dataSource);
            }
        }

        this.master = master;
        this.slaves = slaves.toArray(new DataSourceWrapper[0]);

        this.keyColumn = idColumn;
        this.updateColumn = updateColumn;
    }

    public static interface Selectable {
        public boolean replace(Comparable<Object> original,
                Comparable<Object> replace);

        public static final Selectable SELECT_MAX = new Selectable() {
            @Override
            public boolean replace(Comparable<Object> original,
                    Comparable<Object> replace) {
                return original.compareTo(replace) < 0;
            }
        };

        public static final Selectable SELECT_MIN = new Selectable() {
            @Override
            public boolean replace(Comparable<Object> original,
                    Comparable<Object> replace) {
                return original.compareTo(replace) > 0;
            }
        };
    }

    public Comparable<Object> executeQuery(Selectable selectable,
            boolean stopIfNull, String sql, Object... param)
            throws SQLException {
        ConnectionManager[] connections = new ConnectionManager[dataSources.length];
        try {
            for (int i = 0; i < connections.length; ++i) {
                connections[i] = new ConnectionManager(dataSources[i]);
            }
            return executeQueryAll(connections, selectable, stopIfNull, sql,
                    param);
        } finally {
            for (ConnectionManager conn : connections) {
                if (conn != null) {
                    conn.dispose();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Comparable<Object> executeQueryAll(ConnectionManager[] connections,
            Selectable selectable, boolean stopIfNull, String sql,
            Object... param) throws SQLException {
        Comparable<Object> ret = null;
        for (ConnectionManager conn : connections) {
            if (!conn.isConnected()) {
                continue;
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            for (int i = 0; i < param.length; ++i) {
                stmt.setObject(i + 1, param[i]);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Comparable<Object> curr = (Comparable<Object>) rs.getObject(1);
                if (curr != null) {
                    if (ret == null) {
                        ret = curr;
                    } else if (selectable.replace(ret, curr)) {
                        ret = curr;
                    }
                } else if (stopIfNull) {
                    return null;
                }
            } else if (stopIfNull) {
                return null;
            }
        }
        return ret;
    }

    public long replicate(String tableName) throws SQLException,
            InterruptedException {
        return replicate(tableName, false);
    }

    public long replicateFull(String tableName) throws SQLException,
            InterruptedException {
        return replicate(tableName, true);
    }

    @SuppressWarnings("unchecked")
    private long replicate(String tableName, boolean fullReplicate)
            throws SQLException, InterruptedException {
        String sqlGetAll = String.format("SELECT * FROM %s ORDER BY %s",
                tableName, keyColumn);
        String sqlGetNull = String
                .format("SELECT * FROM %s LIMIT 0", tableName);
        long ret = 0;

        ConnectionManager connMaster = null;
        ConnectionManager[] connSlave = new ConnectionManager[slaves.length];
        try {
            connMaster = new ConnectionManager(master);
            for (int i = 0; i < connSlave.length; ++i) {
                connSlave[i] = new ConnectionManager(slaves[i]);
                if (!connSlave[i].isConnected()) {
                    connSlave[i] = null;
                }
            }

            ResultSet rsMaster = null;
            ResultSet[] rsSlave = new ResultSet[slaves.length];
            ResultSet[] rsSlaveInsertion = new ResultSet[slaves.length];

            {
                connMaster.setAutoCommit(false);
                Statement stmt = connMaster.createStatement();
                stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
                stmt.setFetchSize(fetchSize);
                rsMaster = stmt.executeQuery(sqlGetAll);
            }

            for (int i = 0; i < connSlave.length; ++i) {
                if (connSlave[i] == null) {
                    continue;
                }
                connSlave[i].setAutoCommit(false);
                Statement stmt = connSlave[i].createUpdatableStatement();
                stmt.setFetchDirection(ResultSet.FETCH_UNKNOWN);
                stmt.setFetchSize(fetchSize);
                rsSlave[i] = stmt.executeQuery(sqlGetAll);
                rsSlaveInsertion[i] = connSlave[i].createUpdatableStatement()
                        .executeQuery(sqlGetNull);
            }

            while (rsMaster.next()) {
                Comparable<Object> key = (Comparable<Object>) rsMaster
                        .getObject(keyColumn);
                Comparable<Object> update = null;
                if (!fullReplicate) {
                    update = (Comparable<Object>) rsMaster
                            .getObject(updateColumn);
                }

                for (int i = 0; i < connSlave.length; ++i) {
                    if (connSlave[i] == null) {
                        continue;
                    }
                    if (rsSlave[i].next()) {
                        do {
                            int cmp = key.compareTo(rsSlave[i]
                                    .getObject(keyColumn));
                            if (cmp == 0) {
                                if (fullReplicate
                                        || update.compareTo(rsSlave[i]
                                                .getObject(updateColumn)) != 0) {
                                    copyRecord(rsSlave[i], rsMaster);
                                    ++ret;
                                }
                                break;
                            } else if (cmp < 0) {
                                insertRecord(rsSlaveInsertion[i], rsMaster);
                                rsSlave[i].previous();
                                ++ret;
                                break;
                            } else {
                                removeRecord(rsSlave[i]);
                                ++ret;
                            }
                        } while (rsSlave[i].next());
                    } else {
                        insertRecord(rsSlave[i], rsMaster);
                        ++ret;
                    }
                }

                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
            }

            for (int i = 0; i < connSlave.length; ++i) {
                while (rsSlave[i].next()) {
                    removeRecord(rsSlave[i]);
                    ++ret;

                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                }
                connSlave[i].commit();
            }

            return ret;
        } finally {
            if (connMaster != null) {
                connMaster.dispose();
            }
            if (connSlave != null) {
                for (ConnectionManager dbm : connSlave) {
                    if (dbm != null) {
                        dbm.dispose();
                    }
                }
            }
        }
    }

    private void insertRecord(ResultSet dest, ResultSet src)
            throws SQLException {
        ResultSetMetaData destMetaData = dest.getMetaData();
        dest.moveToInsertRow();
        for (int i = 1; i <= destMetaData.getColumnCount(); ++i) {
            dest.updateObject(i, src.getObject(destMetaData.getColumnName(i)));
        }
        dest.insertRow();
        dest.moveToCurrentRow();
    }

    private void copyRecord(ResultSet dest, ResultSet src) throws SQLException {
        ResultSetMetaData destMetaData = dest.getMetaData();
        for (int i = 1; i <= destMetaData.getColumnCount(); ++i) {
            dest.updateObject(i, src.getObject(destMetaData.getColumnName(i)));
        }
        dest.updateRow();
    }

    private void removeRecord(ResultSet dest) throws SQLException {
        dest.deleteRow();
    }
}
