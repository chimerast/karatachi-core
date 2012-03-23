package org.karatachi.daemon.monitor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.karatachi.daemon.Daemon;
import org.karatachi.translator.IntervalTranslator;

public abstract class MonitorDaemon extends Daemon {
    public static final long[] INTERVAL =
            new long[] { IntervalTranslator.sec(15), IntervalTranslator.min(1),
                    IntervalTranslator.min(5), IntervalTranslator.hour(1),
                    IntervalTranslator.day(1) };
    public static final long[] EXPIRE =
            new long[] { IntervalTranslator.hour(1),
                    IntervalTranslator.hour(4), IntervalTranslator.day(1),
                    IntervalTranslator.day(7), IntervalTranslator.day(365) };

    private final String original;
    private final String count;
    private final String rotate;
    private final String remove;

    private Connection conn;

    public MonitorDaemon(String name, String table, String host) {
        super(name);
        this.original =
                String.format(
                        "INSERT INTO %1$s(time, host, title, level, value) VALUES(?, '%2$s', ?, ?, ?)",
                        table, host);
        this.count =
                String.format(
                        "SELECT count(value) FROM %1$s WHERE time = ? AND host='%2$s' AND title=? AND level=?",
                        table, host);
        this.rotate =
                String.format(
                        "SELECT sum(value) / count(value) FROM %1$s WHERE time >= ? AND time < ? AND host='%2$s' AND title=? AND level=?",
                        table, host);
        this.remove =
                String.format(
                        "DELETE FROM %1$s WHERE time < ? AND host='%2$s' AND level=?",
                        table, host);
    }

    protected abstract Connection getConnection() throws SQLException;

    protected abstract void collectData() throws SQLException;

    @Override
    protected final void updateNextRun() {
        setNextRun(getLastStarted() + INTERVAL[0]);
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

    protected void storeValue(long time, String title, double value)
            throws SQLException {
        PreparedStatement stmt1 = conn.prepareStatement(original);
        PreparedStatement stmt2 = conn.prepareStatement(count);
        PreparedStatement stmt3 = conn.prepareStatement(rotate);

        try {
            stmt1.setLong(1, time);
            stmt1.setString(2, title);
            stmt1.setInt(3, 0);
            stmt1.setDouble(4, value);
            stmt1.executeUpdate();

            for (int i = 1; i < INTERVAL.length; ++i) {
                long interval = INTERVAL[i];
                long starttime = (time / interval - 1) * interval;
                long endtime = starttime + interval;

                stmt2.setLong(1, starttime);
                stmt2.setString(2, title);
                stmt2.setInt(3, i);

                ResultSet rs1 = stmt2.executeQuery();
                try {
                    if (rs1.next() && rs1.getInt(1) != 0) {
                        continue;
                    }
                } finally {
                    rs1.close();
                }

                stmt3.setLong(1, starttime);
                stmt3.setLong(2, endtime);
                stmt3.setString(3, title);
                stmt3.setInt(4, i - 1);

                ResultSet rs2 = stmt3.executeQuery();
                try {
                    if (rs2.next() && rs2.getObject(1) != null) {
                        value = rs2.getDouble(1);
                    } else {
                        continue;
                    }
                } finally {
                    rs2.close();
                }

                stmt1.setLong(1, starttime);
                stmt1.setString(2, title);
                stmt1.setInt(3, i);
                stmt1.setDouble(4, value);
                stmt1.executeUpdate();
            }
        } finally {
            stmt1.close();
            stmt2.close();
            stmt3.close();
        }
    }

    private void deleteExpired() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(remove);
        long time = System.currentTimeMillis();
        for (int i = 0; i < INTERVAL.length; ++i) {
            long interval = INTERVAL[i];
            long starttime = (time / interval - 1) * interval;
            long expiretime = starttime - EXPIRE[i] * 2;

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
