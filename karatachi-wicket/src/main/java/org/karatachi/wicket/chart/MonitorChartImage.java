package org.karatachi.wicket.chart;

import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.time.Duration;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeTableXYDataset;
import org.karatachi.daemon.monitor.MBeanMonitorDaemon;

public abstract class MonitorChartImage extends ChartImage {
    private static final long serialVersionUID = 1L;

    private final String[] titles;
    private final int level;
    private final String sql;

    private DrawingSupplier drawingSupplier;

    protected abstract Connection getConnection() throws SQLException;

    public MonitorChartImage(String id, int width, int height, String table,
            String host, String[] titles, int level) {
        super(id, width, height);
        setModel(new MBeanChartImageModel());

        add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(15)));

        this.titles = titles;
        this.level = level;
        this.sql =
                String.format(
                        "SELECT time, value FROM %1$s WHERE host='%2$s' AND title=? AND level=%3$d",
                        table, host, level);
    }

    public void setDrawingSupplier(DrawingSupplier drawingSupplier) {
        this.drawingSupplier = drawingSupplier;
    }

    public TimeTableXYDataset loadData() {
        TimeTableXYDataset ret = new TimeTableXYDataset();
        try {
            Connection conn = getConnection();
            try {
                PreparedStatement stmt = conn.prepareStatement(sql);
                for (String title : titles) {
                    stmt.setString(1, title);

                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        long rounded =
                                rs.getLong("time")
                                        / MBeanMonitorDaemon.INTERVAL[level]
                                        * MBeanMonitorDaemon.INTERVAL[level];
                        ret.add(new Second(new Date(rounded)),
                                rs.getDouble("value"), title);
                    }
                }
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public JFreeChart createChart() {
        DateAxis domainAxis = new DateAxis();
        domainAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);

        long cur =
                (System.currentTimeMillis()
                        / MBeanMonitorDaemon.INTERVAL[level] - 1)
                        * MBeanMonitorDaemon.INTERVAL[level];
        domainAxis.setRange(new Date(cur - (MBeanMonitorDaemon.EXPIRE[level])
                / 3), new Date(cur));

        NumberAxis rangeAxis = new NumberAxis();

        XYLineAndShapeRenderer renderer =
                new XYLineAndShapeRenderer(true, false);

        XYPlot plot = new XYPlot(loadData(), domainAxis, rangeAxis, renderer);
        AxisSpace space = new AxisSpace();
        space.setLeft(80.0);
        plot.setFixedRangeAxisSpace(space);

        if (drawingSupplier != null) {
            plot.setDrawingSupplier(drawingSupplier);
        }

        JFreeChart chart = new JFreeChart(plot);
        chart.getLegend().setItemFont(new Font(Font.DIALOG, Font.PLAIN, 10));

        return chart;
    }

    public class MBeanChartImageModel extends
            LoadableDetachableModel<JFreeChart> {
        private static final long serialVersionUID = 1L;

        @Override
        protected JFreeChart load() {
            return createChart();
        }
    }
}
