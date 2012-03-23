package org.karatachi.daemon.monitor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.karatachi.daemon.Daemon;
import org.karatachi.translator.IntervalTranslator;

public abstract class MonitorDaemon extends Daemon {
    public static final long[] INTERVAL = new long[] {
            IntervalTranslator.sec(15), IntervalTranslator.min(1),
            IntervalTranslator.min(5), IntervalTranslator.hour(1),
            IntervalTranslator.day(1) };
    public static final long[] EXPIRE = new long[] {
            IntervalTranslator.hour(1), IntervalTranslator.hour(4),
            IntervalTranslator.day(1), IntervalTranslator.day(7),
            IntervalTranslator.day(365) };

    private final String insert;
    private final String count;
    private final String selectAverage;
    private final String selectMaximum;
    private final String selectMinimum;
    private final String delete;

    private Connection conn;

    public MonitorDaemon(String name, String table, String host) {
        super(name);
        this.insert =
                String.format(
                        "INSERT INTO %1$s(time, host, title, level, value) VALUES(?, '%2$s', ?, ?, ?)",
                        table, host);
        this.count =
                String.format(
                        "SELECT count(value) FROM %1$s WHERE time = ? AND host='%2$s' AND title=? AND level=?",
                        table, host);
        this.selectAverage =
                String.format(
                        "SELECT sum(value) / count(value) FROM %1$s WHERE time >= ? AND time < ? AND host='%2$s' AND title=? AND level=?",
                        table, host);
        this.selectMaximum =
                String.format(
                        "SELECT max(value) FROM %1$s WHERE time >= ? AND time < ? AND host='%2$s' AND title=? AND level=?",
                        table, host);
        this.selectMinimum =
                String.format(
                        "SELECT min(value) FROM %1$s WHERE time >= ? AND time < ? AND host='%2$s' AND title=? AND level=?",
                        table, host);
        this.delete =
                String.format(
                        "DELETE FROM %1$s WHERE time < ? AND host='%2$s' AND level=?",
                        table, host);
    }

    protected long[] getInterval() {
        return INTERVAL;
    }

    protected long[] getExpire() {
        return EXPIRE;
    }

    protected abstract Connection getConnection() throws SQLException;

    protected abstract void collectData() throws SQLException;

    @Override
    protected final void updateNextRun() {
        setNextRun(getLastStarted() + getInterval()[0]);
    }

    @Override
    protected final void work() throws Exception {
        conn = null;
        try {
            conn = getConnection();
            collectData();
            deleteExpired();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    protected final void storeValue(String title, double value)
            throws SQLException {
        storeValue(System.currentTimeMillis(), title, value);
    }

    protected final void storeValue(long time, String title, double value)
            throws SQLException {
        PreparedStatement stmt1 = conn.prepareStatement(insert);
        PreparedStatement stmt2 = conn.prepareStatement(count);
        PreparedStatement stmt3 = conn.prepareStatement(selectAverage);
        PreparedStatement stmt4 = conn.prepareStatement(selectMaximum);
        PreparedStatement stmt5 = conn.prepareStatement(selectMinimum);

        try {
            insert(stmt1, time, title, value);
            insert(stmt1, time, title + ".max", value);
            insert(stmt1, time, title + ".min", value);

            for (int i = 1; i < getInterval().length; ++i) {
                long interval = getInterval()[i];
                long starttime = (time / interval - 1) * interval;
                long endtime = starttime + interval;

                if (count(stmt2, starttime, title, i) != 0) {
                    continue;
                }

                rotate(stmt3, stmt1, starttime, endtime, title, i);
                rotate(stmt4, stmt1, starttime, endtime, title + ".max", i);
                rotate(stmt5, stmt1, starttime, endtime, title + ".min", i);
            }
        } finally {
            stmt1.close();
            stmt2.close();
            stmt3.close();
            stmt4.close();
            stmt5.close();
        }
    }

    private void insert(PreparedStatement insertStatement, long time,
            String title, double value) throws SQLException {
        insertStatement.setLong(1, time);
        insertStatement.setString(2, title);
        insertStatement.setInt(3, 0);
        insertStatement.setDouble(4, value);
        insertStatement.executeUpdate();
    }

    private int count(PreparedStatement selectStatement, long starttime,
            String title, int level) throws SQLException {
        selectStatement.setLong(1, starttime);
        selectStatement.setString(2, title);
        selectStatement.setInt(3, level);

        ResultSet rs1 = selectStatement.executeQuery();
        try {
            if (rs1.next()) {
                return rs1.getInt(1);
            } else {
                return 0;
            }
        } finally {
            rs1.close();
        }
    }

    private void rotate(PreparedStatement selectStatement,
            PreparedStatement insertStatement, long starttime, long endtime,
            String title, int level) throws SQLException {
        selectStatement.setLong(1, starttime);
        selectStatement.setLong(2, endtime);
        selectStatement.setString(3, title);
        selectStatement.setInt(4, level - 1);

        double value;
        ResultSet rs2 = selectStatement.executeQuery();
        try {
            if (rs2.next() && rs2.getObject(1) != null) {
                value = rs2.getDouble(1);
            } else {
                return;
            }
        } finally {
            rs2.close();
        }

        insertStatement.setLong(1, starttime);
        insertStatement.setString(2, title);
        insertStatement.setInt(3, level);
        insertStatement.setDouble(4, value);
        insertStatement.executeUpdate();
    }

    private void deleteExpired() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(delete);
        long time = System.currentTimeMillis();
        for (int i = 0; i < getInterval().length; ++i) {
            long interval = getInterval()[i];
            long starttime = (time / interval - 1) * interval;
            long expiretime = starttime - getExpire()[i] * 2;

            stmt.setLong(1, expiretime);
            stmt.setInt(2, i);
            try {
                stmt.executeUpdate();
            } catch (SQLException e) {
                logger.info("Error on DELETE execution was ignored.", e);
            }
        }
    }
}
