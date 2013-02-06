package org.karatachi.wicket.chart;

import java.awt.Color;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.jfree.chart.JFreeChart;

public abstract class ChartImage extends NonCachingImage {
    private static final long serialVersionUID = 1L;

    private final int width;
    private final int height;

    public ChartImage(String id, int width, int height) {
        super(id);
        this.width = width;
        this.height = height;

        add(new AttributeModifier("width", Integer.toString(width)));
        add(new AttributeModifier("height", Integer.toString(height)));
    }

    public void setModel(IModel<JFreeChart> model) {
        setDefaultModel(model);
    }

    @Override
    protected IResource getImageResource() {
        return new DynamicImageResource() {
            private static final long serialVersionUID = 1L;

            @Override
            protected byte[] getImageData(Attributes attributes) {
                JFreeChart chart = (JFreeChart) getDefaultModelObject();
                chart.setBackgroundPaint(Color.WHITE);
                chart.setBorderVisible(false);
                return toImageData(chart.createBufferedImage(width, height));
            }
        };
    }
}
