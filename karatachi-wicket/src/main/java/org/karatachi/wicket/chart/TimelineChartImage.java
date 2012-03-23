package org.karatachi.wicket.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;
import java.util.Date;

import org.apache.wicket.model.LoadableDetachableModel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeTableXYDataset;

public abstract class TimelineChartImage extends ChartImage {
    private static final long serialVersionUID = 1L;

    public TimelineChartImage(String id, int width, int height) {
        super(id, width, height);
        setModel(new MBeanChartImageModel());
    }

    protected abstract TimeTableXYDataset loadData();

    public JFreeChart createChart() {
        DateAxis domainAxis = new DateAxis();
        domainAxis.setTickUnit(new DateTickUnit(DateTickUnit.DAY, 7));
        domainAxis.setTickMarkPosition(DateTickMarkPosition.START);

        Calendar cal = Calendar.getInstance();
        Date max = cal.getTime();
        cal.add(Calendar.MONTH, -3);
        Date min = cal.getTime();
        domainAxis.setRange(min, max);

        NumberAxis rangeAxis = new NumberAxis();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(64, 64, 127));
        renderer.setSeriesShape(0, new Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0));
        renderer.setSeriesPaint(1, new Color(127, 64, 64));
        renderer.setSeriesShape(1, new Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0));

        XYPlot plot = new XYPlot(loadData(), domainAxis, rangeAxis, renderer);
        AxisSpace space = new AxisSpace();
        space.setLeft(80.0);
        plot.setFixedRangeAxisSpace(space);

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
