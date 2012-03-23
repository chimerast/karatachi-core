package org.karatachi.wicket.chart;

import java.util.Map;
import java.util.TreeMap;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarChartImage extends ChartImage {
    private static final long serialVersionUID = 1L;

    private final IModel<Map<String, ? extends Number>> model;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public BarChartImage(String id, TreeMap<String, ? extends Number> map,
            int width, int height) {
        this(id, new Model(map), width, height);
    }

    public BarChartImage(String id,
            IModel<Map<String, ? extends Number>> model, int width, int height) {
        super(id, width, height);
        setModel(new BarChartImageModel());
        this.model = model;
    }

    public class BarChartImageModel extends LoadableDetachableModel<JFreeChart> {
        private static final long serialVersionUID = 1L;

        @Override
        protected JFreeChart load() {
            DefaultCategoryDataset d = new DefaultCategoryDataset();

            Map<String, ? extends Number> map = model.getObject();
            for (String key : map.keySet()) {
                d.addValue(map.get(key), "", key);
            }

            JFreeChart chart =
                    ChartFactory.createBarChart("", "", "", d,
                            PlotOrientation.VERTICAL, false, false, false);

            return chart;
        }
    }
}
