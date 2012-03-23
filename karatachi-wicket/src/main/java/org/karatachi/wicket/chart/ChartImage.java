package org.karatachi.wicket.chart;

import java.awt.Color;

import org.apache.wicket.Resource;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.model.IModel;
import org.jfree.chart.JFreeChart;

public abstract class ChartImage extends NonCachingImage {
    private static final long serialVersionUID = 1L;

    private final int width;
    private final int height;

    public ChartImage(String id, int width, int height) {
        super(id);
        this.width = width;
        this.height = height;

        add(new SimpleAttributeModifier("width", Integer.toString(width)));
        add(new SimpleAttributeModifier("height", Integer.toString(height)));
    }

    public void setModel(IModel<JFreeChart> model) {
        setDefaultModel(model);
    }

    @Override
    protected Resource getImageResource() {
        return new DynamicImageResource() {
            private static final long serialVersionUID = 1L;

            @Override
            protected byte[] getImageData() {
                JFreeChart chart = (JFreeChart) getDefaultModelObject();
                chart.setBackgroundPaint(Color.WHITE);
                chart.setBorderVisible(false);
                return toImageData(chart.createBufferedImage(width, height));
            }
        };
    }
}
